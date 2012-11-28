package at.neuro.number;

import java.io.File;
import java.util.HashMap;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import org.neuroph.contrib.imgrec.ImageRecognitionPlugin;
import org.neuroph.core.NeuralNetwork;

public class NeuroNumber {

	private String storePath = null;
	private String loadPath = null;
	private String filePath = null;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			throw new Exception(
					"Please specify at lease one driectory with image folders");
		}

		String loadPath = args[0];

		String storePath = null;
		
		String filePath = null;

		if (args.length > 1) {
			storePath = args[1];
			if (storePath == "null") {
				storePath = null;
			}
		}
		if (args.length > 2) {
			filePath = args[2];
			if (filePath == "null") {
				filePath = null;
			}
		}

		System.out.println("Start programm with:");
		System.out.println("loadPath: " + loadPath);
		System.out.println("storePath: " + storePath);
		System.out.println("filePath: " + filePath);
		
		NeuroNumber app = new NeuroNumber(loadPath, storePath, filePath);
		app.run();
	}

	public NeuroNumber(String loadPath, String storePath, String filePath) {
		this.loadPath = loadPath;
		this.storePath = storePath;
		this.filePath = filePath;
	}

	public void run() throws Exception {

		System.out.println("create a new network and train it");
		BrainFactory factory = new BrainFactory();
		NeuralNetwork net = factory.createFromTrainset(loadPath);

		net.save(storePath);

		System.out.println("try to recognize the given image");
		ImageRecognitionPlugin imageRecognition = (ImageRecognitionPlugin) net
				.getPlugin(ImageRecognitionPlugin.class);
		
        HashMap<String, Double> output = imageRecognition.recognizeImage(new File(filePath));
        
        System.out.println("The network returned: " + output.toString());
	}
}
