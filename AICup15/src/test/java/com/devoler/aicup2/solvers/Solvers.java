package com.devoler.aicup2.solvers;

import java.io.IOException;

public final class Solvers {

	public static void main(String[] args) {
		try {
			new RemoteSolverAdapter("TrivialSolver", new TrivialSolver(), 8084);
			new RemoteSolverAdapter("TrivialEnhancedSolver", new TrivialEnhancedSolver(), 8080);
			new RemoteSolverAdapter("InvalidFormatSolver", new InvalidFormatSolver(), 8081);
			new RemoteSolverAdapter("IllegalMoveSolver", new IllegalMoveSolver(), 8082);
			new RemoteSolverAdapter("DidNotFinishSolver", new DidNotFinishSolver(), 8083);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
