package com.devoler.aicup2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

public class RaceResult {
	public static RaceResult couldNotParseSolution() {
		return new RaceResult(Status.COULD_NOT_PARSE_SOLUTION, null, 0);
	}

	private static RaceResult fail(List<Pair<Integer, Integer>> raceLog, Status status) {
		Validate.notNull(raceLog);
		Validate.isTrue(!raceLog.isEmpty());
		return new RaceResult(status, raceLog, raceLog.size() - 1);
	}
	
	public static RaceResult didNotFinish(List<Pair<Integer, Integer>> raceLog) {
		return fail(raceLog, Status.DID_NOT_FINISH);
	}

	public static RaceResult illegalMove(List<Pair<Integer, Integer>> raceLog) {
		return fail(raceLog, Status.ILLEGAL_MOVE);
	}

	public static RaceResult finish(List<Pair<Integer, Integer>> raceLog, Number time) {
		Validate.notNull(raceLog);
		Validate.isTrue(!raceLog.isEmpty());
		return new RaceResult(Status.SUCCESS, raceLog, time);
	}

	public static enum Status {
		SUCCESS,
		DID_NOT_FINISH,
		ILLEGAL_MOVE,
		COULD_NOT_PARSE_SOLUTION;
	}
	
	private final List<Pair<Integer, Integer>> raceLog = new ArrayList<>();
	private final Status status;
	private final Number time;
	
	private RaceResult(final Status status, final List<Pair<Integer, Integer>> raceLog, final Number time) {
		this.status = Objects.requireNonNull(status);
		if (raceLog != null) {
			this.raceLog.addAll(raceLog);
		}
		this.time = time;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public List<Pair<Integer, Integer>> getRaceLog() {
		return raceLog;
	}
	
	public Number getTime() {
		return time;
	}
}
