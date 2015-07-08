package com.devoler.aicup2.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import org.apache.commons.lang3.tuple.Pair;

import com.devoler.aicup2.model.RaceResult;
import com.devoler.aicup2.model.RaceResult.Status;
import com.devoler.aicup2.model.RaceTrack;
import com.devoler.aicup2.model.TrackCell;

public final class RaceTrackRenderer {
	private static final int CELL_SIZE = 30;
	
	private static final Image trackImage = Toolkit.getDefaultToolkit().createImage(
			RaceTrackRenderer.class.getResource("/img/30x30_asphalt.png"));
	private static final Image nonTrackImage = Toolkit.getDefaultToolkit().createImage(
			RaceTrackRenderer.class.getResource("/img/30x30_grass.png"));
	private static final Image startLineImage = Toolkit.getDefaultToolkit().createImage(
			RaceTrackRenderer.class.getResource("/img/start.png"));
	private static final Image arrowU = Toolkit.getDefaultToolkit().createImage(
			RaceTrackRenderer.class.getResource("/img/arrow.png"));
	private static final Image arrowR, arrowD, arrowL;
	private static final Image arcLU = Toolkit.getDefaultToolkit().createImage(
			RaceTrackRenderer.class.getResource("/img/15x15_corner.png"));
	private static final Image arcLD = Toolkit.getDefaultToolkit().createImage(
			RaceTrackRenderer.class.getResource("/img/15x15_corner2.png"));
	private static final Image arcRU, arcRD;
	private static final Image stripeD = Toolkit.getDefaultToolkit().createImage(
			RaceTrackRenderer.class.getResource("/img/15x15_hor.png"));
	private static final Image stripeR = Toolkit.getDefaultToolkit().createImage(
			RaceTrackRenderer.class.getResource("/img/15x15_vert.png"));
	private static final Image stripeU, stripeL;
	private static final Image cornerRD = Toolkit.getDefaultToolkit().createImage(
			RaceTrackRenderer.class.getResource("/img/15x15_corner_small.png"));
	private static final Image cornerLU, cornerLD, cornerRU;
	
	static {
		MediaTracker mt = new MediaTracker(new JPanel());
		int id = 0;
		mt.addImage(trackImage, id++);
		mt.addImage(nonTrackImage, id++);
		mt.addImage(startLineImage, id++);
		mt.addImage(arrowU, id++);
		mt.addImage(arcLU, id++);
		mt.addImage(arcLD, id++);
		mt.addImage(stripeD, id++);
		mt.addImage(stripeR, id++);
		mt.addImage(cornerRD, id++);
		try {
			mt.waitForAll();
		} catch (InterruptedException e) {
			throw new RuntimeException("Could not load resources");
		}
		arcRD = rotate(toBufferedImage(arcLU), 180);
		arcRU = rotate(toBufferedImage(arcLD), 180);
		stripeU = rotate(toBufferedImage(stripeD), 180);
		stripeL = rotate(toBufferedImage(stripeR), 180);
		BufferedImage bufCorner = toBufferedImage(cornerRD);
		cornerRU = rotate(bufCorner, 270);
		cornerLU = rotate(bufCorner, 180);
		cornerLD = rotate(bufCorner, 90);
		BufferedImage bufArrow = toBufferedImage(arrowU);
		arrowL = rotate(bufArrow, 270);
		arrowD = rotate(bufArrow, 180);
		arrowR = rotate(bufArrow, 90);
		mt.addImage(arcLD, id++);
		mt.addImage(arcRD, id++);
		mt.addImage(arcRU, id++);
		mt.addImage(stripeU, id++);
		mt.addImage(stripeL, id++);
		mt.addImage(cornerLU, id++);
		mt.addImage(cornerRU, id++);
		mt.addImage(cornerRD, id++);
		mt.addImage(arrowL, id++);
		mt.addImage(arrowD, id++);
		mt.addImage(arrowR, id++);
		try {
			mt.waitForAll();
		} catch (InterruptedException e) {
			throw new RuntimeException("Could not load resources");
		}
	}
	
	private static BufferedImage toBufferedImage(Image img){
	    if (img instanceof BufferedImage) {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
	
	private static Image rotate(BufferedImage source, int degrees) {
		AffineTransform transform = new AffineTransform();
	    transform.rotate(degrees * Math.PI / 180.0d, source.getWidth()/2.0, source.getHeight()/2.0);
	    AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
	    return op.filter(source, null);
	}

	private RaceTrackRenderer() {
		throw new UnsupportedOperationException();
	}

	public static BufferedImage renderTrack(RaceTrack track) {
		BufferedImage image = new BufferedImage(track.getWidth() * CELL_SIZE,
				track.getHeight() * CELL_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();

		for (int x = 0; x < track.getWidth(); x++) {
			for (int y = 0; y < track.getHeight(); y++) {
				renderCell(g, track, x, y);
			}
		}

		// render lines
		g.setColor(new Color(0x4a0072bc, true));
		g.setClip(0, 0, track.getWidth() * CELL_SIZE, track.getHeight()
				* CELL_SIZE);
		for (int x = 0; x < track.getWidth(); x++) {
			g.drawLine(x * CELL_SIZE + CELL_SIZE / 2, 0, x * CELL_SIZE
					+ CELL_SIZE / 2, track.getHeight() * CELL_SIZE);
			g.drawLine(x * CELL_SIZE + CELL_SIZE / 2 - 1, 0, x * CELL_SIZE
					+ CELL_SIZE / 2 - 1, track.getHeight() * CELL_SIZE);
		}
		for (int y = 0; y < track.getHeight(); y++) {
			g.drawLine(0, y * CELL_SIZE + CELL_SIZE / 2, track.getWidth()
					* CELL_SIZE, y * CELL_SIZE + CELL_SIZE / 2);
			g.drawLine(0, y * CELL_SIZE + CELL_SIZE / 2 - 1, track.getWidth()
					* CELL_SIZE, y * CELL_SIZE + CELL_SIZE / 2 - 1);
		}

		return image;
	}
	
	private static boolean isStartLine(TrackCell cell) {
		return (cell == TrackCell.START_CELL) || (cell == TrackCell.START_LINE);
	}

	private static void renderCell(Graphics2D g, RaceTrack track, int x, int y) {
		TrackCell cell = track.cellAt(x, y);
		if (cell == null) {
			throw new NullPointerException();
		}
		g.setClip(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
		switch (cell) {
		case TRACK:
			renderCellTrack(g, x * CELL_SIZE, y * CELL_SIZE);
			break;
		case POST_START_LINE:
		case PRE_START_LINE:
			renderCellTrack(g, x * CELL_SIZE, y * CELL_SIZE);
			Image arrowImage;
			if (isStartLine(track.safeCellAt(x - 1, y))) {
				arrowImage = (cell == TrackCell.POST_START_LINE) ? arrowR: arrowL;
			} else if (isStartLine(track.safeCellAt(x + 1, y))) {
				arrowImage = (cell == TrackCell.POST_START_LINE) ? arrowL: arrowR;
			} else if (isStartLine(track.safeCellAt(x, y - 1))) {
				arrowImage = (cell == TrackCell.POST_START_LINE) ? arrowD: arrowU;
			} else {
				arrowImage = (cell == TrackCell.POST_START_LINE) ? arrowU: arrowD;
			}
			g.drawImage(arrowImage, x * CELL_SIZE, y * CELL_SIZE, null);
			break;
		case NON_TRACK_IN:
		case NON_TRACK_OUT:
			renderCellNonTrack(g, x * CELL_SIZE, y * CELL_SIZE,
					track.isNavigable(x - 1, y - 1),
					track.isNavigable(x, y - 1),
					track.isNavigable(x + 1, y - 1),
					track.isNavigable(x + 1, y),
					track.isNavigable(x + 1, y + 1),
					track.isNavigable(x, y + 1),
					track.isNavigable(x - 1, y + 1),
					track.isNavigable(x - 1, y));
			break;
		case START_CELL:
		case START_LINE:
			renderCellTrack(g, x * CELL_SIZE, y * CELL_SIZE);
			renderCellStartLine(g, x * CELL_SIZE, y * CELL_SIZE);
			break;
		}
	}

	private static void renderCellTrack(Graphics2D g, int x, int y) {
		g.drawImage(trackImage, x, y, null);
	}

	private static void renderCellNonTrack(Graphics2D g, int x, int y, boolean LU, boolean U, boolean RU, boolean R, boolean RD, boolean D, boolean LD, boolean L) {
		// render LU
		g.setClip(x, y, CELL_SIZE / 2, CELL_SIZE / 2);
		if (!(L || LU || U)) {
			g.drawImage(nonTrackImage, x, y, null);
		} else {
			g.drawImage(trackImage, x, y, null);
			final Image fragment;
			if (!(L || U)) {
				fragment = arcLU;
			} else if (!L) {
				fragment = stripeD;
			} else if (!U) {
				fragment = stripeR;
			} else {
				fragment = cornerRD;
			}
			g.drawImage(fragment, x, y, null);
		}
		
		// render RU
		g.setClip(x + CELL_SIZE / 2, y, CELL_SIZE / 2, CELL_SIZE / 2);
		if (!(R || RU || U)) {
			g.drawImage(nonTrackImage, x, y, null);
		} else {
			g.drawImage(trackImage, x, y, null);
			final Image fragment;
			if (!(R || U)) {
				fragment = arcRU;
			} else if (!R) {
				fragment = stripeD;
			} else if (!U) {
				fragment = stripeL;
			} else {
				fragment = cornerLD;
			}
			g.drawImage(fragment, x + CELL_SIZE / 2, y, null);
		}
		
		// render LD
		g.setClip(x, y + CELL_SIZE / 2, CELL_SIZE / 2, CELL_SIZE / 2);
		if (!(L || LD || D)) {
			g.drawImage(nonTrackImage, x, y, null);
		} else {
			g.drawImage(trackImage, x, y, null);
			final Image fragment;
			if (!(L || D)) {
				fragment = arcLD;
			} else if (!L) {
				fragment = stripeU;
			} else if (!D) {
				fragment = stripeR;
			} else {
				fragment = cornerRU;
			}
			g.drawImage(fragment, x, y + CELL_SIZE / 2, null);
		}
		
		// render RD
		g.setClip(x + CELL_SIZE / 2, y + CELL_SIZE / 2, CELL_SIZE / 2, CELL_SIZE / 2);
		if (!(R || RD || D)) {
			g.drawImage(nonTrackImage, x, y, null);
		} else {
			g.drawImage(trackImage, x, y, null);
			final Image fragment;
			if (!(R || D)) {
				fragment = arcRD;
			} else if (!R) {
				fragment = stripeU;
			} else if (!D) {
				fragment = stripeL;
			} else {
				fragment = cornerLU;
			}
			g.drawImage(fragment, x + CELL_SIZE / 2, y + CELL_SIZE / 2, null);
		}
	}

	private static void renderCellStartLine(Graphics2D g, int x, int y) {
		g.drawImage(startLineImage, x, y, null);
	}

	public static BufferedImage renderRaceResult(RaceTrack track,
			RaceResult result) {
		BufferedImage image = new BufferedImage(track.getWidth() * CELL_SIZE,
				track.getHeight() * CELL_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		List<Pair<Integer, Integer>> raceLog = result.getRaceLog();
		// render moves
		Set<Pair<Integer, Integer>> renderedEndpoints = new HashSet<>();
		for (int i = 0; i < raceLog.size() - 1; i++) {
			Pair<Integer, Integer> startPoint = raceLog.get(i);
			if (!renderedEndpoints.contains(startPoint)) {
				renderEndPoint(g, startPoint.getLeft() * CELL_SIZE,
						startPoint.getRight() * CELL_SIZE);
			}
			Pair<Integer, Integer> endPoint = raceLog.get(i + 1);
			if (!renderedEndpoints.contains(endPoint)) {
				renderEndPoint(g, endPoint.getLeft() * CELL_SIZE,
						endPoint.getRight() * CELL_SIZE);
			}
			boolean successfulMove = (result.getStatus() != Status.ILLEGAL_MOVE)
					|| (i < raceLog.size() - 2);
			g.setColor(new Color(successfulMove ? 0xff00ff00 : 0xffff0000, true));
			g.drawLine(startPoint.getLeft() * CELL_SIZE + CELL_SIZE / 2,
					startPoint.getRight() * CELL_SIZE + CELL_SIZE / 2,
					endPoint.getLeft() * CELL_SIZE + CELL_SIZE / 2,
					endPoint.getRight() * CELL_SIZE + CELL_SIZE / 2);
		}
		return image;
	}

	private static void renderEndPoint(Graphics2D g, int x, int y) {
		g.setColor(new Color(0xff00ff00, true));
		g.fillOval(x + CELL_SIZE / 2 - 3, y + CELL_SIZE / 2 - 3, 6, 6);
	}

}
