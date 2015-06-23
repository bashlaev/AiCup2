package com.devoler.aicup2.model;

import static org.junit.Assert.fail;

import org.junit.Test;

public class RaceTrackTest {
	private static final String TRACK_VALID = "2 RRRRUUUUUULLLLLLLLDDDDDDRRRR";
	
	private static final String TRACK_INVALID_WIDTH = "50 RRRUUUUULLLLLDDDDDRR";

	private static final String TRACK_NO_FULL_CIRCLE = "2 RRRUUUUULLLLLDDDDDR";
	
	private static final String TRACK_SELF_CROSSING = "2 RRRUUUUULLLURDLLLDDDDDRR";

	private static final String TRACK_INVALID_START_LINE = "2 RRRUUUUUULLLLLLDDDDDDRRR";
	
	@Test
	public void testValidation() {
		expectException(TRACK_INVALID_WIDTH);
		expectException(TRACK_NO_FULL_CIRCLE);
		expectException(TRACK_SELF_CROSSING);
		expectException(TRACK_INVALID_START_LINE);
		RaceTrack valid = RaceTrack.parse(TRACK_VALID);
		System.out.println(valid.toString());
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
