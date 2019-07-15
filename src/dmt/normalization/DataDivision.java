package dmt.normalization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import dmt.model.Column;
import dmt.model.Element;
import dmt.model.Table;
import dmt.model.data.RowData;
import dmt.model.data.TableData;
import dmt.model.project.DataList;
import dmt.normalization.fd.FD;
import dmt.preprocess.Preprocess;
import dmt.tools.Options;
import dmt.tools.Util;

public class DataDivision {

	private TableData data;
	
	public DataDivision(TableData data) {
		super();
		this.data = data;
	}
	
	public TableData getData() {
		return data;
	}

	public void setData(TableData data) {
		this.data = data;
	}

	private static void splitSubdata(Table table, Table nTable, HashMap<String, Table> nTableMap,
	        JSONArray jsonArray, HashMap<String, Object> pkMap, HashMap<String, TableData> dataMap) {
		for (int i = 0; i < jsonArray.length(); i++) {
			RowData row = dataMap.get(nTable.getName()).createRow();
	        for (String key : pkMap.keySet()) {
	        	row.setValue(key, pkMap.get(key));
	        }
	        JSONObject jObj = jsonArray.getJSONObject(i);
	        for (Element element : table.getElements()) {
	            if (element.getClass().equals(Table.class)){
	                Table subTable = (Table)element;
	                JSONArray jsonSubArray = null;
	                if (jObj.has(subTable.getName())){
	                    jsonSubArray = jObj.getJSONArray(subTable.getName()+"s");
	                }else if (jObj.has(subTable.getName().concat("s"))){
	                    jsonSubArray = jObj.getJSONArray(subTable.getName().concat("s"));
	                }
	                if (jsonSubArray != null){
	                    Table nSubTable = nTableMap.get(subTable.getName());
	                    if (nTable.getSurrogateKey() != null){
	                    	Object value = row.getValue(nTable.getSurrogateKey().getName());
	                    	pkMap.put(nTable.getSurrogateKey().getName(), value);
	                    }
	                    splitSubdata(subTable, nSubTable, nTableMap, jsonSubArray, pkMap, dataMap);
	                }
	            }else{
	                Column column = (Column)element;
	                if (!column.isSurrogateKey()){
	                	if (jObj.has(column.getName())){
	                		Object value = jObj.get(column.getName());
	                		row.setValue(element.getName(), value);
	                	}
	                }
	            }
	        }
	    }
	}

	public DataList splitNestedData(String content){
	    Table table = data.getTable();
		if (!table.havePrimaryKey())
	        table.createSurrogateKey();
		TableDivision td = new TableDivision(table);
	    List<Table> nTables = td.splitNestedTables();
	    HashMap<String, Table> nTableMap = DataDivision.toHash(nTables);
	    HashMap<String, TableData> dataMap = new HashMap<>();
	    //Segundo passo ler o arquivo json
	    JSONObject jsonObj = new JSONObject(content);
	    JSONArray jsonArray = null;
	    if (jsonObj.has(table.getName()))
	        jsonArray = jsonObj.getJSONArray(table.getName());
	    else if (jsonObj.has(table.getName().concat("s")))
	        jsonArray = jsonObj.getJSONArray(table.getName().concat("s"));
	    if (jsonArray != null){
	        //Retorna o esquema normalizado a partir do original
	        Table nTable = nTableMap.get(table.getName());
	        for (Table nt : nTableMap.values()) {
	            dataMap.put(nt.getName(),new TableData(nt));
	        }
	        for (int i = 0; i < jsonArray.length(); i++) {
	            RowData row = dataMap.get(nTable.getName()).createRow();
	            JSONObject jObj = jsonArray.getJSONObject(i);
	            for (Element element : table.getElements()){
	                if (element.getClass().equals(Table.class)){
	                    Table subTable = (Table)element;
	                    Table nSubTable = nTableMap.get(subTable.getName());
	                    JSONArray jsonSubArray = null;
	                    if (jObj.has(subTable.getName()))
	                        jsonSubArray = jObj.getJSONArray(subTable.getName());
	                    else if (jObj.has(subTable.getName().concat("s")))
	                        jsonSubArray = jObj.getJSONArray(subTable.getName().concat("s"));
	                    if (jsonSubArray != null){
	                        HashMap<String, Object> pkMap = DataDivision.getKeyValues(nTable, jObj);
	                        if (nTable.getSurrogateKey() != null){
	                        	Object value = row.getValue(nTable.getSurrogateKey().getName());
	                        	pkMap.put(nTable.getSurrogateKey().getName(), value);
	                        }
	                        splitSubdata(subTable, nSubTable, nTableMap, jsonSubArray, pkMap, dataMap);
	                    }
	                }else{
	                    Column column = (Column)element;
	                    if (!column.isSurrogateKey()){
	                    	if (jObj.has(column.getName())){
	                    		Object value = jObj.get(column.getName());
	                    		row.setValue(column.getName(), value);
	                    	}
	                    }
	                }
	            }
	        }
	    }
	    //Ordena na ordem correta
	    DataList data = new DataList();
	    for (Table nTable : nTables) {
	        data.add(dataMap.get(nTable.getName()));
	    }
	    return data;
	}

	/**
	 * Divide uma tabela com atributos multi valorados em uma lista de dados
	 * @param data: dados originais
	 * @param columns: lista de colunas removidas da tabela original
	 * @return tabelas refatoradas
	 */
	public DataList splitColumnsToList(List<Column> columns, char[] separators, String newColumnName){
	    DataList list = new DataList();
	    Preprocess preprocess = new Preprocess(data);
	    TableData data1 = preprocess.removeColumns(columns);
	    list.add(data1);
	    for (Column column : columns) {
	    	String fkTableName = (newColumnName != null)? newColumnName : Util.toSingular(column.getName());
	        Table fkTable = new Table(fkTableName);
	        if (data.getTable().haveSurrogateKey())
	            fkTable.createSurrogateKey();
	        List<Column> parentKeys = data1.getTable().getPrimaryKeys();
	        for (Column pk : parentKeys) {
	            Column fkColumn = new Column(pk.getName());
	            fkColumn.setDbType(pk.getDbType());
	            fkColumn.setType(pk.getType());
	            fkColumn.setSize(pk.getSize());
	            fkColumn.setPrimaryKey(false);
	            fkColumn.setSurrogateKey(false);
	            fkColumn.setNotNull(true);
	            fkColumn.setForeignKey(pk);
	            fkTable.addColumn(fkColumn);
	        }
	        Column valueColumn = new Column(Options.getValueName(column.getName()));
	        valueColumn.setType(column.getType());
	        valueColumn.setDbType(column.getDbType());
	        valueColumn.setSize(column.getSize());
	        valueColumn.setUnique(column.isUnique());
	        fkTable.addColumn(valueColumn);
	        TableData fkData = new TableData(fkTable);
	        for (RowData row : data.getRows()) {
	            Object rowValue = row.getValue(column.getName());
	            if (rowValue != null){
	            	String[] rowValues = NormUtil.split(rowValue.toString(), separators);
	            	for (String value : rowValues) {
	            		RowData newRow = fkData.createRow();
	            		for (Column pk : parentKeys) {
	            			Object pkValue = row.getValue(pk.getName());
	            			newRow.setValue(pk.getName(), pkValue);
	            		}
	            		newRow.setValue(valueColumn.getName(), value.trim());
	            	}
	            }
	        }
	        list.add(fkData);
	    }
	    return list;
	}

	public DataList splitColumnsToList(char[] separators){
		NormUtil normalization = new NormUtil(data);
	    List<Column> columns = normalization.findMultiValuedColumns(separators);
	    return splitColumnsToList(columns, separators, null);
	}

	public DataList splitColumnsToList(char[] separators, String newTableName){
		NormUtil normalization = new NormUtil(data);
		List<Column> columns = normalization.findMultiValuedColumns(separators);
	    return splitColumnsToList(columns, separators, newTableName);
	}

	public DataList splitColumnToList(char[] separators, Column column, String newTableName){
		List<Column> columns = new ArrayList<>();
		columns.add(column);
		return splitColumnsToList(columns, separators, newTableName);
	}

	public DataList splitDependences(FD fd, String newTableName){
		DataList dl = new DataList();
		//Create sub table
		Table fkTable = new Table(newTableName);
		fkTable.createSurrogateKey();
		//Create list with all FD columns
		List<String> fdCNs = new LinkedList<>();
		fdCNs.addAll(fd.getOriSet().stream().collect(Collectors.toList()));
		fdCNs.addAll(fd.getDestSet().stream().collect(Collectors.toList()));
		fdCNs.forEach(cn->{
			Column c = data.getTable().getColumn(cn).clone();
			fkTable.addColumn(c);
		});
		//create replacement table
		Table rTable = new Table(data.getTable().getName());
		data.getTable().getColumns()
			.stream()
			.filter(c->!fdCNs.contains(c.getName()))
			.forEach(c->rTable.addColumn(c.clone()));
		Column fk = fkTable.getSurrogateKey().clone();
		fk.setPrimaryKey(false);
		fk.setSurrogateKey(false);
		fk.setUnique(false);
		fk.setForeignKey(fkTable.getSurrogateKey());
		rTable.addColumn(fk);
		TableData fkData = new TableData(fkTable);
		TableData rData = new TableData(rTable);
		HashMap<String, Object> fHash = new HashMap<>();
		data.getRows().forEach(row->{
			String combinatedValue = NormUtil.combineValues(row, fd);
			if (!fHash.containsKey(combinatedValue)){
				RowData fkRow = new RowData(fkTable);
				fkTable.getColumns().forEach(fkColumn->{
					fkRow.setValue(fkColumn.getName(), row.getValue(fkColumn.getName()));
				});
				fkData.addRow(fkRow);
				fHash.put(combinatedValue, fkRow.getSurrogeteKeyValue());
			}
			RowData rRow = new RowData(rTable);
			rTable.getColumns().forEach(rColumn->{
				rRow.setValue(rColumn.getName(), row.getValue(rColumn.getName()));
			});
			Object link = fHash.get(combinatedValue);
			if (link != null){
				rRow.setValue(fkTable.getSurrogateKey().getName(), link);
			}
			rData.addRow(rRow);
		});
		NormUtil normalization1 = new NormUtil(rData);
		normalization1.checkNormalForm();
		NormUtil normalization2 = new NormUtil(fkData);
		normalization2.checkNormalForm();
		dl.add(rData);
		dl.add(fkData);
		return dl;
	}

	public DataList splitDependences(String columnName, HashSet<Column> deps){
		Table table0 = new Table(data.getTable().getName());
		for (Column column : data.getTable().getColumns()) {
			if (!deps.contains(column))
				table0.addColumn(column.clone());
		}
		Table table1 = new Table(columnName);
		Column t1Pk = table0.getColumn(columnName).clone();
		t1Pk.setPrimaryKey(true);
		table1.addColumn(t1Pk);
		for (Column column : deps) {
			table1.addColumn(column);
		}
		table0.getColumn(columnName).setForeignKey(table1.getColumn(columnName));
		TableData dt0 = new TableData(table0);
		TableData dt1 = new TableData(table1);
		HashSet<Object> t2pks = new HashSet<>();
		for (RowData row : data.getRows()) {
			RowData r0 = dt0.createRow();
			for (Column column : table0.getColumns()) {
				r0.setValue(column.getName(), row.getValue(column.getName()));
			}
			Object pk = row.getValue(columnName);
			if (!t2pks.contains(pk)){
				t2pks.add(pk);
				RowData r1 = dt1.createRow();
				for (Column column : table1.getColumns()) {
					r1.setValue(column.getName(), row.getValue(column.getName()));
				}
			}
		}
		DataList list = new DataList();
		list.add(dt0);
		list.add(dt1);
		return list;
	}

	/**
	 * Retorna os valores das chaves primarias de um JSON
	 * @param table
	 * @param jObj
	 * @return Lista de valores
	 */
	private static HashMap<String, Object> getKeyValues(Table table, JSONObject jObj){
	    HashMap<String, Object> keys = new HashMap<>();
	    for (Element e : table.getColumns()) {
	        if (e.getClass().equals(Column.class)){
	            Column column = (Column)e;
	            if (column.isPrimaryKey() && !column.isSurrogateKey()){
	                Object value = jObj.get(column.getName());
	                keys.put(column.getName(), value);
	            }
	        }
	    }
	    return keys;
	}

	private static HashMap<String, Table> toHash(List<Table> tables){
	    HashMap<String, Table> map = new HashMap<>();
	    tables.forEach(t -> map.put(t.getName(), t));
	    return map;
	}

}
