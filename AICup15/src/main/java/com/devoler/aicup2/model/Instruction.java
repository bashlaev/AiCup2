package com.devoler.aicup2.model;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Instruction directed at the robot. Robot may only change its position as a
 * side-effect of applying an instruction. Each instruction occupies a single
 * line and has a unique line number. Assigning line numbers and maintaining
 * their uniqueness is the responsibility of the context.
 * 
 * @author homer
 */
public abstract class Instruction {
	private final int lineNumber;

	protected Instruction(final int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public final int getLineNumber() {
		return lineNumber;
	}

	/**
	 * Applies the instruction.
	 * 
	 * @param level
	 *            Level to apply the instruction to.
	 * @param robotPosition
	 *            Current robot's position.
	 * @return New robot's position.
	 * @throws InstructionExecutionException
	 *             when applying a transaction moves the robot out of bounds or
	 *             to a non-playable cell.
	 */
	public abstract Pair<Integer, Integer> apply(Level level, Pair<Integer, Integer> robotPosition)
			throws InstructionExecutionException;

	/**
	 * Returns the cost of the instruction.
	 * 
	 * @return a positive integer that denotes a virtual cost of executing the
	 *         instruction.
	 */
	public abstract int getCost();
}
