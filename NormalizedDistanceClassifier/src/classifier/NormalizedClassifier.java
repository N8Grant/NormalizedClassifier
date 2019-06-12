package classifier;

import java.util.ArrayList;

public class NormalizedClassifier {
	private double[][] averageMatrix;
	private int numClasses;
	private double classCounter[];
	private ArrayList<Integer> classVector;	
	
	public NormalizedClassifier(int classes) {
		averageMatrix = new double[classes][];
		this.numClasses = classes;
		classCounter = new double[classes];
		classVector = new ArrayList<>();
	}
	
	public void addDataPoint(double[] data, int numClass) {

		if (classCounter[numClass] == 0) {
			averageMatrix[numClass] = data;
			classVector.add(numClass);
			classCounter[numClass]++;
			return;
		}
		classVector.add(numClass);
		classCounter[numClass]++;
		
		for (int i = 0; i < averageMatrix[numClass].length; i++) {
			double avg = averageMatrix[numClass][i];
			averageMatrix[numClass][i] = ((classCounter[numClass]-1)*avg+data[i])/classCounter[numClass];
		}
	}
	
	public int predict(double[] testPoint) {
		double[][] percentMatrix = new double[averageMatrix.length][averageMatrix[0].length];
		
		for (int i = 0; i < averageMatrix.length; i++) {
			for (int j = 0; j < testPoint.length; j++) {
				percentMatrix[i][j] = (100*(testPoint[j]-averageMatrix[i][j]))/testPoint[j];
			}
		}
		
		double outputVector[] = new double[numClasses];
		
		for (int i = 0; i < numClasses; i ++) {
			outputVector[i] = calculateDistance(percentMatrix[i]);
		}
		
		double min = Double.MAX_VALUE; 
		int prediction = 0;
		
		// Find lowest average
		for (int i = 0; i < outputVector.length; i++) {
			if (outputVector[i] < min) {
				min = outputVector[i];
				prediction = i;
			}
		}
		
		return prediction;
	}
	
	private double calculateDistance(double[] first) {
		double sum = 0;
		for (int i = 0; i < first.length; i++) {
			sum += Math.pow(first[i], 2);
		}
		
		return Math.sqrt(sum);
	}
}
