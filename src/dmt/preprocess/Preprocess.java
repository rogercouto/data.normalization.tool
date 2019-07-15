package dmt.preprocess;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import dmt.model.Column;
import dmt.model.Table;
import dmt.model.data.RowData;
import dmt.model.data.TableData;
import dmt.normalization.TypeMatch;
import dmt.tools.IntCounter;
import dmt.tools.Options;

public class Preprocess {

	public static final int CLUSTER_FIRST = 0;
	public static final int CLUSTER_HIGGER = 1;
	
	private TableData data;
	
	public Preprocess(TableData data) {
		super();
		this.data = data;
	}

	public TableData getData() {
		return data;
	}

	public void setData(TableData data) {
		this.data = data;
	}

	public void clusterize(String columnName, Cluster cluster, int method){
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

	public void changeTypes(List<Class<?>> types){
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

	public HashMap<String, String> editTable(String tableName, List<String> newColumnNames, List<Class<?>> types, List<Column> remColumns){
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
		changeTypes(types);
		if (remColumns.size() > 0){
			data.removeColumns(remColumns);
		}
		return map;
	}

	/**
	 * https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
	 */
	public static int levenshteinDistanceCS(CharSequence lhs, CharSequence rhs) {
		int len0 = lhs.length() + 1;
	    int len1 = rhs.length() + 1;
	
	    // the array of distances
	    int[] cost = new int[len0];
	    int[] newcost = new int[len0];
	
	    // initial cost of skipping prefix in String s0
	    for (int i = 0; i < len0; i++) cost[i] = i;
	
	    // dynamically computing the array of distances
	
	    // transformation cost for each letter in s1
	    for (int j = 1; j < len1; j++) {
	        // initial cost of skipping prefix in String s1
	        newcost[0] = j;
	
	        // transformation cost for each letter in s0
	        for(int i = 1; i < len0; i++) {
	            // matching current letters in both strings
	            int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;
	
	            // computing cost for each transformation
	            int cost_replace = cost[i - 1] + match;
	            int cost_insert  = cost[i] + 1;
	            int cost_delete  = newcost[i - 1] + 1;
	
	            // keep minimum cost
	            newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
	        }
	
	        // swap cost/newcost arrays
	        int[] swap = cost; cost = newcost; newcost = swap;
	    }
	
	    // the distance is the cost for transforming all letters in both strings
	    return cost[len0 - 1];
	}

	public static TableData mixColumns(TableData data, String cn1, String cn2, String newColumnName, String divisor, boolean keepOriginalColumns){
		Table newTable = new Table(data.getTable().getName());
		data.getTable().getColumns().forEach(column->{
			if (column.getName().compareTo(cn1) != 0 && column.getName().compareTo(cn2) != 0){
				newTable.addColumn(column.clone());
			}else{
				if (keepOriginalColumns)
					newTable.addColumn(column.clone());
				if (column.getName().compareTo(cn2) == 0){
					Column newColumn = column.clone();
	    			newColumn.setName(newColumnName);
	    			newTable.addColumn(newColumn);
				}
			}
		});
		TableData newData = new TableData(newTable);
		data.getRows().forEach(row->{
			RowData newRow = new RowData(newTable);
			data.getTable().getColumns()
	    		.stream()
	    		.filter(column-> keepOriginalColumns||(column.getName().compareTo(cn1)!=0 && column.getName().compareTo(cn2)!=0))
	    		.forEach(column->{
	    			newRow.setValue(column.getName(), row.getValue(column.getName()));
	    		});
			StringBuilder builder = new StringBuilder();
			Object value1 = row.getValue(cn1);
			if (value1 != null)
				builder.append(value1.toString());
			if (divisor != null)
				builder.append(divisor);
			Object value2 = row.getValue(cn2);
			if (value2 != null)
				builder.append(value2.toString());
			if (builder.length() > 0)
				newRow.setValue(newColumnName, builder.toString());
			newData.addRow(newRow);
		});
		return newData;
	}

	public TableData splitColumn(String columnName, String cn1, String cn2, List<String> c1Values, List<String> c2Values){
		Column originalColumn = data.getTable().getColumn(columnName);
		if (originalColumn != null){
	    	Table newTable = new Table(data.getTable().getName());
	    	data.getTable().getColumns().forEach(column->{
	    		if (column.getName().compareTo(originalColumn.getName()) == 0){
	    			Column c1 = originalColumn.clone();
	    			c1.setName(cn1);
	    			newTable.addColumn(c1);
	    			Column c2 = new Column(cn2);
	    			c2.setType(String.class);
	    			newTable.addColumn(c2);
	    		}else{
	    			newTable.addColumn(column.clone());
	    		}
	    	});
	    	IntCounter ic = new IntCounter();
	    	TableData newData = new TableData(newTable);
	    	data.getRows().forEach(row->{
	    		RowData newRow = new RowData(newTable);
	    		data.getTable().getColumns()
					.stream()
					.filter(c->c.getName().compareTo(originalColumn.getName()) != 0)
					.forEach(c->{
						newRow.setValue(c.getName(), row.getValue(c.getName()));
					});
	    		newRow.setValue(cn1, c1Values.get(ic.getValue()).trim());
	    		newRow.setValue(cn2, c2Values.get(ic.getValue()).trim());
	    		ic.inc();
	    		newData.addRow(newRow);
	    	});
	    	return newData;
		}
		return null;
	}
	
	public TableData splitColumn(String columnName, String cn1, String cn2, String delimiter){
		Column originalColumn = data.getTable().getColumn(columnName);
		if (originalColumn != null){
	    	Table newTable = new Table(data.getTable().getName());
	    	data.getTable().getColumns().forEach(column->{
	    		if (column.getName().compareTo(originalColumn.getName()) == 0){
	    			Column c1 = originalColumn.clone();
	    			c1.setName(cn1);
	    			newTable.addColumn(c1);
	    			Column c2 = new Column(cn2);
	    			c2.setType(String.class);
	    			newTable.addColumn(c2);
	    		}else{
	    			newTable.addColumn(column.clone());
	    		}
	    	});
	    	IntCounter ic = new IntCounter();
	    	TableData newData = new TableData(newTable);
	    	data.getRows().forEach(row->{
	    		RowData newRow = new RowData(newTable);
	    		data.getTable().getColumns()
					.stream()
					.filter(c->c.getName().compareTo(originalColumn.getName()) != 0)
					.forEach(c->{
						newRow.setValue(c.getName(), row.getValue(c.getName()));
					});
	    		Object o = row.getValue(columnName);
	    		if (o != null){
	    			String[] value = o.toString().split(String.format("[%s]", delimiter));
	    			if (value.length > 0)
	    				newRow.setValue(cn1, value[0].trim());
	    			if (value.length > 1)
	    				newRow.setValue(cn2, value[1].trim());
	    			ic.inc();
	    			newData.addRow(newRow);
	    		}
	    	});
	    	return newData;
		}
		return null;
	}

	/**
	 * Remove colunas de um conjunto de dados
	 * @param data: dados originais
	 * @param columns: colunas a serem removidas
	 * @return novos dados (copia)
	 */
	public TableData removeColumns(List<Column> columns){
	    if (data.getTable().haveNestedTables())
	        throw new RuntimeException("Normalize.removeColumns: normalize nested tables first!");
	    Table ot = data.getTable();
	    //keep original name ever
	    Table newTable = new Table(ot.getName());
	    newTable.setName(ot.getName());
	    for (Column column : ot.getColumns()) {
	        if (!columns.contains(column)){
	            newTable.addColumn(column.clone());
	        }
	    }
	    TableData newData = new TableData(newTable);
	    for (RowData row : data.getRows()) {
	        RowData newRow = newData.createRow();
	        for (Column column : newTable.getColumns()) {
	            newRow.setValue(column.getName(), row.getValue(column.getName()));
	        }
	    }
	    return newData;
	}

	public static TableData removeColumnsByNames(TableData data, List<String> columnNames){
		if (data.getTable().haveNestedTables())
	        throw new RuntimeException("Normalize.removeColumns: normalize nested tables first!");
	    Table ot = data.getTable();
	    //keep original name ever
	    Table newTable = new Table(ot.getName());
	    newTable.setName(ot.getName());
	    List<Column> oc = ot.getColumns()
	    		.stream()
	    		.filter(otc->!columnNames.contains(otc.getName()))
	    		.collect(Collectors.toList());
	    oc.forEach(c->{
	    	newTable.addColumn(c);
	    });
	    TableData newData = new TableData(newTable);
	    for (RowData row : data.getRows()) {
	        RowData newRow = newData.createRow();
	        for (Column column : newTable.getColumns()) {
	            newRow.setValue(column.getName(), row.getValue(column.getName()));
	        }
	    }
	    return newData;
	}

}
