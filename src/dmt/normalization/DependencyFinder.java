package dmt.normalization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dmt.input.CSVReader;
import dmt.model.Column;
import dmt.model.data.TableData;
import dmt.tools.Benchmark;
import dmt.tools.Options;

public class DependencyFinder {

	private TableData data;
	private double tolerance = 1.0;

	public DependencyFinder(TableData data) {
		this.data = data;
	}

	public double getTolerance() {
		return tolerance;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	private double getDetermination(String cn1, String cn2){
		DependencyMap map = new DependencyMap();
		data.getRows().forEach(row->{
			Object o1 = row.getValue(cn1);
			Object o2 = row.getValue(cn2);
			map.addValues(o1, o2);
		});
		return map.getMinDependencePerc();
	}

	public HashSet<Column> getDependencies(String columnName){
        final boolean ignorePks = (data.getTable().getPrimaryKeys().size() == 1);
        List<Column> oc = data.getTable()
                .getColumns()
                .stream()
                .filter(c -> {
                    if (c.isPrimaryKey() && ignorePks)
                        return false;
                    return (c.getName().compareTo(columnName) != 0);
                })
                .collect(Collectors.toList());
        HashSet<Column> dependences = new HashSet<>();
        for (Column column : oc) {
            if (getDetermination(columnName, column.getName()) >= tolerance){
                dependences.add(column);
            }
        }
        return dependences;
    }

	public HashMap<String, HashSet<Column>> getAllDependencies(){
    	final int numPks = data.getTable().getPrimaryKeys().size();
    	HashMap<String, HashSet<Column>> map = new HashMap<>();
    	List<Column> uniqueColumns = Normalize.findUniqueColumns(data);
    	List<Column> columns = data.getTable().getColumns().parallelStream().filter(c -> {
    				if (c.isPrimaryKey() && numPks == 1)
    					return false;
    				if (!c.isPrimaryKey() && c.isUnique())
    					return false;
    				if (uniqueColumns.contains(c))
    					return false;
    				return true;
    			})
    			.collect(Collectors.toList());
    	Stream<Column> stream = (Options.paralelize())?columns.parallelStream():columns.stream();
        stream.forEach(column->{
        	HashSet<Column> set = getDependencies(column.getName());
            map.put(column.getName(), set);
        });
        return map.entrySet().stream()
        	    .sorted((e1,e2)->(new Integer(e1.getValue().size()).compareTo(new Integer(e2.getValue().size())))*-1)
        	    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,(e1, e2) -> e1, LinkedHashMap::new));
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
	
	public static String depToString(String columnName, HashSet<Column> depSet){
        StringBuilder builder = new StringBuilder();
        builder.append(columnName);
        builder.append(" -> ");
        builder.append("{");
        final int startLenght = builder.length();
        depSet.forEach(c->{
            if (builder.length() > startLenght)
                builder.append(", ");
            builder.append(((Column)c).getName());
        });
        builder.append("}\n");
        return builder.toString();
    }

	public static void main(String[] args) {
		Benchmark b = new Benchmark();
		CSVReader reader = new CSVReader("data/CADASTRO_MATRICULAS_REGIAO_SUL_2012.csv");
		reader.setColumnDescriptionsIndex(1);
		reader.setColumnNamesIndex(0);
		String[][] sample = reader.getSample(';', '"', 100);
        TableData data = reader.getData(sample);
        TableData newData = Normalize.splitColumns(data, new char[]{'\n'});
        System.out.println(newData.getTable());
        DependencyFinder finder = new DependencyFinder(newData);
        //System.out.println(finder.getDetermination("Cat", "Endereco"));
        //System.out.println(finder.getDetermination("Cat", "Salario"));
        HashMap<String, HashSet<Column>> deps = finder.getAllDependencies();
        deps.keySet().forEach(k->{
        	System.out.println(k+": "+deps.get(k).size());
        });
        /*
        */
        b.stop(true);
	}

}
