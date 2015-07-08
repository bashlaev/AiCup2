package com.devoler.aicup2.model;

import org.apache.commons.lang3.tuple.Pair;

public class RaceTrack {
	private final TrackCell[][] track;

	RaceTrack(final TrackCell[][] track) {
		this.track = track;
	}
	
	public int getWidth() {
		return track.length;
	}

	public int getHeight() {
		return track[0].length;
	}

	public Pair<Integer, Integer> getStartCell() {
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

	public boolean isNavigable(Pair<Integer, Integer> coords) {
		return isNavigable(coords.getLeft(), coords.getRight());
	}
	
	public boolean isNavigable(int x, int y) {
		return y >= 0 && x >= 0 && x < track.length && y < track[0].length
				&& track[x][y] != null && track[x][y].isNavigable();
	}

	public TrackCell safeCellAt(Pair<Integer, Integer> coords) {
		int x = coords.getLeft();
		int y = coords.getRight();
		return safeCellAt(x, y);
	}

	public TrackCell safeCellAt(int x, int y) {
		if (y >= 0 && x >= 0 && x < track.length && y < track[0].length) {
			return track[x][y];
		}
		return null;
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
