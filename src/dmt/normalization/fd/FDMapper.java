package dmt.normalization.fd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.ProgressBar;

import dmt.input.CSVReader;
import dmt.model.Column;
import dmt.model.data.RowData;
import dmt.model.data.TableData;
import dmt.normalization.Normalize;
import dmt.tools.IntCounter;
import dmt.tools.Options;
import dmt.tools.Util;

public class FDMapper {

	private TableData data;
	private double tolerance = 1.0;
	private int maxLevel = 5;
	private List<Column> columns = null;
	private int maxData = 50;
	private ProgressBar progressBar;

	public FDMapper(TableData data) {
		super();
		this.data = data;
	}

	public FDMapper(TableData data, double tolerance) {
		super();
		this.data = data;
		this.tolerance = tolerance;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public double getTolerance() {
		return tolerance;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	public int getMaxData() {
		return maxData;
	}

	public void setMaxData(int maxData) {
		this.maxData = maxData;
	}

	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
		if (this.progressBar != null){
			progressBar.setMaximum(100);
		}
	}

	public static List<Column> getCandidateColumns(TableData data){
		List<Column> uColumns = Normalize.findUniqueColumns(data);
		return data.getTable()
				.getColumns()
				.stream()
				.filter(c->{
					if (c.isPrimaryKey())
						return false;
					if (uColumns.contains(c))
						return false;
					List<Object> values = data.getColumnValues(c.getName());
					Set<Object> set = new HashSet<>();
					set.addAll(values);
					if (set.size() < 2)
						return false;
					long l = values.stream().filter(o->o==null||o.toString().trim().length()==0).count();
					double emptyRate = (double)l /(double)values.size();
					if (emptyRate > Options.maxEmptyRate())
						return false;
					return true;
				})
				.collect(Collectors.toList());
	}

	private HashMap<String, HashMap<String,FDMap>> generateMap(){
		if (columns == null){
			columns = getCandidateColumns(data);
		}
		List<Set<String>> cand = combineColumnNames(columns);
		HashMap<String, HashMap<String,FDMap>> hash = new HashMap<>();
		cand.forEach(set->{
			List<String> targets = columns
					.stream()
					.filter(c->!set.contains(c.getName()))
					.map(c->c.getName())
					.collect(Collectors.toList());
			HashMap<String, FDMap> subHash = new HashMap<>();
			targets.forEach(t->{
				subHash.put(t,new FDMap(set, t));
			});
			hash.put(Util.concat(set), subHash);
		});
		List<RowData> sample = data.getRows()
				.stream()
				.filter(row->data.getRows().indexOf(row) < maxData-1)
				.collect(Collectors.toList());
		sample.forEach(row->{
			hash.forEach((key,subHash)->{
				subHash.forEach((subKey, ndMap)->{
					ndMap.add(row);
				});
			});
		});
		return hash;
	}

	public List<FD> getFDs(){
		if (progressBar != null)
			progressBar.setSelection(0);
		HashMap<String, FD> res = new HashMap<>();
		HashMap<String, HashMap<String,FDMap>> map = generateMap();
		map.forEach((k1,subHash)->{
			subHash.forEach((k2,fdMap)->{
				if (progressBar != null)
					progressBar.setSelection(progressBar.getSelection()+1);
				if (fdMap.getAvgDependencePerc()>=tolerance){
					String key = fdMap.getKey();
					if (res.containsKey(key)){
						FD fd = res.get(key);
						fd.addDestColumnName(fdMap.getDestColumnName());
					}else{
						FD fd = new FD();
						fd.setOriSet(fdMap.getKeySet());
						fd.addDestColumnName(fdMap.getDestColumnName());
						res.put(key, fd);
					}
				}
			});
		});
		return
		res.entrySet()
		.stream()
		.filter(fd->{
			int size = fd.getValue().getOriSet().size()+fd.getValue().getDestSet().size();
			if (data.getTable().getColumns().size()==size+1)
				return false;
			return true;
		})
	    .sorted((e1,e2)->(new Integer(e1. getValue().getDestSet().size()).compareTo(new Integer(e2.getValue().getDestSet().size())))*-1)
	    .map(r->r.getValue())
	    .collect(Collectors.toList());
	}
	
	public List<SingleFD> getSingleFDs(){
		List<SingleFD> list = new ArrayList<>();
		HashMap<String, HashMap<String,FDMap>> map = generateMap();
		map.forEach((k1,subHash)->{
			subHash.forEach((k2,fdMap)->{
				SingleFD sfd = new SingleFD();
				sfd.setOri(k1);
				sfd.setDest(k2);
				sfd.setDep(fdMap.getAvgDependencePerc());
				list.add(sfd);
			});
		});
		list.sort(new Comparator<SingleFD>() {
			@Override
			public int compare(SingleFD o1, SingleFD o2) {
				return new Double(o2.getDep()).compareTo(new Double(o1.getDep()));
			}
		});
		return list;
	}

	private Stack<String> createStack(List<String> list, String string){
		List<String> newList = list.stream().filter(s->s.compareTo(string)!=0).collect(Collectors.toList());
		Collections.reverse(newList);
		Stack<String> stack = new Stack<>();
		stack.addAll(newList);
		return stack;
	}

	private String getFirst(Set<String> set){
		Optional<String> opS = set.stream().findFirst();
		if (opS.isPresent())
			return opS.get();
		throw new RuntimeException("FDMapper.getFirst: empty set!");
	}

	private List<Set<String>> getLastLevelItems(List<Set<String>> list, int level){
		return list.stream().filter(set->set.size()==level-1).collect(Collectors.toList());
	}

	private boolean containsAll(List<Set<String>> list, Set<String> set){
		Optional<Set<String>> o = list
				.stream()
				.filter(lSet->lSet.containsAll(set))
				.findFirst();
		return o.isPresent();
	}

	private int incLevel(List<Set<String>> list, List<String> columnNames, int level){
		level++;
		List<Set<String>> newElements = new LinkedList<>();
		List<Set<String>> lastLevelItems = getLastLevelItems(list, level);
		lastLevelItems.forEach(set->{
			String first = getFirst(set);
			Stack<String> le = createStack(columnNames, first);
			while (!le.isEmpty()){
				String lastElement = le.pop();
				if (!set.contains(lastElement)){
					Set<String> newSet = new TreeSet<>();
					newSet.addAll(set);
					newSet.add(lastElement);
					if (!containsAll(newElements, newSet))
						newElements.add(newSet);
				}
			}
		});
		list.addAll(newElements);
		return level;
	}

	private List<Set<String>> combineColumnNames(List<Column> columns){
		List<String> columnNames = columns.stream().map(c->c.getName()).collect(Collectors.toList());
		List<Set<String>> list = new LinkedList<>();
		columnNames.forEach(cn->{
			Set<String> set = new TreeSet<>();
			set.add(cn);
			list.add(set);
		});
		int level = 1;
		while (level < maxLevel)
			level = incLevel(list, columnNames, level);
		return list;
	}

	public static void main(String[] args) {
		CSVReader reader = new CSVReader("data/test/upa.csv");
		String[][] sample = reader.getSample(',', '"', -1);
		TableData data = reader.getData(sample);
		test2(data);
		//getCandidateColumns(data).forEach(c->System.out.println(c.getName()));
	}

	protected static void test0(TableData data){
		List<Object> values = data.getColumnValues("codigo");
		HashSet<Object> set = new HashSet<>();
		IntCounter ic = new IntCounter();
		values.forEach(v->{
			if (set.contains(v)){
				ic.inc();
				System.out.println(v);
			}else{
				set.add(v);
			}
		});
		System.out.println("repetidos: "+ic.getValue());
	}

	protected static void test1(TableData data){
		List<Column> uColumns = Normalize.findUniqueColumns(data);
		List<Column> columns = data.getTable()
				.getColumns()
				.stream()
				.filter(c->!c.isPrimaryKey()
						&&!uColumns.contains(c)
						&&data.getColumnSet(c.getName()).size()>1)
				.collect(Collectors.toList());

		Column c1 = columns.get(6);
		System.out.println(c1.getName());
		Column c2 = columns.get(1);
		System.out.println(c2.getName());
		HashMap<Object, HashMap<Object, Integer>> map = new HashMap<>();
		data.getRows().forEach(row->{
			Object v1 = row.getValue(c1.getName());
			Object v2 = row.getValue(c2.getName());
			if (!map.containsKey(v1))
				map.put(v1, new HashMap<Object, Integer>());
			HashMap<Object, Integer> subHash = map.get(v1);
			if (subHash.containsKey(v2)){
				int count = subHash.get(v2).intValue();
				subHash.put(v2, count + 1);
			}else{
				subHash.put(v2, 1);
			}

		});
		map.forEach((o,sub)->{
			if (sub.size() > 1)
				System.out.print("***");
			if (sub.size() > 0){
				System.out.print(o.toString());
				System.out.print(": {");
				IntCounter ic = new IntCounter();
				sub.forEach((o2, i)->{
					if (ic.getValue() > 0)
						System.out.print(", ");
					System.out.print(o2.toString());
					System.out.print(":");
					System.out.print(i);
					ic.inc();
				});
				System.out.print("(");
				System.out.print(sub.size());
				System.out.println(")} ");
			}
			/*
			System.out.print(sub.size());
			*/
		});
	}

	protected static void test2(TableData data){
		FDMapper mapper = new FDMapper(data);
		mapper.setMaxLevel(1);
		mapper.setMaxData(data.getRowCount());
		mapper.getSingleFDs().forEach(System.out::println);
	}
	
}
