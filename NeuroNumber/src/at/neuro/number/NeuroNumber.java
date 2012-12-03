package at.neuro.number;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

import com.sanityinc.jargs.CmdLineParser;
import com.sanityinc.jargs.CmdLineParser.Option;

public class NeuroNumber {
	public static final String NL = System.getProperty("line.separator");

	private String mode = null;
	private String storePath = null;
	private String loadPath = null;
	private String filePath = null;
	private boolean verbose = false;
	private BrainFactory factory;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		CmdLineParser cmd = new CmdLineParser();
		Option<String> modeOption = cmd.addStringOption('m', "mode");
		Option<String> loadPathOption = cmd.addStringOption('l', "loadPath");
		Option<String> storePathOption = cmd.addStringOption('s', "storePath");
		Option<String> filePathOption = cmd.addStringOption('f', "filePath");
		Option<Boolean> verboseOption = cmd.addBooleanOption('v', "verbose");
		Option<Integer> wImgOption = cmd.addIntegerOption('x', "imageWidth");
		Option<Integer> hImgOption = cmd.addIntegerOption('y', "imageHeight");
		Option<String> layerOption = cmd.addStringOption("hiddenLayers");
		Option<String> learningRuleOption = cmd.addStringOption('r',
				"learningRule");
		Option<Boolean> licenseOption = cmd.addBooleanOption("license");
		Option<String> paramOption = cmd.addStringOption('p', "param");
		Option<Double> lowOption = cmd.addDoubleOption("lo");
		Option<Double> highOption = cmd.addDoubleOption("hi");
		Option<Double> stepOption = cmd.addDoubleOption("st");
		Option<Integer> neuronsOption = cmd.addIntegerOption("nr");
		Option<Integer> setsizeOption = cmd.addIntegerOption("sz");
		cmd.parse(args);

		String mode = cmd.getOptionValue(modeOption, "tell");
		String loadPath = cmd.getOptionValue(loadPathOption);
		String storePath = cmd.getOptionValue(storePathOption);
		String filePath = cmd.getOptionValue(filePathOption);
		boolean verbose = cmd.getOptionValue(verboseOption, false);
		int width = cmd.getOptionValue(wImgOption, 0);
		int height = cmd.getOptionValue(hImgOption, 0);
		String layers = cmd.getOptionValue(layerOption);
		String ruleString = cmd.getOptionValue(learningRuleOption);
		boolean license = cmd.getOptionValue(licenseOption, false);
		Double  low = cmd.getOptionValue(lowOption);
		Double high = cmd.getOptionValue(highOption);
		Double step = cmd.getOptionValue(stepOption);
		String param = cmd.getOptionValue(paramOption);
		Integer neuronsInLayer = cmd.getOptionValue(neuronsOption, null);
		int setSize = cmd.getOptionValue(setsizeOption, -1);
		// distinguish between modes
		if (mode.compareTo("al") == 0) {
			// the code is running in constructor
			new NeuroAnalytics(loadPath, low, high, step, param, neuronsInLayer, setSize, verbose);
		}
		else {
			NeuroNumber app = new NeuroNumber(mode, loadPath, storePath, filePath,
					verbose);
			if (license) {
				app.printLicense();
				return;
			}

			if (width > 0 && height > 0) {
				app.setDimensions(width, height);
			}
			if (layers != null) {
				app.setLayers(layers);
			}
			if (ruleString != null) {
				app.setLearningRule(ruleString);
			}
			app.run();
			}
	}

	public NeuroNumber(String mode, String loadPath, String storePath,
			String filePath, boolean verbose) {
		this.mode = mode;
		this.loadPath = loadPath;
		this.storePath = storePath;
		this.filePath = filePath;
		this.verbose = verbose;
		factory = new BrainFactory();
	}

	public void setDimensions(int w, int h) {
		factory.setDimension(w, h);
	}

	public void setLayers(String layersString) {
		String[] layerStrings = layersString.split(",");
		int[] layers = new int[layerStrings.length];
		int i = 0;
		for (String string : layerStrings) {
			layers[i] = Integer.parseInt(string.trim());
			i++;
		}
		if (verbose) {
			System.out
					.println("Layerconfiguration: " + Arrays.toString(layers));
		}

		factory.setHiddenLayers(layers);
	}

	public void setLearningRule(String learningRuleString)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		factory.setLearningRule(learningRuleString);
	}

	public void run() throws Exception {

		if (mode.compareTo("learn") == 0) {
			System.out.println("Start programm in learn mode.");

			if (loadPath == null || storePath == null) {
				System.out
						.println("Please specify a directory with images '--loadPath' and a place where to store the network '--filePath'.");
				return;
			}

			if (verbose) {
				System.out.println("Learning from: " + loadPath + ".");
				System.out.println("And later storring the results at: "
						+ storePath + ".");
			}
			trainAndSleep();
		} else if (mode.compareTo("tell") == 0) {
			System.out.println("Start programm in tell mode.");

			if (loadPath == null || filePath == null) {
				System.out
						.println("Please specify a network '--loadPath' and an imageFile '--filePath'.");
				return;
			}

			if (verbose) {
				System.out.println("Loading the network from: " + loadPath
						+ ".");
				System.out
						.println("And trying to recognice: " + filePath + ".");
			}
			ask();
		} else {
			System.out.println("I don't know, what you mean with '" + mode
					+ "' and my brain can't help me!");
		}

		System.out.println("Done.");
	}

	private void trainAndSleep() throws Exception {
		System.out.println("Create a new brain and train it ...");
		Brain brain = factory.createFromTrainSet(loadPath, -1, verbose);

		System.out.println("Putting the brain to sleep.");
		brain.sleepAt(storePath, verbose);
	}

	private void ask() throws Exception {
		System.out.println("Wakeing up the brain ...");
		Brain brain = factory.createFromFile(loadPath, verbose);

		HashMap<String, Double> result = brain.ask(filePath, verbose);
		brain.interprete(result, verbose);
	}

	private void printLicense() throws IOException {
		System.out
				.println("This programm uses and contains code of the jargs and the neuroph projects.");

		System.out.println();
		System.out.println("You can get the source code of neuronumber here: https://github.com/white-gecko/NeuroNumber");
		InputStream is = getClass().getResourceAsStream("neuronumber-license.txt");
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);

	    String line;
	    while ((line = br.readLine()) != null) {
			System.out.println(line);
		}

		System.out.println();
		System.out.println("jargs:");
		System.out.println("You can get the source code of jargs here: https://github.com/white-gecko/jargs");
		is = getClass().getResourceAsStream("jargs-license.txt");
	    isr = new InputStreamReader(is);
	    br = new BufferedReader(isr);

	    while ((line = br.readLine()) != null) {
			System.out.println(line);
		}

		System.out.println();
		System.out.println("neuroph:");
		System.out.println("You can get the source code of neuroph here: http://neuroph.sourceforge.net/download.html");
		is = getClass().getResourceAsStream("neuroph-license.txt");
	    isr = new InputStreamReader(is);
	    br = new BufferedReader(isr);

	    while ((line = br.readLine()) != null) {
			System.out.println(line);
		}

	}
}
