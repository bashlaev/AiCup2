package com.devoler.aicup2.view;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.concurrent.TimeUnit;

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
	
	public static void fullScreenWindow(Window window) {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		if ((gs != null) && (gs.length > 0)) {
			Rectangle screenSize = gs[0].getDefaultConfiguration().getBounds();
			int w = Math.min(window.getWidth(), screenSize.width);
			int h = Math.min(window.getHeight(), screenSize.height);
			int x = screenSize.x + (screenSize.width - w) / 2;
			int y = screenSize.y + (screenSize.height - h) / 2;
			window.setLocation(x, y);
			window.setSize(new Dimension(w, h));
		}
	}

	public static String timeToLapTime(long millis) {
		String time = "";
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		if (minutes > 0) {
			time += minutes + ":";
			millis -= TimeUnit.MINUTES.toMillis(minutes);
		}
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
		time += String.format("%02d", seconds) + ".";
		millis -= TimeUnit.SECONDS.toMillis(seconds);
		time += String.format("%03d", millis);
		return time;
	}

}
