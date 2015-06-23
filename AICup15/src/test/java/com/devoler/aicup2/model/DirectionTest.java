package com.devoler.aicup2.model;

import static org.junit.Assert.assertEquals;

import java.util.EnumSet;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import com.devoler.aicup2.model.Direction;

public class DirectionTest {
	@Test
	public void testApply() {
		Pair<Integer, Integer> pos = Pair.of(0, 0);
		assertEquals(Pair.of(1, 0), Direction.DOWN.apply(pos));
		assertEquals(Pair.of(-1, 0), Direction.UP.apply(pos));
		assertEquals(Pair.of(0, -1), Direction.LEFT.apply(pos));
		assertEquals(Pair.of(0, 1), Direction.RIGHT.apply(pos));
	}

	@Test
	public void testOpposite() {
		assertEquals(Direction.LEFT, Direction.RIGHT.opposite());
		assertEquals(Direction.RIGHT, Direction.LEFT.opposite());
		assertEquals(Direction.UP, Direction.DOWN.opposite());
		assertEquals(Direction.DOWN, Direction.UP.opposite());
	}
	
	@Test
	public void testOrthogonal() {
		assertEquals(EnumSet.of(Direction.LEFT, Direction.RIGHT), Direction.UP.orthogonal());
		assertEquals(EnumSet.of(Direction.LEFT, Direction.RIGHT), Direction.DOWN.orthogonal());
		assertEquals(EnumSet.of(Direction.UP, Direction.DOWN), Direction.LEFT.orthogonal());
		assertEquals(EnumSet.of(Direction.UP, Direction.DOWN), Direction.RIGHT.orthogonal());
	}
}
