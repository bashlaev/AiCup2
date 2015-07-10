package com.devoler.aicup2.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.apache.commons.lang3.tuple.Pair;

import com.devoler.aicup2.model.RaceResult;
import com.devoler.aicup2.model.RaceResult.Status;
import com.devoler.aicup2.model.RaceTrack;
import com.devoler.aicup2.model.TrackCell;

public final class RaceTrackRenderer {
	private static final int CELL_SIZE = 30;

	private static final Image trackImage = Toolkit.getDefaultToolkit()
			.createImage(
					RaceTrackRenderer.class
							.getResource("/img/30x30_asphalt.png"));
	private static final Image nonTrackImage = Toolkit
			.getDefaultToolkit()
			.createImage(
					RaceTrackRenderer.class.getResource("/img/30x30_grass.png"));
	private static final Image startLineImage = Toolkit.getDefaultToolkit()
			.createImage(RaceTrackRenderer.class.getResource("/img/start.png"));
	private static final Image arrowU = Toolkit.getDefaultToolkit()
			.createImage(RaceTrackRenderer.class.getResource("/img/arrow.png"));
	private static final Image arrowR, arrowD, arrowL;
	private static final Image arcLU = Toolkit.getDefaultToolkit().createImage(
			RaceTrackRenderer.class.getResource("/img/15x15_corner.png"));
	private static final Image arcLD = Toolkit.getDefaultToolkit().createImage(
			RaceTrackRenderer.class.getResource("/img/15x15_corner2.png"));
	private static final Image arcRU, arcRD;
	private static final Image stripeD = Toolkit.getDefaultToolkit()
			.createImage(
					RaceTrackRenderer.class.getResource("/img/15x15_hor.png"));
	private static final Image stripeR = Toolkit.getDefaultToolkit()
			.createImage(
					RaceTrackRenderer.class.getResource("/img/15x15_vert.png"));
	private static final Image stripeU, stripeL;
	private static final Image cornerRD = Toolkit.getDefaultToolkit()
			.createImage(
					RaceTrackRenderer.class
							.getResource("/img/15x15_corner_small.png"));
	private static final Image cornerLU, cornerLD, cornerRU;
	private static final Image[] cars = new Image[20];
	private static final Image[] bigCars = new Image[20];
	private static final int[] colors = new int[] { 0x77ed1c24, 0x77ffc034,
			0x7700b9ff, 0x77787c5f, 0x770071bc, 0x7776599a, 0x7781cebf,
			0x77ba7581, 0x77e45b49, 0x77005e20, 0x777da367, 0x77293a14,
			0x777b390e, 0x77ffcc00, 0x77e443ff, 0x77252525, 0x77959595,
			0x778aa9d8, 0x775a707d, 0x77f5d79f };

	static {
		for (int i = 0; i < cars.length; i++) {
			String filename = String.format("%02d", i + 1);
			cars[i] = Toolkit.getDefaultToolkit().createImage(
					RaceTrackRenderer.class.getResource("/img/" + filename
							+ ".png"));
			String bigFilename = String.format("%03d", i + 1);
			bigCars[i] = Toolkit.getDefaultToolkit().createImage(
					RaceTrackRenderer.class.getResource("/img/" + bigFilename
							+ ".png"));
		}
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
		for (int i = 0; i < cars.length; i++) {
			mt.addImage(cars[i], id++);
			mt.addImage(bigCars[i], id++);
		}
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

	private static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	private static Image rotate(BufferedImage source, int degrees) {
		AffineTransform transform = new AffineTransform();
		transform.rotate(degrees * Math.PI / 180.0d, source.getWidth() / 2.0,
				source.getHeight() / 2.0);
		AffineTransformOp op = new AffineTransformOp(transform,
				AffineTransformOp.TYPE_BILINEAR);
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
				arrowImage = (cell == TrackCell.POST_START_LINE) ? arrowR
						: arrowL;
			} else if (isStartLine(track.safeCellAt(x + 1, y))) {
				arrowImage = (cell == TrackCell.POST_START_LINE) ? arrowL
						: arrowR;
			} else if (isStartLine(track.safeCellAt(x, y - 1))) {
				arrowImage = (cell == TrackCell.POST_START_LINE) ? arrowD
						: arrowU;
			} else {
				arrowImage = (cell == TrackCell.POST_START_LINE) ? arrowU
						: arrowD;
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

	private static void renderCellNonTrack(Graphics2D g, int x, int y,
			boolean LU, boolean U, boolean RU, boolean R, boolean RD,
			boolean D, boolean LD, boolean L) {
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
		g.setClip(x + CELL_SIZE / 2, y + CELL_SIZE / 2, CELL_SIZE / 2,
				CELL_SIZE / 2);
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
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		List<Pair<Integer, Integer>> raceLog = result.getRaceLog();
		// render moves
		for (int i = 0; i < raceLog.size() - 1; i++) {
			Pair<Integer, Integer> startPoint = raceLog.get(i);
			Pair<Integer, Integer> endPoint = raceLog.get(i + 1);
			boolean successfulMove = (result.getStatus() != Status.ILLEGAL_MOVE)
					|| (i < raceLog.size() - 2);
			g.setColor(new Color(successfulMove ? 0xff00ff00 : 0xffff0000, true));
			g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_MITER));
			int x1 = startPoint.getLeft() * CELL_SIZE + CELL_SIZE / 2;
			int y1 = startPoint.getRight() * CELL_SIZE + CELL_SIZE / 2;
			int x2 = endPoint.getLeft() * CELL_SIZE + CELL_SIZE / 2;
			int y2 = endPoint.getRight() * CELL_SIZE + CELL_SIZE / 2;
			g.drawLine(x1, y1, x2, y2);
			g.drawLine(x1 - 1, y1 - 1, x2 - 1, y2 - 1);
			g.setStroke(new BasicStroke());

			// draw an arrow tip
			int d = 10;
			int h = 6;
			int dx = x2 - x1, dy = y2 - y1;
			double D = Math.sqrt(dx * dx + dy * dy);
			double xm = D - d, xn = xm, ym = h, yn = -h, x;
			double sin = dy / D, cos = dx / D;

			x = xm * cos - ym * sin + x1;
			ym = xm * sin + ym * cos + y1;
			xm = x;

			x = xn * cos - yn * sin + x1;
			yn = xn * sin + yn * cos + y1;
			xn = x;

			int[] xpoints = { x2, (int) xm, (int) xn };
			int[] ypoints = { y2, (int) ym, (int) yn };
			g.fillPolygon(xpoints, ypoints, 3);
		}
		return image;
	}

	public static void renderRace(Graphics2D g, RaceTrack track,
			List<List<Pair<Integer, Integer>>> raceLogs, int currentMove) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		for (int i = 0; i < raceLogs.size(); i++) {
			List<Pair<Integer, Integer>> raceLog = raceLogs.get(i);
			Color color = new Color(colors[i], true);
			for (int j = 1; j <= Math.min(currentMove, raceLog.size() - 1); j++) {
				// draw all moves with player's color
				Pair<Integer, Integer> startPoint = raceLog.get(j - 1);
				Pair<Integer, Integer> endPoint = raceLog.get(j);
				g.setColor(color);
				g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_MITER));
				int x1 = startPoint.getLeft() * CELL_SIZE + CELL_SIZE / 2;
				int y1 = startPoint.getRight() * CELL_SIZE + CELL_SIZE / 2;
				int x2 = endPoint.getLeft() * CELL_SIZE + CELL_SIZE / 2;
				int y2 = endPoint.getRight() * CELL_SIZE + CELL_SIZE / 2;
				g.drawLine(x1, y1, x2, y2);
				g.drawLine(x1 - 1, y1 - 1, x2 - 1, y2 - 1);
				g.setStroke(new BasicStroke());

				// draw an arrow tip
				int d = 10;
				int h = 6;
				int dx = x2 - x1, dy = y2 - y1;
				double D = Math.sqrt(dx * dx + dy * dy);
				double xm = D - d, xn = xm, ym = h, yn = -h, x;
				double sin = dy / D, cos = dx / D;

				x = xm * cos - ym * sin + x1;
				ym = xm * sin + ym * cos + y1;
				xm = x;

				x = xn * cos - yn * sin + x1;
				yn = xn * sin + yn * cos + y1;
				xn = x;

				int[] xpoints = { x2, (int) xm, (int) xn };
				int[] ypoints = { y2, (int) ym, (int) yn };
				g.fillPolygon(xpoints, ypoints, 3);
			}
		}
		Map<Pair<Integer, Integer>, Integer> duplicateCars = new HashMap<>();
		for (int i = 0; i < raceLogs.size(); i++) {
			List<Pair<Integer, Integer>> raceLog = raceLogs.get(i);
			Image car = bigCars[i];
			int lastPosIndex = Math.max(0,
					Math.min(currentMove, raceLog.size() - 1));
			// obtain current position
			Pair<Integer, Integer> pos = track.getStartCell();
			if (lastPosIndex < raceLog.size()) {
				pos = raceLog.get(lastPosIndex);
			}
			// obtain speed from last move, or (if not possible) from the next
			// move
			Pair<Integer, Integer> speed = Pair.of(0, 0);
			if (lastPosIndex < raceLog.size() && lastPosIndex > 0) {
				Pair<Integer, Integer> endPoint = pos;
				Pair<Integer, Integer> startPoint = raceLog
						.get(lastPosIndex - 1);
				speed = Pair.of(endPoint.getLeft() - startPoint.getLeft(),
						endPoint.getRight() - startPoint.getRight());
			}
			if ((speed.equals(Pair.of(0, 0)))
					&& (lastPosIndex < raceLog.size() - 1)) {
				Pair<Integer, Integer> endPoint = raceLog.get(lastPosIndex + 1);
				Pair<Integer, Integer> startPoint = pos;
				speed = Pair.of(endPoint.getLeft() - startPoint.getLeft(),
						endPoint.getRight() - startPoint.getRight());
			}
			
			Integer times = duplicateCars.get(pos);
			if (times == null) {
				times = 0;
			}
			duplicateCars.put(pos, times + 1);

			int carX = pos.getLeft() * CELL_SIZE + CELL_SIZE / 2;
			int carY = pos.getRight() * CELL_SIZE + CELL_SIZE / 2;
			double theta = (speed.equals(Pair.of(0, 0))) ? 0
					: (Math.PI / 2 + Math.atan2(speed.getRight(),
							speed.getLeft()));
			g.rotate(theta, carX, carY);
			g.drawImage(
					car,
					pos.getLeft() * CELL_SIZE + CELL_SIZE / 2
							- car.getWidth(null) / 2, pos.getRight()
							* CELL_SIZE + CELL_SIZE / 2 - car.getHeight(null)
							/ 2, null);
			g.rotate(-theta, carX, carY);
		}
		
		// filter duplicateCars
		for(Iterator<Pair<Integer, Integer>> i = duplicateCars.keySet().iterator(); i.hasNext(); ) {
			if (duplicateCars.get(i.next()).intValue() == 1) {
				i.remove();
			}
		}
		
		for(Pair<Integer, Integer> pos: duplicateCars.keySet()) {
			int times = duplicateCars.get(pos);
			g.setColor(Color.white);
			g.drawString("x" + times, pos.getLeft() * CELL_SIZE + 2 * CELL_SIZE / 3, pos.getRight() * CELL_SIZE + CELL_SIZE / 2);
		}
		
	}

	public static Image getCarImage(int index) {
		return cars[index];
	}

	public static int getColor(int index) {
		return colors[index];
	}
}
