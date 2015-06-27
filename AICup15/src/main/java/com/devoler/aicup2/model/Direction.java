package com.devoler.aicup2.model;

import java.util.EnumSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

public enum Direction {
	UP {
		@Override
		public Pair<Integer, Integer> apply(Pair<Integer, Integer> position) {
			return Pair.of(position.getLeft(), position.getRight() - 1);
		}
	},
	LEFT {
		@Override
		public Pair<Integer, Integer> apply(Pair<Integer, Integer> position) {
			return Pair.of(position.getLeft() - 1, position.getRight());
		}
	},
	RIGHT {
		@Override
		public Pair<Integer, Integer> apply(Pair<Integer, Integer> position) {
			return Pair.of(position.getLeft() + 1, position.getRight());
		}
	},
	DOWN {
		@Override
		public Pair<Integer, Integer> apply(Pair<Integer, Integer> position) {
			return Pair.of(position.getLeft(), position.getRight() + 1);
		}
	};

	public static Direction parseChar(char c) {
		switch (c) {
		case 'U':
			return UP;
		case 'L':
			return LEFT;
		case 'R':
			return RIGHT;
		case 'D':
			return DOWN;
		case '0':
			return null;
		default:
			throw new IllegalArgumentException("Unsupported char: " + c);
		}
	}
	
	public Direction opposite() {
		return Direction.values()[Direction.values().length - 1 - ordinal()];
	}
	
	public Set<Direction> orthogonal() {
		return EnumSet.complementOf(EnumSet.of(this, opposite()));
	}

	public abstract Pair<Integer, Integer> apply(Pair<Integer, Integer> position);
}
