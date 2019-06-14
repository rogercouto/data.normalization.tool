package dmt.model.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import dmt.model.Table;
import dmt.model.data.RowData;
import dmt.model.data.TableData;

public class DataList implements List<TableData>,Serializable,Cloneable {

	private static final long serialVersionUID = 3139770868619027515L;

	private List<TableData> list = new ArrayList<>();
	private HashMap<String, Integer> map = new HashMap<>();

	public DataList() {
		super();
	}

	public DataList(TableData data) {
		super();
		add(data);
	}

	private void remap(){
		map = new HashMap<>();
		list.forEach(d->{
			map.put(d.getTable().getName(), list.indexOf(d));
		});
	}

	@Override
	public boolean add(TableData data) {
		boolean res = list.add(data);
		map.put(data.getTable().getName(), list.size()-1);
		return res;
	}

	@Override
	public void add(int index, TableData data) {
		list.add(index, data);
		remap();
	}

	@Override
	public boolean addAll(Collection<? extends TableData> collection) {
		boolean res = list.addAll(collection);
		remap();
		return res;
	}

	@Override
	public boolean addAll(int index, Collection<? extends TableData> data) {
		list.addAll(index, data);
		remap();
		return true;
	}

	@Override
	public void clear() {
		list.clear();
		map = new HashMap<>();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public TableData get(int index) {
		return list.get(index);
	}

	@Override
	public int indexOf(Object o) {
		if (o.getClass().equals(TableData.class)){
			TableData data = (TableData)o;
			return map.get(data.getTable().getName());
		}
		return -1;
	}

	@Override
	public boolean isEmpty() {
		return list.size() == 0;
	}

	@Override
	public Iterator<TableData> iterator() {
		return list.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<TableData> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<TableData> listIterator(int index) {
		return list.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		if (o.getClass().equals(TableData.class)){
			TableData data = (TableData)o;
			map.remove(data.getTable().getName());
		}
		return list.remove(o);
	}

	@Override
	public TableData remove(int index) {
		TableData data = list.get(index);
		map.remove(data.getTable().getName());
		return list.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean res = list.removeAll(c);
		remap();
		return res;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean res = list.retainAll(c);
		remap();
		return res;
	}

	@Override
	public TableData set(int index, TableData data) {
		TableData oldData = list.get(index);
		map.remove(oldData.getTable().getName());
		TableData res = list.set(index, data);
		map.put(data.getTable().getName(), index);
		return res;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public List<TableData> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return toArray(arg0);
	}

	public String toString(){
		StringBuilder builder = new StringBuilder();
		list.forEach(d->{
			builder.append(d.toString());
			builder.append("\n");
		});
		return builder.toString();
	}

	public void replace(TableData data){
		Integer index = map.get(data.getTable().getName());
		if (index != null){
			list.set(index, data);
		}
	}
	
	public boolean haveTable(Table table){
		Integer index = map.get(table.getName());
		return index != null;
	}
	
	@Override
	public DataList clone(){
		DataList newDL = new DataList();
		list.forEach(d->{
			Table t = d.getTable().clone();
			TableData td = new TableData(t);
			d.getRows().forEach(r->{
				RowData rd = new RowData(t);
				Object[] oData = r.getData();
				for (int i = 0; i < oData.length; i++) {
					rd.setValue(i, oData[i]);
				}
				td.addRow(rd);
			});
			newDL.add(td);
		});
		return newDL;
	}
	
}
