package com.devoler.aicup2.view;

import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.devoler.aicup2.model.RemoteSolver;
import com.devoler.aicup2.model.Solver;

public final class Launcher {
	private static final String[] TEST_TRACKS = {
			"1 LLLUUUURRRRRRDDDDLLL",
			"3 RRRRRRRRRRRRRRRRRRUUUUUUUUUUUUUUUUULLLLLLLLLLLLLLLLLLLLLLLLLLLDDDDDDDDDDDDDDDDDRRRRRRRRR",
			"1 DDDDDDDDDDDLDLDLDLDLDLLDLDLDLDLLUUUULUUUUUUUUULUUUUUUUUULUUUUURRRDRRRRDRRRRRRDRRDDDD",
			"0 LLLLULLULLULLULLULLULLURRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRDDLLDLLDLLDLLDLLDLLLLL",
			"3 UUUUUUUUULUUUUUUUUUUUUUUUUUULUUUUUUUUUUULLULLLULLLLDDDDDDDDDDDDDDLLLLDDDLLLLLLDLDDLDLDDLDDLDLDDLDDLDLDDLDDLDLDDLDDLDLDDLDRDRRDRDRRDRRDRDRRDRRDRDRRDRDLDDDLDDDLDDDLDDDLDDDLDDDLDDDLDDDDRDDDDDDRDDDDDDRDDDDDDRDDDRRUURURUUUUUUUURRRRRUUUUUUUUUUUUUUUUUURUUUUUUUURUUUUURUUUURRRRRRRRDRDDRDDRDDDRDDRDDRDDRDDRDDDRDDRDDRDRRURRRRURRRRURRRURRRRURRUUUURUUUUUUURUUUUUUUURUUUULULULLULULLULULLDLDLLDLLDLDLLLLLLLLLLLULULUUUUUUUU", 
	};

	// "1 DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDLDDDLDDDDLDDLDLDLDLDLDLDLDLDLDLDLLDLLDLLLLDLLLLLLDLLLLLLLLLLULLLLLLLLLLLLLLLULLLLLLLLULULULLULULLULULULLULULULUULUUUULUUUUULUULULULULDDLDDDLDDDDLDDDRDRDDRDRDRDRDRDRDRDRDRRDRDRDRDRDRDRRRDRRDRDRRDRRRDRRRDRRRRDRRRDRRRRDRRRDRRRDRRDRDDDRDDDLDDDLDLDLLLDLLLDLLLLLLLLULLLLLLLULUUULUULUULUUULULULULULULULLULULULULULLULULULULLULULULULULLULULULULULUUULUUUUULUUUUULUUUUURUUUURUUUURUURURRURRURRDDDLDDLDLDLDDDRRRRRUURRRRRRRRRDRRRRRRRDRRRRRRRDRRRRRRRDRRRRRRRDRRRRRRRDRRRRURRURRURUURUUURUUULUULUUULULULULULULULULLULLLULLLLULLLULLLULLLLLLLLULLLLLLLLULLLLLULLLLLULLLULULULULULULULULUULULUULUUUULUUUUULUUUUUUUUULUUUUUUUUUUUURUUUUUUUUUURUUUUUURUURUURUURUURUURUURURURURURURURURURRURRRURRURRURRRRRRRRURRRRRRRRRRRDRRRRRRRRDRRRRRRRRRDRRRRRRRRRDRRRRRRRDRRRRRRDRRRRRRDRRRRRRDRRRRRRDRRRRRRDRRRRRRRDRRRRRRRDRRRDLLLDLDLLLLLLLLLLLLDLLLLDLLDLLDLDLDLDLDDDDDLDDDDDDDDDLDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD"

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
		final Solver solver = new RemoteSolver(url);
		JFrame frame = new JFrame("Racetrack tester");
		JPanel allImages = new JPanel();
		allImages.setLayout(new BoxLayout(allImages, BoxLayout.PAGE_AXIS));
		allImages.add(Box.createVerticalStrut(10));
		for (int testNo = 0; testNo < TEST_TRACKS.length; testNo++) {
			allImages.add(new SingleTestPanel(testNo, TEST_TRACKS[testNo],
					solver));
			allImages.add(Box.createVerticalStrut(20));
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

}
