package at.neuro.number.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import at.neuro.number.Brain;
import at.neuro.number.BrainFactory;
import at.neuro.number.neurophstudio.DrawingListener;
import at.neuro.number.neurophstudio.DrawingPanel;

public class DrawPanel extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8645826887759259894L;

	public DrawPanel(String name, String loadPath) {
		super(name);

		JPanel p = new JPanel();
		BoxLayout layout = new BoxLayout(p, BoxLayout.PAGE_AXIS);
		p.setLayout(layout);

		File brainFile;
		if (loadPath == null) {
			JFileChooser brainChooser = new JFileChooser();
			brainChooser.showOpenDialog(p);
			brainFile = brainChooser.getSelectedFile();
		} else {
			brainFile = new File(loadPath);
		}

		JLabel labelBrain = new JLabel("Brain: " + brainFile.getAbsolutePath());
		p.add(labelBrain);

		JLabel labelLetter = new JLabel("Letter: ");
		p.add(labelLetter);

		DrawingPanel drawingPanel = new DrawingPanel();

		p.add(drawingPanel);

		DrawingListener dl1 = new DrawingListener(drawingPanel);
		drawingPanel.addMouseMotionListener(dl1);
		drawingPanel.addMouseListener(dl1);
		drawingPanel.setPreferredSize(new java.awt.Dimension(200, 250));

		JButton resetButton = new JButton("reset");
		resetButton.addActionListener(new ResetListener(drawingPanel));
		p.add(resetButton);

		JButton recognizeButton = new JButton("recognize");
		recognizeButton.addActionListener(new RecognizeListener(drawingPanel,
				brainFile));
		p.add(recognizeButton);

		add(p, BorderLayout.CENTER);
	}

	private class ResetListener implements ActionListener {

		private DrawingPanel drawingPanel;

		public ResetListener(DrawingPanel drawingPanel) {
			this.drawingPanel = drawingPanel;
		}

		public void actionPerformed(ActionEvent arg0) {
			drawingPanel.clearDrawingArea();
		}
	}

	private class RecognizeListener implements ActionListener {

		private DrawingPanel drawingPanel;
		private File brainFile;

		public RecognizeListener(DrawingPanel drawingPanel, File brainFile) {
			this.drawingPanel = drawingPanel;
			this.brainFile = brainFile;
		}

		public void actionPerformed(ActionEvent arg0) {
			try {
				File file = drawingPanel.saveDrawnLetter("dont_know.png");

				/*
				 * PrintStream original = null; original = System.out;
				 * System.setOut(new PrintStream(new OutputStream() { public
				 * void write(int b) {
				 * 
				 * } }));
				 */

				System.out.println("Wakeing up the brain ...");
				BrainFactory factory = new BrainFactory();
				Brain brain = factory.createFromFile(
						brainFile.getAbsolutePath(), false);

				HashMap<String, Double> result = brain.ask(
						file.getAbsolutePath(), false);
				brain.interprete(result, false);

				/*
				 * // change redirection back to original if (original != null)
				 * { System.setOut(original); }
				 */

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
