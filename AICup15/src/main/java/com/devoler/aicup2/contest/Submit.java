package com.devoler.aicup2.contest;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.tuple.Pair;

import com.devoler.aicup2.model.RaceTrack;
import com.devoler.aicup2.model.RaceTrackParser;
import com.devoler.aicup2.model.RemoteSolver;
import com.devoler.aicup2.model.SolutionChecker;
import com.devoler.aicup2.model.Solver;
import com.devoler.aicup2.persistent.DAO;
import com.devoler.aicup2.persistent.SubmitResults;

public final class Submit {
	public static enum Races {
		MONTE_CARLO("1 UUUUUUUUUUURUUUUURUURUUUULUUURRRRDRRDRRRRRRRRRRRRRRRRURRRRRRRRRRDRRRRRRRRRRURRRRRRRRRDRRRRRRURRRURURURUUUUUULUUULUUULUUUURUURURURURURURURURURURURURURURURURURURRRDRDRDRDDLDDDRDDDRDDDRDDDRRRRUUUUULUUULUUUUURRRRRRRDDDDLDDDDDDDDDDDDLDDDDLDDDLDDLDDLDDLDLDDLDLDLDLLDLLDLLLDLLDLLDLLLLLDLLLLLLLDLLLLLLLLLLLLLLLLLDLLLLLDDDLLLLLULULULLLLLULLLLLLLLLLLULLLLLLLLLDLDDDDDDLDDDDDDDRDRDRDRDRDDDDDDDDDDDDDDDDDDLDLLLDLDLDDDDDDRDDDDDDRDDRDDRDRDRDRRDRDRDDDRDDDLLLLLLLLLDLLLLLLUUUURUUUULUUUUUUUUUUULUUUUUUUUUUUUUUUUUUUULUUUUUUUUUUU"),
		SPA("1 LLLLLLLLLLLLLLLLULLLLLLLLLLLLLLLLLLLLULUUURRURRURRURRURRURRURURRURRURRURRURRRURRRRRURRRRRURRRRRRURRRRRRRRRRURRRRURRURRURRRRDRRRRDRRRDRRDRRRRRRRRRRRRRURRRRRRRRRRRRDRRRRRDRRRRDRRRRRDRRRRDRRRRRDRRRRRDRRRRDRRRRRDRRRRDRRRRRDRRRRDRRRRRDRRDDRDDDDRDDRDRDRRRRRRDRRRRRDRDDDDDDDRDDDDDDDDDDDRDDDDDDDLDDLDLLDLLLULLULUULUUUUUULUUUUULLULLLLULLLULLLLULLLLULLLULLLLULLLLULLLULLLLDLDLDDLDDLDDDLDDLDDLDDRDDRDRDDRDDRDDRDRDDRDDRDRDDRDDRDDRDRDDRDDLDLDLDLDLDLDLDDDRDDRDRDDRDDRDRDDRDDRDDDLDLDLLDLLDLDLLDLLDLLDLLLULLUULULULUULULULULUULULULUULULULUULUULUUULUUUUUUUUUUUUUUUUUULUUUUUUUUULUULUUULULULULULULULULULLULULULULULULLLLLULLLLLLLULLLLLLLLULLLLLLLULLLLLLUUUURRRURURUUUULLLLDDLLLDLLLLLLLLL"),
		MONZA("2 LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLUULUUUULLLLDDDLDLDLLDLLLDLLLLLLLLLLLLLLLLULLLLULLLULLULULULULULUUULUULUUULUUULUUUUULUUUUUUULUUUUUULUULLULLLLULLULUUULUULUUULUULUUULUULUUULUUULUULUUULUULUUULUUUUURURURURRURRRURRRURRRURRRURRRURRRURRRURRRDRRDRDRDRDRDRDRDRDDRDRDRDRDRDRDDRDRDRDRDRDRDRDDRDRDRDRDRDRDDRDRDRRDRDRRDRRDRDRRDRDRRDRRDRDRRDRRDRDRRDRDRRRURRRRRURRRRDRRDRDRDDRDRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRDRRRDRRRDRRDRDRDDRDDDDRDDDDDDDLDDDLDLDLDLDLDLDLLDLLDLLDLLDLLLDLLLDLLLLLLLLLLDLLLLLLLLLLLLLLLLLDLLLLLLLLLLLLL");

		private final String raceTrackString;
		private final RaceTrack raceTrack;
		
		private Races(final String raceTrackString) {
			this.raceTrackString = raceTrackString;
			this.raceTrack = RaceTrackParser.parse(raceTrackString);
		}
		
		public String getRaceTrackString() {
			return raceTrackString;
		}
		
		public RaceTrack getRaceTrack() {
			return raceTrack;
		}
		
		public String getSolution(SubmitResults submit) {
			switch(this) {
			case MONTE_CARLO:
				return submit.getSolution1();
			case SPA:
				return submit.getSolution2();
			case MONZA:
				return submit.getSolution3();
			}
			throw new IllegalStateException();
		}
	}
	
	private final String name;
	
	private volatile boolean finished = false;
	private final StringBuffer status = new StringBuffer();
	
	public Submit(final String name, final URL url, final long timestamp) {
		this.name = name;
		status.append("Submit from " + name + " ");
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(new Runnable(){
			@Override
			public void run() {
				try{
					Solver solver = new RemoteSolver(url);
					Pair<String, String> result1 = solve(solver, Races.MONTE_CARLO.getRaceTrackString());
					String solution1 = result1.getLeft();
					if (solution1 == null) {
						solution1 = "";
						status.append("1. - (").append(result1.getRight()).append("), ");
					} else {
						status.append("1. ").append(SolutionChecker.checkSolution(Races.MONTE_CARLO.getRaceTrack(), solution1).getStatus()).append(", ");
					}
					Pair<String, String> result2 = solve(solver, Races.SPA.getRaceTrackString());
					String solution2 = result2.getLeft();
					if (solution2 == null) {
						solution2 = "";
						status.append("2. - (").append(result2.getRight()).append("), ");
					} else {
						status.append("2. ").append(SolutionChecker.checkSolution(Races.SPA.getRaceTrack(), solution2).getStatus()).append(", ");
					}
					Pair<String, String> result3 = solve(solver, Races.MONZA.getRaceTrackString());
					String solution3 = result3.getLeft();
					if (solution3 == null) {
						solution3 = "";
						status.append("3. - (").append(result3.getRight()).append(")");
					} else {
						status.append("3. ").append(SolutionChecker.checkSolution(Races.MONZA.getRaceTrack(), solution3).getStatus());
					}
					System.out.println(status.toString());
					DAO.submitResult(name, solution1, solution2, solution3, timestamp);
				} finally {
					executor.shutdown();
					finished = true;
				}
			}
		});
	}
	
	public String getName() {
		return name;
	}
	
	public String getStatus() {
		String currentStatus = status.toString();
		if (!finished) {
			int dots = (int) ((System.currentTimeMillis() / 500) % 4);
			for (int i = 0; i < dots; i++) {
				currentStatus += ".";
			}
		}
		return currentStatus;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	private Pair<String, String> solve(Solver solver, String raceTrackString) {
		try {
			Future<String> future = solver.solve(raceTrackString);
			String result = waitForSolution(future);
			return Pair.of(result, null);
		} catch (ExecutionException ee) {
			if (ee.getCause() instanceof ConnectException) {
				return Pair.of(null, "Could not connect to solution server");
			} else if (ee.getCause() instanceof SocketTimeoutException) {
				return Pair.of(null, "Timed out");
			} else if (ee.getCause() instanceof IOException) {
				return Pair.of(null, "I/O error: " + ee.getCause().getMessage());
			} else {
				ee.printStackTrace();
				return Pair.of(null, "Error: " + ee.getCause().getMessage());
			}
		} catch (InterruptedException ie) {
			return Pair.of(null, "Interrupted");
		}
	}
	
	private String waitForSolution(final Future<String> future) throws InterruptedException,
			ExecutionException {
		while (true) {
			try {
				return future.get(500, TimeUnit.MILLISECONDS);
			} catch (TimeoutException e) {
				Thread.sleep(500);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		Submit s = new Submit("homer", new URL("http://localhost:8084"), System.currentTimeMillis());
		while(!s.isFinished()) {
			System.out.println(s.getStatus());
			Thread.sleep(500);
		}
		System.out.println(DAO.getSubmitResults());
	}
}
