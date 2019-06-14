package dmt.model.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import dmt.model.Column;
import dmt.model.Table;

public class TableData implements Serializable, Cloneable {

	private static final long serialVersionUID = -7430376656350865117L;
	
	private Table table;
	private int autoIncrement = 0;
	private List<RowData> rows = new LinkedList<>();
	private NormalForm normalForm = NormalForm.NN;

	public TableData(Table table) {
		super();
		this.table = table;
	}

	public void addRow(RowData row){
		if (row.getTable().getName().compareTo(table.getName()) != 0)
			throw new RuntimeException("TableData.addRow: row table different from data table!");
		if (table.haveSurrogateKey()){
			row.setValue(table.getSurrogateKeyIndex(), ++autoIncrement);
		}
		rows.add(row);
	}

	public String toString(){
		StringBuilder builder = new StringBuilder();
		for (RowData rowData : rows) {
			builder.append(rowData.toString());
			builder.append("\n");
		}
		return builder.toString();
	}

	public Table getTable() {
		return table;
	}

	public List<RowData> getRows(){
		return rows;
	}

	public List<RowData> getRows(int limit){
		return rows.stream().limit(limit).collect(Collectors.toList());
	}

	public RowData getRow(int index){
		return rows.get(index);
	}

	public int getRowCount(){
		return rows.size();
	}

	public RowData createRow(){
		RowData row = new RowData(table);
		addRow(row);
		return row;
	}
	
	public HashSet<Object> getColumnSet(int index){
		List<Object> list = rows.stream().map(data -> data.getValue(index)).collect(Collectors.toList());
		return new HashSet<Object>(list);
	}
	
	public HashSet<Object> getColumnSet(String columnName){
		int index = table.getElementIndex(columnName);
		return getColumnSet(index);
	}

	public List<Object> getColumnValues(int index){
		return rows.stream().map(data -> data.getValue(index)).collect(Collectors.toList());
	}
	public List<Object> getColumnValues(String columnName){
		int index = table.getElementIndex(columnName);
		return getColumnValues(index);
	}

	public NormalForm getNormalForm() {
		return normalForm;
	}

	public void setNormalForm(NormalForm normalForm) {
		this.normalForm = normalForm;
	}
	
	public TableData clone(){
		Table newTable = new Table(table.getName());
		table.getElements().forEach(e->{
			if (e.getClass().equals(Column.class)){
				Column c = (Column)e;
				newTable.addColumn(c.clone());
			}else if (e.getClass().equals(Table.class)){
				Table t = (Table)e;
				newTable.addTable(t.clone());
			}
		});
		TableData data = new TableData(newTable);
		rows.forEach(row->{
			RowData newRow = new RowData(newTable);
			Object[] values = row.getData();
			for (int i = 0; i < values.length; i++) {
				newRow.setValue(i, values[i]);
			}
			data.addRow(newRow);
		});
		return data;
	}
}
