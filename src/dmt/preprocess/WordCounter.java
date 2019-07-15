package dmt.preprocess;

/**
 * Word couter used in Cluster
 */
public class WordCounter {
	
	private String value;
	private int count;

	public WordCounter() {
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}