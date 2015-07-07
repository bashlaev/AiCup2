package com.devoler.aicup2.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

import com.devoler.aicup2.model.RaceResult;
import com.devoler.aicup2.model.RaceResult.Status;
import com.devoler.aicup2.model.SolutionChecker;
import com.devoler.aicup2.model.Solver;
import com.devoler.aicup2.persistent.DAO;
import com.devoler.aicup2.persistent.TestResults;

@SuppressWarnings("serial")
public final class SingleTestPanel extends JPanel {
	private static final int MAX_RESULTS = 10;
	
	private static class HighScores extends AbstractListModel<String> {
		private final List<TestResults> contents;
		
		public HighScores(final List<TestResults> contents) {
			this.contents = new ArrayList<>(contents);
		}
		
		@Override
		public int getSize() {
			return MAX_RESULTS;
		}

		@Override
		public String getElementAt(int index) {
			if (index >= contents.size()) {
				return (index + 1) + ". -";
			}
			TestResults r = contents.get(index);
			long millis = r.getTestResult();
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
			final String when;
			long millisPast = System.currentTimeMillis() - r.getTestTimestamp().getTime(); 
			if (millisPast >= TimeUnit.HOURS.toMillis(1)) {
				when = TimeUnit.MILLISECONDS.toHours(millisPast) + " hr. ago";
			} else if (millisPast >= TimeUnit.MINUTES.toMillis(1)) {
				when = TimeUnit.MILLISECONDS.toMinutes(millisPast) + " min. ago";
			} else {
				when = "just now";
			}
			return (index + 1) + ". " + time + "  (" + when + ")";
		}
		
	}
	
	private final int testNo;
	private final JList<String> highScoresList;
	private final RaceTrackPanel raceTrackPanel;
	
	public SingleTestPanel(final int testNo, final String testTrack, final Solver solver) {
		super();
		this.testNo = testNo;
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		add(Box.createHorizontalStrut(10));
		raceTrackPanel = new RaceTrackPanel(testTrack); 
		add(raceTrackPanel);
		add(Box.createHorizontalStrut(10));
		JPanel navPanel = new JPanel();
		navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.PAGE_AXIS));
		highScoresList = new JList<>();
		updateHighScores();
		highScoresList.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLoweredBevelBorder(),
				"High scores", TitledBorder.CENTER, TitledBorder.BELOW_TOP));
		highScoresList.setPreferredSize(new Dimension(300, 500));
		highScoresList.setMinimumSize(new Dimension(300, 500));
		highScoresList.setMaximumSize(new Dimension(300, 500));
		highScoresList.setVisibleRowCount(MAX_RESULTS);
		highScoresList.setLayoutOrientation(JList.VERTICAL);
		highScoresList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		navPanel.add(new JScrollPane(highScoresList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		navPanel.add(Box.createVerticalStrut(10));
		JButton solveButton = new JButton("Solve");
		solveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Future<String> future = solver.solve(testTrack);
				try {
					String solution = future.get(1, TimeUnit.MINUTES);
					RaceResult result = SolutionChecker.checkSolution(
							raceTrackPanel.getRaceTrack(), solution);
					if (result.getStatus() == Status.SUCCESS) {
						DAO.saveTestResult(testNo, Math.round(result.getTime().doubleValue() * 1000));
						updateHighScores();
					} else {
						System.out.println("Fail: " + result.getStatus());
					}
				} catch (InterruptedException | ExecutionException
						| TimeoutException e1) {
					e1.printStackTrace();
				}
			}
		});
		navPanel.add(solveButton);
		navPanel.add(Box.createVerticalGlue());
		add(navPanel);
		add(Box.createHorizontalStrut(10));
	}
	
	private void updateHighScores() {
		List<TestResults> testResults = DAO.getTestResults(testNo, MAX_RESULTS);
		highScoresList.setModel(new HighScores(testResults));
	}
}
