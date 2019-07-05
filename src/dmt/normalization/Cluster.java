package dmt.normalization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import dmt.tools.Util;

public class Cluster {

	private int editDistance;
	private HashMap<String, List<Counter>> map = new HashMap<>();
	
	public Cluster(int editDistance) {
		this.editDistance = editDistance;
	}
	
	public void addAll(List<Object> values){
		values.forEach(v->add(v.toString()));
	}
	
	public void add(String value){
		if (map.containsKey(value)){
			map.get(value).get(0).setCount(map.get(value).get(0).getCount() + 1);
		}else{
			boolean found = false;
			for (String key : map.keySet()) {
				boolean same = Util.levenshteinDistance(key, value) <= editDistance;
				if (same){
					found = true;
					int index = getIndex(value, map.get(key));
					if (index >= 0){
						map.get(key).get(index).setCount(map.get(key).get(index).getCount() + 1);
					}else{
						Counter c = new Counter();
						c.setValue(value);
						c.setCount(1);
						map.get(key).add(c);
					}
				}
			}
			if (!found){
				Counter c = new Counter();
				c.setValue(value);
				c.setCount(1);
				List<Counter> l = new ArrayList<>();
				l.add(c);
				map.put(value, l);
			}
		}
	}
	
	private static int getIndex(String value, List<Counter> list){
		int index = -1;
		int count = 0;
		for (Counter counter : list) {
			if (counter.getValue().compareTo(value)==0){
				index = count;
			}
			count++;
		}
		return index;
	}
	
	public String getFirst(String value){
		if (map.containsKey(value)){
			return value;
		}else{
			for (String key : map.keySet()) {
				boolean same = Util.levenshteinDistance(key, value) <= editDistance;
				if (same){
					return key;
				}
			}
		}
		return null;
	}
	
	public String getHigher(String value){
		if (map.containsKey(value)){
			OptionalInt oi = map.get(value).stream().mapToInt(c->c.getCount()).max();
			if (oi.isPresent()){
				Optional<Counter> oc = map.get(value).stream().filter(c->c.getCount() == oi.getAsInt()).findFirst();
				if (oc.isPresent())
					return oc.get().getValue();
			}
		}else{
			for (String key : map.keySet()) {
				boolean same = Util.levenshteinDistance(key, value) <= editDistance;
				if (same){
					OptionalInt oi = map.get(key).stream().mapToInt(c->c.getCount()).max();
					if (oi.isPresent()){
						Optional<Counter> oc = map.get(key).stream().filter(c->c.getCount() == oi.getAsInt()).findFirst();
						if (oc.isPresent())
							return oc.get().getValue();
					}
				}
			}
		}
		return null;
	}
	
	public HashMap<String, List<Counter>> getMap() {
		return map;
	}
	
	public void remove(String key){
		map.remove(key);
	}

	public void print(){
		map.forEach((s,l)->{
			System.out.print("key: "+s+"[");
			l.forEach(c->{
				if (l.indexOf(c) > 0)
					System.out.print(", ");
				System.out.print(c.getValue()+": "+c.getCount());
			});
			System.out.println("]");
		});
	}
	
	public static void main(String[] args) {
		Cluster cluster = new Cluster(2);
		cluster.add("Restinga Sêca");
		cluster.add("Santa Maria");
		cluster.add("Restinga Seca");
		cluster.add("Porto Alegre");
		cluster.add("Forno Alegre");
		cluster.add("Restinga Seca");
		cluster.add("Porto Alegre");
		cluster.add("Porto Alegre");
		cluster.add("Restinga Seca");
		cluster.add("Forno Alegre");
		cluster.add("Santa Marta");
		cluster.add("Restinga Sêca");
		cluster.print();
		System.err.println(cluster.getFirst("Restinga Seca"));
		System.err.println(cluster.getHigher("Restinga Sêca"));
	}

}
