package dmt.controller;

import java.util.HashMap;
import java.util.List;
import java.util.OptionalInt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import dmt.model.Column;
import dmt.model.data.TableData;
import dmt.normalization.Cluster;
import dmt.normalization.Counter;
import dmt.normalization.Preprocess;
import dmt.view.DialogClusterize;

public class DialogClusterizeController extends DialogClusterize{

	private List<Column> columns;
	private TableData data;
	private Cluster cluster = null;

	public DialogClusterizeController(Shell parent, TableData data) {
		super(parent);
		this.data = data;
		initialize();
	}
	
	private void initialize(){
		columns = data.getTable().getColumns();
		columns.stream().filter(c->c.getType().equals(String.class)).forEach(c->{
			this.cmbColumn.add(c.getName());
		});
		cmbMethod.select(0);
	}
	
	protected void docmbColumnwidgetSelected(SelectionEvent e) {
		remap();
	}
	
	protected void dobtnConfirmwidgetSelected(SelectionEvent e) {
		if (cluster == null)
			throw new RuntimeException("DialogClusterize.dobtnConfirmwidgetSelected: cluster not set!");
		String columnName = cmbColumn.getText();
		if (columnName != null && columnName.trim().length() > 0){
			Preprocess.clusterize(data, columnName, cluster, 
					(cmbMethod.getSelectionIndex() == 0)? Preprocess.CLUSTER_HIGGER : Preprocess.CLUSTER_FIRST);
			shell.close();
		}
	}
	
	protected void dospinnerwidgetSelected(SelectionEvent e) {
		remap();
	}
	
	protected void docmbMethodwidgetSelected(SelectionEvent e) {
		remap();
	}
	
	private void remap(){
		tree.removeAll();
		if (cmbColumn.getSelectionIndex() >= 0){
			cluster = new Cluster(spinner.getSelection());
			cluster.addAll(data.getColumnValues(cmbColumn.getText()));
			HashMap<String, List<Counter>> map = cluster.getMap();
			map.forEach((s,l)->{
				TreeItem item= new TreeItem(tree, SWT.NONE);
				item.setText(0, s);
				if (cmbMethod.getSelectionIndex() == 1)
					item.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
				else
					item.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				l.forEach(c->{
					TreeItem sub = new TreeItem(item, SWT.NONE);
					sub.setText(c.getValue()+" ("+c.getCount()+" )");
					if (cmbMethod.getSelectionIndex() == 0){
						OptionalInt oi = l.stream().mapToInt(c2->c2.getCount()).max();
						if (oi.isPresent()){
							if (c.getCount() == oi.getAsInt())
								sub.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
							else
								sub.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
						}
					}
				});
				item.setExpanded(true);
			});
		}
	}

}
