package dmt.controller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;

import dmt.model.Column;
import dmt.model.data.TableData;
import dmt.model.project.DataList;
import dmt.normalization.DataDivision;
import dmt.normalization.NormUtil;
import dmt.normalization.DataReplication;
import dmt.tools.Options;
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
		NormUtil normalization = new NormUtil(data);
		List<Column> columns = normalization.findMultiValuedColumns(seps);
		if (columns.size() > 0){
			txtNewTableName.setText(Util.toSingular(columns.get(0).getName()));
		}
		refresh();
	}

	protected void check(){
		if (seps == null)
			seps = Options.getDefaultSeparators();
		NormUtil normalization = new NormUtil(data);
		columns = normalization.findMultiValuedColumns(seps);
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
		Column selColumn = columns.get(index);
		List<Column> excludedColumns = new ArrayList<>();
		if (checkSingle.getSelection()){
			int[] indices = lstColumn.getSelectionIndices();
			List<Column> selColumns = new ArrayList<>();
			for (int i = 0; i < indices.length; i++) {
				selColumns.add(columns.get(i));
				excludedColumns.add(columns.get(i));
			}
			DataReplication dr = new DataReplication(data);
			TableData nd = dr.splitColumns(seps, selColumns);
			modelEditor2.addTable(nd.getTable());
			res = new DataList(nd);
		}else if (chkMulti.getSelection()){
			DataDivision dd = new DataDivision(data);
			List<TableData> list = dd.splitColumnToList(seps, selColumn, txtNewTableName.getText());
			DataList dl = new DataList();
			list.forEach(d->{
				modelEditor2.addTable(d.getTable());
				dl.add(d);
			});
			res = dl;
			excludedColumns.add(selColumn);
		}

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
		int[] i = lstColumn.getSelectionIndices();
		int index = lstColumn.getSelectionIndex();
		if (i.length > 0){
			if (chkMulti.getSelection() && i.length > 1){
				lstColumn.deselectAll();
				lstColumn.setSelection(index);
				Column column = columns.get(index);
				txtNewTableName.setText(column.getName());
			}else{
				txtNewTableName.setText("");
			}

			refresh();
		}
	}

}
