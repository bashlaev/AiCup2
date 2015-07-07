package com.devoler.aicup2.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.devoler.aicup2.model.RemoteSolver;

public final class Launcher {
	private static final String[] TEST_TRACKS = {
			"1 LLLUUUURRRRRRDDDDLLL",
			"3 RRRRRRRRRRRRRRRRRRUUUUUUUUUUUUUUUUULLLLLLLLLLLLLLLLLLLLLLLLLLLDDDDDDDDDDDDDDDDDRRRRRRRRR",
			"1 DDDDDDDDDDDLDLDLDLDLDLLDLDLDLDLLUUUULUUUUUUUUULUUUUUUUUULUUUUURRRDRRRRDRRRRRRDRRDDDD",
			"0 LLLLULLULLULLULLULLULLURRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRDDLLDLLDLLDLLDLLDLLLLL",
			"3 UUUUUUUUULUUUUUUUUUUUUUUUUUULUUUUUUUUUUULLULLLULLLLDDDDDDDDDDDDDDLLLLDDDLLLLLLDLDDLDLDDLDDLDLDDLDDLDLDDLDDLDLDDLDDLDLDDLDRDRRDRDRRDRRDRDRRDRRDRDRRDRDLDDDLDDDLDDDLDDDLDDDLDDDLDDDLDDDDRDDDDDDRDDDDDDRDDDDDDRDDDRRUURURUUUUUUUURRRRRUUUUUUUUUUUUUUUUUURUUUUUUUURUUUUURUUUURRRRRRRRDRDDRDDRDDDRDDRDDRDDRDDRDDDRDDRDDRDRRURRRRURRRRURRRURRRRURRUUUURUUUUUUURUUUUUUUURUUUULULULLULULLULULLDLDLLDLLDLDLLLLLLLLLLLULULUUUUUUUU",
			"1 DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDLDDDLDDDDLDDLDLDLDLDLDLDLDLDLDLDLLDLLDLLLLDLLLLLLDLLLLLLLLLLULLLLLLLLLLLLLLLULLLLLLLLULULULLULULLULULULLULULULUULUUUULUUUUULUULULULULDDLDDDLDDDDLDDDRDRDDRDRDRDRDRDRDRDRDRRDRDRDRDRDRDRRRDRRDRDRRDRRRDRRRDRRRRDRRRDRRRRDRRRDRRRDRRDRDDDRDDDLDDDLDLDLLLDLLLDLLLLLLLLULLLLLLLULUUULUULUULUUULULULULULULULLULULULULULLULULULULLULULULULULLULULULULULUUULUUUUULUUUUULUUUUURUUUURUUUURUURURRURRURRDDDLDDLDLDLDDDRRRRRUURRRRRRRRRDRRRRRRRDRRRRRRRDRRRRRRRDRRRRRRRDRRRRRRRDRRRRURRURRURUURUUURUUULUULUUULULULULULULULULLULLLULLLLULLLULLLULLLLLLLLULLLLLLLLULLLLLULLLLLULLLULULULULULULULULUULULUULUUUULUUUUULUUUUUUUUULUUUUUUUUUUUURUUUUUUUUUURUUUUUURUURUURUURUURUURUURURURURURURURURURRURRRURRURRURRRRRRRRURRRRRRRRRRRDRRRRRRRRDRRRRRRRRRDRRRRRRRRRDRRRRRRRDRRRRRRDRRRRRRDRRRRRRDRRRRRRDRRRRRRDRRRRRRRDRRRRRRRDRRRDLLLDLDLLLLLLLLLLLLDLLLLDLLDLLDLDLDLDLDDDDDLDDDDDDDDDLDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD" };

	private static final String TRACK = "2 RRRRUUUUUULLLLLLLLDDDDDDRRRR";
	private static final String SOLUTION = "RLRLRLRLUDUDUDUDUDUDLRLRLRLRLRLRLRLRDUDUDUDUDUDURLRR";

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out
					.println("Required args: url\r\nE.g. java -jar AICup2.jar http://localhost:8080");
			return;
		}
		final URL url;
		try {
			url = new URL(args[0]);
		} catch (MalformedURLException e) {
			System.out.println("Malformed URL: " + args[0]);
			return;
		}
		JFrame frame = new JFrame("Launcher");
		JPanel allImages = new JPanel();
		// allImages.getInsets().set(5, 5, 5, 5);
		allImages.setLayout(new BoxLayout(allImages, BoxLayout.PAGE_AXIS));
		allImages.add(Box.createVerticalStrut(10));
		for (int testNo = 0; testNo < TEST_TRACKS.length; testNo++) {
			allImages.add(panelForTrack(testNo, TEST_TRACKS[testNo], url));
			allImages.add(Box.createVerticalStrut(30));
		}
		allImages.revalidate();
		JScrollPane scrollPane = new JScrollPane(allImages,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		Dimension d = scrollPane.getPreferredSize();
		scrollPane.setPreferredSize(new Dimension(d.width
				+ scrollPane.getVerticalScrollBar().getPreferredSize().width,
				d.height));
		frame.getContentPane().add(scrollPane);
		scrollPane.revalidate();
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Utils.centerWindow(frame);
		frame.setVisible(true);
	}

	private static JPanel panelForTrack(final int testNo, final String testTrack, final URL url) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.add(Box.createHorizontalStrut(10));
		panel.add(new RaceTrackPanel(testTrack));
		panel.add(Box.createHorizontalStrut(10));
		JButton solveButton = new JButton("Solve");
		solveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Future<String> future = new RemoteSolver(url).solve(testTrack);
				try {
					String solution = future.get(1, TimeUnit.MINUTES);
					System.out.println(solution);
				} catch (InterruptedException | ExecutionException
						| TimeoutException e1) {
					e1.printStackTrace();
				}
			}
		});
		panel.add(solveButton);
		panel.add(Box.createHorizontalStrut(10));
		return panel;
	}

}
