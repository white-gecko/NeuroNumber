package at.neuro.number;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neuroph.imgrec.ColorMode;
import org.neuroph.imgrec.FractionRgbData;
import org.neuroph.imgrec.ImageRecognitionHelper;
import org.neuroph.imgrec.image.Dimension;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.DataSet;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import at.neuro.number.easyneurons.imgrec.ImagesLoader;

public class BrainFactory {

	/**
	 * The name of the network
	 */
	private String netLabel = "NeuroNumber";

	/**
	 * The width to which the input image is scaled imageWidth × imageHeight =
	 * size of input layer
	 */
	private int imageWidth = 30;

	/**
	 * The height to which the input image is scaled imageWidth × imageHeight =
	 * size of input layer
	 */
	private int imageHeight = 30;

	/**
	 * The number of neurons on each hidden layer
	 */
	private ArrayList<Integer> hiddenLayers;

	/**
	 * The learning rule which is used to train the network
	 */
	private LearningRule learningRule = null;
	private Double MBP_learnRate = 0.1;

	/**
	 * The constructor method, used to set default values
	 */
	public BrainFactory() {
		// set default values
		hiddenLayers = new ArrayList<Integer>();
		hiddenLayers.add(50);
	}

	/**
	 * Set the dimensions to which the input images should be scalled w × h =
	 * size of input layer
	 *
	 * @param w
	 *            value for imageWidth
	 * @param h
	 *            value for imageHeight
	 */
	public void setDimension(int w, int h) {
		imageWidth = w;
		imageHeight = h;
	}

	/**
	 * Set the configuration of the hidden layers e.g. {50, 30, 20} would mean,
	 * there are 3 hidden layers: 1st with 50 neurons, 2nd with 30 neurons and
	 * 3rd with 20 neurons
	 *
	 * @param layers an array with the configuration of the hidden layers
	 */
	public void setHiddenLayers(int[] layers) {
		hiddenLayers = new ArrayList<Integer>();

		for (int i : layers) {
			hiddenLayers.add(i);
		}
	}

	/**
	 * Set the learning rule to use for training the network
	 * @param learningRule an instance of LearningRule
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public void setLearningRule(String learningRuleClass) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

		Class<LearningRule> lr = (Class<LearningRule>) Class.forName(learningRuleClass);

		learningRule = lr.newInstance();
	}

	public void setMBP_learnRate(Double learnRate) {
		MBP_learnRate = learnRate;
	}

	/**
	 * Set the learning rule to use for training the network
	 * @param learningRule an instance of LearningRule
	 */
	public void setLearningRule(LearningRule learningRule) {
		this.learningRule = learningRule;
	}

	/**
	 * Get the learning rule
	 * @return
	 */
	private LearningRule getLearningRule() {
		if (learningRule == null) {
			MomentumBackpropagation backpropagation = new MomentumBackpropagation();
			backpropagation.setLearningRate(MBP_learnRate);
			backpropagation.setMaxError(0.01);
			backpropagation.setMomentum(0.0);

			learningRule = backpropagation;
		}
		return learningRule;
	}

	/**
	 * Train a new network with the given image files
	 *
	 * @param path the path to the input image file
	 * @param verbose whether the console output should be verbose
	 * @return a new instance of Brain, which is a wrapper class of NeuralNetwork
	 * @throws Exception
	 */
	public Brain createFromTrainSet(String path, boolean verbose)
			throws Exception {
		// The trainings data
		DataSet trainSet; // could be TrainingSet<TrainingElement>
		List<String> labels = new ArrayList<String>();
		Map<String, FractionRgbData> images;

		// The network
		NeuralNetwork net;
		// The network properties
		Dimension dimension = new Dimension(imageWidth, imageHeight);
		ColorMode colorMode = ColorMode.BLACK_AND_WHITE;

		// TODO load files from path
		File dir = new File(path);
		if (!dir.isDirectory()) {
			throw new Exception("Please specify a directory");
		}

		String label;
		File[] content = dir.listFiles();
		images = new HashMap<String, FractionRgbData>();

		if (verbose) {
			System.out.println("processing dir " + dir.getName());
		}

		for (File directory : content) {
			if (verbose) {
				System.out.println("processing dir " + directory.getName());
			}

			if (!directory.isDirectory()) {
				throw new Exception("The directory has to contain directories");
			}

			images.putAll(ImagesLoader.getFractionRgbDataForDirectory(
					directory, dimension));

			label = directory.getName();
			labels.add(label);
		}

		trainSet = ImageRecognitionHelper.createBlackAndWhiteTrainingSet(
				labels, images);

		net = ImageRecognitionHelper.createNewNeuralNetwork(netLabel,
				dimension, colorMode, labels, hiddenLayers,
				TransferFunctionType.SIGMOID);

		if (verbose) {
			System.out.println("New Network of type " + net.getClass().getCanonicalName());
		}

		net.learn(trainSet, getLearningRule());
		// learnInNewThread();

		return new Brain(net);
	}

	/**
	 * Load a ready trained network from a file
	 *
	 * @param loadPath path to the .nnet file of the neural network
	 * @param verbose whether the console output should be verbose
	 * @return a new instance of Brain, which is a wrapper class of NeuralNetwork
	 */
	public Brain createFromFile(String loadPath, boolean verbose) {
		NeuralNetwork net = NeuralNetwork.load(loadPath);
		return new Brain(net);
	}
}
