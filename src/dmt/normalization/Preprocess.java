package dmt.normalization;

import dmt.model.data.TableData;
import dmt.tools.Options;

public class Preprocess {

	public static final int CLUSTER_FIRST = 0;
	public static final int CLUSTER_HIGGER = 1;
	
	public static void clusterize(TableData data, String columnName, Cluster cluster, int method){
		data.getRows().forEach(r->{
			String oldValue = r.getValue(columnName).toString();
			String newValue;
			switch (method) {
			case CLUSTER_FIRST:
				newValue = cluster.getFirst(oldValue);
				break;
			case CLUSTER_HIGGER:
				newValue = cluster.getHigher(oldValue);
				break;
			default:
				newValue = cluster.getFirst(oldValue);
				break;
			}
			if (oldValue.compareTo(newValue)!=0)
				r.setValue(columnName, newValue);
		});
	}
	
	public static String upperFirst(String string, boolean ignoreShortStrings){
		char[] ca = string.toCharArray();
		StringBuilder builder = new StringBuilder();
		int count = 0;
		if (ignoreShortStrings){
			for (int i = ca.length-1; i >= 0; i--) {
				if (ca[i] ==' '){
					count = 0;
				}else if (Character.isLetter(ca[i])){
					count++;
					if ((i > 0 && ca[i-1] == ' ') || i ==0){
						if (count > Options.getShortStringLimit())
							ca[i] = Character.toUpperCase(ca[i]);
					}else{
						ca[i] = Character.toLowerCase(ca[i]);
					}
				}
				builder.append(ca[i]);
			}
		}else{
			for (int i = ca.length-1; i >= 0; i--) {
				if (ca[i] ==' '){
					count = 0;
				}else if (Character.isLetter(ca[i])){
					count++;
					if ((i > 0 && ca[i-1] == ' ') || i ==0){
						ca[i] = Character.toUpperCase(ca[i]);
					}else{
						ca[i] = Character.toLowerCase(ca[i]);
					}
				}
				builder.append(ca[i]);
			}
		}
		return builder.reverse().toString();
	}
	
	public static void main(String[] args) {
		System.out.println(upperFirst("rOasTeds di cOld", false));
	}
	
}
