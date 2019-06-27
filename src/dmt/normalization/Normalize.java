package dmt.normalization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import dmt.model.Column;
import dmt.model.Element;
import dmt.model.Table;
import dmt.model.data.NormalForm;
import dmt.model.data.RowData;
import dmt.model.data.TableData;
import dmt.model.project.DataList;
import dmt.normalization.fd.FD;
import dmt.normalization.fd.FDMapper;
import dmt.tools.IntCounter;
import dmt.tools.Options;
import dmt.tools.Util;

public class Normalize {

    /**
     * Procura coluna com valores unicos em dados
     * @param data
     * @return Lista de detalhes
     */
    public static List<Column> findUniqueColumns(TableData data){
        List<Column> list = new LinkedList<>();
        for (int i = 0; i < data.getTable().getElementCount(); i++) {
            if (!data.getTable().getElement(i).getClass().equals(Column.class))
                continue;
            final int index = i;
            List<String> strings = data.getRows().stream().map(dl -> {
            	if (dl.getValue(index) == null)
            		return null;
            	return dl.getValue(index).toString();
            }).collect(Collectors.toList());
            Set<String> setStrings = new HashSet<String>();
            boolean repeated = false;
            for (String string : strings) {
                if (setStrings.contains(string)){
                    repeated = true;
                    break;
                }else{
                    setStrings.add(string);
                }
            }
            if (!repeated){
                Column c = data.getTable().getColumn(i);
                list.add(c);
            }
        }
        return list;
    }

    private static boolean pkCandidate(String value){
        char[] ca = value.toCharArray();
        for (char c : ca) {
            if (!Character.isLetterOrDigit(c))
                return false;
        }
        return true;
    }

    /**
     * Procura nos dados colunas candiadatas a chave primaria
     * @param data: dados
     * @return lista de colunas candidatas
     */
    public static List<Column> findPrimaryKeyCandidates(TableData data){
        List<Column> list = new LinkedList<>();
        for (int i = 0; i < data.getTable().getElementCount(); i++) {
            if (!data.getTable().getElement(i).getClass().equals(Column.class))
                continue;
            final int index = i;
            List<String> strings = data.getRows().stream()
            		.filter(d1 -> d1.getValue(index) != null)
            		.map(dl -> dl.getValue(index).toString())
            		.collect(Collectors.toList());
            Set<String> setStrings = new HashSet<String>();
            boolean repeated = false;
            for (String string : strings) {
                if (setStrings.contains(string)){
                    repeated = true;
                    break;
                }else{
                    setStrings.add(string);
                }
            }
            if (!repeated){
                boolean candidate = true;
                for (String value : setStrings) {
                    if (!pkCandidate(value))
                        candidate = false;
                }
                if (candidate){
                    Column c = data.getTable().getColumn(i);
                    list.add(c);
                }
            }
        }
        return list;
    }

    /**
     * Normaliza uma tabela com tabelas aninhadas para uma lista de tabelas
     * @param table: tabela a ser normalizada
     * @param parentPKs: chave primaria da tabela pai
     * @return lista de tabelas
     */
    public static List<Table> splitNestedTables(Table table, List<Column> parentPKs){
        List<Table> list = new LinkedList<>();
        Table t1 = new Table(table.getName());
        if (parentPKs != null){
            for (Column key : parentPKs) {
                Column fk = key.clone();
                fk.setPrimaryKey(false);
                fk.setSurrogateKey(false);
                fk.setUnique(false);
                fk.setForeignKey(key);
                t1.addColumn(fk);
            }
        }
        list.add(t1);
        for (Element element : table.getElements()) {
            if (element.getClass().equals(Column.class)){
                Column c = (Column)element;
                t1.addColumn(c);//Removed clone
            }else if (element.getClass().equals(Table.class)){
                Table t = ((Table)element).clone();
                if (t1.haveSurrogateKey())
                    t.createSurrogateKey();
                list.addAll(splitNestedTables(t, table.getPrimaryKeys()));
            }
        }
        return list;
    }

    /**
     * Normaliza uma tabela com tabelas aninhadas para uma lista de tabelas
     * @param table: tabela a ser normalizada
     * @return lista de tabelas
     */
    public static List<Table> splitNestedTables(Table table){
        return splitNestedTables(table, null);
    }

    public static List<Column> getNestedColumns(Table table){
    	List<Column> columns = new LinkedList<>();
    	table.getElements().forEach(e->{
    		if (e.getClass().equals(Column.class)){
    			columns.add((Column)e);
    		}else{
    			columns.addAll(getNestedColumns((Table)e));
    		}
    	});
    	return columns;
    }

    /**
     * Normaliza uma tabela com tabelas aninhadas para uma tabela s�
     * @param table
     * @return tabela resultante
     */
    public static Table mixNestedTables(Table table){
    	Table nTable = new Table(table.getName());
    	table.getColumns().forEach(c->{
    		nTable.addColumn(c.clone());
    	});
    	table.getSubtables().forEach(t->{
    		List<Column> lc = getNestedColumns(t);
    		lc.forEach(sc->{
    			nTable.addColumn(sc.clone());
    		});
    	});
    	return nTable;
    }

    public static HashMap<String, Table> toHash(List<Table> tables){
        HashMap<String, Table> map = new HashMap<>();
        tables.forEach(t -> map.put(t.getName(), t));
        return map;
    }

    @Deprecated
    public static HashMap<String, Table> splitNestedTablesToHash(Table table){
        HashMap<String, Table> map = new HashMap<>();
        List<Table> tables = splitNestedTables(table);
        for (Table t : tables) {
            map.put(t.getName(), t);
        }
        return map;
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

    public static DataList splitNestedData(Table table, String content){
        if (!table.havePrimaryKey())
            table.createSurrogateKey();
        List<Table> nTables = splitNestedTables(table);
        HashMap<String, Table> nTableMap = toHash(nTables);
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
                            HashMap<String, Object> pkMap = getKeyValues(nTable, jObj);
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
     * Junta as tabelas aninhadas de um JSON em uma �nica coluna replicando os registros da tabela
     * @param table
     * @param jsonArray
     * @return Conjunto de dados
     */
    public static TableData mixNestedData(Table table, JSONArray jsonArray){
    	Table nTable = mixNestedTables(table);
    	nTable.createSurrogateKey();
    	TableData data = new TableData(nTable);
        if (jsonArray != null){
        	 for (int i = 0; i < jsonArray.length(); i++) {
        		 JSONObject jObj = jsonArray.getJSONObject(i);
        		 RowData row = data.createRow();
        		 table.getColumns().forEach(c->{
        			 if (!c.isSurrogateKey()){
                        if (jObj.has(c.getName())){
                        	Object value = jObj.get(c.getName());
                        	row.setValue(c.getName(), value);
                        }
                     }
        		 });
        		 table.getSubtables().forEach(t->{
        			 JSONArray jsonSubArray = null;
        			 if (jObj.has(t.getName()))
                         jsonSubArray = jObj.getJSONArray(t.getName());
                     else if (jObj.has(t.getName().concat("s")))
                         jsonSubArray = jObj.getJSONArray(t.getName().concat("s"));
                     if (jsonSubArray != null){
                    	 TableData subData = mixNestedData(t, jsonSubArray);
                    	 subData.getRows().forEach(r->{
                    		 if (subData.getRows().indexOf(r) > 0){
                    			 RowData newRow = new RowData(nTable);
                    			 table.getColumns().forEach(c->{
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

    public static TableData mixNestedData(Table table, String content){
    	JSONObject jsonObj = new JSONObject(content);
        JSONArray jsonArray = null;
        if (jsonObj.has(table.getName()))
            jsonArray = jsonObj.getJSONArray(table.getName());
        else if (jsonObj.has(table.getName().concat("s")))
            jsonArray = jsonObj.getJSONArray(table.getName().concat("s"));
        if (jsonArray == null)
        	throw new RuntimeException("mixNestedData: jsonArray = null!");
        return mixNestedData(table, jsonArray);
    }

    /**
     * Procura colunas multi-valoradas conforme separadores
     * @param data dados
     * @param separators divisores de valores selecioandos
     * @return lista de detalhes
     */
    public static List<Column> findMultiValuedColumns(TableData data, char[] separators){
        if (data.getTable().haveNestedTables())
            throw new RuntimeException("Normalize.findMultiValuedColumns: normalize nested tables first!");
        int[][] counts = new int[data.getRowCount()][data.getTable().getElementCount()];
        for (int i = 0; i < counts.length; i++) {
            for (int j = 0; j < counts[i].length; j++) {
                for (int k = 0; k < separators.length; k++) {
                	if (data.getRow(i).getValue(j) != null)
                		counts[i][j] += Util.countChar(separators[k], data.getRow(i).getValue(j).toString());
                }
            }
        }
        int[] sums = new int[data.getTable().getElementCount()];
        for (int i = 0; i < counts.length; i++) {
            for (int j = 0; j < counts[i].length; j++) {
                sums[j] += counts[i][j];
            }
        }
        List<Column> list = new LinkedList<>();
        for (int i = 0; i < sums.length; i++) {
            if (sums[i] > 0){
                list.add(data.getTable().getColumn(i));
            }
        }
        return list;
    }

    /**
     * Remove colunas de um conjunto de dados
     * @param data: dados originais
     * @param columns: colunas a serem removidas
     * @return novos dados (copia)
     */
    public static TableData removeColumns(TableData data, List<Column> columns){
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



    /**
     * Divide um texto em um vetor
     * @param text - texto a ser dividido
     * @param separators - separadores de divisao
     * @return vetor de strings
     */
    private static String[] split(String text, char[] separators){
        List<String> list = new LinkedList<>();
        char[] ca = text.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (char c : ca) {
            boolean isSep = false;
            for (char sep : separators) {
                if (c == sep){
                    isSep = true;
                    break;
                }
            }
            if (isSep){
                list.add(builder.toString());
                builder = new StringBuilder();
            }else{
                builder.append(c);
            }
        }
        if (builder.length() > 0)
            list.add(builder.toString());
        return list.toArray(new String[list.size()]);
    }

    /**
     * Divide uma tabela com atributos multi valorados em uma lista de dados
     * @param data: dados originais
     * @param columns: lista de colunas removidas da tabela original
     * @return tabelas refatoradas
     */
    public static DataList splitColumnsToList(TableData data, List<Column> columns, char[] separators, String newColumnName){
        DataList list = new DataList();
        TableData data1 = removeColumns(data, columns);
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
                	String[] rowValues = split(rowValue.toString(), separators);
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

    public static DataList splitColumnsToList(TableData data, char[] separators){
        List<Column> columns = findMultiValuedColumns(data, separators);
        return splitColumnsToList(data, columns, separators, null);
    }

    public static DataList splitColumnsToList(TableData data, char[] separators, String newTableName){
    	List<Column> columns = findMultiValuedColumns(data, separators);
        return splitColumnsToList(data, columns, separators, newTableName);
    }

    public static DataList splitColumnToList(TableData data, char[] separators, Column column, String newTableName){
    	List<Column> columns = new ArrayList<>();
    	columns.add(column);
    	return splitColumnsToList(data, columns, separators, newTableName);
    }
    /**
     * Replica os registros de uma tabela em uncao de uma coluna multi-valorada
     * @param data
     * @param column
     * @param separators
     * @return
     */
    public static TableData splitColumn(TableData data, Column column, char[] separators){
    	Table newTable = data.getTable().clone();
    	String newName = Util.toSingular(column.getName());
    	if (newName.compareTo(column.getName()) != 0)
    		newTable.setElementName(column.getName(), newName);
    	TableData newData = new TableData(newTable.clone());
    	IntCounter skg = new IntCounter();
    	data.getRows().forEach(r->{
    		//Colunas diferente de column
    		List<Column> oc = data.getTable().getColumns().stream()
    				.filter(c -> c.getName().compareTo(column.getName()) != 0)
    				.collect(Collectors.toList());
    		Object rowValue = r.getValue(column.getName());
    		if (rowValue != null){
    			String[] rowValues = split(rowValue.toString(), separators);
    			for (String value : rowValues) {
    				RowData row = new RowData(newTable);
    				//Setar o valor original
    				oc.forEach(c->{
    					if (!c.isSurrogateKey()){
    						row.setValue(c.getName(), Util.copyObject(r.getValue(c.getName())));
    					}else{
    						row.setValue(c.getName(), skg.inc());
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

    public static TableData splitColumns(TableData data, char[] separators){
    	List<Column> columns = findMultiValuedColumns(data, separators);
    	for (Column column : columns) {
    		data = splitColumn(data, column, separators);
		}
    	return data;
    }

    public static TableData splitColumn(TableData data, char[] separators, Column column){
    	return splitColumn(data, column, separators);
    }
    /**
     * Procura pela melhor correspond�ncia de tipo para cada coluna
     * @param data dados
     * @return lista de tipos
     */
    public static List<Class<?>> getBestMatchTypes(TableData data){
        List<Class<?>> types = new ArrayList<>();
        for (int i = 0; i < data.getTable().getElementCount(); i++) {
            HashSet<Object> values = data.getColumnSet(i);
            Class<?> c = TypeMatch.bestMatch(values);
            types.add(c);
        }
        return types;
    }

    /**
     * Muda os tipo dos dado para a melhor correspondencia
     * @param data
     * @param columns
     */
    public static void matchBestTypes(TableData data, int[] columns){
        if (columns == null){
            columns = new int[data.getTable().getElementCount()];
            for (int i = 0; i < columns.length; i++) {
                columns[i] = i;
            }
        }
        List<Class<?>> types = getBestMatchTypes(data);
        for (int j = 0; j < columns.length; j++) {
            data.getTable().getColumn(columns[j]).setType(types.get(j));
        }
        for (int i = 0; i < data.getRowCount(); i++) {
            for (int j = 0; j < columns.length; j++) {
                Class<?> newType = data.getTable().getColumn(columns[j]).getType();
                Object newValue = TypeMatch.convert(data.getRow(i).getValue(columns[j]), newType);
                data.getRow(i).setValue(columns[j], newValue);
            }
        }
    }

    public static Class<?> getBestMatchType(TableData data, String columnName){
        HashSet<Object> values = data.getColumnSet(columnName);
        return TypeMatch.bestMatch(values);
    }

    public static HashMap<String, Class<?>> getBestMatchTypeMap(TableData data){
        HashMap<String, Class<?>> map = new HashMap<>();
        for (Column column : data.getTable().getColumns()) {
            HashSet<Object> values = data.getColumnSet(column.getName());
            Class<?> bestType = TypeMatch.bestMatch(values);
            if (!column.getType().equals(bestType)){
                map.put(column.getName(), bestType);
            }
        }
        return map;
    }

    public static void changeType(TableData data, String columnName, Class<?> type){
        Column column = data.getTable().getColumn(columnName);
        column.setType(type);
        for (int i = 0; i < data.getRowCount(); i++) {
            Object newValue = TypeMatch.convert(data.getRow(i).getValue(columnName), type);
            data.getRow(i).setValue(columnName, newValue);
        }
    }

    public static void changeTypes(TableData data, HashMap<String, Class<?>> map){
        map.forEach((columnName,type) -> {
            Column column = data.getTable().getColumn(columnName);
            column.setType(type);
            for (int i = 0; i < data.getRowCount(); i++) {
                Object newValue = TypeMatch.convert(data.getRow(i).getValue(columnName), type);
                data.getRow(i).setValue(columnName, newValue);
            }
        });
    }

    public static void matchBestTypes(TableData data){
        HashMap<String, Class<?>> map = getBestMatchTypeMap(data);
        changeTypes(data, map);
    }

    public static void setStringSizes(TableData data, int plusSize){
    	for (RowData row : data.getRows()) {
    		for (int i = 0; i < row.getTable().getElementCount(); i++) {
				if (row.getTable().getElement(i).getClass().equals(Column.class)){
					Column c = (Column)row.getTable().getElement(i);
					if (c.getType().equals(String.class)){
						Set<Object> values = data.getColumnSet(i);
						int maxSize = 0;
						for (Object object : values) {
							String s = (String)object;
							if (s.length() > maxSize)
								maxSize = s.length();
						}
						maxSize += plusSize;
						data.getTable().getColumn(i).setSize(maxSize);
					}
				}
			}
		}
    }


    public static void printDependences(HashMap<String, HashSet<Column>> deps){
        StringBuilder builder = new StringBuilder();
        deps.forEach((s,hs)->{
            builder.append(s);
            builder.append(" -> ");
            builder.append("{");
            final int startLenght = builder.length();
            hs.forEach(c->{
                if (builder.length() > startLenght)
                    builder.append(", ");
                builder.append(((Column)c).getName());
            });
            builder.append("}\n");
        });
        System.out.println(builder.toString());
    }

    public static String getMajorDeterminant(HashMap<String, HashSet<Column>> deps){
    	int max = 0;
    	String columnName = null;
    	for (String key : deps.keySet()) {
			if (deps.get(key).size() > max){
				max = deps.get(key).size();
				columnName = key;
			}
		}
    	return columnName;
    }

    public static DataList splitDependences(TableData data, String columnName, HashSet<Column> deps){
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

    public static String combineValues(RowData row,FD fd){
    	Set<String> set = new TreeSet<>();
		set.addAll(fd.getOriSet());
		set.addAll(fd.getDestSet());
    	StringBuilder builder = new StringBuilder();
    	set.forEach(cn->{
    		Object value = row.getValue(cn);
    		if (value != null)
    			builder.append(value.toString());
    	});
    	return builder.toString();
    }

    public static DataList splitDependences(TableData data, FD fd, String newTableName){
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
    		String combinatedValue = combineValues(row, fd);
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
    	checkNormalForm(rData);
    	checkNormalForm(fkData);
    	dl.add(rData);
    	dl.add(fkData);
    	return dl;
    }

    public static TableData splitColumn(TableData data, String columnName, String cn1, String cn2, List<String> c1Values, List<String> c2Values){
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

    public static void checkNormalForm(TableData data){
    	List<Column> mvc = findMultiValuedColumns(data, Options.getDefaultSeparators());
    	if (mvc.size() > 0){
    		data.setNormalForm(NormalForm.NN);
    	}else{
    		FDMapper mapper = new FDMapper(data);
    		mapper.setMaxData(Options.getDefaultSampleSize());
    		mapper.setMaxLevel(1);
    		List<FD> fd = mapper.getFDs();
    		if (fd.size() > 0){
    			data.setNormalForm(NormalForm.NF2);
    		}else{
    			data.setNormalForm(NormalForm.NF3);
    		}
    	}
    }

}
