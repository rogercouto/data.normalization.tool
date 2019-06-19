package dmt.view;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.wb.swt.SWTResourceManager;

import dmt.model.Column;
import dmt.model.Table;

public class TableModel {

	private Table table;
	private Rectangle rect;
	private List<Column> excludedColumns = new LinkedList<>();

	public TableModel(Table table, Point position) {
		super();
		this.table = table;
		Point size = calculeSize();
		rect = new Rectangle(position.x, position.y, size.x, size.y);
	}

	public Table getTable() {
		return table;
	}
	public Point getPosition() {
		return new Point(rect.x, rect.y);
	}
	public void setPosition(Point position) {
		Point size = calculeSize();
		rect = new Rectangle(position.x, position.y, size.x, size.y);
	}
	public Rectangle getRect(){
		return rect;
	}

	public void setTable(Table table) {
		this.table = table;
		setPosition(new Point(rect.x, rect.y));
	}

	public void setRect(Rectangle rect) {
		this.rect = rect;
	}

	private static int calculeWidth(Table t){
		OptionalInt oMax = t.getElements().stream().mapToInt(e->e.getNameWidth()).max();
		if (oMax.isPresent()){
			if (t.getNameWidth()+100 > oMax.getAsInt())
				return t.getNameWidth()+100;
		}
		return oMax.isPresent()?oMax.getAsInt()+50:200;
	}

	private static int calculeHeigth(Table t){
		return t.getColumns().size() * 20 + 25;
	}

	
	public Point calculateSize(Table table){
		Point size = new Point(calculeWidth(table), calculeHeigth(table));
		table.getSubtables().forEach(t->{
			Point sSize = calculateSize(t);
			size.x = Math.max(size.x,sSize.x+10);
			size.y += sSize.y+5;
		});
		return size;
	}
	
	public Point calculeSize(){
		return calculateSize(table);
	}
	
	public void draw(GC gc, int hSel, int vSel){
		Rectangle tNameRect = new Rectangle(rect.x-hSel, rect.y-vSel, rect.width, 20);
		Rectangle rectangle = new Rectangle(rect.x-hSel, rect.y-vSel, rect.width, rect.height);
		gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		gc.fillRectangle(rectangle);
		gc.drawRectangle(rectangle);
		gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		gc.fillRectangle(tNameRect);
		gc.drawRectangle(tNameRect);
		gc.drawLine(rect.x-hSel, rect.y-vSel+20, rect.width+rect.x-hSel, rect.y-vSel+20);
		gc.drawText(getTable().getName().concat(" ").concat(getTable().getNfToString()), rect.x-hSel+3, rect.y-vSel+3);
		gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		table.getColumns().forEach(c->{
			int yDesl = getTable().getElementIndex(c.getName());
			gc.drawText(c.getName(), rect.x-hSel+25, rect.y-vSel+20*yDesl+25);
			Optional<Column> opc = excludedColumns.stream().filter(ec->ec.getName().compareTo(c.getName())==0).findFirst();
			if (opc.isPresent())
				gc.drawLine(rect.x-hSel+5, rect.y-vSel+20*yDesl+32, rect.x+rect.width-hSel-5, rect.y-vSel+20*yDesl+32);
			if (c.isPrimaryKey() && c.getForeignKey() == null){
				gc.drawImage(SWTResourceManager.getImage(TableModel.class, "/icon/key0.png"), rect.x-hSel+5, rect.y-vSel+20*yDesl+25);
			}else if (c.getForeignKey() != null && !c.isPrimaryKey()){
				gc.drawImage(SWTResourceManager.getImage(TableModel.class, "/icon/link.png"), rect.x-hSel+5, rect.y-vSel+20*yDesl+25);
			}else if (c.getForeignKey() != null && c.isPrimaryKey()){
				gc.drawImage(SWTResourceManager.getImage(TableModel.class, "/icon/link_key_0.png"), rect.x-hSel+5, rect.y-vSel+20*yDesl+25);
			}
		});
		table.getSubtables().forEach(t->{
			int yDesl = getTable().getElementIndex(t.getName());
			TableModel model = new TableModel(t, new Point(rect.x-hSel+5, rect.y-vSel+20*yDesl+25));
			model.draw(gc, hSel, vSel);
		});
	}

	public void drawRelations(GC gc, List<TableModel> otherModels, int hSel, int vSel){
		table.getForeignKeys().forEach(fk->{
			Optional<TableModel> opt = otherModels.stream().filter(m -> m.table.equals(fk.getForeignKey().getTable())).findFirst();
			if (opt.isPresent()){
				TableModel fkModel = opt.get();
				int xdist = (fkModel.rect.x - (rect.x-hSel+rect.width)) / 2;
				gc.drawLine(rect.x-hSel+rect.width, rect.y-vSel+rect.height/2, rect.x-hSel+rect.width+xdist, rect.y-vSel+rect.height/2);
				gc.drawLine(fkModel.rect.x-hSel-xdist, fkModel.rect.y-vSel+fkModel.rect.height/2, fkModel.rect.x-hSel, fkModel.rect.y-vSel+fkModel.rect.height/2);
				gc.drawLine(rect.x-hSel+rect.width+xdist, rect.y-vSel+rect.height/2, fkModel.rect.x-hSel-xdist, fkModel.rect.y-vSel+fkModel.rect.height/2);
			}
		});
	}

	public List<Column> getExcludedColumns() {
		return excludedColumns;
	}

	public void setExcludedColumns(List<Column> excludedColumns) {
		this.excludedColumns = excludedColumns;
	}
}
