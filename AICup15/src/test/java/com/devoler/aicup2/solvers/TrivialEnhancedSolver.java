package com.devoler.aicup2.solvers;

import java.util.StringTokenizer;
import java.util.concurrent.Callable;

public final class TrivialEnhancedSolver extends CallableSolver {
	@Override
	public Callable<String> solverFor(final String task) {
		return new Callable<String>() {
			@Override
			public String call() throws Exception {
				StringTokenizer st = new StringTokenizer(task, " ");
				// ignore width
				st.nextToken();
				String directions = st.nextToken();
				StringBuilder solution = new StringBuilder();
				int i = 0;
				while(i < directions.length()) {
					char c = directions.charAt(i);
					int startIndex = i;
					while(i + 1 < directions.length()) {
						if (directions.charAt(i + 1) == c) {
							i++;
						} else {
							break;
						}
					}
					int endIndex = i;
					int charsCount = endIndex - startIndex + 1;
					solution.append(solveSequence(c, charsCount));
					i++;
				}
				return solution.toString();
			}
		};
	}
	
	private static String solveSequence(char c, int count) {
		if (count == 0) {
			return "";
		}
		int sqr = 1;
		while(count >= (sqr + 1) * (sqr + 1)) {
			sqr++;
		}
		StringBuilder b = new StringBuilder();
		for(int i = 0 ; i < sqr; i++) {
			b.append(c);
		}
		for(int i = 0 ; i < sqr; i++) {
			b.append(oppositeChar(c));
		}
		b.append(solveSequence(c, count - sqr * sqr));
		return b.toString();
	}
	
	private static char oppositeChar(char c) {
		switch (c) {
		case 'U':
			return 'D';
		case 'D':
			return 'U';
		case 'L':
			return 'R';
		case 'R':
			return 'L';
		}
		throw new RuntimeException("Invalid char: " + c);
	}
}
