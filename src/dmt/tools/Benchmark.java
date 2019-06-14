package dmt.tools;

import java.util.Date;

public class Benchmark {

	long ti;

	public Benchmark() {
		super();
		ti = new Date().getTime();
	}

	public void start(){
		ti = new Date().getTime();
	}

	public double stop(boolean print){
		long tf = new Date().getTime();
		long tempo = tf - ti;
		double res = (double)tempo/1000.0;
		if (print)
			System.out.println("Tempo de execucao: "+res+" segundos");
		return res;
	}


}
