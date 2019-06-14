package dmt.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import dmt.model.Column;
import dmt.model.Table;
import dmt.model.data.RowData;
import dmt.model.data.TableData;
import dmt.tools.Benchmark;
import dmt.tools.Util;

public class CSVReader {

	private File file;
	private int columnNamesIndex = 0;
	private int columnDescriptionsIndex = -1;
	private boolean createSurrogateKeys = false;

	public CSVReader(String fileName) {
		super();
		file = new File(fileName);
		if (!file.exists())
			throw new RuntimeException("Arquivo nao existe!");
	}

	public int getColumnNamesIndex() {
		return columnNamesIndex;
	}

	public void setColumnNamesIndex(int columnNamesIndex) {
		this.columnNamesIndex = columnNamesIndex;
	}

	public int getColumnDescriptionsIndex() {
		return columnDescriptionsIndex;
	}

	public void setColumnDescriptionsIndex(int columnDescriptionsIndex) {
		this.columnDescriptionsIndex = columnDescriptionsIndex;
	}

	public boolean creatingSurrogateKeys() {
		return createSurrogateKeys;
	}

	public void createSurrogateKeys(boolean createSurrogateKeys) {
		this.createSurrogateKeys = createSurrogateKeys;
	}

	public String[] splitLine(String line, char separator, char stringDelimiter){
		List<String> wordList = new LinkedList<>();
		char[] ca = line.toCharArray();
		StringBuilder builder = new StringBuilder();
		boolean ignoreSep = false;
		for (char c : ca) {
			if (Character.compare(stringDelimiter, c) == 0){
				ignoreSep = !ignoreSep;
				continue;
			}
			if (Character.compare(separator, c) == 0 && !ignoreSep){
				wordList.add(builder.toString());
				builder = new StringBuilder();
			}else{
				builder.append(c);
			}
		}
		String[] res = new String[wordList.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = wordList.get(i);
		}
		return res;
	}

	public String[][] getSample(char separator, char stringDelimiter, int sampleSize){
		List<String[]> lines = new LinkedList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			StringBuilder builder = new StringBuilder();
			boolean delimitated = false;
			while (reader.ready() && sampleSize != 0){
				String thisLine = reader.readLine();
				builder.append(thisLine);
				int dCount = Util.countChar(stringDelimiter, thisLine);
				if ((dCount & 1) == 1){
					if (!delimitated){
						delimitated = true;
						builder.append("\n");
						continue;
					}else{
						delimitated = false;
					}
				}
				String fullLine = builder.toString();
				builder = new StringBuilder();
				String[] line = splitLine(fullLine, separator, stringDelimiter);
				int emptyColumns = Util.countSpaces(line);
				//porcentagem das colunas vazias
				double test = (double)emptyColumns/(double)line.length;
				if (test <= 0.5){
					lines.add(line);
					sampleSize--;
				}
			}
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		return lines.toArray(new String[lines.size()][]);
	}

	public String[][] getSample(char separator, char stringDelimiter){
		return getSample(separator, stringDelimiter, -1);
	}

	public static void printSample(String[][] sample){
		for (int i = 0; i < sample.length; i++) {
			for (int j = 0; j < sample[i].length; j++) {
				if (j > 0)
					System.out.print(", ");
				System.out.print(sample[i][j].toString());
			}
			System.out.println();
		}
	}

	public TableData getData(String[][] sample){
		Table table = new Table(file.getAbsoluteFile().getName().split("[.]")[0]);
		int iInc = 1;
		int jInc = 0;
		if (columnDescriptionsIndex >= 0)
			iInc = 2;
		if (createSurrogateKeys){
			table.createSurrogateKey();
			jInc = 1;
		}
		int numColumns = sample[columnNamesIndex].length;
		//corrigir nomes das colunas
		for (int j = 1; j < numColumns; j++){
			int inc = 1;
			for (int k = 0; k < j; k++){
				if (sample[columnNamesIndex][j].compareTo(sample[columnNamesIndex][k])==0){
					inc++;
					sample[columnNamesIndex][j] = sample[columnNamesIndex][j].concat(String.valueOf(inc));
				}
			}

		}
		for (int j = 0; j < numColumns; j++) {
			Column column = new Column(sample[columnNamesIndex][j]);
			//TPDO Procurar aqui o tipo certo
			column.setType(String.class);
			table.addColumn(column);
		}
		//System.out.println(table);
		TableData data = new TableData(table);
		for (int i = 0+iInc; i < sample.length; i++) {
			RowData row = new RowData(table);
			for (int j = 0; j < sample[i].length; j++) {
				row.setValue(j+jInc, sample[i][j]);
			}
			data.addRow(row);
		}
		return data;
	}

	public TableData getData(char separator, char stringDelimiter){
		String[][] sample = getSample(separator, stringDelimiter, -1);
		return getData(sample);
	}

	public static void main(String[] args) {
		Benchmark b = new Benchmark();
		CSVReader reader = new CSVReader("data/exemplo1.csv");
		reader.createSurrogateKeys(true);
		//String[][] sample = reader.getSample(';', '"', -1);
		//printSample(sample);
		reader.setColumnNamesIndex(0);
		//reader.setColumnDescriptionsIndex(0);
		//reader.setCreateSurrogateKeys(true);
		TableData data = reader.getData(';','"');
		System.out.println(data.getTable());
		data.getRows().forEach(System.out::println);
		System.out.println(data);
		//System.out.println(data.getTable().haveNestedTables());
		//System.out.println("Fim!");
		b.stop(true);
		//System.out.println(data);
		/*
		Table table = new Table("test", true);
		Column column = new Column("description");
		column.setType(String.class);
		column.setNotNull(true);
		table.addColumn(column);
		System.out.println(table);
		TableData data = new TableData(table);
		RowData row = new RowData(table);
		row.setValue("description", "teste");
		data.addRow(row);
		RowData row2 = new RowData(table);
		row2.setValue("description", "aff");
		data.addRow(row2);
		System.out.println(data);
		*/
	}

}
