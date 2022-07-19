package mattias.hemtest;

/**
 * A simple pair class where you can set and get the pairs data individually
 * 
 * @author Mattias Viggeborn
 * @version 1.0
 * @since	2022-07-19
 * */

public class MyWordPair {

	private String word;
	private int occurrence;
	
	MyWordPair(String word, int occurrence){
		this.word=word;
		this.occurrence=occurrence;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getOccurrence() {
		return occurrence;
	}

	public void setOccurrence(int occurrence) {
		this.occurrence = occurrence;
	}
}
