package com.devoler.aicup2.trackeditor;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public final class Editor {	
	public static void main(String[] args) {
		final JPanel editorPanel = new EditorPanel();
		JScrollPane scrollPane = new JScrollPane(editorPanel);
		scrollPane.setPreferredSize(new Dimension(600, 600));
		JFrame frame = new JFrame("Editor");
		frame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				editorPanel.dispatchEvent(e);
			}
		});
		frame.getContentPane().add(scrollPane);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		centerWindow(frame);
		scrollPane.getVerticalScrollBar().setValue((scrollPane.getVerticalScrollBar().getMaximum() - scrollPane.getVerticalScrollBar().getVisibleAmount()) / 2);
		scrollPane.getHorizontalScrollBar().setValue((scrollPane.getHorizontalScrollBar().getMaximum() - scrollPane.getHorizontalScrollBar().getVisibleAmount()) / 2);
		frame.setVisible(true);
	}

	private static void centerWindow(Window window) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		window.setLocation(screenWidth / 2 - window.getWidth() / 2,
				screenHeight / 2 - window.getHeight() / 2);
	}

}
