package languageidentification.classifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import languageidentification.util.Constants;

public class MarkovModel implements Classifier {

	public MarkovModel(final int order, final List<Sample> trainingSet) {
		this.order = order;
		languages = new HashSet<String>();
		prefixOccuranceCount = new HashMap<String, Integer>();
		ngramOccuranceCount = new HashMap<String, Integer>();

		for (Sample trainingSample : trainingSet) {
			String language = trainingSample.getLanguage();
			String phrase = " " + trainingSample.getPhrase().replaceAll("[,.!?:;\"()]", "").toLowerCase() + " ";
			int phraseLength = phrase.length();

			languages.add(language);

			for (int i = order; i <= phraseLength; i++) {
				String prefix = phrase.substring(i - order, i - 1);
				String ngram = phrase.substring(i - order, i);

				prefixOccuranceCount.put(language + ":" + prefix,
						prefixOccuranceCount.getOrDefault(language + ":" + prefix, 0) + 1);
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

		for (String language : languages) {
			double logProbability = 0;

			for (int i = order; i <= phraseLength; i++) {
				String prefix = phraseLowerCase.substring(i - order, i - 1);
				String ngram = phraseLowerCase.substring(i - order, i);

				double conditionalFrequency = ngramOccuranceCount.getOrDefault(language + ":" + ngram, 0)
						/ (double) prefixOccuranceCount.getOrDefault(language + ":" + prefix, 0);

				logProbability += Math
						.log10(conditionalFrequency == 0 ? Constants.DEFAULT_PROBABILITY : conditionalFrequency);
			}

			if (maxLogProbabilitySum < logProbability) {
				maxLogProbabilitySum = logProbability;
				bestLanguage = language;
			}
		}

		return bestLanguage;
	}

	private int order;

	private Set<String> languages;

	private Map<String, Integer> prefixOccuranceCount;

	private Map<String, Integer> ngramOccuranceCount;
}
