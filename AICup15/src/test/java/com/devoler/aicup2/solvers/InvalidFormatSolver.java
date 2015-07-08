package com.devoler.aicup2.solvers;

import java.util.concurrent.Callable;

public final class InvalidFormatSolver extends CallableSolver {
	@Override
	public Callable<String> solverFor(final String task) {
		return new Callable<String>() {
			@Override
			public String call() throws Exception {
				return "RRILLUD";
			}
		};
	}
}
