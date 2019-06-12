package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import classifier.NormalizedClassifier;

public class Main {

	static int maxCompleteness = 0;
	public static List<List<String>> records = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		
		List<String> classes = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\NathanGrant\\eclipse-workspace\\NormalizedDistanceClassifier\\src\\main\\housing.csv"))) {
		    String line;
		    int i =0;
		    while ((line = br.readLine()) != null) {
		        String[] values = line.split(",", 10);
		        records.add(Arrays.asList(values));
		        
		        if (!classes.contains(values[9]))
		        	classes.add(values[9]);
		    }
		}
		
		classes.remove(0);
		records.remove(0);
		System.out.println(classes);
		double accuracy = 0;
		int epochs;
		for (epochs = 0; epochs < 45; epochs++) {
			Collections.shuffle(records);		// Re-shuffle data
			
			ArrayList<double[]> data = transformData(classes);		// Transform data into a usable form
			
			// Make training Set and Test Set
			int trainIndex = (int) (data.size()*.80);	// Specify size of training set
			
			NormalizedClassifier classifier = new NormalizedClassifier(5);	// Make a new classifier
			
			// Add all data into classififier from training data
			for (int i = 0; i < trainIndex; i++) {
				classifier.addDataPoint(data.get(i), classes.indexOf(records.get(i).get(9)));
			}
			
			// Test on rest of data
			accuracy += test(classifier, data.subList(trainIndex+1, data.size()-1), classes, trainIndex+1);
			completeness(epochs, 45);
		}
		
		System.out.println("Accuracy: " + new DecimalFormat("##.###").format(accuracy/epochs)+ "%");
	}
	
	private static double test(NormalizedClassifier classifier, List<double[]> subList, List<String> classes, int index) {
		int total = 0;
		int correct = 0;
	
		int guess;
		int actual;
		for (int i = 0; i < subList.size(); i ++) {
			guess = classifier.predict(subList.get(i));
			actual = classes.indexOf(records.get(index+i).get(9));
			total++;
			if (guess == actual) {
				correct++;
			}
			classifier.addDataPoint(subList.get(i), actual);
		}
		//System.out.println("Accuracy: " + new DecimalFormat("##.###").format((double) correct/total * 100)+ "%"); 
		return (double) correct/total * 100;
	}

	public static ArrayList<double[]> transformData(List<String> classes) {
		ArrayList<String> classList = new ArrayList<>();
		ArrayList<double[]> newList = new ArrayList<>();
		int faultyData = 0;
		int counter = 0;
		
		// Iterate over all of the data
		for (int i = 0; i < records.size(); i++) {
			List<String> temp = records.get(i);
			double[] data = new double[temp.size()-1];
			// Convert all values to doubles
			for (int j = 0; j < temp.size()-1; j++) {
				try {
					data[j] = Double.valueOf(temp.get(j));
				} catch(NumberFormatException e) {
					faultyData = 1;
					records.remove(temp);
					break;
				}
			}
			classList.add(temp.get(9));
			
			// If data was null
			if (faultyData == 1) {
				faultyData = 0;
				counter++;
				continue;
			}
			newList.add(data);
		}
		return newList;
	}
	
	public static void completeness(int done, int total) {
		if (done  == 0) {
			System.out.print("<");
		} else if (done == total-1) {
			System.out.println(">");
		}
		else {
			if ((int) (100* ((double) done/total)) / 10 > maxCompleteness) {
				System.out.print("=");
				maxCompleteness++;
			}
		}
	}
}
