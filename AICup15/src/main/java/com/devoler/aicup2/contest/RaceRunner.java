package com.devoler.aicup2.contest;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import com.devoler.aicup2.contest.Submit.Races;
import com.devoler.aicup2.view.Utils;

public final class RaceRunner {

	public static void main(String[] args) {
		JFrame frame = new JFrame("Race Runner");
		final RacePanel monteCarlo = new RacePanel(Races.MONTE_CARLO);
		JScrollPane scrollPane = new JScrollPane(monteCarlo);
		frame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				monteCarlo.dispatchEvent(e);
			}
		});
		frame.getContentPane().add(scrollPane);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Utils.fullScreenWindow(frame);
		frame.setVisible(true);
	}

}
