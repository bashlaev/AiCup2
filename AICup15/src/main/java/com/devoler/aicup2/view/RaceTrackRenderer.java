package com.devoler.aicup2.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

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
		for(int x = 0; x < track.getWidth(); x++) {
			g.fillRect(x * CELL_SIZE + CELL_SIZE / 2 - 1, 0, 3, track.getHeight() * CELL_SIZE);
		}
		for(int y = 0; y < track.getHeight(); y++) {
			g.fillRect(0, y * CELL_SIZE + CELL_SIZE / 2 - 1, track.getWidth() * CELL_SIZE, 3);
		}

		for(int x = 0; x < track.getWidth(); x++) {
			for(int y = 0; y < track.getHeight(); y++) {
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
		switch(cell) {
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

}
