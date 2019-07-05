package dmt.test;

import dmt.input.CSVReader;
import dmt.model.data.TableData;
import dmt.normalization.Normalize;
import dmt.tools.Benchmark;

public class Test {

	public static void main(String[] args) {
		testCSV(1000);
		testCSV(5000);
		testCSV(10000);
		testCSV(50000);
		testCSV(100000);
	}

	private static void testCSV(int sampleSize){
		Benchmark b = new Benchmark();
		String fileName = "data/test/PDA_PROUNI_2016_CSV.csv";
		CSVReader reader = new CSVReader(fileName);
		String[][] sample =  reader.getSample(';', '"', sampleSize);
		TableData data = reader.getData(sample);
		Normalize.checkNormalForm(data);
		System.out.println("SampleSize: "+sampleSize+", NF: "+data.getNormalForm());
		b.stop(true);
	}

}
