package com.devoler.aicup2.model;

import org.apache.commons.lang3.tuple.Pair;

public enum Direction {
	UP {
		@Override
		public Pair<Integer, Integer> apply(Pair<Integer, Integer> position) {
			return Pair.of(position.getLeft() - 1, position.getRight());
		}
	},
	LEFT {
		@Override
		public Pair<Integer, Integer> apply(Pair<Integer, Integer> position) {
			return Pair.of(position.getLeft(), position.getRight() - 1);
		}
	},
	RIGHT {
		@Override
		public Pair<Integer, Integer> apply(Pair<Integer, Integer> position) {
			return Pair.of(position.getLeft(), position.getRight() + 1);
		}
	},
	DOWN {
		@Override
		public Pair<Integer, Integer> apply(Pair<Integer, Integer> position) {
			return Pair.of(position.getLeft() + 1, position.getRight());
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
		default:
			return null;
		}
	}

	public abstract Pair<Integer, Integer> apply(Pair<Integer, Integer> position);
}
