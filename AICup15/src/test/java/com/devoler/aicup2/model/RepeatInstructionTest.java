package com.devoler.aicup2.model;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

public class RepeatInstructionTest {
	@Test
	public void testMoveInstructions() {
		Level l2 = new Level(Levels.concatWithLineFeed(Levels.L2));

		// correct moves
		try {
			RepeatInstruction repeat1 = new RepeatInstruction(0, 2, Arrays.asList(new Instruction[] {
					new MoveInstruction(1, Direction.LEFT), new MoveInstruction(2, Direction.UP) }));
			assertEquals(Pair.of(0, 2), repeat1.apply(l2, l2.getInitialRobotPosition()));
			RepeatInstruction repeat2 = new RepeatInstruction(0, 100, Arrays.asList(new Instruction[] {
					new MoveInstruction(1, Direction.LEFT), new MoveInstruction(2, Direction.UP),
					new MoveInstruction(3, Direction.DOWN), new MoveInstruction(4, Direction.RIGHT) }));
			assertEquals(l2.getInitialRobotPosition(), repeat2.apply(l2, l2.getInitialRobotPosition()));
			RepeatInstruction repeat3 = new RepeatInstruction(0, 5,
					Arrays.asList(new Instruction[] { new ConditionalMoveInstruction(1, Direction.LEFT),
							new ConditionalMoveInstruction(2, Direction.UP) }));
			assertEquals(l2.getTargetPosition(), repeat3.apply(l2, l2.getInitialRobotPosition()));
			RepeatInstruction repeat4 = new RepeatInstruction(0, 10,
					Arrays.asList(new Instruction[] { new ConditionalMoveInstruction(1, Direction.LEFT),
							new ConditionalMoveInstruction(2, Direction.UP) }));
			assertEquals(Pair.of(0, 0), repeat4.apply(l2, l2.getInitialRobotPosition()));
		} catch (InstructionExecutionException e) {
			fail(e.getMessage());
		}

		// invalid moves
		expectInvalidMove(
				new RepeatInstruction(0, 3, Arrays.asList(new Instruction[] { new MoveInstruction(1, Direction.LEFT),
						new MoveInstruction(2, Direction.UP) })), l2, l2.getInitialRobotPosition());
		expectInvalidMove(
				new RepeatInstruction(0, 5, Arrays.asList(new Instruction[] {
						new ConditionalMoveInstruction(1, Direction.LEFT), new MoveInstruction(2, Direction.UP) })),
				l2, l2.getInitialRobotPosition());
	}

	private static void expectInvalidMove(RepeatInstruction move, Level level, Pair<Integer, Integer> position) {
		try {
			move.apply(level, position);
			fail("Instruction should be invalid: " + move);
		} catch (InstructionExecutionException e) {
		}
	}

}
