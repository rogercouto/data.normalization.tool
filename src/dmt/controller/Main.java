package dmt.controller;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;

import dmt.model.data.TableData;
import dmt.model.project.DataList;
import dmt.model.project.Project;
import dmt.tools.Util;
import dmt.view.ShellMain;

public class Main extends ShellMain {

	public static Project project = null;

	private Composite activeComp = null;

	private void importCSV(){
		FileDialog dialog = new FileDialog(shell);
		dialog.setFilterExtensions(new String[]{"*.csv"});
		String fileName = dialog.open();
		if (fileName != null){
			if (activeComp != null){
				activeComp.dispose();
				activeComp = null;
			}
			DialogCSVLoaderController window = new DialogCSVLoaderController(shell, fileName);
			String[][] matrix = (String[][])window.open();
			if (matrix == null)
				return;
			System.out.println("Arquivo importado em "+window.getTaskTime()+" segundos");
			if (activeComp == null){
				CompImportCSVController controller = new CompImportCSVController(compCenter, SWT.NONE);
				controller.setTableName(Util.getTableName(fileName));
				controller.setImportListener(new Listener() {
					@Override
					public void handleEvent(Event arg0) {
						TableData data = (TableData)arg0.data;
						activeComp.dispose();
						activeComp = null;
						preprocessData(data);
					}
				});
				controller.setMatrix(matrix);
				activeComp = controller;
				compCenter.layout(true);
			}
		}
	}
	
	private void importJSON(){
		FileDialog dialog = new FileDialog(shell);
		dialog.setFilterExtensions(new String[]{"*.json"});
		String fileName = dialog.open();
		if (fileName != null){
			if (activeComp != null){
				activeComp.dispose();
				activeComp = null;
			}
			CompImportJSONController controller = new CompImportJSONController(compCenter, SWT.NONE);
			controller.setFileName(fileName);
			controller.setImportListener(new Listener() {
				@Override
				public void handleEvent(Event arg0) {
					DataList list = (DataList)arg0.data;
					activeComp.dispose();
					activeComp = null;
					preprocessData(list);
				}
			});
			activeComp = controller;
			compCenter.layout(true);
		}
	}

	private void preprocessData(TableData data){
		CompMainController controller = new CompMainController(compCenter, SWT.NONE);
		controller.setData(data);
		project = new Project();
		project.setDataList(new DataList(data));
		btnSave.setEnabled(true);
		activeComp = controller;
		compCenter.layout(true);
	}
	
	private void preprocessData(DataList list){
		CompMainController controller = new CompMainController(compCenter, SWT.NONE);
		controller.setDataList(list);
		project = new Project();
		project.setDataList(list);
		btnSave.setEnabled(true);
		activeComp = controller;
		compCenter.layout(true);
	}

	@Override
	protected void domenuArqCSVwidgetSelected(SelectionEvent e) {
		importCSV();
	}

	protected void domntmJsonFilewidgetSelected(SelectionEvent e) {
		importJSON();
	}
	
	public static void main(String[] args) {
		Main window = new Main();
		window.open();
	}

	//Save project
	protected void dobtnSavewidgetSelected(SelectionEvent e) {
		if (project == null)
			return;
		if(project.saved()){
			project.save();
		}else{
			FileDialog dialog = new FileDialog(shell, SWT.SAVE);
			dialog.setFilterExtensions(new String[]{"*.dnp"});
			String fileName = dialog.open();
			File file = new File(fileName);
			try {
				file.createNewFile();
				project.saveAs(file);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	//Open project
	protected void domntmOpenwidgetSelected(SelectionEvent e) {
		FileDialog dialog = new FileDialog(shell);
		dialog.setFilterExtensions(new String[]{"*.dnp"});
		String fileName = dialog.open();
		if (fileName != null){
			if (activeComp != null){
				activeComp.dispose();
				activeComp = null;
			}
			project = new Project(new File(fileName));
			CompMainController controller = new CompMainController(compCenter, SWT.NONE);
			controller.setDataList(project.getDataList());
			activeComp = controller;
			compCenter.layout(true);
			btnSave.setEnabled(true);
		}
	}
	
}
