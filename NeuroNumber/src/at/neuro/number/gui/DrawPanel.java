package at.neuro.number.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import at.neuro.number.Brain;
import at.neuro.number.BrainFactory;
import at.neuro.number.neurophstudio.DrawingListener;
import at.neuro.number.neurophstudio.DrawingPanel;

public class DrawPanel extends JFrame {

	private boolean verbose = false;
	private Brain brain = null;
	private File brainFile = null;

	private static final long serialVersionUID = -8645826887759259894L;

	public DrawPanel(String name, String loadPath, boolean verbose)
			throws IOException {
		super(name);

		this.verbose = verbose;

		JPanel p = new JPanel();
		BoxLayout layout = new BoxLayout(p, BoxLayout.PAGE_AXIS);
		p.setLayout(layout);

		if (loadPath == null) {
			JFileChooser brainChooser = new JFileChooser();
			brainChooser.showOpenDialog(p);
			brainFile = brainChooser.getSelectedFile();
			if (brainFile == null) {
				JOptionPane.showMessageDialog(p, "No network file selected!",
						"Error", JOptionPane.PLAIN_MESSAGE);
				System.out.println("No network file selected!");
				System.exit(1);
			}
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

		JPanel buttonPanel = new JPanel();
		FlowLayout buttonLayout = new FlowLayout();
		buttonPanel.setLayout(buttonLayout);
		p.add(buttonPanel);

		JButton resetButton = new JButton("reset");
		buttonPanel.add(resetButton);

		JButton recognizeButton = new JButton("recognize");
		buttonPanel.add(recognizeButton);

		JTextArea output = new JTextArea("", 13, 50);
		output.setLineWrap(true);
		output.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(output);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		p.add(scrollPane);

		resetButton.addActionListener(new ResetListener(drawingPanel));

		recognizeButton.addActionListener(new RecognizeListener(drawingPanel));

		System.setOut(new PrintStream(new TextAreaStream(output)));

		add(p, BorderLayout.CENTER);
	}

	private Brain getBrain() {

		if (brain == null) {
			System.out.println("Wakeing up the brain …");
			BrainFactory factory = new BrainFactory();
			brain = factory
					.createFromFile(brainFile.getAbsolutePath(), verbose);
		}

		return brain;
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

		public RecognizeListener(DrawingPanel drawingPanel) {
			this.drawingPanel = drawingPanel;
		}

		public void actionPerformed(ActionEvent arg0) {
			try {
				File tmpFile = File.createTempFile("neuronumber-", ".png");
				drawingPanel.saveDrawnLetter(tmpFile);

				Brain brain = getBrain();

				HashMap<String, Double> result = brain.ask(
						tmpFile.getAbsolutePath(), verbose);
				brain.interprete(result, verbose);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class TextAreaStream extends OutputStream {

		JTextArea output;

		public TextAreaStream(JTextArea output) {
			this.output = output;
		}

		@Override
		public void write(int b) throws IOException {
			addText(String.valueOf((char) b));
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			addText(new String(b, off, len));
		}

		@Override
		public void write(byte[] b) throws IOException {
			write(b, 0, b.length);
		}

		private void addText(String text) {
			output.append(text);
			int end = output.getText().length();
			output.setCaretPosition(end);
		}

	}

}
