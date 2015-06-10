package com.devoler.aicup2.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

public final class RepeatInstruction extends Instruction {
	private final int times;
	private final List<Instruction> instructions;

	public RepeatInstruction(final int lineNumber, final int times, final List<Instruction> instructions) {
		super(lineNumber);
		Validate.isTrue(times > 0, "the number of repeats should be positive");
		this.times = times;
		this.instructions = new ArrayList<>(instructions);
	}

	@Override
	public Pair<Integer, Integer> apply(Level level, Pair<Integer, Integer> robotPosition)
			throws InstructionExecutionException {
		Pair<Integer, Integer> position = robotPosition;
		for (int counter = 0; counter < times; counter++) {
			Pair<Integer, Integer> positionAtCycleStart = position;
			for (Instruction instruction : instructions) {
				position = instruction.apply(level, position);
			}
			// instructions are stateless, therefore if one iteration doesn't
			// change robot's position, the following iterations will neither
			// and can be ignored.
			if (position.equals(positionAtCycleStart)) {
				break;
			}
		}
		return position;
	}

	@Override
	public int getCost() {
		int cost = 0;
		for (Instruction instruction : instructions) {
			cost += instruction.getCost();
		}
		return cost + times;
	}

}
