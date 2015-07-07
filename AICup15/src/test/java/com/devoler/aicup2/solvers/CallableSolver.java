package com.devoler.aicup2.solvers;

import java.util.concurrent.Callable;

abstract class CallableSolver {
	public abstract Callable<String> solverFor(String task);
}
