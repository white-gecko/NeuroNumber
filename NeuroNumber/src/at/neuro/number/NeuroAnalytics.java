package at.neuro.number;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class NeuroAnalytics {

	private String loadPath = "Trainset";
	private boolean verbose = false;
	private Brain curBrain = null;
	enum evalMode {simple, firstmatch, relative};
	private int setSize = -1;

	public NeuroAnalytics(String loadPath, Double low, Double high, Double step, String param, Integer
			neuronsInLayer, int setSize, boolean verbose) throws Exception {
		// parse arguments
		/*
		CmdLineParser cmd = new CmdLineParser();
		Option<String> paramOption = cmd.addStringOption('p', "param");
		Option<Double> lowOption = cmd.addDoubleOption('l', "low");
		Option<Double> highOption = cmd.addDoubleOption('h', "high");
		Option<Double> stepOption = cmd.addDoubleOption('s', "step");
		cmd.parse(args);
		// set the parameters
		final Double  low = cmd.getOptionValue(lowOption);
		final Double high = cmd.getOptionValue(highOption);
		final Double step = cmd.getOptionValue(stepOption);
		String param = cmd.getOptionValue(paramOption);
		*/
		// verify if everything necessary is provided
		this.setSize = setSize;
		if (param==null || high == null || low == null) {
			System.out.println("Specify all params! --lo(num) -- hi(num) --st(num) -p('hidden'|'lrate'|'layer') [--nr(num)]");
			return;
		}
		if(loadPath!=null) this.loadPath = loadPath;
		// the next is the general variable going from low to high in a cycle
		Double next = low;
		// defaults for the parameters which are not going to be changed
		Integer hidden = 50;
		double learnrate = 0.1;
		Integer layers = 1;
		// if we wanna change number of layers, user should give us number
		// of hidden neurons pro layer
		if (param.compareTo("layer")==0) {
			if (neuronsInLayer!=null )hidden = neuronsInLayer;
			else {
				System.out.println("Specify number of neurons in a layer!");
				return;
				}
			if (step == null) step = 1.0;
		}
		else if (step == null) System.out.println("Specify the step!");
		System.out.println("Entering analytics! Going to manipulare param "+param+"within bounds"
				+low.toString()+" to "+high.toString()+" by "+step.toString());
		System.out.println(param+";simple_accurancy;firstmatch;relative_accurancy");
		// instantiate, there will be just 1 instance and curBrain will
		// be overwritten each time; Constructor needs the path to training set
		// cycle for the parameter examined
		while ( next <= high) {
			System.out.print(next.toString()+";");
			// depending on -p option either hidden or learning rate are changed
			if (param.compareTo("hidden")== 0) {
				hidden = next.intValue();
			}
			else if (param.compareTo("lrate")== 0) {
				learnrate = next;
			}
			else if (param.compareTo("layer")== 0) {
				layers = next.intValue();
			}
			else if (param.compareTo("size")== 0) {
				this.setSize = next.intValue();
			}
			// make a new brain with this new value of a parameter
			curBrain = trainAndReady(layers, hidden, learnrate);
			// evaluate the different accuracies on this brain
			double simAccuracy = evaluateAccuracy(evalMode.simple);
			System.out.print(simAccuracy+";");
			double fmAccuracy = evaluateAccuracy(evalMode.firstmatch);
			System.out.print(fmAccuracy+";");
			double relAccuracy = evaluateAccuracy(evalMode.relative);
			System.out.print(relAccuracy);
			System.out.println();
			next+=step;
			}
	}

	// to evaluate accuracy(predition capability) of the network by given eval.method
	public double evaluateAccuracy(evalMode howEval) throws Exception {
		double forall = 0;
		// the overall accuracy is the sum of accuracies for each possible number
		for(Integer number = 0; number<=9; number++) {
			if (verbose) System.out.println("\nGoing to guess:"+number.toString());
			double acc = 0;
			HashMap<String, Double> result = curBrain.ask(tellFileName(number), verbose);
			switch(howEval) {
				case simple: acc+=evaluateSimpleAccuracy(result, number.toString());break;
				case relative: acc+=evaluateRelativeAccuracy(result, number.toString());break;
				case firstmatch: acc+=evaluateFMAccuracy(result, number.toString());break;
			}
			if(verbose) System.out.print("\nacc for "+number.toString()+":"+Double.toString(acc));
			forall+=acc;
		}
		return forall;
	}

	/*
	// used for testing accuracy evalution methods
    static void testAccuracy() {
		HashMap<String, Double> res = new HashMap<String, Double>();
		res.put("1", 0.89);
		res.put("2", 0.79);
		res.put("3", 0.40);
		res.put("4", 0.30);
		res.put("5", 0.20);
		res.put("6", 0.10);
		System.out.println("simple: "+evaluateSimpleAccuracy(res, "6").toString());
		System.out.println("fm: "+evaluateFMAccuracy(res, "6").toString());
		System.out.println("rel: "+evaluateRelativeAccuracy(res,"6").toString());
	}
	*/

	// simple accuracy, just give the calculated probability for the right number
	public Double evaluateSimpleAccuracy(HashMap<String, Double> result, String wanted) {
		return result.get(wanted);
	}

	// relative accuracy taking into account also the probabilities of another
	// numbers. Returns the difference between the probability of the right number
	// and the probability of the highest not right number. Might me negative.
	// Idea behind: if I have a good brain, for a certain number it should give
	// back high probability, but for the others small one! More on the logic in readme.
	public Double evaluateRelativeAccuracy(HashMap<String, Double> result, String wanted) {
		ValueComparator bvc = new ValueComparator(result);
		TreeMap<String, Double> sortedResult = new TreeMap<String, Double>(bvc);
		sortedResult.putAll(result);
		Entry<String, Double> properHit = sortedResult.ceilingEntry(wanted);
		Entry<String, Double> bestWrongHit;
		if(verbose) System.out.println("\nrelative accuracy for: "+wanted);
		if(verbose) System.out.println("proper key value "+properHit.getKey()+" "+properHit.getValue().toString());
		if(verbose) System.out.println("first key value "+sortedResult.firstEntry().getKey()+" "+sortedResult.firstEntry().getValue().toString());
		if (properHit.getKey() == sortedResult.firstEntry().getKey()) {
			bestWrongHit = sortedResult.higherEntry(wanted);
		//	System.out.println("lower key value "+bestWrongHit.getKey()+" "+bestWrongHit.getValue().toString());
		}
		else bestWrongHit = sortedResult.firstEntry();
		if(verbose) System.out.println("best wrong key value "+bestWrongHit.getKey()+" "+bestWrongHit.getValue().toString());
		return properHit.getValue() - bestWrongHit.getValue();
	}

	// first match accuracy, 1 if the right number has highest probability, else 0
	public Double evaluateFMAccuracy(HashMap<String, Double> result, String wanted) {
		ValueComparator bvc = new ValueComparator(result);
		TreeMap<String, Double> sortedResult = new TreeMap<String, Double>(bvc);
		sortedResult.putAll(result);
		Entry<String, Double> besthit = sortedResult.firstEntry();
		if(verbose) System.out.println("\nFMacc: best "+besthit.getKey()+" wanted"+wanted);
		if (besthit.getKey().compareTo(wanted)==0) return 1.0;
		return 0.0;
	}

	// get a file path for a number, must be in Evalset folder, ending with _1, format png
	private static String tellFileName(Integer num){
		return "Evalset/"+num.toString()+"_1.png";
	}

	// constructor
	public NeuroAnalytics(String loadpath, boolean verbose) {
		this.loadPath = loadpath;
		this.verbose = verbose;
	}

	// create a new Brain and train it based on testset on loadpath(is hardcoded to 'Trainset' now.
	private Brain trainAndReady(Integer layercnt, Integer hidden, double learnrate) throws Exception {
		//** System.out.println("Create a new brain and train it ...");
		//** System.out.println("Having "+layercnt+" layers with "+hidden+" neurons each");
		BrainFactory factory = new BrainFactory();
		int[] layers = new int[layercnt];
		for (int l=0; l<layercnt;l++) layers[l] = hidden;
		factory.setHiddenLayers(layers);
		factory.setMBP_learnRate(learnrate);
		// TODO: specify set size
		//System.out.println("debug: going to train with setsize"+setSize);
		return factory.createFromTrainSet(loadPath, setSize, verbose);
	}

	// used to be able to sort results of the ask function.
	static class ValueComparator implements Comparator<String> {

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
