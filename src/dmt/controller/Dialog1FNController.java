package dmt.controller;

import java.util.List;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import dmt.model.Column;
import dmt.model.data.TableData;
import dmt.model.project.DataList;
import dmt.normalization.Normalize;
import dmt.view.TableModel;
import dmt.view.Dialog1FN;

public class Dialog1FNController extends Dialog1FN {

	private TableData data = null;
	private char[] seps = null;
	private DataList res = null;
	
	public Dialog1FNController(Shell parent, TableData data, char[] seps) {
		super(parent);
		this.data = data;
		this.seps = seps;
		if (seps != null)
			setSeparators(seps);
		initialize();
	}

	private void initialize() {
		modelEditor1.addTableModel(new TableModel(data.getTable(), new Point(50,50)));
		refresh();
	}
	
	protected void docheckSinglewidgetSelected(SelectionEvent e) {
		refresh();
	}
	protected void dochkMultiwidgetSelected(SelectionEvent e) {
		refresh();
	}
	
	protected void refresh(){
		seps = getSeparators();
		modelEditor2.clear();
		List<Column> columns = Normalize.findMultiValuedColumns(data, seps);
		if (columns.size() == 0){
			modelEditor2.draw();
			return;
		}
		if (checkSingle.getSelection()){
			TableData nd = Normalize.splitColumns(data, seps);
			modelEditor2.addTableModel(new TableModel(nd.getTable(), new Point(50,50)));
			res = new DataList(nd);
		}else if (chkMulti.getSelection()){
			List<TableData> list = Normalize.splitColumnsToList(data, seps);
			DataList dl = new DataList();
			list.forEach(d->{
				modelEditor2.addTableModel(new TableModel(d.getTable(), new Point(50,50)));
				dl.add(d);
			});
			res = dl;
		}
		modelEditor2.draw();
	}

	protected void dobtnConfirmwidgetSelected(SelectionEvent e) {
		if (res != null)
			result = res;
		shell.close();
	}
}