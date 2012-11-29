package at.neuro.number;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.neuroph.contrib.imgrec.ImageRecognitionPlugin;
import org.neuroph.contrib.imgrec.ImageSizeMismatchException;
import org.neuroph.core.NeuralNetwork;

public class Brain {
	public static final String NL = System.getProperty("line.separator");

	private NeuralNetwork network;

	public Brain(NeuralNetwork net) {
		this.network = net;
	}

	public void train(boolean verbose) {

		// This method might to something, if we want to do incremental training

		// learn the training set
		// this.network.learn(trainingSet);
	}

	public HashMap<String, Double> ask(String filePath, boolean verbose)
			throws ImageSizeMismatchException, IOException {
		System.out
				.println("I try to guess what you mean with this scribbling ...");
		ImageRecognitionPlugin imageRecognition = (ImageRecognitionPlugin) network
				.getPlugin(ImageRecognitionPlugin.class);

		HashMap<String, Double> output = imageRecognition
				.recognizeImage(new File(filePath));

		if (verbose) {
			System.out.println("The network returned: " + output.toString());
		}

		return output;

	}

	public void sleepAt(String storePath, boolean verbose) {
		network.save(storePath);
	}

	public void interprete(HashMap<String, Double> result, boolean verbose) {

		ValueComparator bvc = new ValueComparator(result);
		TreeMap<String, Double> sortedResult = new TreeMap<String, Double>(bvc);
		sortedResult.putAll(result);

		if (verbose) {
			System.out.println("Result: " + sortedResult);
		}

		Entry<String, Double> hit = sortedResult.firstEntry();

		if (hit.getValue() > .9) {
			System.out.println("It must be a '" + hit.getKey() + "' ("
					+ hit.getValue() + ").");
		} else if (hit.getValue() > .8) {
			System.out.println("I'm quite sure it is a '" + hit.getKey() + "' ("
					+ hit.getValue() + ").");
		} else if (hit.getValue() > .5) {
			System.out.println("I think it is a '" + hit.getKey() + "' ("
					+ hit.getValue() + ").");
		} else {
			System.out.print("I could be a ");

			for (String number : sortedResult.keySet()) {
				System.out.print("'" + number + "' (" + sortedResult.get(number) + ")"
						+ NL + "or maybe a ");
			}
			System.out.println("something else …");
		}
	}

	class ValueComparator implements Comparator<String> {

		Map<String, Double> base;

		public ValueComparator(Map<String, Double> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(String a, String b) {
			if (base.get(a) > base.get(b)) {
				return -1;
			} else if (base.get(a) < base.get(b)) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
