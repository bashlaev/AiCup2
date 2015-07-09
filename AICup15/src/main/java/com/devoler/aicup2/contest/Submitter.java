package com.devoler.aicup2.contest;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import com.devoler.aicup2.persistent.DAO;
import com.devoler.aicup2.persistent.SubmitResults;
import com.devoler.aicup2.view.Utils;

public final class Submitter {
	public static void main(String[] args) {
		final SortedMap<String, Submit> submits = new ConcurrentSkipListMap<>();
		JFrame frame = new JFrame("Racetrack submitter");
		frame.getContentPane().setLayout(
				new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		JLabel nameLabel = new JLabel("name");
		nameLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		frame.getContentPane().add(nameLabel);
		final JTextField nameField = new JTextField(15);
		nameField.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		nameField.setMaximumSize(nameField.getPreferredSize());
		frame.getContentPane().add(nameField);
		JLabel urlLabel = new JLabel("URL");
		urlLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		frame.getContentPane().add(urlLabel);
		final JTextField urlField = new JTextField(30);
		urlField.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		urlField.setMaximumSize(urlField.getPreferredSize());
		frame.getContentPane().add(urlField);
		final JLabel submittingLabel = new JLabel("<html> - </html>");
		submittingLabel.setVerticalAlignment(JLabel.TOP);
		submittingLabel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "In progress"));
		submittingLabel.setMinimumSize(new Dimension(400, 200));
		final JLabel submittedLabel = new JLabel();
		submittedLabel.setVerticalAlignment(JLabel.TOP);
		submittedLabel.setMinimumSize(new Dimension(400, 200));
		submittedLabel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Submitted"));
		JButton submitBtn = new JButton("Submit");
		submitBtn.setAlignmentX(JButton.LEFT_ALIGNMENT);
		submitBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = nameField.getText();
				String urlString = urlField.getText();
				if (name.isEmpty() || urlString.isEmpty()) {
					return;
				}
				if (submits.containsKey(name)) {
					System.out.println("Already submitting");
					return;
				}
				final URL url;
				try {
					url = new URL(urlString);
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
					return;
				}
				submits.put(name,
						new Submit(name, url, System.currentTimeMillis()));
			}
		});

		Executors.newSingleThreadScheduledExecutor(
				new BasicThreadFactory.Builder().daemon(true).build())
				.scheduleWithFixedDelay(new Runnable() {
					@Override
					public void run() {
						StringBuilder submitting = new StringBuilder();
						boolean submitsPending = false;
						boolean submitsFinished = false;
						for (Iterator<Entry<String, Submit>> i = submits
								.entrySet().iterator(); i.hasNext();) {
							Entry<String, Submit> entry = i.next();
							if (entry.getValue().isFinished()) {
								submitsFinished = true;
								i.remove();
							} else {
								submitsPending = true;
								submitting.append(entry.getValue().getStatus())
										.append("<br>");
							}
						}
						if (submitsPending || submitsFinished) {
							submittingLabel.setText("<html>"
									+ submitting.toString() + "</html>");
							submittingLabel.getTopLevelAncestor().invalidate();
						}
						if (submitsFinished) {
							submittedLabel.setText(getSubmitsString());
							submittedLabel.getTopLevelAncestor().invalidate();
						}
					}
				}, 500, 500, TimeUnit.MILLISECONDS);

		frame.getContentPane().add(submitBtn);
		frame.getContentPane().add(new JScrollPane(submittingLabel));
		frame.getContentPane().add(new JScrollPane(submittedLabel));
		submittedLabel.setText(getSubmitsString());
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Utils.centerWindow(frame);
		frame.setVisible(true);
	}

	private static String getSubmitsString() {
		List<SubmitResults> results = DAO.getSubmitResults();
		StringBuilder b = new StringBuilder();
		b.append("<html>");
		for (SubmitResults result : results) {
			b.append("<font color=green><b>").append(result.getName())
					.append("</b></font> ").append(result.getSubmitTime())
					.append("<br>");
		}
		b.append("</html>");
		return b.toString();
	}
}
