package dmt.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import dmt.input.JSONReader;
import dmt.model.Column;
import dmt.model.Table;
import dmt.model.data.TableData;
import dmt.model.project.DataList;
import dmt.normalization.Normalize;
import dmt.view.CompImportJSON;
import dmt.view.TableModel;

public class CompImportJSONController extends CompImportJSON{

	private String fileName;
	private Listener importListener;
	private JSONReader reader;
	private Table table;
	
	public CompImportJSONController(Composite parent, int style) {
		super(parent, style);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		reader = new JSONReader(fileName);
		table = reader.readTable();
		TableModel model = new TableModel(table, new Point(50, 50));
		modelEditor1.addTableModel(model);
		modelEditor2.setDoubleClickListener(new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				if (arg0.data != null && arg0.data.getClass().equals(Table.class)){
					Table table = (Table)arg0.data;
					DialogKeepColumnsController dialog = new DialogKeepColumnsController(getShell(), table);
					List<Column> excludedColumns =  dialog.open();
					if (excludedColumns != null){
						TableModel model = modelEditor2.geteModel(table);
						model.setExcludedColumns(excludedColumns);
						modelEditor2.draw();
					}
				}
			}
		});
		refresh();
	}
	
	public Listener getImportListener() {
		return importListener;
	}

	public void setImportListener(Listener importListener) {
		this.importListener = importListener;
	}

	protected void dobtnReplicatewidgetSelected(SelectionEvent e) {
		refresh();
	}
	protected void dobtnSplitwidgetSelected(SelectionEvent e) {
		refresh();
	}
	
	private void refresh() {
		TableModel model = modelEditor1.getFirstModel();
		Table table = model.getTable();
		modelEditor2.clear();
		modelEditor2.draw();
		if (btnReplicate.getSelection()){
			Table newTable = Normalize.mixNestedTables(table);
			TableModel newModel = new TableModel(newTable, new Point(50, 50));
			modelEditor2.addTableModel(newModel);
		}else if (btnSplit.getSelection()){
			Table nTable = table.clone();
			nTable.createSurrogateKey();
			List<Table> list = Normalize.splitNestedTables(nTable);
			list.forEach(t->{
				int i = list.indexOf(t);
				int x = 50+i*(t.getNameWidth()*10);
				TableModel newModel = new TableModel(t, new Point(x, 50));
				modelEditor2.addTableModel(newModel);
			});
		}
	}
	
	protected void dobtnImportwidgetSelected(SelectionEvent e) {
		final DataList dataList = new DataList();
		if (btnReplicate.getSelection()){
			String content = reader.getContent();
			TableData data = Normalize.mixNestedData(table, content);
			dataList.add(data);
		}else if (btnSplit.getSelection()){
			String content = reader.getContent();
			dataList.addAll(Normalize.splitNestedData(table, content));
		}
		HashMap<String, List<String>> map = modelEditor2.getExcludedColumnNames();
		map.forEach((s,l)->{
			if (l.size() > 0){
				Optional<TableData> oData = dataList.stream().filter(dl->dl.getTable().getName().compareTo(s) == 0).findFirst();
				if (oData.isPresent()){
					TableData data = Normalize.removeColumnsByNames(oData.get(), l);
					dataList.replace(data);
				}
			}
		});
		if (importListener != null){
			Event event = new Event();
			event.data = dataList;
			importListener.handleEvent(event);
		}
	}

}
