package dmt.model.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
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

	public String getDbValue(String columnName){
		Object value = getValue(columnName);
		StringBuilder builder = new StringBuilder();
		Column column = table.getColumn(columnName);
		boolean delimiter = false;
		if (column.getType().equals(String.class)
			||(column.getType().equals(Character.class))
			||(column.getType().equals(Date.class))
			||(column.getType().equals(LocalDate.class))
			||(column.getType().equals(LocalTime.class))
			||(column.getType().equals(LocalDateTime.class))){
			delimiter = true;
		}
		if (delimiter)
			builder.append('\'');
		if (column.getDbType().equals(Date.class) || column.getType().equals(LocalDateTime.class)){
			String formattedValue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date)value);
			builder.append(formattedValue);
		}else if (column.getDbType().equals(LocalDate.class)){
			String formattedValue = new SimpleDateFormat("yyyy-MM-dd").format((Date)value);
			builder.append(formattedValue);
		}else if (column.getDbType().equals(LocalTime.class)){
			String formattedValue = new SimpleDateFormat("HH:mm:ss").format((Date)value);
			builder.append(formattedValue);
		}else{
			builder.append(value.toString());
		}
		if (delimiter)
			builder.append('\'');
		return builder.toString();
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

	public void removeColumns(List<Integer> indices){
		Object[] copy = new Object[data.length];
		for (int i = 0; i < copy.length; i++) {
			copy[i] = data[i];
		}
		data = new Object[copy.length-indices.size()];
		int index = 0;
		for (int i = 0; i < copy.length; i++) {
			if (!indices.contains(i)){
				data[index++] = copy[i];
			}
		}
	}

}
