package com.devoler.aicup2.solvers;

import java.io.IOException;

public final class Solvers {

	public static void main(String[] args) {
		try {
			new RemoteSolverAdapter("BetterSolver(3)", new BetterSolver(3), 8088);
			new RemoteSolverAdapter("BetterSolver(5)", new BetterSolver(5), 8080);
			new RemoteSolverAdapter("BetterSolver(9)", new BetterSolver(9), 8089);
			new RemoteSolverAdapter("GoodSolver", new GoodSolver(), 8086);
			new RemoteSolverAdapter("GoodSolverRandomized", new GoodSolverRandomized(), 8087);
			new RemoteSolverAdapter("TrivialSolver", new TrivialSolver(), 8084);
			new RemoteSolverAdapter("TrivialEnhancedSolver", new TrivialEnhancedSolver(), 8085);
			new RemoteSolverAdapter("InvalidFormatSolver", new InvalidFormatSolver(), 8081);
			new RemoteSolverAdapter("IllegalMoveSolver", new IllegalMoveSolver(), 8082);
			new RemoteSolverAdapter("DidNotFinishSolver", new DidNotFinishSolver(), 8083);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
