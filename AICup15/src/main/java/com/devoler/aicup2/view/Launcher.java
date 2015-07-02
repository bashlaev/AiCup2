package com.devoler.aicup2.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.devoler.aicup2.model.RaceTrackParser;

public final class Launcher {
	private static final String TRACK = "2 RRRRUUUUUULLLLLLLLDDDDDDRRRR";

	public static void main(String[] args) {
		final BufferedImage image = RaceTrackRenderer.renderTrack(RaceTrackParser.parse(TRACK));
		@SuppressWarnings("serial")
		JPanel imagePanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(image, 0, 0, null);
			}
			
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(image.getWidth(), image.getHeight());
			}
		};
		JFrame frame = new JFrame("Launcher");
		frame.getContentPane().add(imagePanel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		centerWindow(frame);
		frame.setVisible(true);
	}
	
	private static void centerWindow(Window window) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		window.setLocation(screenWidth / 2 - window.getWidth() / 2, screenHeight / 2 - window.getHeight() / 2);
	}


}
