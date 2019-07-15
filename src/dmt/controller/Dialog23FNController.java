package dmt.controller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import dmt.model.Column;
import dmt.model.data.TableData;
import dmt.model.project.DataList;
import dmt.normalization.DataDivision;
import dmt.normalization.fd.FD;
import dmt.normalization.fd.FDMapper;
import dmt.tools.IntCounter;
import dmt.view.Dialog23FN;

public class Dialog23FNController extends Dialog23FN {

	private static final int DEF_SAMPLE = 5000;
	
	private TableData data;
	private List<Column> columnsAvaliable;
	private List<Column> columnsSelected = new ArrayList<>();
	private List<FD> fds;
	private DataList dl;

	public Dialog23FNController(Shell parent, TableData data) {
		super(parent);
		this.data = data;
		initialize();
	}

	private void initialize() {
		columnsAvaliable = FDMapper.getCandidateColumns(data);
		if (data.getRowCount() < DEF_SAMPLE){
			spnSample.setSelection(data.getRowCount());
		}
		spnSample.setMaximum(data.getRowCount());
		refresh();
	}

	private void refresh(){
		lstAval.removeAll();
		lstSel.removeAll();
		columnsAvaliable.forEach(c->{
			lstAval.add(c.getName());
		});
		columnsSelected.forEach(c->{
			lstSel.add(c.getName());
		});
	}

	protected void dobtnAddwidgetSelected(SelectionEvent e) {
		int index = lstAval.getSelectionIndex();
		if (index >= 0){
			Column c = columnsAvaliable.get(index);
			columnsAvaliable.remove(index);
			lstAval.remove(index);
			columnsSelected.add(c);
			lstSel.add(c.getName());
		}
	}

	protected void dobtnAddAllwidgetSelected(SelectionEvent e) {
		columnsSelected.addAll(columnsAvaliable);
		columnsAvaliable.clear();
		refresh();
	}

	protected void dobtnRemwidgetSelected(SelectionEvent e) {
		int index = lstSel.getSelectionIndex();
		if (index >= 0){
			Column c = columnsSelected.get(index);
			columnsSelected.remove(index);
			lstSel.remove(index);
			columnsAvaliable.add(c);
			lstAval.add(c.getName());
		}
	}

	protected void dobtnRemAllwidgetSelected(SelectionEvent e) {
		columnsAvaliable.addAll(columnsSelected);
		columnsSelected.clear();
		refresh();
	}

	protected void dobtnSearchwidgetSelected(SelectionEvent e) {
		progressBar.setVisible(true);
		Display.getDefault().asyncExec(new Runnable() {
		    public void run() {
		    	FDMapper fdMapper = new FDMapper(data);
		    	fdMapper.setProgressBar(progressBar);
		    	fdMapper.setColumns(columnsSelected);
		    	fdMapper.setMaxLevel(spnCombin.getSelection());
		    	fdMapper.setMaxData(spnSample.getSelection());
		    	double tol = (double)spnTolerance.getSelection()/10000.0;
		    	fdMapper.setTolerance(tol);
		    	fds = fdMapper.getFDs();
		    	lstFD.removeAll();
		    	IntCounter ic = new IntCounter();
		    	fds.forEach(fd->{
		    				lstFD.add(fd.toString());
		    				ic.inc(fd.getDestSet().size());
		    			});
		    	progressBar.setVisible(false);
		    	System.out.println("\nDependencies found: "+ic.getValue());
		    	/*
		    	ic.setValue(0);
		    	 	fdMapper.getSingleFDs().forEach(sfd->{
		    		if (sfd.getOri().compareTo("DS_ENDERECO")==0||sfd.getDest().compareTo("DS_ENDERECO")==0){
		    			if(sfd.getDep() >= tol){
		    				System.out.println(sfd);
		    				ic.inc();
		    			}
		    		}
		    	});
		    	System.out.println("\nDS_ENDERECO deps: "+ic.getValue());
		    	*/
		    	//fdMapper.getSingleFDs().forEach(System.out::println);
		    }
		});
	}

	protected void dospnCombinwidgetSelected(SelectionEvent e) {
	}

	protected void dolstFDwidgetSelected(SelectionEvent e) {
		int index = lstFD.getSelectionIndex();
		if (index >= 0){
			FD fd = fds.get(index);
			txtNewTableName.setText(fd.getFirstDet());
		}
	}

	protected void dobtnNormalizewidgetSelected(SelectionEvent e) {
		int index = lstFD.getSelectionIndex();
		if (index >= 0){
			createPreview();
			modelBefore.addTable(data.getTable());
			modelBefore.calcPositions();
			FD fd = fds.get(index);
			DataDivision dd = new DataDivision(data);
			dl = dd.splitDependences(fd, txtNewTableName.getText());
			modelAfter.clear();
			modelAfter.addTables(dl);
			modelAfter.calcPositions();
			tabFolder.setSelection(1);
		}
	}

	protected void dobtnConfirmChangeswidgetSelected(SelectionEvent e) {
		if (dl != null){
			result = dl;
			shell.close();
		}
	}

}
