package com.devoler.aicup2.model;

public enum TrackCell {
	TRACK(true, '\u2591'),
	NON_TRACK_OUT(false, ' '),
	NON_TRACK_IN(false, ' '),
	PRE_START_LINE(true, '\u2592'),
	POST_START_LINE(true, '\u2592'),
	START_LINE(true, '\u2593'),
	START_CELL(true, '\u2588');
	
	private final boolean navigable;
	private final char character;
	
	private TrackCell(final boolean navigable, final char character) {
		this.navigable = navigable;
		this.character = character;
	}
	
	public boolean isNavigable() {
		return navigable;
	}
	
	public char getCharacter() {
		return character;
	}
}
