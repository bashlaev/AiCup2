package com.devoler.aicup2.model;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import com.devoler.aicup2.model.Intersections.Intersection;
import com.devoler.aicup2.model.Intersections.Segment;
import com.devoler.aicup2.model.Intersections.Vertex;

public class IntersectionsTest {
	private static final Intersection vertex(int x, int y) {
		return new Vertex(Pair.of(x, y));
	}

	private static final Intersection segment(int x1, int y1, int x2, int y2) {
		return new Segment(Pair.of((Vertex) vertex(x1, y1),
				(Vertex) vertex(x2, y2)));
	}
	
	private static final int[][] SEGMENTS = { { 0, 0, 1, 2 }, {0, 0, 2, 4}, {0, 0, 3, 3}, {4, 3, 0, 0}, {-4, 3, 0, 0}, {-5, -2, -5, -4} };
	private static final List<List<Intersection>> EXPECTED = Arrays.asList(
			Arrays.asList(vertex(0, 0), segment(0, 1, 1, 1), vertex(1, 2)),
			Arrays.asList(vertex(0, 0), segment(0, 1, 1, 1), vertex(1, 2), segment(1, 3, 2, 3), vertex(2, 4)), 
			Arrays.asList(vertex(0, 0), vertex(1, 1), vertex(2, 2), vertex(3, 3)),
			Arrays.asList(vertex(4, 3), segment(3, 2, 3, 3), segment(2, 2, 3, 2), segment(2, 1, 2, 2), segment(1, 1, 2, 1), segment(1, 0, 1, 1), vertex(0, 0)), 
			Arrays.asList(vertex(-4, 3), segment(-3, 2, -3, 3), segment(-3, 2, -2, 2), segment(-2, 1, -2, 2), segment(-2, 1, -1, 1), segment(-1, 0, -1, 1), vertex(0, 0)), 
			Arrays.asList(vertex(-5, -2), vertex(-5, -3), vertex(-5, -4))
	);

	@Test
	public void test() {
		for(int i = 0; i < SEGMENTS.length; i++) {
			List<Intersection> intersections = Intersections.getIntersections(Pair.of(SEGMENTS[i][0], SEGMENTS[i][1]), Pair.of(SEGMENTS[i][2], SEGMENTS[i][3]));
			assertEquals(EXPECTED.get(i), intersections);
		}
	}

}
