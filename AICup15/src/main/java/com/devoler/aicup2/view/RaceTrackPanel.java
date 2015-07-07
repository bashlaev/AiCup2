package com.devoler.aicup2.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.devoler.aicup2.model.RaceResult;
import com.devoler.aicup2.model.RaceTrack;
import com.devoler.aicup2.model.RaceTrackParser;
import com.devoler.aicup2.model.SolutionChecker;

@SuppressWarnings("serial")
public final class RaceTrackPanel extends JPanel {
	private static final int SIZE = 600;

	private final Object solutionMutex = new Object();

	private final RaceTrack raceTrack;
	private final BufferedImage trackImage;
	private final Image trackImageResized;
	private BufferedImage solutionImage;
	private Image solutionImageResized;

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
		Image image = source.getScaledInstance(scaleWidth, scaleHeight,
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
		synchronized (solutionMutex) {
			if (solutionImageResized != null) {
				g.drawImage(solutionImageResized,
						(getWidth() - solutionImageResized.getWidth(null)) / 2,
						(getHeight() - solutionImageResized.getHeight(null)) / 2,
						null);
			}
		}
	}

	public void showSolution(String solution) {
		synchronized (solutionMutex) {
			if (solutionImage != null) {
				solutionImage.flush();
				solutionImage = null;
			}
			if (solutionImageResized != null) {
				solutionImageResized.flush();
				solutionImageResized = null;
			}
			if (solution == null) {
				return;
			}
			RaceResult result = SolutionChecker.checkSolution(raceTrack,
					solution);
			solutionImage = RaceTrackRenderer.renderRaceResult(raceTrack,
					result);
			solutionImageResized = resize(solutionImage, SIZE);
		}
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
