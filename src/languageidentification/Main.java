package languageidentification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import languageidentification.classifier.Classifier;
import languageidentification.classifier.MarkovModel;
import languageidentification.classifier.NaiveBayes;
import languageidentification.classifier.NgramModel;
import languageidentification.classifier.Sample;
import languageidentification.util.Constants;

public class Main {

	public static void main(String[] args) throws IOException {
		int count = 0;
		FileInputStream fstream = new FileInputStream(Constants.DATA_SET_PATH + Constants.DATASET_FILENAME);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream, Constants.ENCODING));
		String sentence;
		List<Sample> trainingSet = new ArrayList<Sample>();
		List<Sample> testSet = new ArrayList<Sample>();

		while ((sentence = br.readLine()) != null) {
			String[] data = sentence.split("\t");

			if (data[1].equals("cz") || data[1].equals("sk")) {
				if (count % 4 == 0) {
					testSet.add(new Sample(data[1], data[0]));
				} else {
					trainingSet.add(new Sample(data[1], data[0]));
				}
			}

			count++;
		}

		Classifier naiveBayes = new NaiveBayes(trainingSet);
		int naiveBayesCorrectCount = 0;

		Classifier ngramModel = new NgramModel(3, trainingSet);
		int ngramModelCorrectCount = 0;

		Classifier markovModel = new MarkovModel(3, trainingSet);
		int markovModelCorrectCount = 0;

		for (Sample testSample : testSet) {
			String naiveBayesResult = naiveBayes.classify(testSample.getPhrase());

			if (naiveBayesResult.equals(testSample.getLanguage())) {
				naiveBayesCorrectCount++;
			}

			String ngramModelResult = ngramModel.classify(testSample.getPhrase());

			if (ngramModelResult.equals(testSample.getLanguage())) {
				ngramModelCorrectCount++;
			}

			String markovModelResult = markovModel.classify(testSample.getPhrase());

			if (markovModelResult.equals(testSample.getLanguage())) {
				markovModelCorrectCount++;
			}
		}

		System.out.println(100 * naiveBayesCorrectCount / (double) testSet.size());
		System.out.println(100 * ngramModelCorrectCount / (double) testSet.size());
		System.out.println(100 * markovModelCorrectCount / (double) testSet.size());
	}
}
