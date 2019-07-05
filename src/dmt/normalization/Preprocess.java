package dmt.normalization;

import java.util.HashMap;
import java.util.List;

import dmt.model.Column;
import dmt.model.data.TableData;
import dmt.tools.Options;

public class Preprocess {

	public static final int CLUSTER_FIRST = 0;
	public static final int CLUSTER_HIGGER = 1;

	public static void clusterize(TableData data, String columnName, Cluster cluster, int method){
		data.getRows().forEach(r->{
			Object value = r.getValue(columnName);
			if (value != null){
				String oldValue = value.toString();
				String newValue;
				switch (method) {
				case CLUSTER_FIRST:
					newValue = cluster.getFirst(oldValue);
					break;
				case CLUSTER_HIGGER:
					newValue = cluster.getHigher(oldValue);
					break;
				default:
					newValue = cluster.getFirst(oldValue);
					break;
				}
				if (newValue != null && oldValue.compareTo(newValue)!=0)
					r.setValue(columnName, newValue);
			}
		});
	}

	public static String upperFirst(String string, boolean ignoreShortStrings){
		char[] ca = string.toCharArray();
		StringBuilder builder = new StringBuilder();
		int count = 0;
		if (ignoreShortStrings){
			for (int i = ca.length-1; i >= 0; i--) {
				if (ca[i] ==' '){
					count = 0;
				}else if (Character.isLetter(ca[i])){
					count++;
					if ((i > 0 && ca[i-1] == ' ') || i ==0){
						if (count > Options.getShortStringLimit())
							ca[i] = Character.toUpperCase(ca[i]);
					}else{
						ca[i] = Character.toLowerCase(ca[i]);
					}
				}
				builder.append(ca[i]);
			}
		}else{
			for (int i = ca.length-1; i >= 0; i--) {
				if (ca[i] ==' '){
					count = 0;
				}else if (Character.isLetter(ca[i])){
					count++;
					if ((i > 0 && ca[i-1] == ' ') || i ==0){
						ca[i] = Character.toUpperCase(ca[i]);
					}else{
						ca[i] = Character.toLowerCase(ca[i]);
					}
				}
				builder.append(ca[i]);
			}
		}
		return builder.reverse().toString();
	}

	public static void changeTypes(TableData data, List<Class<?>> types){
		List<Column> columns = data.getTable().getColumns();
		if (columns.size() == types.size()){
			columns.forEach(column->{
				Class<?> type = types.get(columns.indexOf(column));
				column.setType(type);
		        for (int i = 0; i < data.getRowCount(); i++) {
		            Object newValue = TypeMatch.convert(data.getRow(i).getValue(column.getName()), type);
		            data.getRow(i).setValue(column.getName(), newValue);
		        }
			});
		}
	}

	public static HashMap<String, String> editTable(TableData data, String tableName, List<String> newColumnNames, List<Class<?>> types, List<Column> remColumns){
		HashMap<String, String> map = new HashMap<>();
		String oldName = data.getTable().getName();
		if (tableName.compareTo(oldName) != 0){
			data.getTable().setName(tableName);
		}
		for (int i = 0; i < newColumnNames.size(); i++) {
			String oldColumnName = data.getTable().getColumn(i).getName();
			String newColumnName = newColumnNames.get(i);
			if (oldColumnName.compareTo(newColumnName) != 0){
				map.put(oldColumnName, newColumnName);
				data.getTable().setElementName(oldColumnName, newColumnName);
			}
		}
		changeTypes(data, types);
		/*
		data.getTable().getColumns().forEach(column->{
			Normalize.changeType(data, column.getName(), types.get(data.getTable().getElementIndex(column.getName())));
		});
		*/
		if (remColumns.size() > 0){
			data.removeColumns(remColumns);
		}
		return map;
	}

	public static void main(String[] args) {
		System.out.println(upperFirst("rOasTeds di cOld", false));
	}

}
