package dmt.controller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;

import dmt.model.Column;
import dmt.model.data.TableData;
import dmt.model.project.DataList;
import dmt.normalization.Normalize;
import dmt.tools.Util;
import dmt.view.Dialog1FN;

public class Dialog1FNController extends Dialog1FN {

	private TableData data = null;
	private char[] seps = null;
	private DataList res = null;
	private List<Column> columns = new ArrayList<Column>();

	public Dialog1FNController(Shell parent, TableData data, char[] seps) {
		super(parent);
		this.data = data;
		this.seps = seps;
		if (seps != null)
			setSeparators(seps);
		initialize();
	}

	private void initialize() {
		modelEditor1.addTable(data.getTable());
		modelEditor1.calcPositions();
		check();
		refresh();
	}

	protected void docheckSinglewidgetSelected(SelectionEvent e) {
		txtNewTableName.setText("");
		txtNewTableName.setEnabled(false);
		refresh();
	}
	protected void dochkMultiwidgetSelected(SelectionEvent e) {
		txtNewTableName.setEnabled(true);
		List<Column> columns = Normalize.findMultiValuedColumns(data, seps);
		if (columns.size() > 0){
			txtNewTableName.setText(Util.toSingular(columns.get(0).getName()));
		}
		refresh();
	}
	
	protected void check(){
		columns = Normalize.findMultiValuedColumns(data, seps);
		lstColumn.removeAll();
		columns.forEach(c->{
			lstColumn.add(c.getName());
		});
	}

	protected void refresh(){
		seps = getSeparators();
		modelEditor2.clear();
		int index = lstColumn.getSelectionIndex();
		if (index < 0){
			modelEditor2.calcPositions();
			modelEditor2.draw();
			return;
		}
		Column column = columns.get(index);
		if (checkSingle.getSelection()){
			TableData nd = Normalize.splitColumn(data, seps, column);
			modelEditor2.addTable(nd.getTable());
			res = new DataList(nd);
		}else if (chkMulti.getSelection()){
			List<TableData> list = Normalize.splitColumnToList(data, seps, column, txtNewTableName.getText());
			DataList dl = new DataList();
			list.forEach(d->{
				modelEditor2.addTable(d.getTable());
				dl.add(d);
			});
			res = dl;
		}
		List<Column> excludedColumns = new ArrayList<>();
		excludedColumns.add(column);
		modelEditor1.getFirstModel().setExcludedColumns(excludedColumns);
		modelEditor1.draw();
		modelEditor2.calcPositions();
		modelEditor2.draw();
	}

	protected void dobtnConfirmwidgetSelected(SelectionEvent e) {
		if (res != null)
			result = res;
		shell.close();
	}

	protected void dolstColumnwidgetSelected(SelectionEvent e) {
		int index = lstColumn.getSelectionIndex();
		if (index >= 0){
			Column column = columns.get(index);
			txtNewTableName.setText(column.getName());
			refresh();
		}
	}

}
