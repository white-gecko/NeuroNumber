package at.neuro.number;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neuroph.contrib.imgrec.ColorMode;
import org.neuroph.contrib.imgrec.FractionRgbData;
import org.neuroph.contrib.imgrec.ImageRecognitionHelper;
import org.neuroph.contrib.imgrec.image.Dimension;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import at.neuro.number.easyneurons.imgrec.ImagesLoader;

public class BrainFactory {

	/**
	 * Train a new network with given image files
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public NeuralNetwork createFromTrainset(String path) throws Exception {
		// The trainings data
		TrainingSet trainSet; // could be TrainingSet<TrainingElement>
		List<String> labels = new ArrayList<String>();
		Map<String, FractionRgbData> images;

		// The network
		NeuralNetwork net;
		// The network properties
		String netLabel = "NeuroNumber";
		Dimension dimension = new Dimension(40, 40);
		ColorMode colorMode = ColorMode.BLACK_AND_WHITE;
		List<Integer> hiddenLayers = new ArrayList<Integer>();
		hiddenLayers.add(50);
		hiddenLayers.add(20);

		// TODO load files from path
		File dir = new File(path);
		if (!dir.isDirectory()) {
			throw new Exception("Please specify a directory");
		}

		String label;
		File[] content = dir.listFiles();
		images = new HashMap<String, FractionRgbData>();

		System.out.println("processing dir " + dir.getName());
		
		for (File directory : content) {
			System.out.println("processing dir " + directory.getName());
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
		
		// specify learningrull
        MomentumBackpropagation backpropagation = new MomentumBackpropagation();
        backpropagation.setLearningRate(0.1);
        backpropagation.setMaxError(0.01);
        backpropagation.setMomentum(0.0);
		
		net.learn(trainSet, backpropagation);
		//learnInNewThread();
		return net;
	}

	/**
	 * Load a ready traines network from a file
	 * @param loadPath
	 * @return
	 */
	public NeuralNetwork createFromFile(String loadPath) {
		NeuralNetwork net = NeuralNetwork.load(loadPath);
		return net;
	}
}
