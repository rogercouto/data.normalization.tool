package dmt.controller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;

import dmt.input.MatrixReader;
import dmt.model.data.TableData;
import dmt.normalization.Normalize;
import dmt.tools.Options;
import dmt.view.CompImportCSV;

public class CompImportCSVController extends CompImportCSV{

	private String[][] matrix = null;

	private int page = 1;
	private double maxPages;
	private Listener importListener;

	public CompImportCSVController(Composite parent, int style) {
		super(parent, style);
	}

	public String getTableName() {
		return txtTableName.getText();
	}

	public void setTableName(String tableName) {
		txtTableName.setText(tableName);
	}

	public void setMatrix(String[][] matrix){
		this.matrix = matrix;
		if (matrix.length < Options.getPageSize()){
			maxPages = 1;
		}else{
			maxPages = (double)matrix.length / (double)Options.getPageSize();
			double ver = maxPages % 1;
			if (ver != 0){
				maxPages = ((int)maxPages)+1;
			}
		}
		spinner.setMaximum((int)maxPages);
		if (matrix == null || matrix.length == 0)
			return;
		for (int i = 0; i < matrix[0].length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.pack();
		}
		spinnerBegin.setMinimum(0);
		spinnerBegin.setMaximum(matrix.length-2);
		spinnerBegin.setSelection(1);
		spinnerEnd.setMinimum(1);
		spinnerEnd.setMaximum(matrix.length-1);
		spinnerEnd.setSelection(matrix.length-1);
		reload();
	}

	public Listener getImportListener() {
		return importListener;
	}

	public void setImportListener(Listener importListener) {
		this.importListener = importListener;
	}

	private List<Button> checks;

	public void reload(){
		if (matrix == null || matrix.length == 0)
			return;
		table.removeAll();
		int coef = page -1;
		int min = coef*Options.getPageSize();
		int max = (page < (int)maxPages)? min + (Options.getPageSize()-1) : matrix.length-1;
		for (int i = min; i < max; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			if (i == spinnerNames.getSelection()){
				item.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
				if (checks != null)
					checks.forEach(c->c.dispose());
				checks = new ArrayList<>();
				for (int j = 0; j < matrix[i].length; j++) {
					TableEditor editor = new TableEditor(table);
					Button check = new Button(table, SWT.CHECK);
					check.setText(matrix[i][j]);
					check.setSelection(true);
					editor.grabHorizontal = true;
					editor.setEditor(check, item, j);
					checks.add(check);
				}

			}
			if (!checkBox.getSelection() && i == spinnerDescriptions.getSelection())
				item.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
			for (int j = 0; j < matrix[i].length; j++) {
				if (i != spinnerNames.getSelection())
					item.setText(j, matrix[i][j]);
			}
		}
		for (TableColumn column : table.getColumns()) {
			column.pack();
			column.setWidth(column.getWidth()+20);
		}
	}

	protected void dospinnerwidgetSelected(SelectionEvent e) {
		page = spinner.getSelection();
		reload();
	}

	protected void dobtnFirstwidgetSelected(SelectionEvent e) {
		if (page > 1){
			page = 1;
			spinner.setSelection(page);
			reload();
		}
	}

	protected void dobtnPrevwidgetSelected(SelectionEvent e) {
		if (page > 1){
			page--;
			spinner.setSelection(page);
			reload();
		}
	}

	protected void dobtnNextwidgetSelected(SelectionEvent e) {
		if (page < (int)maxPages){
			page++;
			spinner.setSelection(page);
			reload();
		}
	}

	protected void dobtnLastwidgetSelected(SelectionEvent e) {
		if (page < (int)maxPages){
			page = (int)maxPages;
			spinner.setSelection(page);
			reload();
		}
	}

	protected void dospinnerNameswidgetSelected(SelectionEvent e) {
		reload();
	}

	protected void dospinnerDescriptionswidgetSelected(SelectionEvent e) {
		reload();
	}

	protected void dobtnCheckButtonwidgetSelected(SelectionEvent e) {
		super.dobtnCheckButtonwidgetSelected(e);
		reload();
	}

	private String[][] subMatrix(){
		int inCount = (int)checks.stream().filter(c->c.getSelection()).count();
		String[][] sub = new String[matrix.length][inCount];
		for (int i = 0; i < matrix.length; i++) {
			int k = 0;
			for (int j = 0; j < matrix[i].length; j++) {
				boolean in = checks.get(j).getSelection();
				if (in){
					sub[i][k++] = matrix[i][j];
				}
			}
		}
		return sub;
	}

	protected void dolinkSelectAllwidgetSelected(SelectionEvent e) {
		if (checks != null)
			checks.forEach(c->c.setSelection(true));
	}

	protected void dolinkUnselectAllwidgetSelected(SelectionEvent e) {
		if (checks != null)
			checks.forEach(c->c.setSelection(false));
	}

	protected void dobtnImportwidgetSelected(SelectionEvent e) {
		if (txtTableName.getText().trim().length() > 0){
			MatrixReader reader = new MatrixReader();
			reader.setTableName(txtTableName.getText());
			reader.setColumnNamesIndex(spinnerNames.getSelection());
			if (!checkBox.getSelection())
				reader.setColumnDescriptionsIndex(spinnerDescriptions.getSelection());
			reader.setBegin(spinnerBegin.getSelection());
			reader.setEnd(spinnerEnd.getSelection());
			reader.setCreateSurrogateKeys(true);
			TableData data = reader.getData(subMatrix());
			if (comboTypes.getSelectionIndex() == 1)
				Normalize.matchBestTypes(data);
			if (importListener != null){
				Event event = new Event();
				event.data = data;
				importListener.handleEvent(event);
			}
		}
	}

	protected void dotxtSeparatormodifyText(ModifyEvent arg0) {
		reload();
	}
	protected void dotxtDelimitermodifyText(ModifyEvent arg0) {
		reload();
	}
}
