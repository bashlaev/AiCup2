package com.devoler.aicup2.model;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

public class RaceTrack {
	private static final int MIN_WIDTH = 0;
	private static final int MAX_WIDTH = 20;

	/**
	 * Parses a race track from a formatted string.
	 * 
	 * @param s
	 *            String to parse.
	 * @return a RaceTrack instance.
	 */
	public static RaceTrack parse(String s) {
		Validate.notEmpty(s, "Could not parse race track: empty string");
		String[] tokens = s.split(" ");
		Validate.isTrue(tokens.length == 2, "Could not parse race track: expected two tokens delimited by space");
		int width = Integer.parseInt(tokens[0]);
		String axisString = tokens[1];
		Direction[] axis = new Direction[axisString.length()];
		for (int i = 0; i < axisString.length(); i++) {
			axis[i] = Direction.parseChar(axisString.charAt(i));
		}
		return new RaceTrack(axis, width);
	}

	private final Direction[] axis;
	private final int width;

	private RaceTrack(final Direction[] axis, final int width) {
		validate(axis, width);
		this.axis = axis;
		this.width = width;
	}

	private void validate(final Direction[] axis, final int width) {
		// race track should be non-empty and shouldn't contain null elements
		Validate.notNull(axis, "RaceTrack axis is null");
		Validate.noNullElements(axis);

		// width should be adequate
		Validate.inclusiveBetween(MIN_WIDTH, MAX_WIDTH, width, "Illegal width: $d", width);

		// make sure the race track goes full circle
		validateFullCircle(axis);

		// make sure the race track doesn't cross itself
		validateNoSelfCrossing(axis);

		// make sure the race track has an internal area
		// validateHasInternalArea(axis, width);
	}

	private void validateFullCircle(final Direction[] axis) {
		final Pair<Integer, Integer> start = Pair.of(0, 0);
		Pair<Integer, Integer> pos = start;
		for (Direction direction : axis) {
			pos = direction.apply(pos);
		}
		Validate.isTrue(pos.equals(start), "The race track doesn't go full circle");
	}

	private void validateNoSelfCrossing(final Direction[] axis) {
		final Set<Pair<Integer, Integer>> visited = new HashSet<>();
		final Pair<Integer, Integer> start = Pair.of(0, 0);
		Pair<Integer, Integer> pos = start;
		for (Direction direction : axis) {
			pos = direction.apply(pos);
			Validate.isTrue(visited.add(pos), "The track crosses itself at %1", pos);
		}
	}
}
