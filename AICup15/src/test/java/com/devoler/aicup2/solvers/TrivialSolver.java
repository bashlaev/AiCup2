package com.devoler.aicup2.solvers;

import java.util.StringTokenizer;
import java.util.concurrent.Callable;

public final class TrivialSolver extends CallableSolver {
	@Override
	public Callable<String> solverFor(final String task) {
		return new Callable<String>() {
			@Override
			public String call() throws Exception {
				Thread.sleep(20000);
				StringTokenizer st = new StringTokenizer(task, " ");
				// ignore width
				st.nextToken();
				String directions = st.nextToken();
				StringBuilder solution = new StringBuilder();
				for (char c : directions.toCharArray()) {
					switch (c) {
					case 'U':
						solution.append("UD");
						break;
					case 'D':
						solution.append("DU");
						break;
					case 'L':
						solution.append("LR");
						break;
					case 'R':
						solution.append("RL");
						break;
					}
				}
				return solution.toString();
			}
		};
	}
}
