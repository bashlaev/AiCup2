package com.devoler.aicup2.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import com.devoler.aicup2.model.Intersections.Intersection;
import com.devoler.aicup2.model.Intersections.Segment;
import com.devoler.aicup2.model.Intersections.Vertex;

public class RaceTrack {
	private static final int MIN_WIDTH = 0;
	private static final int MAX_WIDTH = 20;
	
	private static final Set<TrackCell> START_CELLS = EnumSet.of(
			TrackCell.START_CELL, TrackCell.START_LINE);
	
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
		for (int x = 0; x < source.length; x++) {
			for (int y = 0; y < source[x].length; y++) {
				TrackCell cell = source[x][y];
				if (cell == TrackCell.TRACK) {
					// fill in this cell and all of its neighbours
					target[x][y] = TrackCell.TRACK;
					target[x - 1][y] = TrackCell.TRACK;
					target[x + 1][y] = TrackCell.TRACK;
					target[x][y - 1] = TrackCell.TRACK;
					target[x][y + 1] = TrackCell.TRACK;
				}
			}
		}
		return target;
	}

	private static boolean convertToOuterNonTrack(TrackCell[][] source,
			int x, int y) {
		if (source[x][y] != null) {
			return false;
		}
		if (y == 0 || x == 0 || x == source.length - 1
				|| y == source[0].length - 1) {
			return true;
		}
		return (source[x - 1][y] == TrackCell.NON_TRACK_OUT)
				|| (source[x + 1][y] == TrackCell.NON_TRACK_OUT)
				|| (source[x][y - 1] == TrackCell.NON_TRACK_OUT)
				|| (source[x][y + 1] == TrackCell.NON_TRACK_OUT);
	}

	private static void fillOuterNonTrack(TrackCell[][] source) {
		while (true) {
			int converted = 0;
			for (int x = 0; x < source.length; x++) {
				for (int y = 0; y < source[x].length; y++) {
					if (convertToOuterNonTrack(source, x, y)) {
						source[x][y] = TrackCell.NON_TRACK_OUT;
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
		for (int x = 0; x < source.length; x++) {
			for (int y = 0; y < source[x].length; y++) {
				if (source[x][y] == null) {
					source[x][y] = TrackCell.NON_TRACK_IN;
					counter++;
				}
			}
		}
		return counter;
	}

	private static boolean isNavigable(TrackCell[][] source, int x, int y) {
		return y >= 0 && x >= 0 && x < source.length
				&& y < source[0].length && source[x][y] != null
				&& source[x][y].isNavigable();
	}

	private static boolean isOfType(TrackCell[][] source, int x, int y, TrackCell type) {
		return y >= 0 && x >= 0 && x < source.length
				&& y < source[0].length && source[x][y] != null
				&& source[x][y] == type;
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
		validateStartLine(cells);
		return cells;
	}
	
	private static void validateStartLine(TrackCell[][] cells) {
		int startCells = 0;
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				if (cells[x][y] == TrackCell.START_CELL) {
					startCells++;
					// neighbours must not be of type TRACK
					Validate.isTrue(!isOfType(cells, x - 1, y, TrackCell.TRACK));
					Validate.isTrue(!isOfType(cells, x + 1, y, TrackCell.TRACK));
					Validate.isTrue(!isOfType(cells, x, y - 1, TrackCell.TRACK));
					Validate.isTrue(!isOfType(cells, x, y + 1, TrackCell.TRACK));
				}
			}
		}
		Validate.isTrue(startCells == 1, "Multiple start spots");
	}

	private final TrackCell[][] track;

	private RaceTrack(final TrackCell[][] track) {
		this.track = track;
	}
	
	private Pair<Integer, Integer> getStartCell() {
		for (int x = 0; x < track.length; x++) {
			for (int y = 0; y < track[x].length; y++) {
				if (track[x][y] == TrackCell.START_CELL) {
					return Pair.of(x, y);
				}
			}
		}
		throw new IllegalStateException("Start cell not found");
	}
	
	public TrackCell cellAt(int x, int y) {
		return track[x][y];
	}

	public TrackCell cellAt(Pair<Integer, Integer> coords) {
		return cellAt(coords.getLeft(), coords.getRight());
	}
	
	public RaceResult checkSolution(String instructions) {
		if (instructions == null) {
			return RaceResult.couldNotParseSolution();
		}
		Direction[] directions = new Direction[instructions.length()];
		try {
			for (int i = 0; i < directions.length; i++) {
				directions[i] = Direction.parseChar(instructions.charAt(i));
			}
		} catch (Exception exc) {
			return RaceResult.couldNotParseSolution();
		}
		
		// examine solution step-by-step
		Pair<Integer, Integer> pos = getStartCell();
		Pair<Integer, Integer> speed = Pair.of(0, 0);
		List<Pair<Integer, Integer>> log = new ArrayList<>();
		int time = 0;
		log.add(pos);
		for(Direction move: directions) {
			speed = move == null ? speed: move.apply(speed);
			Pair<Integer, Integer> newPos = applySpeed(pos, speed);
			Pair<Boolean, Intersection> result = applyMove(pos, newPos); 
			if (result.getLeft()) {
				log.add(newPos);
				if (result.getRight() != null) {
					// need to calculate the last piece of time correctly
					double prop = calcProportion(pos, newPos, result.getRight());
					return RaceResult.finish(log, time + prop);
				}
				pos = newPos;
				time++;
			} else {
				return RaceResult.illegalMove(log);
			}
		}

		return RaceResult.didNotFinish(log);
	}

	private double calcProportion(Pair<Integer, Integer> from,
			Pair<Integer, Integer> to, Intersection i) {
		if (i instanceof Vertex) {
			return calcProportion(from, to, ((Vertex) i).getCoords());
		} else if (i instanceof Segment) {
			Segment segment = (Segment) i;
			return (calcProportion(from, to, segment.getVertices().getLeft()) + calcProportion(
					from, to, segment.getVertices().getRight())) / 2;
		}
		throw new IllegalArgumentException("Illegal intersection: " + i);
	}

	private double calcProportion(Pair<Integer, Integer> from, Pair<Integer, Integer> to, Pair<Integer, Integer> pivot) {
		if (to.equals(pivot)) {
			return 1.0d;
		}
		Point2D fromPoint = new Point2D.Double(from.getLeft(), from.getRight());
		Point2D toPoint = new Point2D.Double(to.getLeft(), to.getRight());
		Point2D pivotPoint = new Point2D.Double(pivot.getLeft(), pivot.getRight());
		double fromPivot = fromPoint.distance(pivotPoint);
		double toPivot = toPoint.distance(pivotPoint);
		return fromPivot / (fromPivot + toPivot);
	}
	
	private Pair<Integer, Integer> applySpeed(Pair<Integer, Integer> pos, Pair<Integer, Integer> speed) {
		return Pair.of(pos.getLeft() + speed.getLeft(), pos.getRight() + speed.getRight());
	}
	
	private boolean isNavigable(Pair<Integer, Integer> coords) {
		int x = coords.getLeft();
		int y = coords.getRight();
		return y >= 0 && x >= 0 && x < track.length
				&& y < track[0].length && track[x][y] != null
				&& track[x][y].isNavigable();
	}
	
	private TrackCell typeAt(Pair<Integer, Integer> coords) {
		int x = coords.getLeft();
		int y = coords.getRight();
		if (y >= 0 && x >= 0 && x < track.length
				&& y < track[0].length) {
			return track[x][y];
		}
		return null;
	}
	
	private Pair<Boolean, Intersection> applyMove(Pair<Integer, Integer> from, Pair<Integer, Integer> to) {
		List<Intersection> intersections = Intersections.getIntersections(from, to);
		List<TrackCell> intersectionTypes = new ArrayList<>();
		Intersection finish = null;
		for(Intersection i: intersections) {
			if (!isIntersectionLegal(i)) {
				return Pair.of(Boolean.FALSE, null);
			}
			intersectionTypes.add(cellTypeOfIntersection(i));
		}
		// illegal sequences are: anything but pre-start/start/start cell -> start/start cell
		for(int i = 1; i < intersectionTypes.size(); i++) {
			if (START_CELLS.contains(intersectionTypes.get(i))) {
				TrackCell prev = intersectionTypes.get(i - 1);
				if (START_CELLS.contains(prev)) {
					continue;
				} else if (prev == TrackCell.PRE_START_LINE) {
					// this is a finish
					finish = intersections.get(i);
					break;
				} else {
					// illegal move
					return Pair.of(Boolean.FALSE, null);
				}
			}
		}
		return Pair.of(Boolean.TRUE, finish);
	}
	
	private boolean isIntersectionLegal(Intersection i) {
		if (i instanceof Vertex) {
			Pair<Integer, Integer> coords = ((Vertex) i).getCoords();
			return isNavigable(coords);
		} else if (i instanceof Segment) {
			Pair<Vertex, Vertex> vertices = ((Segment) i).getVertices();
			// at least one of the vertices should be navigable
			return isNavigable(vertices.getLeft().getCoords()) || isNavigable(vertices.getRight().getCoords());
		}
		throw new IllegalArgumentException("Illegal intersection: " + i);
	}
	
	private TrackCell segmentType(TrackCell t1, TrackCell t2) {
		if (t1 == null) {
			return t2;
		}
		if (t2 == null) {
			return t1;
		}
		return t1.getPriority() > t2.getPriority() ? t1: t2;
	}
	
	private TrackCell cellTypeOfIntersection(Intersection i) {
		if (i instanceof Vertex) {
			Pair<Integer, Integer> coords = ((Vertex) i).getCoords();
			return typeAt(coords);
		} else if (i instanceof Segment) {
			Pair<Vertex, Vertex> vertices = ((Segment) i).getVertices();
			// at least one of the vertices should be navigable
			return segmentType(cellTypeOfIntersection(vertices.getLeft()), cellTypeOfIntersection(vertices.getRight()));
		}
		throw new IllegalArgumentException("Illegal intersection: " + i);
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
		for (int y = 0; y < track[0].length; y++) {
			for (int x = 0; x < track.length; x++) {
				b.append(track[x][y].getCharacter());
			}
			b.append("\r\n");
		}
		return b.toString();
	}
}
