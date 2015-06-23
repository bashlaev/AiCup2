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
		Validate.isTrue(tokens.length == 2,
				"Could not parse race track: expected two tokens delimited by space");
		int width = Integer.parseInt(tokens[0]);
		String axisString = tokens[1];
		Direction[] axis = new Direction[axisString.length()];
		for (int i = 0; i < axisString.length(); i++) {
			axis[i] = Direction.parseChar(axisString.charAt(i));
		}
		validate(axis, width);
		return new RaceTrack(transform(axis, width));
	}

	private static Pair<TrackCell[][], Pair<Integer, Integer>> formAxis(
			Direction[] axis, int width) {
		final Set<Pair<Integer, Integer>> axisCoords = new HashSet<>();
		final Pair<Integer, Integer> start = Pair.of(0, 0);
		Pair<Integer, Integer> pos = start;
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (Direction direction : axis) {
			int x = pos.getLeft();
			int y = pos.getRight();
			if (x < minX) {
				minX = x;
			}
			if (x > maxX) {
				maxX = x;
			}
			if (y < minY) {
				minY = y;
			}
			if (y > maxY) {
				maxY = y;
			}
			axisCoords.add(pos);
			pos = direction.apply(pos);
		}
		minX -= width;
		minY -= width;
		maxX += width;
		maxY += width;
		TrackCell[][] cells = new TrackCell[maxX - minX + 1][maxY - minY + 1];
		for (Pair<Integer, Integer> axisCoord : axisCoords) {
			int x = axisCoord.getLeft();
			int y = axisCoord.getRight();
			cells[x - minX][y - minY] = TrackCell.TRACK;
		}
		return Pair.of(cells, Pair.of(-minX, -minY));
	}

	private static TrackCell[][] growLayer(TrackCell[][] source) {
		TrackCell[][] target = new TrackCell[source.length][source[0].length];
		for (int col = 0; col < source.length; col++) {
			for (int row = 0; row < source[col].length; row++) {
				TrackCell cell = source[col][row];
				if (cell == TrackCell.TRACK) {
					// fill in this cell and all of its neighbours
					target[col][row] = TrackCell.TRACK;
					target[col - 1][row] = TrackCell.TRACK;
					target[col + 1][row] = TrackCell.TRACK;
					target[col][row - 1] = TrackCell.TRACK;
					target[col][row + 1] = TrackCell.TRACK;
				}
			}
		}
		return target;
	}

	private static boolean convertToOuterNonTrack(TrackCell[][] source,
			int row, int col) {
		if (source[col][row] != null) {
			return false;
		}
		if (row == 0 || col == 0 || col == source.length - 1
				|| row == source[0].length - 1) {
			return true;
		}
		return (source[col - 1][row] == TrackCell.NON_TRACK_OUT)
				|| (source[col + 1][row] == TrackCell.NON_TRACK_OUT)
				|| (source[col][row - 1] == TrackCell.NON_TRACK_OUT)
				|| (source[col][row + 1] == TrackCell.NON_TRACK_OUT);
	}

	private static void fillOuterNonTrack(TrackCell[][] source) {
		while (true) {
			int converted = 0;
			for (int col = 0; col < source.length; col++) {
				for (int row = 0; row < source[col].length; row++) {
					if (convertToOuterNonTrack(source, row, col)) {
						source[col][row] = TrackCell.NON_TRACK_OUT;
						converted++;
					}
				}
			}
			if (converted == 0) {
				break;
			}
		}
	}

	private static int fillInnerNonTrack(TrackCell[][] source) {
		int counter = 0;
		for (int col = 0; col < source.length; col++) {
			for (int row = 0; row < source[col].length; row++) {
				if (source[col][row] == null) {
					source[col][row] = TrackCell.NON_TRACK_IN;
					counter++;
				}
			}
		}
		return counter;
	}

	private static boolean isNavigable(TrackCell[][] source, int col, int row) {
		return row >= 0 && col >= 0 && col < source.length
				&& row < source[0].length && source[col][row] != null
				&& source[col][row].isNavigable();
	}
	
	private static void drawOrthogonalLine(TrackCell[][] source, Pair<Integer, Integer> startSpot, Direction direction, int width, TrackCell type) {
		for (Direction ort : direction.orthogonal()) {
			Pair<Integer, Integer> pos = startSpot;
			for (int i = 0; i < width; i++) {
				pos = ort.apply(pos);
				source[pos.getLeft()][pos.getRight()] = type;
			}
			pos = ort.apply(pos);
			Validate.isTrue(
					!isNavigable(source, pos.getLeft(), pos.getRight()),
					"Start/finish line should be straight");
		}
	}

	private static void fillStartLine(TrackCell[][] source,
			Pair<Integer, Integer> startSpot, Direction direction, int width) {
		source[startSpot.getLeft()][startSpot.getRight()] = TrackCell.START_CELL;
		drawOrthogonalLine(source, startSpot, direction, width, TrackCell.START_LINE);
		Pair<Integer, Integer> postStartSpot = direction.apply(startSpot);
		source[postStartSpot.getLeft()][postStartSpot.getRight()] = TrackCell.POST_START_LINE;
		drawOrthogonalLine(source, postStartSpot, direction, width, TrackCell.POST_START_LINE);
		Pair<Integer, Integer> preStartSpot = direction.opposite().apply(startSpot);
		source[preStartSpot.getLeft()][preStartSpot.getRight()] = TrackCell.PRE_START_LINE;
		drawOrthogonalLine(source, preStartSpot, direction, width, TrackCell.PRE_START_LINE);
	}

	private static TrackCell[][] transform(Direction[] axis, int width) {
		Pair<TrackCell[][], Pair<Integer, Integer>> trackAxis = formAxis(axis,
				width);
		TrackCell[][] cells = trackAxis.getLeft();
		for (int i = 0; i < width; i++) {
			cells = growLayer(cells);
		}
		fillOuterNonTrack(cells);
		int counter = fillInnerNonTrack(cells);
		Validate.isTrue(counter > 0, "Race track should have an inner area");
		fillStartLine(cells, trackAxis.getRight(), axis[0], width);
		return cells;
	}

	private final TrackCell[][] track;

	private RaceTrack(final TrackCell[][] track) {
		this.track = track;
	}

	private static void validate(final Direction[] axis, final int width) {
		// race track should be non-empty and shouldn't contain null elements
		Validate.notNull(axis, "RaceTrack axis is null");
		Validate.noNullElements(axis);

		// width should be adequate
		Validate.inclusiveBetween(MIN_WIDTH, MAX_WIDTH, width,
				"Illegal width: $d", width);

		// make sure the race track goes full circle
		validateFullCircle(axis);

		// make sure the race track has a proper start/finish line
		validateProperStartLine(axis);

		// make sure the race track doesn't cross itself
		validateNoSelfCrossing(axis);
	}

	private static void validateFullCircle(final Direction[] axis) {
		final Pair<Integer, Integer> start = Pair.of(0, 0);
		Pair<Integer, Integer> pos = start;
		for (Direction direction : axis) {
			pos = direction.apply(pos);
		}
		Validate.isTrue(pos.equals(start),
				"The race track doesn't go full circle");
	}

	private static void validateProperStartLine(final Direction[] axis) {
		Validate.isTrue((axis[0] == axis[1])
				&& (axis[0] == axis[axis.length - 1]),
				"The race track doesn't have a proper start/finish line");
	}

	private static void validateNoSelfCrossing(final Direction[] axis) {
		final Set<Pair<Integer, Integer>> visited = new HashSet<>();
		final Pair<Integer, Integer> start = Pair.of(0, 0);
		Pair<Integer, Integer> pos = start;
		for (Direction direction : axis) {
			pos = direction.apply(pos);
			Validate.isTrue(visited.add(pos), "The track crosses itself at %1",
					pos);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int col = 0; col < track.length; col++) {
			for (int row = 0; row < track[col].length; row++) {
				b.append(track[col][row].getCharacter());
			}
			b.append("\r\n");
		}
		return b.toString();
	}
}
