package dmt.controller;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import dmt.model.Column;
import dmt.model.Table;
import dmt.view.DialogKeepColumns;

public class DialogKeepColumnsController extends DialogKeepColumns{
	
	private Table table;
	private List<Column> excludedColumns = new LinkedList<>();
	
	public DialogKeepColumnsController(Shell parent, Table table) {
		super(parent);
		this.table = table;
		initialize();
	}

	private void initialize() {
		table.getColumns().forEach(c->{
			TableItem item = new TableItem(tblColumns, SWT.CHECK);
			item.setChecked(true);
			item.setText(c.getName());
		});
	}

	protected void dobtnRemovewidgetSelected(SelectionEvent e) {
		for (int i = 0; i < tblColumns.getItemCount(); i++) {
			TableItem item = tblColumns.getItem(i);
			if (!item.getChecked())
				excludedColumns.add(table.getColumn(i));
		}
		result = excludedColumns;
		shell.close();
	}
	
}
