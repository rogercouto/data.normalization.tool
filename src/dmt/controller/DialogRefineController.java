package dmt.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import dmt.model.Column;
import dmt.model.data.RowData;
import dmt.model.data.TableData;
import dmt.normalization.Preprocess;
import dmt.view.DialogRefine;

public class DialogRefineController extends DialogRefine {

	private TableData data;
	private List<Column> columns;
	private List<Object> newValues;
	
	public DialogRefineController(Shell parent, TableData data) {
		super(parent);
		this.data = data;
		initialzie();
	}
	
	private void initialzie(){
		columns = data.getTable()
				.getColumns()
				.stream()
				.filter(c->c.getType().equals(String.class))
				.collect(Collectors.toList());
		columns.forEach(c->cmbColumn.add(c.getName()));
	}
	
	protected void refresh(){
		super.refresh();
		tblValue.removeAll();
		btnConfirm.setEnabled(false);
		if (cmbColumn.getSelectionIndex() >= 0){
			String columnName = cmbColumn.getText();
			List<Object> values = data.getColumnValues(columnName);
			newValues = new LinkedList<>(); 
			values.stream()
				.filter(v->v!=null)
				.forEach(v->{
					TableItem item = new TableItem(tblValue, SWT.NONE);
					item.setText(0, v.toString().replace(' ', '_'));
					String newValue = chkCleanSpaces.getSelection() ? v.toString().trim() : v.toString();
					if (rdUpper.getSelection()){
						newValue = newValue.toUpperCase();
					}else if (rdLower.getSelection()){
						newValue = newValue.toLowerCase();
					}else if (rdUpperFirst.getSelection()){
						boolean iss = chkIgnoreShort.getSelection();
						newValue = Preprocess.upperFirst(newValue, iss);
					}
					newValues.add(newValue);
					item.setText(1, newValue);
				});
			if (rdUpper.getSelection() || rdLower.getSelection() || rdUpperFirst.getSelection() || chkCleanSpaces.getSelection())
				btnConfirm.setEnabled(true);
		}
	}
	
	protected void dobtnConfirmwidgetSelected(SelectionEvent e) {
		if (newValues != null){
			String columnName = cmbColumn.getText();
			if (data.getRowCount() != newValues.size())
				System.err.println("Something wrong is not right!");
			Iterator<RowData> iR = data.getRows().iterator();
			Iterator<Object> iV = newValues.iterator();
			while (iV.hasNext()){
				Object value = iV.next();
				iR.next().setValue(columnName, value);
			}
		}
		/*
		tblValue.removeAll();
		cmbColumn.deselectAll();
		btnConfirm.setEnabled(false);
		*/
		shell.close();
	}

}
