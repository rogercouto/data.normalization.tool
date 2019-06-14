package dmt.controller;

import org.eclipse.swt.widgets.Shell;

import dmt.input.CSVThreadReader;
import dmt.view.DialogCSVLoading;

public class DialogCSVLoaderController extends DialogCSVLoading{

	private String fileName;

	public DialogCSVLoaderController(Shell parent, String fileName) {
		super(parent);
		this.fileName = fileName;
		setMessage("Load CSV");
		txtSeparator.setTextLimit(1);
		txtDelimiter.setTextLimit(1);
	}

	@Override
	protected void doTask(){
		if (txtDelimiter.getText().trim().length() == 1 && txtSeparator.getText().trim().length() == 1){
			char separator = txtSeparator.getText().trim().charAt(0);
			char delimiter = txtDelimiter.getText().trim().charAt(0);
			CSVThreadReader reader = new CSVThreadReader(progressBar, fileName, separator, delimiter);
			reader.run();
			result = reader.getSample();
			shell.close();
		}
	}
	
}
