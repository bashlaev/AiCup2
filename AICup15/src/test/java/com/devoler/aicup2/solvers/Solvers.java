package com.devoler.aicup2.solvers;

import java.io.IOException;

public final class Solvers {

	public static void main(String[] args) {
		try {
			new RemoteSolverAdapter("TrivialSolver", new TrivialSolver(), 8080);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
