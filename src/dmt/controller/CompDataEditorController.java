package dmt.controller;

import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;

import dmt.model.Column;
import dmt.model.data.NormalForm;
import dmt.model.data.TableData;
import dmt.model.project.DataList;
import dmt.normalization.NormUtil;
import dmt.tools.Options;
import dmt.tools.Util;
import dmt.view.CompDataEditor;

public class CompDataEditorController extends CompDataEditor {

	private TableData data = null;
	private int page = 1;
	private double maxPages;
	private char[] seps;

	private Listener editTableListener;
	private Listener preprocessListener;
	private Listener normalizeListener;

	public CompDataEditorController(Composite parent, int style) {
		super(parent, style);
		cmbActions.add("Edit table...");
		cmbActions.add("Refine column...");
		cmbActions.add("Clusterize column...");
		cmbActions.add("Split column...");
		cmbActions.add("Join columns...");
		cmbActions.add("Check normal form...");
		cmbActions.add("Normalize...");
	}

	public TableData getData() {
		return data;
	}

	public void setData(TableData data) {
		this.data = data;
		if (data.getRowCount() < Options.getPageSize()){
			maxPages = 1;
		}else{
			maxPages = (double)data.getRowCount() / (double)Options.getPageSize();
			double ver = maxPages % 1;
			if (ver != 0){
				maxPages = ((int)maxPages)+1;
			}
		}
		spinner.setMaximum((int)maxPages);
		data.getTable().getColumns().forEach(c->{
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(c.getName());
			column.pack();
		});
		reload();
	}

	public void setEditTableListener(Listener editTableListener) {
		this.editTableListener = editTableListener;
	}

	public void setNormalizeListener(Listener normalizeListener) {
		this.normalizeListener = normalizeListener;
	}

	public Listener getPreprocessListener() {
		return preprocessListener;
	}

	public void setPreprocessListener(Listener preprocessListener) {
		this.preprocessListener = preprocessListener;
	}

	private void reload(){
		table.removeAll();
		int coef = page -1;
		int min = coef*Options.getPageSize();
		int max = (page < (int)data.getRowCount())? min + (Options.getPageSize()-1) : data.getRowCount()-1;
		data.getRows().stream().filter(r->{
			int i = data.getRows().indexOf(r);
			if (i >= min && i <= max)
				return true;
			else
				return false;
		}).forEach(r->{
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(r.getDataToStrings());
			Integer[] pkIndices = r.getTable().getPkIndices();
			for (Integer index : pkIndices) {
				Util.boldFont(item, index.intValue());
			}
		});
		for (int i = 0; i < table.getColumns().length; i++) {
			Column column = data.getTable().getColumnIFExists(i);
			if (column != null){
				table.getColumn(i).setText(column.getName());
				table.getColumn(i).pack();
			}else{
				table.getColumn(i).setWidth(0);
			}
		}
		if (seps != null){
			NormUtil normalization = new NormUtil(data);
			normalization.findMultiValuedColumns(seps).forEach(c->{
				int index = data.getTable().getElementIndex(c.getName());
				if (index < table.getColumnCount()){
					for (int i = 0; i < table.getItemCount(); i++) {
						table.getItem(i).setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
						table.getItem(i).setBackground(index,SWTResourceManager.getColor(SWT.COLOR_YELLOW));
					}
				}
			});
		}
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

	protected void dospinnerwidgetSelected(SelectionEvent e) {
		page = spinner.getSelection();
		reload();
	}

	protected void docomboActionswidgetSelected(SelectionEvent e) {
		int index = cmbActions.getSelectionIndex();
		switch (index) {
		case 0:
			editTable();
			break;
		case 1:
			refineColumn();
			break;
		case 2:
			clusterizeColumn();
			break;
		case 3:
			splitColumn();
			break;
		case 4:
			mixColumn();
			break;
		case 5:
			checkNormalTable();
			break;
		case 6:
			normalizeTable();
			break;
		default:
			break;
		}
	}

	private void editTable(){
		DialogModifyTableController dialog = new DialogModifyTableController(getShell(), data);
		boolean modified = (boolean)dialog.open();
		if (modified){
			removeTable();
			removeNav();
			createTable();
			createNav();
			data.getTable().getColumns().forEach(c->{
				TableColumn column = new TableColumn(table, SWT.NONE);
				column.setText(c.getName());
				column.pack();
			});
			if (editTableListener != null){
				Event e = new Event();
				e.data = data;
				editTableListener.handleEvent(e);
			}
			this.layout(true);
			reload();
		}
		cmbActions.deselectAll();
	}

	private void clusterizeColumn(){
		DialogClusterizeController dialog = new DialogClusterizeController(getShell(), data);
		dialog.open();
		cmbActions.deselectAll();
		reload();
	}

	private void refineColumn(){
		DialogRefineController dialog = new DialogRefineController(getShell(), data);
		dialog.open();
		cmbActions.deselectAll();
		reload();
	}

	private void splitColumn(){
		DialogSplitController dialog = new DialogSplitController(getShell(), data);
		TableData newData = (TableData)dialog.open();
		if (newData != null){
			if (preprocessListener != null){
				Event e = new Event();
				e.data = newData;
				preprocessListener.handleEvent(e);
			}
			data = newData;
			new TableColumn(table, SWT.NONE);
			TableColumn[] columns = table.getColumns();
			for (int i = 0; i < newData.getTable().getColumns().size(); i++) {
				String name = data.getTable().getColumn(i).getName();
				columns[i].setText(name);
			}
			cmbActions.deselectAll();
			reload();
		}
	}

	private void mixColumn(){
		DialogMixController dialog = new DialogMixController(getShell(), data);
		TableData newData = (TableData)dialog.open();
		if (newData != null){
			if (preprocessListener != null){
				Event e = new Event();
				e.data = newData;
				preprocessListener.handleEvent(e);
			}
			data = newData;
			if (data.getTable().getColumns().size() > table.getColumnCount())
				new TableColumn(table, SWT.NONE);
			TableColumn[] columns = table.getColumns();
			for (int i = 0; i < data.getTable().getColumns().size(); i++) {
				String name = data.getTable().getColumn(i).getName();
				columns[i].setText(name);
			}
			cmbActions.deselectAll();
			reload();
		}
	}

	private void checkNormalTable(){
		DialogCheckNormalFormController dialog = new DialogCheckNormalFormController(getShell(), data);
		Object res = dialog.open();
		if (res != null){
			seps = (char[]) res;
			if (data.getNormalForm() != NormalForm.NF3)
				normalizeTable();
		}
		cmbActions.deselectAll();
		reload();
	}

	private void normalizeTable(){
		String name = data.getTable().getName();
		DataList dl = null;
		if (data.getNormalForm().equals(NormalForm.NN)){
			Dialog1FNController dialog = new Dialog1FNController(getShell(), data, seps);
			dl = (DataList)dialog.open();
		}else{
			Dialog23FNController dialog = new Dialog23FNController(getShell(), data);
			dl = (DataList) dialog.open();
		}
		if (dl != null){
			Main.project.getDataList().remap();
			Main.project.addOrReplace(dl);
			Optional<TableData> od = dl.stream().filter(d->d.getTable().getName().compareTo(name) == 0).findFirst();
			if (od.isPresent()){
				data = od.get();
			}
			reload();
			if (normalizeListener != null){
				Event event = new Event();
				event.data = data.getNormalForm();
				normalizeListener.handleEvent(event);
			}
			cmbActions.deselectAll();
			reload();
		}
	}
}
