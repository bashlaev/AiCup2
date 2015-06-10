package com.devoler.aicup2.model;

import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

/**
 * A level is defined by: (1) a playing field, (2) position of the target and
 * (3)position of the robot. A playing field is a rectangle of cells that the
 * robot can navigate. Each cell is either playable (can be occupied by the
 * robot) or not.
 * 
 * The target's and the robot's positions must be playable.
 * 
 * @author homer
 */
public final class Level {
	private static final Pattern LEVEL_PATTERN = Pattern
			.compile("^(?![^r]*?r[^r]*?r)(?![^\\*]*?\\*[^\\*]*?\\*)[ Or\\*\n\r]{2,}$");
	private static final String LINE_DELIMITERS = "\r\n";

	private final boolean[][] field;
	private final Pair<Integer, Integer> target;
	private final Pair<Integer, Integer> robot;

	/**
	 * Constructs a new level from a formatted string.
	 * 
	 * @param levelString
	 *            A string that defines a level. This string represents a
	 *            playing field as a sequence of characters, where linefeed
	 *            chars are used to delimit rows. The chars used to denote cells
	 *            are: 'Space' for empty (non-playable), 'O' for playable cell,
	 *            '*' for the target (this cell is automatically playable), 'r'
	 *            for the robot (this cell is automatically playable).
	 */
	public Level(String levelString) {
		// match the pattern to validate that (1) only allowed chars are present
		// in a string, (2) there is exactly one target and (3) there is exactly
		// one robot
		Validate.isTrue(LEVEL_PATTERN.matcher(levelString).matches(), "Level does not match pattern");

		// split into rows
		String[] rows = levelString.split(LINE_DELIMITERS);
		int colCount = rows[0].length();
		for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
			String row = rows[rowIndex];
			// validate that all rows have the same length
			Validate.isTrue(row.length() == colCount, "Row length disrepancy, row #%d should have %d cells", rowIndex,
					colCount);
		}
		field = new boolean[rows.length][colCount];
		Pair<Integer, Integer> target = null;
		Pair<Integer, Integer> robot = null;
		for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
			for (int colIndex = 0; colIndex < colCount; colIndex++) {
				char c = rows[rowIndex].charAt(colIndex);
				field[rowIndex][colIndex] = true;
				switch (c) {
				case ' ':
					field[rowIndex][colIndex] = false;
					break;
				case '*':
					target = Pair.of(rowIndex, colIndex);
					break;
				case 'r':
					robot = Pair.of(rowIndex, colIndex);
					break;
				case 'O':
					// do nothing
					break;
				default:
					throw new RuntimeException("Unexpected character: " + c);
				}
			}
		}
		Validate.notNull(target);
		Validate.notNull(robot);
		this.target = target;
		this.robot = robot;
	}

	// (row, column)
	public Pair<Integer, Integer> getTargetPosition() {
		return target;
	}

	// (row, column)
	public Pair<Integer, Integer> getInitialRobotPosition() {
		return robot;
	}

	// (rows, columns)
	public Pair<Integer, Integer> getFieldSize() {
		return Pair.of(field.length, field[0].length);
	}

	public boolean isCellPlayable(int row, int column) {
		return (row >= 0) && (row < field.length) && (column >= 0) && (column < field[row].length)
				&& field[row][column];
	}

}
