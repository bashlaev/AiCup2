package com.devoler.aicup2.trackeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.commons.lang3.tuple.Pair;

import com.devoler.aicup2.model.Direction;
import com.devoler.aicup2.view.Utils;

@SuppressWarnings("serial")
public final class EditorPanel extends JPanel {
	private static final int CELL_SIZE = 10;
	private static final int EDITOR_SIZE = 200;
	
	private final EditorModel model = new EditorModel(3);
	
	public EditorPanel() {
		super();
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getClickCount() > 1) { 
					int x = (e.getX() / CELL_SIZE) - EDITOR_SIZE / 2;
					int y = (e.getY() / CELL_SIZE) - EDITOR_SIZE / 2;
					List<Pair<Integer, Integer>> axisPoints = model.getAxisPoints();
					Pair<Integer, Integer> tip = axisPoints.get(axisPoints.size() - 1);
					List<Direction> shortestPath = fillShortestPath2(tip, Pair.of(x, y));
					model.addAxis(shortestPath);
					repaint();
				}
			}
		});
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_RIGHT:
					model.addAxis(Arrays.asList(Direction.RIGHT));
					break;
				case KeyEvent.VK_UP:
					model.addAxis(Arrays.asList(Direction.UP));
					break;
				case KeyEvent.VK_LEFT:
					model.addAxis(Arrays.asList(Direction.LEFT));
					break;
				case KeyEvent.VK_DOWN:
					model.addAxis(Arrays.asList(Direction.DOWN));
					break;
				case KeyEvent.VK_BACK_SPACE:
				case KeyEvent.VK_DELETE:
					model.removeTip();
					break;
				case KeyEvent.VK_Q:
					model.incWidth();
					break;
				case KeyEvent.VK_A:
					model.decWidth();
					break;
				case KeyEvent.VK_ENTER:
					JDialog dialog = new JDialog((JFrame) getTopLevelAncestor(), true);
					JTextArea text = new JTextArea(model.validateAndEncode());
					dialog.getContentPane().add(text);
					dialog.pack();
					Utils.centerWindow(dialog);
					dialog.setVisible(true);
					break;
				}
				repaint();
			}
		});
	}
	
	private  List<Direction> fillShortestPath2(Pair<Integer, Integer> from, Pair<Integer, Integer> to) {
		List<Direction> path = new ArrayList<>();
		Pair<Integer, Integer> pos = from;
		Line2D line = new Line2D.Double(from.getLeft(), from.getRight(), to.getLeft(), to.getRight());
		while(!pos.equals(to)) {
			Direction xDir = (pos.getLeft() < to.getLeft()) ? Direction.RIGHT : (pos.getLeft() > to.getLeft() ? Direction.LEFT: null);  
			Direction yDir = (pos.getRight() < to.getRight()) ? Direction.DOWN : (pos.getRight() > to.getRight() ? Direction.UP: null);
			final Direction chosenDir;
			if (xDir == null) {
				chosenDir = yDir;
			} else if (yDir == null) {
				chosenDir = xDir;
			} else {
				Pair<Integer, Integer> xPoint = xDir.apply(pos); 
				Pair<Integer, Integer> yPoint = yDir.apply(pos);
				double xDistance = line.ptSegDist(xPoint.getLeft(), xPoint.getRight());
				double yDistance = line.ptSegDist(yPoint.getLeft(), yPoint.getRight());
				chosenDir = xDistance > yDistance ? yDir: xDir;
			}
			path.add(chosenDir);
			pos = chosenDir.apply(pos);
		}
		return path;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		List<Pair<Integer, Integer>> axisPoints = model.getAxisPoints();
		Set<Pair<Integer, Integer>> trackPoints = model.getTrackPoints();
		// draw editor cells
		for(int x = 0; x < EDITOR_SIZE; x++) {
			for(int y = 0; y < EDITOR_SIZE; y++) {
				g.setColor(Color.black);
				g.drawRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
				Pair<Integer, Integer> point = Pair.of(x - EDITOR_SIZE / 2, y - EDITOR_SIZE / 2);
				if (axisPoints.contains(point)) {
					g.setColor(Color.green);
					g.fillRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, CELL_SIZE - 1, CELL_SIZE - 1);
				} else if (trackPoints.contains(point)) {
					g.setColor(Color.yellow);
					g.fillRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, CELL_SIZE - 1, CELL_SIZE - 1);
				}
			}
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(EDITOR_SIZE * CELL_SIZE,
				EDITOR_SIZE * CELL_SIZE);
	}
	
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

}
