package com.devoler.aicup2.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.devoler.aicup2.model.Intersections.Intersection;
import com.devoler.aicup2.model.Intersections.Segment;
import com.devoler.aicup2.model.Intersections.Vertex;

public class RaceTrack {
	private static final Set<TrackCell> START_CELLS = EnumSet.of(
			TrackCell.START_CELL, TrackCell.START_LINE);

	private final TrackCell[][] track;

	RaceTrack(final TrackCell[][] track) {
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
			log.add(newPos);
			if (result.getLeft()) {
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
		for(int i = 1; i < intersectionTypes.size(); i++) {
			TrackCell prev = intersectionTypes.get(i - 1);
			// illegal sequences are: 
			if (START_CELLS.contains(intersectionTypes.get(i))) {
				// 1. anything but pre-start/start/start cell -> start/start cell
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
			} else if (intersectionTypes.get(i) == TrackCell.PRE_START_LINE) {
				// 2. anything but track/pre-start -> pre-start
				if ((prev != TrackCell.PRE_START_LINE) && (prev != TrackCell.TRACK)) {
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
