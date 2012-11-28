package at.neuro.number;

import java.util.Arrays;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.Perceptron;

public class Brain {

	private NeuralNetwork network;

	public Brain() {
		this.network = new Perceptron(8, 4);
	}

	public void train() {
		// create training set
		TrainingSet<SupervisedTrainingElement> trainingSet = new TrainingSet<SupervisedTrainingElement>(
				8, 4);

		String binI, binJ, binO;
		double[] dI, dO;
		for (int i = 0; i < 8; i++) {
			binI = addZero(Integer.toBinaryString(i));

			for (int j = 0; j < 8; j++) {
				binJ = addZero(Integer.toBinaryString(j));
				binO = addZero(Integer.toBinaryString(i + j));

				dI = sToD(binI + binJ);
				dO = sToD(binO);
				
				System.out.println("dI: " + binI + binJ + " l(" + dI.length + ")");
				System.out.println("dO: " + binO + " l(" + dO.length + ")");

				trainingSet.addElement(new SupervisedTrainingElement(dI, dO));
			}
		}
		// learn the training set
		this.network.learn(trainingSet);
	}

	public double ask(int a, int b) {

		String binA, binB;
		binA = addZero(Integer.toBinaryString(a));
		binB = addZero(Integer.toBinaryString(b));
		
		// set network input
		this.network.setInput(sToD(binA + binB));
		// calculate network
		this.network.calculate();
		// get network output
		double[] networkOutput = this.network.getOutput();

		System.out.println(Arrays.toString(networkOutput));

		return networkOutput[0];

	}

	private double[] sToD(String s) {
		double[] d = new double[s.length()];
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '1') {
				d[i] = 1;
			} else {
				d[i] = 0;
			}
		}
		return d;
	}
	
	private String addZero (String s) {
		while (s.length() < 4) {
			s = '0' + s;
		}
		return s;
	}

}
