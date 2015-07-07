package com.devoler.aicup2.model;

import java.util.concurrent.Future;

public interface Solver {
	public Future<String> solve(String raceTrackString);
}
