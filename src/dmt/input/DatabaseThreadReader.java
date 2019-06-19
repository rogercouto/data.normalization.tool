package dmt.input;

import java.util.List;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;

import dmt.database.ReverseEng;
import dmt.database.Server;
import dmt.model.Table;
import dmt.model.data.TableData;
import dmt.model.project.DataList;

public class DatabaseThreadReader extends Thread {

	private Listener doneListener;
	private ProgressBar bar;
	private Server server;

	private DataList dataList;

	public DatabaseThreadReader(ProgressBar bar, Server server) {
		super();
		this.bar = bar;
		this.server = server;
	}

	public void setDoneListener(Listener doneListener) {
		this.doneListener = doneListener;
	}

	public void run(){
		ReverseEng revEng = new ReverseEng(server);
		List<Table> tables = revEng.getTables(bar);
		DataList dl = new DataList();
		tables.forEach(table->{
			TableData dt = new TableData(table);
			dl.add(dt);
		});
		dataList = dl;
		if (doneListener != null)
			doneListener.handleEvent(new Event());
	}

	public DataList getDataList(){
		return dataList;
	}

}
