package com.devoler.aicup2.contest;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import com.devoler.aicup2.contest.Submit.Races;
import com.devoler.aicup2.view.Utils;

public final class RaceRunner {
	private static final RacePanel[] panels = {new RacePanel(Races.MONTE_CARLO), new RacePanel(Races.SPA), new RacePanel(Races.MONZA)};
	private static int currentRace = 0; 

	public static void main(String[] args) {
		final JFrame frame = new JFrame("Race Runner");		
		frame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (currentRace < panels.length) {
						panels[currentRace].getLegendPanel().addPoints();
					}
					if (currentRace < panels.length - 1) {
						panels[currentRace].dispose();
					}
					currentRace++;
					if (currentRace < panels.length) {
						showCurrentRace(frame);
					}
				}
				if (currentRace < panels.length) {
					panels[currentRace].dispatchEvent(e);
				}
			}
		});
		frame.getContentPane().setLayout(
				new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		showCurrentRace(frame);
		frame.setVisible(true);
	}

	private static void showCurrentRace(JFrame frame) {
		frame.setVisible(false);
		frame.getContentPane().removeAll();
		frame.getContentPane().add(new JScrollPane(panels[currentRace]));
		frame.getContentPane().add(panels[currentRace].getLegendPanel());
		panels[currentRace].getLegendPanel().setCurrentMove(0);
		frame.pack();
		Utils.fullScreenWindow(frame);
		frame.setVisible(true);
	}
}
