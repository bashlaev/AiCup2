package com.devoler.aicup2.model;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Algorithm is a list of instructions to be executed sequentially.
 * 
 * @author homer
 */
public class Algorithm {
	private final List<Instruction> instructions;

	
	public Algorithm(final String algorithmString) {
		this.instructions = null;
	}

	public boolean apply(Level level) throws InstructionExecutionException {
		Pair<Integer, Integer> robotPosition = level.getInitialRobotPosition();
		for (Instruction instruction : instructions) {
			robotPosition = instruction.apply(level, robotPosition);
		}
		if (robotPosition.equals(level.getTargetPosition())) {
			return true;
		}
		return false;
	}

	public int getCost() {
		int cost = 0;
		for (Instruction instruction : instructions) {
			cost += instruction.getCost();
		}
		return cost;
	}
}
