package dmt.normalization;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import dmt.model.Column;
import dmt.model.Table;
import dmt.model.data.RowData;
import dmt.model.data.TableData;
import dmt.tools.Util;

public class DataReplication {

	private TableData data;
	
	public DataReplication(TableData data) {
		super();
		this.data = data;
	}

	public TableData splitNestedData(String content){
		Table table = data.getTable();
		JSONObject jsonObj = new JSONObject(content);
	    JSONArray jsonArray = null;
	    if (jsonObj.has(table.getName()))
	        jsonArray = jsonObj.getJSONArray(table.getName());
	    else if (jsonObj.has(table.getName().concat("s")))
	        jsonArray = jsonObj.getJSONArray(table.getName().concat("s"));
	    if (jsonArray == null)
	    	throw new RuntimeException("mixNestedData: jsonArray = null!");
	    return splitNestedData(jsonArray);
	}

	/**
	 * Junta as tabelas aninhadas de um JSON em uma unica coluna replicando os registros da tabela
	 * @param table
	 * @param jsonArray
	 * @return Conjunto de dados
	 */
	private TableData splitNestedData(JSONArray jsonArray){
		TableReplication tr = new TableReplication(data.getTable());
		Table nTable = tr.mixNestedTables();
		nTable.createSurrogateKey();
		TableData data = new TableData(nTable);
	    if (jsonArray != null){
	    	 for (int i = 0; i < jsonArray.length(); i++) {
	    		 JSONObject jObj = jsonArray.getJSONObject(i);
	    		 RowData row = data.createRow();
	    		 data.getTable().getColumns().forEach(c->{
	    			 if (!c.isSurrogateKey()){
	                    if (jObj.has(c.getName())){
	                    	Object value = jObj.get(c.getName());
	                    	row.setValue(c.getName(), value);
	                    }
	                 }
	    		 });
	    		 data.getTable().getSubtables().forEach(t->{
	    			 JSONArray jsonSubArray = null;
	    			 if (jObj.has(t.getName()))
	                     jsonSubArray = jObj.getJSONArray(t.getName());
	                 else if (jObj.has(t.getName().concat("s")))
	                     jsonSubArray = jObj.getJSONArray(t.getName().concat("s"));
	                 if (jsonSubArray != null){
	                	 TableData subData = splitNestedData(jsonSubArray);
	                	 subData.getRows().forEach(r->{
	                		 if (subData.getRows().indexOf(r) > 0){
	                			 RowData newRow = new RowData(nTable);
	                			 data.getTable().getColumns().forEach(c->{
	                				 newRow.setValue(c.getName(), row.getValue(c.getName()));
	                			 });
	                			 data.addRow(newRow);
	                			 r.getTable().getColumns().forEach(rc->{
	                				 newRow.setValue(rc.getName(), r.getValue(rc.getName()));
	                			 });
	                		 }else{
	                			 r.getTable().getColumns().forEach(rc->{
	                				 row.setValue(rc.getName(), r.getValue(rc.getName()));
	                			 });
	                		 }
	                	 });
	                 }
	    		 });
	    	 }
	    }
		return data;
	}
	/**
	 * Replica os registros de uma tabela em uncao de uma coluna multi-valorada
	 * @param data
	 * @param column
	 * @param separators
	 * @return
	 */
	public TableData splitColumn(Column column, char[] separators){
		Table newTable = data.getTable().clone();
		String newName = Util.toSingular(column.getName());
		if (newName.compareTo(column.getName()) != 0)
			newTable.setElementName(column.getName(), newName);
		TableData newData = new TableData(newTable.clone());
		data.getRows().forEach(r->{
			//Colunas diferente de column
			List<Column> oc = data.getTable().getColumns().stream()
					.filter(c -> c.getName().compareTo(column.getName()) != 0)
					.collect(Collectors.toList());
			Object rowValue = r.getValue(column.getName());
			if (rowValue != null){
				String[] rowValues = NormUtil.split(rowValue.toString(), separators);
				for (String value : rowValues) {
					RowData row = new RowData(newTable);
					//Setar o valor original
					oc.forEach(c->{
						if (!c.isSurrogateKey()){
							row.setValue(c.getName(), Util.copyObject(r.getValue(c.getName())));
						}
					});
					//Setar o novo valor
					row.setValue(newName, value);
					newData.addRow(row);
				}
			}
		});
		return newData;
	}

	public TableData splitColumns(char[] separators, List<Column> columns){
		for (Column column : columns) {
			data = splitColumn(column, separators);
		}
		return data;
	}

	public TableData splitColumn(char[] separators, Column column){
		return splitColumn(column, separators);
	}

	public TableData splitColumns(char[] separators){
		NormUtil normalization = new NormUtil(data);
		List<Column> columns = normalization.findMultiValuedColumns(separators);
		for (Column column : columns) {
			data = splitColumn(column, separators);
		}
		return data;
	}

	public TableData getData() {
		return data;
	}

	public void setData(TableData data) {
		this.data = data;
	}

}
