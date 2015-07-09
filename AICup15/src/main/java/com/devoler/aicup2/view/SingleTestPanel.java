package com.devoler.aicup2.view;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.devoler.aicup2.model.RaceResult;
import com.devoler.aicup2.model.RaceResult.Status;
import com.devoler.aicup2.model.RemoteSolver;
import com.devoler.aicup2.model.SolutionChecker;
import com.devoler.aicup2.model.Solver;
import com.devoler.aicup2.persistent.DAO;
import com.devoler.aicup2.persistent.TestResults;

@SuppressWarnings("serial")
public final class SingleTestPanel extends JPanel {
	private static final int MAX_RESULTS = 10;

	private static class SolutionDialog {
		private static final String WAITING_TEXT = "Waiting for solution";
		private final JFrame owner;
		private final JDialog dialog;
		private final JLabel label;

		private int dots = 0;

		public SolutionDialog(JFrame owner) {
			this.owner = owner;
			dialog = new JDialog(owner, true);
			dialog.getContentPane().setLayout(
					new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
			label = new JLabel(WAITING_TEXT + "...");
			label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setVerticalAlignment(JLabel.CENTER);
			label.setAlignmentX(Component.CENTER_ALIGNMENT);
			dialog.getContentPane().add(label);
			dialog.pack();
			Utils.centerWindow(dialog);
			dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			owner.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}

		public void show() {
			dialog.setVisible(true);
		}

		public synchronized void animate() {
			dots = (dots + 1) % 4;
			String text = WAITING_TEXT;
			for (int i = 0; i < dots; i++) {
				text += ".";
			}
			label.setText(text);
		}

		public synchronized void reportResult(String result) {
			owner.setCursor(Cursor.getDefaultCursor());
			label.setText(result);
			JButton closeButton = new JButton("OK");
			closeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dialog.setVisible(false);
					dialog.dispose();
				}
			});
			closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			dialog.getContentPane().add(closeButton);
			dialog.pack();
			Utils.centerWindow(dialog);
		}
	}

	private static class HighScores extends AbstractListModel<String> {
		private final List<TestResults> contents = new ArrayList<>();
		private final Object mutex = new Object();

		@Override
		public int getSize() {
			return MAX_RESULTS;
		}

		public void updateContents(List<TestResults> contents) {
			synchronized (mutex) {
				this.contents.clear();
				this.contents.addAll(contents);
			}
		}

		public String getSolutionAt(int index) {
			synchronized (mutex) {
				if ((index >= 0) && (index < contents.size())) {
					return contents.get(index).getSolution();
				}
			}
			return null;
		}

		@Override
		public String getElementAt(int index) {
			StringBuilder b = new StringBuilder();
			b.append("<html>").append(index + 1).append(". ");
			synchronized (mutex) {
				if (index >= contents.size()) {
					b.append(" - </html>");
				} else {
					TestResults r = contents.get(index);
					String time = Utils.timeToLapTime(r.getTestResult());
					String when = whenToString(r.getTestTimestamp().getTime());
					RaceResult.Status status = Status.values()[r.getStatus()];
					b.append(statusToString(status));
					if (status == Status.SUCCESS) {
						b.append("<font color=green><b>").append(time).append("</b></font>");
					}
					b.append("  (").append(when).append(")</html>");
				}
			}
			return b.toString();
		}

	}
	
	private static String whenToString(long time) {
		long millisPast = System.currentTimeMillis()
				- time;
		if (millisPast >= TimeUnit.HOURS.toMillis(1)) {
			return TimeUnit.MILLISECONDS.toHours(millisPast)
					+ " hr. ago";
		} else if (millisPast >= TimeUnit.MINUTES.toMillis(1)) {
			return TimeUnit.MILLISECONDS.toMinutes(millisPast)
					+ " min. ago";
		}
		return "just now";
	}
	
	private static String statusToString(RaceResult.Status status) {
		switch(status) {
		case SUCCESS:
			return "";
		case COULD_NOT_PARSE_SOLUTION:
			return "<font color=red>BAD FORMAT</font>";
		case DID_NOT_FINISH:
			return "<font color=red>DID NOT FINISH</font>";
		case ILLEGAL_MOVE:
			return "<font color=red>CRASH</font>";
		}
		throw new IllegalArgumentException("Unexpected status: " + status);
	}

	private final int testNo;
	private final HighScores highScores;
	private final RaceTrackPanel raceTrackPanel;

	public SingleTestPanel(final int testNo, final String testTrack,
			final Solver solver) {
		super();
		this.testNo = testNo;
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		add(Box.createHorizontalStrut(10));
		raceTrackPanel = new RaceTrackPanel(testTrack);
		add(raceTrackPanel);
		add(Box.createHorizontalStrut(10));
		JPanel navPanel = new JPanel();
		navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.PAGE_AXIS));
		highScores = new HighScores();
		final JList<String> highScoresList = new JList<>(highScores);
		updateHighScores();
		highScoresList.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLoweredBevelBorder(), "High scores",
				TitledBorder.CENTER, TitledBorder.BELOW_TOP));
		highScoresList.setPreferredSize(new Dimension(300, 500));
		highScoresList.setMinimumSize(new Dimension(300, 500));
		highScoresList.setMaximumSize(new Dimension(300, 500));
		highScoresList.setVisibleRowCount(MAX_RESULTS);
		highScoresList.setLayoutOrientation(JList.VERTICAL);
		highScoresList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		highScoresList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					String solution = highScores.getSolutionAt(highScoresList
							.getSelectedIndex());
					raceTrackPanel.showSolution(solution);
					repaint();
				}
			}
		});
		navPanel.add(new JScrollPane(highScoresList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		navPanel.add(Box.createVerticalStrut(10));
		JButton solveButton = new JButton("Solve");
		solveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final ExecutorService executor = Executors
						.newSingleThreadExecutor();
				final SolutionDialog solutionDialog = new SolutionDialog(
						(JFrame) getTopLevelAncestor());
				executor.execute(new Runnable() {
					@Override
					public void run() {
						Future<String> future = solver.solve(testTrack);
						String resultString = "";
						try {
							String solution = waitForSolution(future,
									solutionDialog);
							RaceResult result = SolutionChecker.checkSolution(
									raceTrackPanel.getRaceTrack(), solution);
							long time = (result.getStatus() == Status.SUCCESS) ? Math
									.round(result.getTime().doubleValue() * 1000)
									: 0;
							DAO.saveTestResult(testNo, result.getStatus(), time, solution);
							updateHighScores();
							switch(result.getStatus()) {
							case SUCCESS:
								resultString = "<html><font color=green>Success!</font><br>Finished in <b>" + Utils.timeToLapTime(time) + "</b></html>";
								break;
							case COULD_NOT_PARSE_SOLUTION:
								resultString = "<html>Could not parse solution<br>Check format of your solution.</html>";
								break;
							case DID_NOT_FINISH:
								resultString = "<html>Looks like you forgot to cross the finish line!<br>Oh well, at least you didn't crash...</html>";
								break;
							case ILLEGAL_MOVE:
								resultString = "<html>Crash!<br>Stay on track next time!</html>";
								break;
							}
						} catch (ExecutionException ee) {
							if (ee.getCause() instanceof ConnectException) {
								resultString = "Could not connect to solution server";
								if (solver instanceof RemoteSolver) {
									resultString += " at "
											+ ((RemoteSolver) solver).getUrl();
								}
							} else if (ee.getCause() instanceof SocketTimeoutException) {
								resultString = "Timed out";
							} else if (ee.getCause() instanceof IOException) {
								resultString = "I/O error: " + ee.getCause().getMessage();
							} else {
								ee.printStackTrace();
								resultString = "Error: " + ee.getCause().getMessage();
							}
						} catch (InterruptedException ie) {
							resultString = "Interrupted";
						} finally {
							solutionDialog.reportResult(resultString);
							executor.shutdownNow();
						}
					}
				});
				solutionDialog.show();
			}
		});
		navPanel.add(solveButton);
		navPanel.add(Box.createVerticalGlue());
		add(navPanel);
		add(Box.createHorizontalStrut(10));
	}

	private String waitForSolution(final Future<String> future,
			final SolutionDialog solutionDialog) throws InterruptedException,
			ExecutionException {
		while (true) {
			try {
				return future.get(500, TimeUnit.MILLISECONDS);
			} catch (TimeoutException e) {
				solutionDialog.animate();
				Thread.sleep(500);
				solutionDialog.animate();
			}
		}
	}

	private void updateHighScores() {
		List<TestResults> testResults = DAO.getTestResults(testNo, MAX_RESULTS);
		highScores.updateContents(testResults);
		repaint();
	}
}
