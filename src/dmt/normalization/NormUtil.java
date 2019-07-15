package dmt.normalization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import dmt.model.Column;
import dmt.model.data.NormalForm;
import dmt.model.data.RowData;
import dmt.model.data.TableData;
import dmt.normalization.fd.FD;
import dmt.normalization.fd.FDMapper;
import dmt.tools.IntCounter;
import dmt.tools.Options;
import dmt.tools.Util;

public class NormUtil {

	private TableData data;
	
    public NormUtil(TableData data) {
		super();
		this.data = data;
	}

	public TableData getData() {
		return data;
	}

	public void setData(TableData data) {
		this.data = data;
	}

	/**
     * Procura coluna com valores unicos em dados
     * @param data
     * @return Lista de detalhes
     */
    public List<Column> findUniqueColumns(){
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
            HashSet<String> set = new HashSet<>();
            set.addAll(strings);
            if (set.size() == data.getRowCount()){
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
    @Deprecated
    public List<Column> findPrimaryKeyCandidates(){
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
     * Procura colunas multi-valoradas conforme separadores
     * @param data dados
     * @param separators divisores de valores selecioandos
     * @return lista de detalhes
     */
    public List<Column> findMultiValuedColumns(char[] separators){
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
     * Divide um texto em um vetor
     * @param text - texto a ser dividido
     * @param separators - separadores de divisao
     * @return vetor de strings
     */
    protected static String[] split(String text, char[] separators){
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

    public void remakeSks(){
    	if (data.getTable().haveSurrogateKey()){
    		IntCounter ic = new IntCounter();
    		Column sk = data.getTable().getSurrogateKey();
    		data.getRows().forEach(row->{
    			row.setValue(sk.getName(), ic.inc());
    		});
    	}
    }
    
    /**
     * Procura pela melhor correspondencia de tipo para cada coluna
     * @param data dados
     * @return lista de tipos
     */
    public List<Class<?>> getBestMatchTypes(){
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
    public void matchBestTypes(int[] columns){
        if (columns == null){
            columns = new int[data.getTable().getElementCount()];
            for (int i = 0; i < columns.length; i++) {
                columns[i] = i;
            }
        }
        List<Class<?>> types = getBestMatchTypes();
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

    public Class<?> getBestMatchType(String columnName){
        HashSet<Object> values = data.getColumnSet(columnName);
        return TypeMatch.bestMatch(values);
    }

    public HashMap<String, Class<?>> getBestMatchTypeMap(){
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

    public void matchBestTypes(){
        HashMap<String, Class<?>> map = getBestMatchTypeMap();
        changeTypes(data, map);
    }

    public void setStringSizes(int plusSize){
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

    public void checkNormalForm(){
    	List<Column> mvc = findMultiValuedColumns( Options.getDefaultSeparators());
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
