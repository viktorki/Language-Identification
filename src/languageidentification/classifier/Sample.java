package languageidentification.classifier;

public class Sample {

	public Sample(String language, String phrase) {
		this.language = language;
		this.phrase = phrase;
	}

	private String language;

	private String phrase;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}
}
