package languageidentification.classifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NaiveBayes implements Classifier {

	public NaiveBayes(final List<Sample> trainingSet) {
		trainingSetSize = trainingSet.size();
		dictionary = new HashSet<String>();
		sampleCountByLanguage = new HashMap<String, Integer>();
		wordCountByLanguage = new HashMap<String, Integer>();
		occuranceCountInLanguage = new HashMap<String, Integer>();

		for (Sample trainingSample : trainingSet) {
			String language = trainingSample.getLanguage();
			String sentence = trainingSample.getPhrase();
			String[] words = sentence.split("[ ,.!?:;\"()]");

			for (String word : words) {
				word = word.toLowerCase();

				dictionary.add(word);
				occuranceCountInLanguage.put(language + ":" + word,
						occuranceCountInLanguage.getOrDefault(language + ":" + word, 0) + 1);
			}

			sampleCountByLanguage.put(language, sampleCountByLanguage.getOrDefault(language, 0) + 1);
			wordCountByLanguage.put(language, wordCountByLanguage.getOrDefault(language, 0) + words.length);
		}
	}

	@Override
	public String classify(final String phrase) {
		String[] words = phrase.split("[ ,.!?:;\"()]");
		Set<String> knownWords = new HashSet<String>();
		double maxProbability = 0;
		String bestLanguage = "";

		for (String word : words) {
			word = word.toLowerCase();

			if (dictionary.contains(word)) {
				knownWords.add(word);
			}
		}

		for (Map.Entry<String, Integer> sampleCount : sampleCountByLanguage.entrySet()) {
			int dictionarySize = dictionary.size();
			String language = sampleCount.getKey();
			int wordCountByCurrentLanguage = wordCountByLanguage.get(language);
			double probability = sampleCount.getValue() / (double) trainingSetSize;

			for (String word : knownWords) {
				probability *= (occuranceCountInLanguage.getOrDefault(language + ":" + word, 0) + 1)
						/ (double) (wordCountByCurrentLanguage + dictionarySize);
			}

			if (maxProbability < probability) {
				maxProbability = probability;
				bestLanguage = language;
			}
		}

		return bestLanguage;
	}

	private int trainingSetSize;

	private Set<String> dictionary;

	private Map<String, Integer> sampleCountByLanguage;

	private Map<String, Integer> wordCountByLanguage;

	private Map<String, Integer> occuranceCountInLanguage;
}
