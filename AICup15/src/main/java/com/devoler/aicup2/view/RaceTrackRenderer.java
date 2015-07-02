package com.devoler.aicup2.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.devoler.aicup2.model.RaceResult;
import com.devoler.aicup2.model.RaceResult.Status;
import com.devoler.aicup2.model.RaceTrack;
import com.devoler.aicup2.model.TrackCell;

public final class RaceTrackRenderer {
	private static final int CELL_SIZE = 20;

	private RaceTrackRenderer() {
		throw new UnsupportedOperationException();
	}

	public static BufferedImage renderTrack(RaceTrack track) {
		BufferedImage image = new BufferedImage(track.getWidth() * CELL_SIZE,
				track.getHeight() * CELL_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		// render lines
		g.setColor(new Color(0x333390));
		for (int x = 0; x < track.getWidth(); x++) {
			g.fillRect(x * CELL_SIZE + CELL_SIZE / 2 - 1, 0, 3,
					track.getHeight() * CELL_SIZE);
		}
		for (int y = 0; y < track.getHeight(); y++) {
			g.fillRect(0, y * CELL_SIZE + CELL_SIZE / 2 - 1, track.getWidth()
					* CELL_SIZE, 3);
		}

		for (int x = 0; x < track.getWidth(); x++) {
			for (int y = 0; y < track.getHeight(); y++) {
				renderCell(g, track.cellAt(x, y), x * CELL_SIZE, y * CELL_SIZE);
			}
		}
		return image;
	}

	private static void renderCell(Graphics2D g, TrackCell cell, int x, int y) {
		if (cell == null) {
			throw new NullPointerException();
		}
		g.setClip(x, y, CELL_SIZE, CELL_SIZE);
		switch (cell) {
		case TRACK:
		case POST_START_LINE:
		case PRE_START_LINE:
			renderCellTrack(g, x, y);
			break;
		case NON_TRACK_IN:
		case NON_TRACK_OUT:
			renderCellNonTrack(g, x, y);
			break;
		case START_CELL:
		case START_LINE:
			renderCellStartLine(g, x, y);
			break;
		}
	}

	private static void renderCellTrack(Graphics2D g, int x, int y) {
		g.setColor(new Color(0xaaffffff, true));
		g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
	}

	private static void renderCellNonTrack(Graphics2D g, int x, int y) {
		g.setColor(new Color(0xaa000000, true));
		g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
	}

	private static void renderCellStartLine(Graphics2D g, int x, int y) {
		g.setColor(new Color(0xaa00ff00, true));
		g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
	}

	public static BufferedImage renderRaceResult(RaceTrack track,
			RaceResult result) {
		BufferedImage image = new BufferedImage(track.getWidth() * CELL_SIZE,
				track.getHeight() * CELL_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		List<Pair<Integer, Integer>> raceLog = result.getRaceLog();
		// render moves
		Set<Pair<Integer, Integer>> renderedEndpoints = new HashSet<>();
		for (int i = 0; i < raceLog.size() - 1; i++) {
			Pair<Integer, Integer> startPoint = raceLog.get(i);
			if (!renderedEndpoints.contains(startPoint)) {
				renderEndPoint(g, startPoint.getLeft() * CELL_SIZE,
						startPoint.getRight() * CELL_SIZE);
			}
			Pair<Integer, Integer> endPoint = raceLog.get(i + 1);
			if (!renderedEndpoints.contains(endPoint)) {
				renderEndPoint(g, endPoint.getLeft() * CELL_SIZE,
						endPoint.getRight() * CELL_SIZE);
			}
			boolean successfulMove = (result.getStatus() != Status.ILLEGAL_MOVE)
					|| (i < raceLog.size() - 2);
			g.setColor(new Color(successfulMove ? 0xff00ff00 : 0xffff0000, true));
			g.drawLine(startPoint.getLeft() * CELL_SIZE + CELL_SIZE / 2,
					startPoint.getRight() * CELL_SIZE + CELL_SIZE / 2,
					endPoint.getLeft() * CELL_SIZE + CELL_SIZE / 2,
					endPoint.getRight() * CELL_SIZE + CELL_SIZE / 2);
		}
		return image;
	}

	private static void renderEndPoint(Graphics2D g, int x, int y) {
		g.setColor(new Color(0xff00ff00, true));
		g.fillOval(x + CELL_SIZE / 2 - 3, y + CELL_SIZE / 2 - 3, 6, 6);
	}

}
