package dmt.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import dmt.model.Column;
import dmt.model.data.TableData;
import dmt.normalization.Normalize;
import dmt.tools.IntCounter;
import dmt.view.DialogMix;

public class DialogMixController extends DialogMix {

	private TableData data = null;
	private List<Column> columns;

	public DialogMixController(Shell parent, TableData data) {
		super(parent);
		this.data = data;
		initialize();
	}

	private void initialize(){
		columns = data.getTable().getColumns().stream().filter(c->c.getType().equals(String.class)).collect(Collectors.toList());
		columns.forEach(c->{
			combo1.add(c.getName());
		});
	}

	protected void docombo1widgetSelected(SelectionEvent e) {
		if (combo2.getSelectionIndex() < 0 || combo2.getText().compareTo(combo1.getText()) == 0){
			combo2.removeAll();
			columns.stream()
			.filter(c->c.getName().compareTo(combo1.getText()) != 0)
			.forEach(c->{
				combo2.add(c.getName());
			});
		}
		refresh();
	}
	protected void docombo2widgetSelected(SelectionEvent e) {
		if (combo1.getSelectionIndex() >= 0 && combo2.getSelectionIndex() >= 0 && txtColumnName.getText().trim().length() == 0){
			txtColumnName.setText(combo1.getText().concat(combo2.getText()));
		}
		refresh();
	}
	protected void dotxtDivmodifyText(ModifyEvent arg0) {
		refresh();
	}
	protected void dotxtColumnNamemodifyText(ModifyEvent arg0) {
		refresh();
	}

	private void refresh(){
		table.removeAll();
		if (combo1.getSelectionIndex() < 0 || combo2.getSelectionIndex() < 0)
			return;
		List<Object> v1 = data.getColumnValues(combo1.getText());
		List<Object> v2 = data.getColumnValues(combo2.getText());
		IntCounter ic = new IntCounter();
		while (ic.getValue() < v1.size()){
			StringBuilder builder = new StringBuilder();
			TableItem item = new TableItem(table, SWT.NONE);
			Object o1 = v1.get(ic.getValue());
			if (o1 != null){
				builder.append(o1.toString());
				item.setText(0, o1.toString());
			}
			builder.append(txtDiv.getText());
			Object o2 = v2.get(ic.getValue());
			if (o2 != null){
				builder.append(o2.toString());
				item.setText(1, o2.toString());
			}
			item.setText(2, builder.toString());
			ic.inc();
		}
		btnConfirm.setEnabled(table.getItemCount() > 0);
	}

	protected void dobtnConfirmwidgetSelected(SelectionEvent e) {
		TableData newData = Normalize.mixColumns(data, combo1.getText(), combo2.getText(),
				txtColumnName.getText(), txtDiv.getText(), btnKeepOriginalColumns.getSelection());
		if (newData != null){
			result = newData;
			shell.close();
		}
	}


}
