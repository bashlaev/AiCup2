package com.devoler.aicup2.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.devoler.aicup2.model.RaceTrack;
import com.devoler.aicup2.model.RaceTrackParser;

@SuppressWarnings("serial")
public final class RaceTrackPanel extends JPanel {
	private static final int SIZE = 600;

	private final RaceTrack raceTrack;
	private final BufferedImage trackImage;
	private final Image trackImageResized;

	public RaceTrackPanel(final String raceTrackString) {
		super();
		raceTrack = RaceTrackParser.parse(raceTrackString);
		trackImage = RaceTrackRenderer.renderTrack(raceTrack);
		trackImageResized = resize(trackImage, SIZE);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}
	
	public RaceTrack getRaceTrack() {
		return raceTrack;
	}

	private Image resize(BufferedImage source, int size) {
		if ((source.getWidth() <= size) && (source.getHeight() <= size)) {
			return source;
		}
		int scaleWidth = SIZE;
		int scaleHeight = SIZE;
		if (source.getWidth() > source.getHeight()) {
			scaleHeight = -1;
		} else {
			scaleWidth = -1;
		}
		Image image = trackImage.getScaledInstance(scaleWidth, scaleHeight,
				Image.SCALE_SMOOTH);
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(image, 1);
		try {
			mt.waitForAll();
		} catch (InterruptedException ignored) {
		}
		return image;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(trackImageResized,
				(getWidth() - trackImageResized.getWidth(null)) / 2,
				(getHeight() - trackImageResized.getHeight(null)) / 2, null);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(SIZE, SIZE);
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
}
