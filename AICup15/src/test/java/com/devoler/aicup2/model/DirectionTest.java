package com.devoler.aicup2.model;

import static org.junit.Assert.*;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

public class DirectionTest {
	@Test
	public void testDirections() {
		Pair<Integer, Integer> pos = Pair.of(0, 0);
		assertEquals(Pair.of(1, 0), Direction.DOWN.apply(pos));
		assertEquals(Pair.of(-1, 0), Direction.UP.apply(pos));
		assertEquals(Pair.of(0, -1), Direction.LEFT.apply(pos));
		assertEquals(Pair.of(0, 1), Direction.RIGHT.apply(pos));
	}

}
