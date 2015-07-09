package com.devoler.aicup2.solvers;

import java.util.StringTokenizer;
import java.util.concurrent.Callable;

public final class IllegalMoveSolver extends CallableSolver {
	@Override
	public Callable<String> solverFor(final String task) {
		return new Callable<String>() {
			@Override
			public String call() throws Exception {
				StringTokenizer st = new StringTokenizer(task, " ");
				// ignore width
				st.nextToken();
				return st.nextToken();
			}
		};
	}
}
