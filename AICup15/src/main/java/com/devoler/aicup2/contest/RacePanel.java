package com.devoler.aicup2.contest;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.apache.commons.lang3.tuple.Pair;

import com.devoler.aicup2.contest.Submit.Races;
import com.devoler.aicup2.model.RaceResult;
import com.devoler.aicup2.model.RaceTrack;
import com.devoler.aicup2.model.SolutionChecker;
import com.devoler.aicup2.persistent.DAO;
import com.devoler.aicup2.persistent.SubmitResults;
import com.devoler.aicup2.view.RaceTrackRenderer;

@SuppressWarnings("serial")
public final class RacePanel extends JPanel {
	private final RaceTrack raceTrack;
	private final BufferedImage trackImage;
	private final List<String> names = new ArrayList<>();
	private final List<List<Pair<Integer, Integer>>> raceLogs = new ArrayList<>();
	private volatile int currentMove = 0;

	private final LegendPanel legendPanel;

	public RacePanel(final Races race) {
		super();
		raceTrack = race.getRaceTrack();
		trackImage = RaceTrackRenderer.renderTrack(raceTrack);
		List<SubmitResults> submitResults = DAO.getSubmitResults();
		List<RaceResult> raceResults = new ArrayList<>();
		for (SubmitResults submit : submitResults) {
			names.add(submit.getName());
			RaceResult result = SolutionChecker.checkSolution(raceTrack,
					race.getSolution(submit));
			raceLogs.add(result.getRaceLog());
			raceResults.add(result);
		}
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_SPACE:
					currentMove++;
					updatePlayerTimes();
					break;
				case KeyEvent.VK_BACK_SPACE:
				case KeyEvent.VK_DELETE:
					currentMove = Math.max(0, currentMove - 1);
					updatePlayerTimes();
					break;
				}
				repaint();
			}
		});

		legendPanel = new LegendPanel(names, raceResults);
		updatePlayerTimes();
	}
	
	private void updatePlayerTimes() {
		legendPanel.setCurrentMove(currentMove);
	}

	public JPanel getLegendPanel() {
		return legendPanel;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(trackImage, 0, 0, null);
		RaceTrackRenderer.renderRace((Graphics2D) g, raceTrack, raceLogs,
				currentMove);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(trackImage.getWidth(), trackImage.getHeight());
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	public void dispose() {
		trackImage.flush();
	}
}
