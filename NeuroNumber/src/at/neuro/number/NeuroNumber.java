package at.neuro.number;

public class NeuroNumber {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("new Brain");
		Brain b = new Brain();
		System.out.println("train");
		b.train();
		System.out.println("ask");
		double answer = b.ask(1,1);
		System.out.println(answer);
	}
}
