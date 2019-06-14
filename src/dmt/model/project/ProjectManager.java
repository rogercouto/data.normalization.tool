package dmt.model.project;

import java.io.File;
import java.io.IOException;
import java.util.List;

import dmt.input.CSVReader;
import dmt.model.data.TableData;
import dmt.normalization.Normalize;
import dmt.tools.Benchmark;

public class ProjectManager {

	public static void main(String[] args) {
		Benchmark b = new Benchmark();
		b.start();
		//write();
		load();
		b.stop(true);
	}
	
	public static void write(){
		CSVReader reader = new CSVReader("data/exemplo1.csv");
        TableData data = reader.getData(';','"');
        List<TableData> list = Normalize.splitColumnsToList(data, new char[]{'\n'});
        DataList dl = new DataList();
        dl.addAll(list);
        //System.out.println(dl);
        try {
        	File file = new File("project1.dnp");
			file.createNewFile();
			Project project = new Project();
			project.setDataList(dl);
			project.saveAs(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void load(){
		Project project = new Project(new File("project1.dnp"));
		DataList dl = project.getDataList();
		System.out.println(dl);
	}
	
}
