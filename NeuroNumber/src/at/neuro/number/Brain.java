package at.neuro.number;

import java.util.Arrays;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.Perceptron;

public class Brain {

	private NeuralNetwork network;

	public Brain() {
		this.network = new Perceptron(2, 1);
	}

	public void train() {
		// create training set
		TrainingSet<SupervisedTrainingElement> trainingSet = new TrainingSet<SupervisedTrainingElement>(
				2, 1);

		double[] dI, dO;
		dI = new double[] {1,0};
		dO = new double[] {1};
		trainingSet.addElement(new SupervisedTrainingElement(dI, dO));
		

		dI = new double[] {0,1};
		dO = new double[] {1};
		trainingSet.addElement(new SupervisedTrainingElement(dI, dO));
		

		dI = new double[] {0,0};
		dO = new double[] {0};
		trainingSet.addElement(new SupervisedTrainingElement(dI, dO));
		

		dI = new double[] {1,1};
		dO = new double[] {1};
		trainingSet.addElement(new SupervisedTrainingElement(dI, dO));
		
		// learn the training set
		this.network.learn(trainingSet);
	}

	public double ask(int a, int b) {
		
		// set network input
		this.network.setInput(new double[] {a,b});
		// calculate network
		this.network.calculate();
		// get network output
		double[] networkOutput = this.network.getOutput();

		System.out.println(Arrays.toString(networkOutput));

		return networkOutput[0];

	}
}
