package com.devoler.aicup2.model;

import org.apache.commons.lang3.tuple.Pair;

public class InstructionExecutionException extends Exception {
	private static final long serialVersionUID = -4678510855750858189L;

	public InstructionExecutionException(Level level, Pair<Integer, Integer> robotPosition, Instruction instruction) {
		super("Instruction " + instruction + " failed, robot position: " + robotPosition);
	}
}
