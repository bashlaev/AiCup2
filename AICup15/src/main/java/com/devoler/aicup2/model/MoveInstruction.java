package com.devoler.aicup2.model;

import org.apache.commons.lang3.tuple.Pair;

public class MoveInstruction extends Instruction {
	private static final int COST = 2;
	
	private final Direction direction;
	
	public MoveInstruction(final int lineNumber, final Direction direction) {
		super(lineNumber);
		this.direction = direction;
	}

	public Pair<Integer, Integer> apply(Level level, Pair<Integer, Integer> robotPosition)
			throws InstructionExecutionException {
		Pair<Integer, Integer> newPosition = direction.apply(robotPosition);
		if (level.isCellPlayable(newPosition.getLeft(), newPosition.getRight())) {
			return newPosition;
		} else {
			throw new InstructionExecutionException(level, robotPosition, this);
		}
	}
	
	@Override
	public int getCost() {
		return COST;
	}

	@Override
	public String toString() {
		return getLineNumber() + " " + direction.name();
	}
}
