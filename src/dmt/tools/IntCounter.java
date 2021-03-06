package dmt.tools;

public class IntCounter {

	private int value = 0;

	public IntCounter() {
	}
	public IntCounter(int startValue) {
		value = startValue;
	}

	public int inc(){
		return ++value;
	}
	public int inc(int amount){
		value += amount;
		return value;
	}

	public int dec(){
		return --value;
	}

	public int dec(int amount){
		value -= amount;
		return value;
	}

	public int getValue(){
		return value;
	}

	public void setValue(int value){
		this.value = value;
	}


}
