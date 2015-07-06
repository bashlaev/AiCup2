package com.devoler.aicup2.view;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;

public final class Utils {
	private Utils() {
		throw new UnsupportedOperationException();
	}
	
	public static void centerWindow(Window window) {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		if ((gs != null) && (gs.length > 0)) {
			Rectangle screenSize = gs[0].getDefaultConfiguration().getBounds();
			int x = (int) (screenSize.getX() + (screenSize.getWidth() - window
					.getWidth()) / 2);
			int y = (int) (screenSize.getY() + (screenSize.getHeight() - window
					.getHeight()) / 2);
			window.setLocation(x, y);
		}
	}
}
