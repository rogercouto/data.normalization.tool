package dmt.controller;

import org.eclipse.swt.widgets.Shell;

import dmt.database.Server;
import dmt.input.DatabaseThreadReader;
import dmt.view.DialogLoading;

public class DialogServerLoadingController extends DialogLoading{

	private Server server;
	
	public DialogServerLoadingController(Shell parent, Server server) {
		super(parent);
		this.server = server;
	}

	@Override
	protected void doTask(){
		DatabaseThreadReader reader = new DatabaseThreadReader(progressBar, server);
		reader.run();
		result = reader.getDataList();
		shell.close();
	}
	
}
