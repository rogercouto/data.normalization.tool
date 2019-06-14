package dmt.tools;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class GenericThread extends Thread {

	private Listener execListener;
	private Listener doneListener;
	
	public void run(){
		if (execListener == null || doneListener == null)
			throw new RuntimeException("Thread actions not defined!");
		Event e = new Event();
		execListener.handleEvent(e);
		doneListener.handleEvent(e);
	}

	public Listener getExecListener() {
		return execListener;
	}

	public void setExecListener(Listener execListener) {
		this.execListener = execListener;
	}

	public Listener getDoneListener() {
		return doneListener;
	}

	public void setDoneListener(Listener doneListener) {
		this.doneListener = doneListener;
	}
	
}
