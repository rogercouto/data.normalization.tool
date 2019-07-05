package dmt.normalization.fd;

import java.text.DecimalFormat;

public class SingleFD {

	private String ori;
	private String dest;
	private double dep;
	
	public SingleFD() {
		super();
	}
	public String getOri() {
		return ori;
	}
	public void setOri(String ori) {
		this.ori = ori;
	}
	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	public double getDep() {
		return dep;
	}
	public void setDep(double dep) {
		this.dep = dep;
	}
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append(ori);
		builder.append(" -> ");
		builder.append(dest);
		builder.append(" (");
		builder.append(new DecimalFormat("0.00").format(dep*100));
		builder.append(" %)");
		return builder.toString();
	}
	
}
