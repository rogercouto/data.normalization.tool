package dmt.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import dmt.model.Table;
import dmt.view.CompModelEditor;
import dmt.view.TableModel;

public class CompModelEditorController extends CompModelEditor {

	public List<TableModel> models = new LinkedList<>();
	private TableModel movingModel = null;
	private Listener doubleClickListener = null;

	public CompModelEditorController(Composite parent, int style) {
		super(parent, style);
		addListeners();
	}

	public CompModelEditorController(Composite parent, int style, int canvasStyle) {
		super(parent, style, canvasStyle);
		addListeners();
	}

	private void addListeners() {
		canvas.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	e.gc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
	        	models.forEach(tm->{
	        		List<TableModel> om = models.stream().filter(am -> !am.equals(tm)).collect(Collectors.toList());
	        		tm.drawRelations(e.gc, om, hSelection, vSelection);
	        	});
	        	models.forEach(tm->{
	        		tm.draw(e.gc, hSelection, vSelection);
	        	});
	        }
	    });
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				movingModel = getModel(arg0);
			}
			@Override
			public void mouseUp(MouseEvent arg0) {
				movingModel = null;
				canvas.redraw();
			}
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				movingModel = null;
				if (doubleClickListener != null){
					Event event = new Event();
					TableModel model = getModel(arg0);
					if (model != null){
						event.data = model.getTable();
						doubleClickListener.handleEvent(event);
					}
				}
			}
		});
		canvas.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent arg0) {
				if (movingModel != null){
					Point pos = movingModel.getPosition();
					pos.x = arg0.x - (movingModel.getRect().width / 2) +hSelection;
					pos.y = arg0.y - (movingModel.getRect().height / 2) +vSelection;
					movingModel.setPosition(pos);
					canvas.redraw();
				}
			}
		});
	}

	private TableModel getModel(MouseEvent event){
		Optional<TableModel> opt = models.stream().filter(m -> {
			Rectangle r = new Rectangle(m.getPosition().x-hSelection, m.getPosition().y-vSelection, m.getRect().width, m.getRect().height);
			return r.contains(new Point(event.x, event.y));
		}).findAny();
		if (opt.isPresent()){
			return opt.get();
		}
		return null;
	}

	public void addTableModel(TableModel tableModel){
		models.add(tableModel);
	}
	
	public int getTableIndex(String tableName){
		for (int i = 0; i < models.size(); i++) {
			if (models.get(i).getTable().getName().compareTo(tableName) == 0)
				return i;
		}
		return -1;
	}
	
	public void setTable(int index, Table table){
		models.get(index).setTable(table);
	}

	public void draw(){
		canvas.redraw();
	}

	public void setDoubleClickListener(Listener listener){
		this.doubleClickListener = listener;
	}

	@Override
	protected void scrollMoved(){
		super.scrollMoved();
		draw();
	}
	
	public TableModel getFirstModel(){
		if (models.size() > 0)
			return models.get(0);
		return null;
	}
	
	public void clear(){
		models = new LinkedList<>();
	}
	
	public TableModel geteModel(Table table){
		Optional<TableModel> om = models.stream().filter(m->m.getTable().getName().compareTo(table.getName())==0).findFirst();
		if (om.isPresent())
			return om.get();
		return null;
	}
	
	public HashMap<String, List<String>> getExcludedColumnNames() {
		HashMap<String, List<String>> map = new HashMap<>();
		models.forEach(m->{
			map.put(m.getTable().getName(), m.getExcludedColumns().stream().map(c->c.getName()).collect(Collectors.toList()));
		});
		return map;
	}
}
