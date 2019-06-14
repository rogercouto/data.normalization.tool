package dmt.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import dmt.database.DataTypes;
import dmt.model.Column;
import dmt.model.Table;
import dmt.model.data.TableData;
import dmt.normalization.Normalize;
import dmt.view.DialogModifyTable;

public class DialogModifyTableController extends DialogModifyTable{

	private static final List<Class<?>> TYPES = DataTypes.getAllTypes();

	private TableData data;
	private List<Text> nameTexts = new ArrayList<>();
	private List<CCombo> typeCombos = new ArrayList<>();
	private List<Button> remChecks = new ArrayList<>();

	public DialogModifyTableController(Shell parent, TableData data) {
		super(parent);
		this.data = data;
		initTable();
	}

	private void initTable() {
		txtName.setText(data.getTable().getName());
		TableColumn tCol = new TableColumn(tblModifier, SWT.NONE);
		tCol.setText("Column name");
		TableColumn tColType = new TableColumn(tblModifier, SWT.NONE);
		tColType.setText("Type");
		tColType.setWidth(100);
		TableColumn tColRem = new TableColumn(tblModifier, SWT.NONE);
		tColRem.setText("Remove");
		//tColRem.setAlignment(SWT.CENTER);
		tColRem.setWidth(75);
		Table table = data.getTable();
		OptionalInt oi = table.getColumns().stream().mapToInt(c -> c.getNameWidth()).max();
		if (oi.isPresent())
			tCol.setWidth(oi.getAsInt()+60);
		else
			tCol.pack();
		table.getColumns().forEach(c->{
			TableItem item = new TableItem(tblModifier, SWT.NONE);
			//item.setText(c.getName());
			TableEditor editor0 = new TableEditor(tblModifier);
			Text text = new Text(tblModifier, SWT.BORDER);
			text.setText(c.getName());
			nameTexts.add(text);
			editor0.grabHorizontal = true;
			editor0.setEditor(text, item, 0);
			//Type combo
			Class<?> bm = Normalize.getBestMatchType(data, c.getName());
			TableEditor editor1 = new TableEditor(tblModifier);
			CCombo combo = new CCombo(tblModifier, SWT.READ_ONLY);
			combo.setText(c.getType().getSimpleName());
			combo.add("String");
			if (!bm.equals(String.class))
				combo.add(bm.getSimpleName());
			if (bm.equals(Integer.class)){
				combo.add(Long.class.getSimpleName());
				combo.add(Number.class.getSimpleName());
			}else if (bm.equals(Float.class)){
				combo.add(Double.class.getSimpleName());
				combo.add(Number.class.getSimpleName());
			}
			typeCombos.add(combo);
			editor1.grabHorizontal = true;
			editor1.setEditor(combo, item, 1);
			//Check remove
			TableEditor editor2 = new TableEditor(tblModifier);
			Button check = new Button(tblModifier, SWT.CHECK);
			remChecks.add(check);
			editor2.grabHorizontal = true;
			editor2.setEditor(check, item, 2);
		});
	}

	protected void dobtnOkwidgetSelected(SelectionEvent e) {
		HashMap<String, Class<?>> map = new HashMap<>();
		data.getTable().getColumns().forEach(c->{
			int index = data.getTable().getElementIndex(c.getName());
			String typeName = typeCombos.get(index).getText();
			Optional<Class<?>> type = TYPES.stream().filter(t->t.getSimpleName().compareTo(typeName)==0).findFirst();
			if (type.isPresent()){
				map.put(c.getName(), type.get());
			}
		});
		Normalize.changeTypes(data, map);
		if (data.getTable().getName().compareTo(txtName.getText()) != 0 && txtName.getText().trim().length() > 0){
			data.getTable().setName(txtName.getText());
		}
		List<Column> remColumns = new ArrayList<>();
		for (int i = 0; i < remChecks.size(); i++) {
			if (remChecks.get(i).getSelection())
				remColumns.add(data.getTable().getColumn(i));
		}
		if (remColumns.size() > 0)
			data = Normalize.removeColumns(data, remColumns);
		result = data;
		shell.close();
	}

}
