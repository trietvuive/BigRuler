
public class Runner {
	public static void main(String[] args) {
		StatCalc myStatCalc = new StatCalc();
		double[] willenter = new double[] {5,7,12,23,3,2,8,14,10,5,9,13};
		for(double i:willenter) {
			myStatCalc.enter(i);
		}
		System.out.println("Count: "+myStatCalc.getCount());
		System.out.println("Mean: "+myStatCalc.getMean());
		System.out.println("Standard Deviation :"+myStatCalc.getStandardDeviation());
	}
}
