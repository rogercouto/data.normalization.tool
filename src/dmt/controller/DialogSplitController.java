package dmt.controller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import dmt.model.Column;
import dmt.model.data.TableData;
import dmt.preprocess.Preprocess;
import dmt.view.DialogSplit;

public class DialogSplitController extends DialogSplit {

	private TableData data = null;
	private List<Column> columns;
	private List<Object> values = new ArrayList<>();
	private List<String> c1Values = new ArrayList<>();
	private List<String> c2Values = new ArrayList<>();

	public DialogSplitController(Shell parent, TableData data) {
		super(parent);
		this.data = data;
		initialize();
	}

	private void initialize(){
		columns = data.getTable().getColumns();
		columns.stream().filter(c->c.getType().equals(String.class)).forEach(c->{
			this.cmbColumn.add(c.getName());
		});
	}

	protected void refresh(){
		super.refresh();
		table.removeAll();
		values = new ArrayList<>();
		c1Values = new ArrayList<>();
		c2Values = new ArrayList<>();
		int index = cmbColumn.getSelectionIndex();
		if (index >= 0){
			String delimiter = txtDelimiter.getText();
			if (delimiter.length() > 0){
				String cn1 = txtFirstColumn.getText().trim();
				String cn2 = txtSecondColumn.getText().trim();
				if (cn1.length() > 0 && cn2.length() > 0){
					table.getColumn(1).setText(cn1);
					table.getColumn(2).setText(cn2);
					values = data.getColumnValues(cmbColumn.getText());
					values.forEach(value->{
						TableItem item = new TableItem(table, SWT.NONE);
						item.setText(0, value.toString());
						String[] split = value.toString().split(String.format("[%s]", delimiter));
						if (split.length > 0){
							c1Values.add(split[0]);
							item.setText(1, split[0]);
						}
						StringBuilder builder = new StringBuilder();
						for (int i = 1; i < split.length; i++) {
							builder.append(split[i]);
						}
						if (split.length > 1){
							c2Values.add(builder.toString());
							item.setText(2, builder.toString());
						}else{
							c2Values.add(null);
						}
					});
					btnConfirm.setEnabled(true);
				}else{
					btnConfirm.setEnabled(false);
				}
			}
			TableColumn[] tcs = table.getColumns();
			for (TableColumn tableColumn : tcs) {
				tableColumn.pack();
			}
		}
	}

	protected void dobtnConfirmwidgetSelected(SelectionEvent e) {
		if (values.size() == c1Values.size() && values.size() == c2Values.size()){
			Preprocess preprocess = new Preprocess(data);
			result = preprocess.splitColumn(cmbColumn.getText(), txtFirstColumn.getText(), txtSecondColumn.getText(), c1Values, c2Values);
			shell.close();
		}
	}

}
