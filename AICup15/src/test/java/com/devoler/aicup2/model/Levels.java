package com.devoler.aicup2.model;

public class Levels {

	static final String[] L1 = {
		"O*OOO",
		"  O  ",
		"  OOr",
	};
	
	static final String[] L2 = {
		"O*OOO",
		"  OO ",
		"  OOr",
	};

	static final String[] ERR_ROW_DISREPANCY = {
		"O*OOO",
		"  O   ",
		"  OOr",
	};
	
	static final String[] ERR_INVALID_CHARS = {
		"O*OOO",
		"  Oo  ",
		"  OOr",
	};
	
	static final String[] ERR_NO_ROBOT = {
		"O*OOO",
		"  O   ",
		"  OOO",
	};
	
	static final String[] ERR_DUPLICATE_TARGET = {
		"O*OOO",
		"  O   ",
		" *OOr",
	};

	static String concatWithLineFeed(String... strings) {
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < strings.length; i++) {
			b.append(strings[i]);
			if (i < strings.length - 1) {
				b.append("\r\n");
			}
		}
		return b.toString();
	}

}
