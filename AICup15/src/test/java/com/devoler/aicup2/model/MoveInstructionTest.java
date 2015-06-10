package com.devoler.aicup2.model;

import static org.junit.Assert.*;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

public class MoveInstructionTest {
	@Test
	public void testMoveInstructions() {
		Level l1 = new Level(Levels.concatWithLineFeed(Levels.L1));
		
		// correct moves
		try {
			assertEquals(Pair.of(0, 0), new MoveInstruction(0, Direction.LEFT).apply(l1, Pair.of(0, 1)));
			assertEquals(Pair.of(0, 1), new MoveInstruction(0, Direction.RIGHT).apply(l1, Pair.of(0, 0)));
			assertEquals(Pair.of(0, 2), new MoveInstruction(0, Direction.UP).apply(l1, Pair.of(1, 2)));
			assertEquals(Pair.of(2, 2), new MoveInstruction(0, Direction.DOWN).apply(l1, Pair.of(1, 2)));
		} catch (InstructionExecutionException e) {
			fail(e.getMessage());
		}
		
		// invalid moves
		expectInvalidMove(new MoveInstruction(0, Direction.LEFT), l1, Pair.of(0, 0));
		expectInvalidMove(new MoveInstruction(0, Direction.UP), l1, Pair.of(0, 0));
		expectInvalidMove(new MoveInstruction(0, Direction.DOWN), l1, Pair.of(0, 0));
		expectInvalidMove(new MoveInstruction(1, Direction.LEFT), l1, Pair.of(1, 2));
		expectInvalidMove(new MoveInstruction(0, Direction.RIGHT), l1, Pair.of(1, 2));
	}
	
	private static void expectInvalidMove(MoveInstruction move, Level level, Pair<Integer, Integer> position) {
		try {
			move.apply(level, position);
			fail("Instruction should be invalid: " + move);
		} catch (InstructionExecutionException e) {
		}		
	}

}
