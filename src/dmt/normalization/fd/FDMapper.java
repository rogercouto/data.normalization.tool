package dmt.normalization.fd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.ProgressBar;

import dmt.input.CSVReader;
import dmt.model.Column;
import dmt.model.data.RowData;
import dmt.model.data.TableData;
import dmt.normalization.Normalize;
import dmt.tools.Benchmark;
import dmt.tools.IntCounter;
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

	@SuppressWarnings("unused")
	@Deprecated
	private List<Set<String>> columnComb(List<Column> columns){
		List<String> columnNames = columns.stream().map(c->c.getName()).collect(Collectors.toList());
		SortedSet<SortedSet<Comparable<String>>> allCombList = new TreeSet<SortedSet<Comparable<String>>>(new Comparator<SortedSet<Comparable<String>>>() {
			@Override
			public int compare(SortedSet<Comparable<String>> o1, SortedSet<Comparable<String>> o2) {
				int sizeComp = o1.size() - o2.size();
				if (sizeComp == 0) {
					Iterator<Comparable<String>> o1iIterator = o1.iterator();
					Iterator<Comparable<String>> o2iIterator = o2.iterator();
					while (sizeComp == 0 && o1iIterator.hasNext() ) {
						sizeComp = o1iIterator.next().compareTo((String) o2iIterator.next());
					}
				}
				return sizeComp;
			}
		});
		columnNames.forEach(cn->{
			allCombList.add(new TreeSet<Comparable<String>>(Arrays.asList(cn)));
		});
		for (int nivel = 1; nivel < columnNames.size(); nivel++) {
			List<SortedSet<Comparable<String>>> statusAntes = new ArrayList<SortedSet<Comparable<String>>>(allCombList);
			for (Set<Comparable<String>> antes : statusAntes) {
				SortedSet<Comparable<String>> novo = new TreeSet<Comparable<String>>(antes);
				novo.add(columnNames.get(nivel));
				allCombList.add(novo);
			}
		}
		List<Set<String>> res = new ArrayList<>();
		allCombList.forEach(s->{
			Set<String> subSet = new TreeSet<>();
			s.forEach(ss->{
				subSet.add(ss.toString());
			});
			if (subSet.size() < columns.size())
			res.add(subSet);
		});
		return res;
	}
	
	private HashMap<String, HashMap<String,FDMap>> generateMap(){
		List<Column> uColumns = Normalize.findUniqueColumns(data);
		if (columns == null){
			columns = data.getTable()
					.getColumns()
					.stream()
					.filter(c->!uColumns.contains(c))
					.collect(Collectors.toList());
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
				if (fdMap.getMinDependencePerc()>=tolerance){
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
		res.entrySet().stream()
	    .sorted((e1,e2)->(new Integer(e1. getValue().getDestSet().size()).compareTo(new Integer(e2.getValue().getDestSet().size())))*-1)
	    .map(r->r.getValue())
	    .collect(Collectors.toList());
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
		testOld();
	}

	protected static void testZero(){
		CSVReader reader = new CSVReader("data/funcionario_dm.csv");
		String[][] sample = reader.getSample(';', '"', -1);
		TableData data = reader.getData(sample);
		FDMapper mapper = new FDMapper(data);
		mapper.setMaxLevel(3);
		List<FD> fds = mapper.getFDs();
		fds.forEach(fd->{
			System.out.println(fd.getOriSet()+" -> "+fd.getDestSet());
		});
	}
	
	protected static void testNew(){
		CSVReader reader = new CSVReader("data/CADASTRO_MATRICULAS_REGIAO_SUL_2012.csv");
		reader.setColumnDescriptionsIndex(0);
		reader.setColumnNamesIndex(1);
		String[][] sample = reader.getSample(';', '"', 100);
		TableData data = reader.getData(sample);
		List<Column> list0 = data.getTable().getColumns();
		List<Column> list = new ArrayList<Column>();
		IntCounter ic = new IntCounter();
		while (ic.getValue() < 10){
			list.add(list0.get(ic.getValue()));
			ic.inc();
		}
		FDMapper mapper = new FDMapper(data);
		mapper.setColumns(list);
		mapper.setMaxLevel(3);
		List<FD> fds = mapper.getFDs();
		fds.forEach(fd->{
			System.out.println(fd.getOriSet()+" -> "+fd.getDestSet());
		});
	}
	
	protected static void testOld(){
		Benchmark b = new Benchmark();
		System.out.println("READING DATA ...");
		CSVReader reader = new CSVReader("data/CADASTRO_MATRICULAS_REGIAO_SUL_2012.csv");
		reader.setColumnDescriptionsIndex(0);
		reader.setColumnNamesIndex(1);
		String[][] sample = reader.getSample(';', '"', -1);
		TableData data = reader.getData(sample);
		b.stop(true);
		/*
		System.out.println("GETTING DEPENDENCES OLD ...");
		b.start();
		data.getTable().getColumn(1).setPrimaryKey(true);
		DependencyFinder df = new DependencyFinder(data);
		System.out.println(df.getAllDependencies().size());
		b.stop(true);
		*/
		b.start();
		System.out.println("GETTING DEPENDENCES NEW ...");
		FDMapper mapper = new FDMapper(data);
		mapper.setMaxLevel(1);
		mapper.setMaxData(300);
		List<FD> fds = mapper.getFDs();
		System.out.println(fds.size());
		b.stop(true);
	}
	
}
