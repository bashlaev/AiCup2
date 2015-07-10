package com.devoler.aicup2.contest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang3.mutable.MutableInt;

import com.devoler.aicup2.model.RaceResult;
import com.devoler.aicup2.model.RaceResult.Status;
import com.devoler.aicup2.view.RaceTrackRenderer;
import com.devoler.aicup2.view.Utils;

@SuppressWarnings("serial")
public final class LegendPanel extends JPanel {
	private static final Map<Integer, Integer> POINTS_PER_POSITION = new HashMap<Integer, Integer>() {
		{
			put(1, 10);
			put(2, 8);
			put(3, 6);
			put(4, 4);
			put(5, 3);
			put(6, 2);
			put(7, 1);
		}
	};
	private static final Map<String, MutableInt> TOTAL_POINTS = new HashMap<>();

	private final List<String> names;
	private final List<RaceResult> raceResults;
	private final List<JLabel> labels = new ArrayList<>();
	private final List<Integer> positions = new ArrayList<>();
	private volatile int currentMove;

	public LegendPanel(final List<String> names,
			final List<RaceResult> raceResults) {
		this.names = names;
		this.raceResults = raceResults;
		// calc positions
		// make a set of all distinct times
		Set<Long> times = new HashSet<>();
		for (int i = 0; i < raceResults.size(); i++) {
			TOTAL_POINTS.put(names.get(i), new MutableInt(0));
			if (raceResults.get(i).getStatus() == Status.SUCCESS) {
				times.add(Math
						.round(raceResults.get(i).getTime().doubleValue() * 1000));
			}
		}
		for (int i = 0; i < raceResults.size(); i++) {
			final Integer position;
			if (raceResults.get(i).getStatus() != RaceResult.Status.SUCCESS) {
				position = null;
			} else {
				// calculate how many distinct times are better than this one
				long thisTime = Math.round(raceResults.get(i).getTime()
						.doubleValue() * 1000);
				int betterTimes = 0;
				for (Long time : times) {
					if (time < thisTime) {
						betterTimes++;
					}
				}
				position = 1 + betterTimes;
			}
			positions.add(position);
		}
		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Racers",
				TitledBorder.CENTER, TitledBorder.ABOVE_TOP));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		for (int i = 0; i < names.size(); i++) {
			JLabel label = new JLabel("", new ImageIcon(
					RaceTrackRenderer.getCarImage(i)), SwingConstants.LEFT);
			label.setAlignmentX(LEFT_ALIGNMENT);
			labels.add(label);
			add(label);
		}

		setCurrentMove(0);
	}

	public void setCurrentMove(int currentMove) {
		this.currentMove = currentMove;
		for (int i = 0; i < names.size(); i++) {
			String color = Integer
					.toHexString(RaceTrackRenderer.getColor(i) & 0xffffff);
			String text = String
					.format("<html><pre><font color=%s>%12s</font>%15s<b>%4s%4s</b></pre></html>",
							color,
							names.get(i),
							getTime(raceResults.get(i), currentMove),
							getPosition(raceResults.get(i), currentMove,
									positions.get(i)), TOTAL_POINTS.get(names
									.get(i)));
			labels.get(i).setText(text);
		}
		repaint();
	}

	public void addPoints() {
		for (int i = 0; i < raceResults.size(); i++) {
			int points = 0;
			if ((positions.get(i) != null)
					&& (POINTS_PER_POSITION.containsKey(positions.get(i)))) {
				points = POINTS_PER_POSITION.get(positions.get(i)).intValue();
			}
			TOTAL_POINTS.get(names.get(i)).add(points);
		}
		System.out.println(TOTAL_POINTS);
		setCurrentMove(currentMove);
	}

	private static String getTime(RaceResult result, int currentMove) {
		long timeMillis = Math.round(result.getTime().doubleValue() * 1000);
		long currentMillis = currentMove * 1000;
		if (timeMillis > currentMillis) {
			return Utils.timeToLapTime(currentMillis);
		}
		if (result.getStatus() != RaceResult.Status.SUCCESS) {
			return "DNF";
		}
		return Utils.timeToLapTime(timeMillis);
	}

	private static String getPosition(RaceResult result, int currentMove,
			Integer position) {
		if (position == null) {
			return "-";
		}
		long timeMillis = Math.round(result.getTime().doubleValue() * 1000);
		long currentMillis = currentMove * 1000;
		if (timeMillis > currentMillis) {
			return "-";
		}
		return String.format("%d", position.intValue());
	}

}
