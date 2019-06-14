package dmt.model.data;

import java.io.Serializable;
import java.util.List;

import dmt.model.Column;
import dmt.model.Table;

public class RowData implements Serializable{

	private static final long serialVersionUID = -2811403323790781849L;
	
	private Table table;
	private Object[] data;

	public RowData(Table table) {
		super();
		this.table = table;
		data = new Object[table.getElementCount()];
	}

	public Table getTable() {
		return table;
	}

	public Object[] getData() {
		return data;
	}

	public String[] getDataToStrings(){
		String[] s = new String[data.length];
		for (int i = 0; i < s.length; i++) {
			s[i] = data[i] != null ? data[i].toString() : "";
		}
		return s;
	}
	
	public Object getValue(String columnName){
		int index = table.getColumnIndexIfExists(columnName);
		if (index < 0)
			return null;
		return data[index];	
	}

	public void setValue(int index, Object value){
		data[index] = value;
	}

	public void setValue(String columnName, Object value){
		int index = table.getElementIndex(columnName);
		if (index >= 0)
			data[index] = value;
	}

	public Object getValue(int index){
		return data[index];
	}

	public String toString(){
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(table.getElement(i).getName());
			builder.append(": ");
			builder.append(data[i]);
		}
		return builder.toString();
	}

	public String getPrimaryKeysValues(){
		List<Column> columns = table.getPrimaryKeys();
		StringBuilder builder = new StringBuilder();
		for (Column column : columns) {
			builder.append(getValue(column.getName()).toString());
		}
		return builder.toString();
	}

	public Object getSurrogeteKeyValue(){
		Column oc = getTable().getSurrogateKey();
		if (oc != null){
			return getValue(oc.getName());
		}
		return null;
	}
	
}
