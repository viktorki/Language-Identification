package languageidentification.classifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import languageidentification.util.Constants;

public class NgramModel implements Classifier {

	public NgramModel(final int n, final List<Sample> trainingSet) {
		this.n = n;
		ngramCountByLanguage = new HashMap<String, Integer>();
		ngramOccuranceCount = new HashMap<String, Integer>();

		for (Sample trainingSample : trainingSet) {
			String language = trainingSample.getLanguage();
			String phrase = " " + trainingSample.getPhrase().replaceAll("[,.!?:;\"()]", "").toLowerCase() + " ";
			int phraseLength = phrase.length();

			for (int i = n; i <= phraseLength; i++) {
				String ngram = phrase.substring(i - n, i);

				ngramCountByLanguage.put(language, ngramCountByLanguage.getOrDefault(language, 0) + 1);
				ngramOccuranceCount.put(language + ":" + ngram,
						ngramOccuranceCount.getOrDefault(language + ":" + ngram, 0) + 1);
			}
		}
	}

	@Override
	public String classify(final String phrase) {
		String phraseLowerCase = " " + phrase.replaceAll("[,.!?:;\"()]", "").toLowerCase() + " ";
		int phraseLength = phraseLowerCase.length();
		double maxLogProbabilitySum = -Constants.INFINITY;
		String bestLanguage = "";

		for (Map.Entry<String, Integer> ngramCount : ngramCountByLanguage.entrySet()) {
			double logProbability = 0;
			String language = ngramCount.getKey();
			int ngramCountForCurrentLanguage = ngramCount.getValue();

			for (int i = n; i <= phraseLength; i++) {
				String ngram = phraseLowerCase.substring(i - n, i);

				double ngramFrequency = ngramOccuranceCount.getOrDefault(language + ":" + ngram, 0)
						/ (double) ngramCountForCurrentLanguage;

				logProbability += Math.log10(ngramFrequency == 0 ? Constants.DEFAULT_PROBABILITY : ngramFrequency);
			}

			if (maxLogProbabilitySum < logProbability) {
				maxLogProbabilitySum = logProbability;
				bestLanguage = language;
			}
		}

		return bestLanguage;
	}

	private int n;

	private Map<String, Integer> ngramCountByLanguage;

	private Map<String, Integer> ngramOccuranceCount;
}
