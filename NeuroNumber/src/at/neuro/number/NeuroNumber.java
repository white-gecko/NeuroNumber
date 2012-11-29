package at.neuro.number;

import java.util.HashMap;

import com.sanityinc.jargs.CmdLineParser;
import com.sanityinc.jargs.CmdLineParser.Option;

public class NeuroNumber {

	private String mode = null;
	private String storePath = null;
	private String loadPath = null;
	private String filePath = null;
	private boolean verbose = false;

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
		cmd.parse(args);

		String mode = cmd.getOptionValue(modeOption, "tell");
		String loadPath = cmd.getOptionValue(loadPathOption);
		String storePath = cmd.getOptionValue(storePathOption);
		String filePath = cmd.getOptionValue(filePathOption);
		boolean verbose = cmd.getOptionValue(verboseOption, false);

		NeuroNumber app = new NeuroNumber(mode, loadPath, storePath, filePath,
				verbose);
		app.run();
	}

	public NeuroNumber(String mode, String loadPath, String storePath,
			String filePath, boolean verbose) {
		this.mode = mode;
		this.loadPath = loadPath;
		this.storePath = storePath;
		this.filePath = filePath;
		this.verbose = verbose;
	}

	public void run() throws Exception {

		if (mode.compareTo("learn") == 0) {
			System.out.println("Start programm in learn mode.");
			
			if (loadPath == null || storePath == null) {
				System.out.println("Please specify a directory with images '--loadPath' and a place where to store the network '--filePath'.");
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
				System.out.println("Please specify a network '--loadPath' and an imageFile '--filePath'.");
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
		BrainFactory factory = new BrainFactory();
		Brain brain = factory.createFromTrainSet(loadPath, verbose);

		System.out.println("Putting the brain to sleep.");
		brain.sleepAt(storePath, verbose);
	}

	private void ask() throws Exception {
		System.out.println("Wakeing up the brain ...");
		BrainFactory factory = new BrainFactory();
		Brain brain = factory.createFromFile(loadPath, verbose);

		HashMap<String, Double> result = brain.ask(filePath, verbose);
		brain.interprete(result, verbose);
	}
}
