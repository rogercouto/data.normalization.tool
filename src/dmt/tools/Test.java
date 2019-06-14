package dmt.tools;

import java.util.HashSet;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import dmt.input.CSVReader;
import dmt.model.Column;
import dmt.model.data.TableData;
import dmt.normalization.Normalize;

public class Test {

	public static String[] columns = {"FUNC_TEMPLO_IGREJA", "MAT4S", "COMPUSOALUNOS", "ALOJAM_ALUNO"};
	public static Object[] results = {null, null, null, null};

	private static boolean done = false;

	public static void main(String[] args) {
		Benchmark b = new Benchmark();
		b.start();
		CSVReader reader = new CSVReader("data/CADASTRO_MATRICULAS_REGIAO_SUL_2012.csv");
		String[][] sample = reader.getSample(';', '"', 800);
		reader.setColumnDescriptionsIndex(0);
		reader.setColumnNamesIndex(1);
        TableData data = reader.getData(sample);
        test1(data);
        b.stop(true);
        b.start();
		test2(data);
		b.stop(true);
	}

	@SuppressWarnings("deprecation")
	public static void test1(final TableData data){
        Normalize.matchBestTypes(data);
        Normalize.setStringSizes(data, 0);
        HashSet<Column> d1 = Normalize.getDependencies(data, "FUNC_TEMPLO_IGREJA", 1, 1.0);
        HashSet<Column> d2 = Normalize.getDependencies(data, "MAT4S", 1, 1.0);
        HashSet<Column> d3 = Normalize.getDependencies(data, "COMPUSOALUNOS", 1, 1.0);
        HashSet<Column> d4 = Normalize.getDependencies(data, "ALOJAM_ALUNO", 1, 1.0);
        System.out.println("FUNC_TEMPLO_IGREJA: "+d1.size());
        System.out.println("MAT4S: "+d2.size());
        System.out.println("COMPUSOALUNOS: "+d3.size());
        System.out.println("ALOJAM_ALUNO: "+d4.size());
	}

	@SuppressWarnings("unchecked")
	public static void test2(final TableData data){
		for (int i = 0; i < columns.length; i++) {
			final int index = i;
			GenericThread thread = new GenericThread();
			thread.setExecListener(new Listener() {
				@SuppressWarnings("deprecation")
				@Override
				public void handleEvent(Event arg0) {
					arg0.data = Normalize.getDependencies(data, columns[index], 1, 1.0);
				}
			});
			thread.setDoneListener(new Listener(){
				@Override
				public void handleEvent(Event arg0) {
					results[index] = arg0.data;
					boolean d = true;
					for (Object object : results) {
						if (object == null)
							d = false;
					}
					done = d;
				}
			});
			thread.run();
		}
		while (!done);
		System.out.println("FUNC_TEMPLO_IGREJA: "+((HashSet<Column>) results[0]).size());
        System.out.println("MAT4S: "+((HashSet<Column>) results[1]).size());
        System.out.println("COMPUSOALUNOS: "+((HashSet<Column>) results[2]).size());
        System.out.println("ALOJAM_ALUNO: "+((HashSet<Column>) results[3]).size());
	}

}
