package dmt.controller;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import dmt.database.ReverseEng;
import dmt.model.Table;
import dmt.model.data.TableData;
import dmt.model.project.DataList;
import dmt.view.CompMain;

public class CompMainController extends CompMain {

	private HashMap<String, TableData> map;
	private HashMap<String, Integer> openMap = new HashMap<>();

	public CompMainController(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize(){
		modelEditor.setDoubleClickListener(new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				Table table = (Table)arg0.data;
				int mIndex = modelEditor.getTableIndex(table.getName());
				if (openMap.containsKey(table.getName())){
					int index = openMap.get(table.getName()).intValue();
					tabFolder.setSelection(index);
				}else{
					TableData data = map.get(table.getName());
					if (!data.isLoaded() && Main.project.getServer() != null){
						ReverseEng re = new ReverseEng(Main.project.getServer());
						re.fillData(data);
					}
					CompDataEditorController controller = new CompDataEditorController(tabFolder, SWT.NONE);
					controller.setData(data);
					controller.setEditTableListener(new Listener() {
						@Override
						public void handleEvent(Event arg0) {
							TableData newData = (TableData)arg0.data;
							Main.project.addOrReplace(newData.clone());
							//Main.project.getDataList().replace(newData);
							CTabItem item = tabFolder.getSelection();
							item.setText(newData.getTable().getName());
							map.remove(table.getName());
							map.put(newData.getTable().getName(), newData);
							int index = tabFolder.indexOf(item);
							openMap.remove(table.getName());
							openMap.put(newData.getTable().getName(), index);
							modelEditor.setTable(mIndex, newData.getTable());
							modelEditor.redraw();
						}
					});
					controller.setNormalizeListener(new Listener() {
						@Override
						public void handleEvent(Event arg0) {
							setDataList(Main.project.getDataList());
						}
					});
					CTabItem item = new CTabItem(tabFolder, SWT.CLOSE);
					item.setData(table.getName());
					item.setText(table.getName());
					item.setControl(controller);
					item.addDisposeListener(new DisposeListener() {
						@Override
						public void widgetDisposed(DisposeEvent arg0) {
							openMap.remove(table.getName());
						}
					});
					tabFolder.setSelection(item);
					openMap.put(table.getName(), tabFolder.indexOf(item));
				}
			}
		});
	}

	public void setDataList(DataList list){
		map = new HashMap<>();
		list.forEach(d->{
			map.put(d.getTable().getName(), d);
		});
		reload();
	}

	public void setData(TableData data){
		map = new HashMap<>();
		map.put(data.getTable().getName(), data);
		reload();
	}

	private void reload(){
		modelEditor.clear();
		map.values().forEach(d->{
			modelEditor.addTable(d.getTable());
		});
		modelEditor.calcPositions();
	}

}
