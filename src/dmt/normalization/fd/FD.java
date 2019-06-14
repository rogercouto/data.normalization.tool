package dmt.normalization.fd;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class FD {

	private Set<String> oSet;
	private Set<String> dSet;
	
	public FD() {
		super();
		oSet = new TreeSet<>();
		dSet = new TreeSet<>();
	}
	
	public void addOriColumnName(String columnName){
		oSet.add(columnName);
	}
	
	public void addDestColumnName(String columnName){
		dSet.add(columnName);
	}

	public Set<String> getOriSet() {
		return oSet;
	}

	public Set<String> getDestSet() {
		return dSet;
	}

	public void setOriSet(Set<String> oSet) {
		this.oSet = oSet;
	}

	public void setDestSet(Set<String> dSet) {
		this.dSet = dSet;
	}

	public String getFirstDet(){
		Optional<String> opS = oSet.stream().findFirst();
		if (opS.isPresent())
			return opS.get();
		throw new RuntimeException("FDMapper.getFirst: empty set!");
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append(getOriSet());
		builder.append(" -> ");
		builder.append(getDestSet());
		return builder.toString();
	}
}
