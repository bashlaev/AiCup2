package com.devoler.aicup2.model;

import static org.junit.Assert.*;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

public class RaceTrackTest {
	private static final String TRACK_VALID = "2 RRRUUUUULLLLLDDDDDRR";
	
	private static final String TRACK_INVALID_WIDTH = "50 RRRUUUUULLLLLDDDDDRR";

	private static final String TRACK_NO_FULL_CIRCLE = "2 RRRUUUUULLLLLDDDDDR";
	
	private static final String TRACK_SELF_CROSSING = "2 RRRUUUUULLLURDLLLDDDDDRR";

	@Test
	public void testValidation() {
		RaceTrack.parse(TRACK_VALID);
		expectException(TRACK_INVALID_WIDTH);
		expectException(TRACK_NO_FULL_CIRCLE);
		expectException(TRACK_SELF_CROSSING);
	}
	
	private void expectException(String track) {
		try {
			RaceTrack.parse(track);
			fail("Exception expected");
		} catch(Exception expected) {
			// ignored
		}

	}

}
