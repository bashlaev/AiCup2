package com.devoler.aicup2.model;

public enum TrackCell {
	TRACK(true, '\u2591', 5),
	NON_TRACK_OUT(false, ' ', 0),
	NON_TRACK_IN(false, ' ', 0),
	PRE_START_LINE(true, '\u2592', 3),
	POST_START_LINE(true, '\u2592', 3),
	START_LINE(true, '\u2593', 4),
	START_CELL(true, '\u2588', 4);
	
	private final boolean navigable;
	private final char character;
	private final int priority;
	
	private TrackCell(final boolean navigable, final char character, final int priority) {
		this.navigable = navigable;
		this.character = character;
		this.priority = priority;
	}
	
	public boolean isNavigable() {
		return navigable;
	}
	
	public char getCharacter() {
		return character;
	}
	
	public int getPriority() {
		return priority;
	}
}
