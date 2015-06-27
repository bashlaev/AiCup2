package com.devoler.aicup2.model;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;

public final class Intersections {
	private Intersections() {
		throw new UnsupportedOperationException();
	}

	public static interface Intersection {
	}

	public static class Vertex implements Intersection {
		private final Pair<Integer, Integer> coords;

		public Vertex(final Pair<Integer, Integer> coords) {
			this.coords = Objects.requireNonNull(coords);
		}

		public Pair<Integer, Integer> getCoords() {
			return coords;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Vertex) {
				return coords.equals(((Vertex) obj).coords);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return coords.hashCode();
		}
		
		@Override
		public String toString() {
			return "[Vertex " + coords.getLeft() + "," + coords.getRight() + "]";
		}
	}

	public static class Segment implements Intersection {
		private final Pair<Vertex, Vertex> vertices;

		public Segment(final Pair<Vertex, Vertex> vertices) {
			this.vertices = Objects.requireNonNull(vertices);
		}

		public Pair<Vertex, Vertex> getVertices() {
			return vertices;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Segment) {
				return vertices.equals(((Segment) obj).vertices);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return vertices.hashCode();
		}
		
		@Override
		public String toString() {
			return "[Segment (" + vertices.getLeft() + " - " + vertices.getRight() + ")]";
		}
	}

	public static List<Intersection> getIntersections(final Pair<Integer, Integer> segmentStart, final Pair<Integer, Integer> segmentEnd) {
		List<Intersection> intersections = new ArrayList<>();
		int minX = Math.min(segmentStart.getLeft(), segmentEnd.getLeft());
		int maxX = Math.max(segmentStart.getLeft(), segmentEnd.getLeft());
		int minY = Math.min(segmentStart.getRight(), segmentEnd.getRight());
		int maxY = Math.max(segmentStart.getRight(), segmentEnd.getRight());
		Line2D line = new Line2D.Double(segmentStart.getLeft(), segmentStart.getRight(), segmentEnd.getLeft(), segmentEnd.getRight());
		// check vertices
		for(int x = minX; x <= maxX; x++) {
			for(int y = minY; y <= maxY; y++) {
				// check vertex
				if (isPointOnLine(line, x, y)) {
					intersections.add(new Vertex(Pair.of(x, y)));
				}
			}
		}
		
		// check horizontal segments
		for(int y = minY + 1; y < maxY; y++) {
			for(int x = minX; x < maxX; x++) {
				if (isLineOverSegment(line, x, y, x + 1, y)) {
					intersections.add(new Segment(Pair.of(new Vertex(Pair.of(x, y)), new Vertex(Pair.of(x + 1, y)))));
				}
			}
		}
		
		// check vertical segments
		for(int x = minX + 1; x < maxX; x++) {
			for(int y = minY; y < maxY; y++) {
				if (isLineOverSegment(line, x, y, x, y + 1)) {
					intersections.add(new Segment(Pair.of(new Vertex(Pair.of(x, y)), new Vertex(Pair.of(x, y + 1)))));
				}
			}
		}
		Collections.sort(intersections, new Comparator<Intersection>() {
			@Override
			public int compare(Intersection o1, Intersection o2) {
				int x = segmentStart.getLeft();
				int y = segmentStart.getRight();
				return Double.compare(distance(o1, x, y), distance(o2, x, y));
			}
		});
		return intersections;
	}
	
	private static final double distance(Intersection i, int x, int y) {
		if (i instanceof Vertex) {
			Pair<Integer, Integer> coords = ((Vertex) i).coords;
			return new Point2D.Double(x, y).distance(coords.getLeft(), coords.getRight());
		} else if (i instanceof Segment) {
			Pair<Vertex, Vertex> vertices = ((Segment) i).vertices;
			return Math.min(distance(vertices.getLeft(), x, y), distance(vertices.getRight(), x, y));
		}
		throw new IllegalArgumentException("Illegal intersection: " + i);
	}
	
	private static boolean isPointOnLine(Line2D line, int x, int y) {
		return line.ptLineDist(x, y) < 0.0001d;
	}
	
	private static boolean isLineOverSegment(Line2D line, int x1, int y1, int x2, int y2) {
		if (isPointOnLine(line, x1, y1) || isPointOnLine(line, x2, y2)) {
			return false;
		}
		return line.intersectsLine(new Line2D.Double(x1, y1, x2, y2));
	}
}
