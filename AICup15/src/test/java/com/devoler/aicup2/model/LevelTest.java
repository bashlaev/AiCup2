package com.devoler.aicup2.model;

import static org.junit.Assert.*;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

public class LevelTest {
	@Test
	public void testInvalidLevels() {
		try {
			new Level(Levels.concatWithLineFeed(Levels.ERR_DUPLICATE_TARGET));
			fail("Level with duplicate target should not be instantiated");
		} catch(Exception e) {
			// expected
		}
		try {
			new Level(Levels.concatWithLineFeed(Levels.ERR_NO_ROBOT));
			fail("Level with no robot should not be instantiated");
		} catch(Exception e) {
			// expected
		}
		try {
			new Level(Levels.concatWithLineFeed(Levels.ERR_INVALID_CHARS));
			fail("Level with invalid chars should not be instantiated");
		} catch(Exception e) {
			// expected
		}
		try {
			new Level(Levels.concatWithLineFeed(Levels.ERR_ROW_DISREPANCY));
			fail("Level with row size disrepancy should not be instantiated");
		} catch(Exception e) {
			// expected
		}
	}

	@Test
	public void testCorrectLevel() {
		Level l1 = new Level(Levels.concatWithLineFeed(Levels.L1));
		assertEquals(Pair.of(0, 1), l1.getTargetPosition());
		assertEquals(Pair.of(2, 4), l1.getInitialRobotPosition());
		assertEquals(Pair.of(3, 5), l1.getFieldSize());
		assertTrue(l1.isCellPlayable(0, 0));
		assertTrue(l1.isCellPlayable(0, 1));
		assertFalse(l1.isCellPlayable(1, 1));
		assertFalse(l1.isCellPlayable(-1, 0));
		assertFalse(l1.isCellPlayable(0, 6));
		assertFalse(l1.isCellPlayable(5, 3));
	}

}
