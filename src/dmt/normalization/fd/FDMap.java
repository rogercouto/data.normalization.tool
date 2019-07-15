package dmt.normalization.fd;

import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Set;

import dmt.model.data.RowData;
import dmt.tools.Util;

public class FDMap {

	private Set<String> columnNames;
	private String destColumnName;

	private HashMap<Object, HashMap<Object, Integer>> map = new HashMap<>();

	public FDMap(Set<String> columnNames, String targetColumnName) {
		super();
		this.columnNames = columnNames;
		this.destColumnName = targetColumnName;
	}

	public void add(RowData row){
		StringBuilder builder = new StringBuilder();
		columnNames.forEach(cn->{
			builder.append(row.getValue(cn));
		});
		if (builder.length() == 0)
			return;
		Object c1Value = builder.toString();
		Object c2Value = row.getValue(destColumnName);
		if (c2Value == null)
			return;
		if (!map.containsKey(c1Value)){
			map.put(c1Value, new HashMap<Object, Integer>());
		}
		if (map.get(c1Value).isEmpty()){
			map.get(c1Value).put(c2Value, 1);
			return;
		}else{
			HashMap<Object, Integer> map2 = map.get(c1Value);
			if (map2.containsKey(c2Value)){
				int count = map2.get(c2Value).intValue();
				map2.put(c2Value, count+1);
			}else{
				map2.put(c2Value, 1);
			}
		}
	}

	public double getDependencePerc(Object c1Value){
		if (map.size() == 1)
			return 1.0;
		int total = 0;
		int max = 0;
		for (Object key : map.get(c1Value).keySet()) {
			int count = map.get(c1Value).get(key).intValue();
			total += count;
			if (count > max)
				max = count;
		}
		return (double)max/(double)total;
	}

	@Deprecated
	public double getMinDependencePerc(){
		double min = 1.0;
		for (Object key : map.keySet()) {
			double perc = getDependencePerc(key);
			if (perc < min)
				min = perc;
		}
		return min;
	}

	public double getAvgDependencePerc(){
		DoubleSummaryStatistics st = map.entrySet().stream().mapToDouble(e-> getDependencePerc(e.getKey())).summaryStatistics();
		return st.getAverage();
	}


	public String getKey(){
		return Util.concat(columnNames);
	}

	public String getDestColumnName(){
		return destColumnName;
	}

	public Set<String> getKeySet(){
		return columnNames;
	}

	protected HashMap<Object, HashMap<Object, Integer>> getMap(){
		return map;
	}
}
