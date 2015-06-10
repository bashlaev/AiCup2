package com.devoler.aicup2.model;

import org.apache.commons.lang3.tuple.Pair;

public class ConditionalMoveInstruction extends Instruction {
	private static final int COST = 5;
	
	private final Direction direction;

	public ConditionalMoveInstruction(final int lineNumber, final Direction direction) {
		super(lineNumber);
		this.direction = direction;
	}

	public Pair<Integer, Integer> apply(Level level, Pair<Integer, Integer> robotPosition)
			throws InstructionExecutionException {
		Pair<Integer, Integer> newPosition = direction.apply(robotPosition);
		if (level.isCellPlayable(newPosition.getLeft(), newPosition.getRight())) {
			return newPosition;
		} else {
			return robotPosition;
		}
	}

	@Override
	public int getCost() {
		return COST;
	}
	
	@Override
	public String toString() {
		return getLineNumber() + " TRY_" + direction.name();
	}
}
