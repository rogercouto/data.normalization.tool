package dmt.normalization;

import java.util.HashMap;

public class DependencyMap {

	private HashMap<Object, HashMap<Object, Integer>> map = new HashMap<>();

	public DependencyMap(){
		super();
	}

	public void addValues(Object c1Value, Object c2Value){
		if (c1Value == null)
			return;
		if (!map.containsKey(c1Value)){
			map.put(c1Value, new HashMap<Object, Integer>());
		}
		if (c2Value == null)
			return;
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

	public double getMinDependencePerc(){
		double min = 1.0;
		for (Object key : map.keySet()) {
			double perc = getDependencePerc(key);
			if (perc < min)
				min = perc;
		}
		return min;
	}

	public void test(){
		map.forEach((o, h)->{
			System.out.print(o.toString());
			h.forEach((o2,i)->{
				System.out.print(" ");
				System.out.print(o2+": "+i);
			});
			System.out.println();
		});
	}

	public static void main(String[] args) {
		DependencyMap counter = new DependencyMap();
		//counter.addC1Value("PJ");
		//counter.addC1Value("PS");
		//counter.addC1Value("TI");
		counter.addValues("PJ", "Programador Junior");
		counter.addValues("PJ", "Programador J�nior");
		counter.addValues("TI","T�cnico em Inform�tica");
		counter.addValues("TI","Tecnico em Inform�tica");
		counter.addValues("TI","T�cnico em Inform�tica");
		counter.addValues("TI","Tecnico em TI");
		counter.addValues("TI","Error");
		counter.addValues("TI","Error2");
		counter.addValues("PS","Programador S�nior");
		counter.addValues("PJ|PS","Programador J�nior");
		counter.test();
		System.out.println(counter.getMinDependencePerc());
	}

}
