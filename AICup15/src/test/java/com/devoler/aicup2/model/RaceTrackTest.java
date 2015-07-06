package com.devoler.aicup2.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.devoler.aicup2.model.RaceResult.Status;

public class RaceTrackTest {
	private static final String[] VALID_TRACKS = {
			"1 LLLUUUURRRRRRDDDDLLL",
			"3 RRRRRRRRRRRRRRRRRRUUUUUUUUUUUUUUUUULLLLLLLLLLLLLLLLLLLLLLLLLLLDDDDDDDDDDDDDDDDDRRRRRRRRR",
			"1 DDDDDDDDDDDLDLDLDLDLDLLDLDLDLDLLUUUULUUUUUUUUULUUUUUUUUULUUUUURRRDRRRRDRRRRRRDRRDDDD",
			"0 LLLLULLULLULLULLULLULLURRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRDDLLDLLDLLDLLDLLDLLLLL",
			"3 UUUUUUUUULUUUUUUUUUUUUUUUUUULUUUUUUUUUUULLULLLULLLLDDDDDDDDDDDDDDLLLLDDDLLLLLLDLDDLDLDDLDDLDLDDLDDLDLDDLDDLDLDDLDDLDLDDLDRDRRDRDRRDRRDRDRRDRRDRDRRDRDLDDDLDDDLDDDLDDDLDDDLDDDLDDDLDDDDRDDDDDDRDDDDDDRDDDDDDRDDDRRUURURUUUUUUUURRRRRUUUUUUUUUUUUUUUUUURUUUUUUUURUUUUURUUUURRRRRRRRDRDDRDDRDDDRDDRDDRDDRDDRDDDRDDRDDRDRRURRRRURRRRURRRURRRRURRUUUURUUUUUUURUUUUUUUURUUUULULULLULULLULULLDLDLLDLLDLDLLLLLLLLLLLULULUUUUUUUU",
			"1 DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDLDDDLDDDDLDDLDLDLDLDLDLDLDLDLDLDLLDLLDLLLLDLLLLLLDLLLLLLLLLLULLLLLLLLLLLLLLLULLLLLLLLULULULLULULLULULULLULULULUULUUUULUUUUULUULULULULDDLDDDLDDDDLDDDRDRDDRDRDRDRDRDRDRDRDRRDRDRDRDRDRDRRRDRRDRDRRDRRRDRRRDRRRRDRRRDRRRRDRRRDRRRDRRDRDDDRDDDLDDDLDLDLLLDLLLDLLLLLLLLULLLLLLLULUUULUULUULUUULULULULULULULLULULULULULLULULULULLULULULULULLULULULULULUUULUUUUULUUUUULUUUUURUUUURUUUURUURURRURRURRDDDLDDLDLDLDDDRRRRRUURRRRRRRRRDRRRRRRRDRRRRRRRDRRRRRRRDRRRRRRRDRRRRRRRDRRRRURRURRURUURUUURUUULUULUUULULULULULULULULLULLLULLLLULLLULLLULLLLLLLLULLLLLLLLULLLLLULLLLLULLLULULULULULULULULUULULUULUUUULUUUUULUUUUUUUUULUUUUUUUUUUUURUUUUUUUUUURUUUUUURUURUURUURUURUURUURURURURURURURURURRURRRURRURRURRRRRRRRURRRRRRRRRRRDRRRRRRRRDRRRRRRRRRDRRRRRRRRRDRRRRRRRDRRRRRRDRRRRRRDRRRRRRDRRRRRRDRRRRRRDRRRRRRRDRRRRRRRDRRRDLLLDLDLLLLLLLLLLLLDLLLLDLLDLLDLDLDLDLDDDDDLDDDDDDDDDLDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD" };

	private static final String TRACK_1 = "2 RRRRUUUUUULLLLLLLLDDDDDDRRRR";

	private static final String[] SOLUTIONS_1_BAD_FORMAT = { "RFUL", " U",
			"ULd" };

	private static final String[] SOLUTIONS_1_DID_NOT_FINISH = { "", "R",
			"RRR", "RLRLRLRLUDUDUDUDUDUDLRLRLRLRLRLRLRLRDUDUDUDUDUDURLRLRL" };
	private static final int[] SOLUTIONS_1_DID_NOT_FINISH_TIMES = { 0, 1, 3, 54 };

	private static final String[] SOLUTIONS_1_ILLEGAL_MOVE = { "RRRLUU", "L",
			"UL", "U00" };
	private static final int[] SOLUTIONS_1_ILLEGAL_MOVE_TIMES = { 4, 1, 2, 3 };

	private static final String[] SOLUTIONS_1_SUCCESS = {
			"RLRLRLRLUDUDUDUDUDUDLRLRLRLRLRLRLRLRDUDUDUDUDUDURLRLRLR",
			"RLRLRLRLUDUDUDUDUDUDLRLRLRLRLRLRLRLRDUDUDUDUDUDURLRR",
			"RLRLRLRLUDUDUDUDUDUDLRLRLRLRLRLRLRLRDUDUDUDUDUDURLRUR" };
	private static final double[] SOLUTIONS_1_SUCCESS_TIMES = { 55, 52, 52.5 };

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
		RaceTrackParser.parse(TRACK_1);
	}

	@Test
	public void testValidTracks() {
		for(String track: VALID_TRACKS) {
			RaceTrackParser.parse(track);
		}
	}

	@Test
	public void testSolutionBadFormat() {
		RaceTrack track = RaceTrackParser.parse(TRACK_1);
		for (String badFormatSolution : SOLUTIONS_1_BAD_FORMAT) {
			assertParseError(SolutionChecker.checkSolution(track,
					badFormatSolution));
		}
	}

	@Test
	public void testSolutionSuccess() {
		RaceTrack track = RaceTrackParser.parse(TRACK_1);
		for (int i = 0; i < SOLUTIONS_1_SUCCESS.length; i++) {
			assertSuccess(SolutionChecker.checkSolution(track,
					SOLUTIONS_1_SUCCESS[i]), SOLUTIONS_1_SUCCESS_TIMES[i]);
		}
	}

	@Test
	public void testSolutionDidNotFinish() {
		RaceTrack track = RaceTrackParser.parse(TRACK_1);
		for (int i = 0; i < SOLUTIONS_1_DID_NOT_FINISH.length; i++) {
			assertDidNotFinish(SolutionChecker.checkSolution(track,
					SOLUTIONS_1_DID_NOT_FINISH[i]),
					SOLUTIONS_1_DID_NOT_FINISH_TIMES[i]);
		}
	}

	@Test
	public void testSolutionIllegalMove() {
		RaceTrack track = RaceTrackParser.parse(TRACK_1);
		for (int i = 0; i < SOLUTIONS_1_ILLEGAL_MOVE.length; i++) {
			assertIllegalMove(SolutionChecker.checkSolution(track,
					SOLUTIONS_1_ILLEGAL_MOVE[i]),
					SOLUTIONS_1_ILLEGAL_MOVE_TIMES[i]);
		}
	}

	private void assertParseError(RaceResult result) {
		assertEquals(Status.COULD_NOT_PARSE_SOLUTION, result.getStatus());
		assertEquals(0, result.getTime().intValue());
	}

	private void assertSuccess(RaceResult result, Number expectedTime) {
		assertEquals(Status.SUCCESS, result.getStatus());
		assertTrue(
				"actual: " + result.getTime(),
				Math.abs(expectedTime.doubleValue()
						- result.getTime().doubleValue()) < 0.01d);
	}

	private void assertDidNotFinish(RaceResult result, int expectedTime) {
		assertEquals(Status.DID_NOT_FINISH, result.getStatus());
		assertEquals(expectedTime, result.getTime().intValue());
	}

	private void assertIllegalMove(RaceResult result, int expectedTime) {
		assertEquals(Status.ILLEGAL_MOVE, result.getStatus());
		assertEquals(expectedTime, result.getTime().intValue());
	}

	private void expectException(String track) {
		try {
			RaceTrackParser.parse(track);
			fail("Exception expected");
		} catch (Exception expected) {
			// ignored
		}

	}

}
