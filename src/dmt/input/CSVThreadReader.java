package dmt.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;

import dmt.tools.Util;

public class CSVThreadReader extends Thread {

	private Listener doneListener;
	private ProgressBar bar;
	private File file;

	private char separator = ';';
	private char stringDelimiter = '"';

	private String[][] matrix;

	public CSVThreadReader(ProgressBar bar, String fileName, char separator, char stringDelimiter) {
		super();
		this.bar = bar;
		file = new File(fileName);
		this.separator = separator;
		this.stringDelimiter = stringDelimiter;
	}

	public char getDelimiter() {
		return stringDelimiter;
	}

	public void setDelimiter(char delimiter) {
		this.stringDelimiter = delimiter;
	}

	public char getSeparator() {
		return separator;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}

	public void setDoneListener(Listener doneListener) {
		this.doneListener = doneListener;
	}

	public String[] splitLine(String line){
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

	public void run(){
		List<String[]> lines = new LinkedList<>();
		try {
			int count = Util.countLines(file.getAbsolutePath());
			bar.setMaximum(count);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			StringBuilder builder = new StringBuilder();
			boolean delimitated = false;
			while (reader.ready()){
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
				String[] line = splitLine(fullLine);
				int emptyColumns = Util.countSpaces(line);
				//porcentagem das colunas vazias
				double test = (double)emptyColumns/(double)line.length;
				if (test <= 0.5){
					lines.add(line);
				}
				bar.setSelection(bar.getSelection()+1);
			}
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		matrix = lines.toArray(new String[lines.size()][]);
		if (doneListener != null)
			doneListener.handleEvent(new Event());
	}

	public String[][] getSample() {
		return matrix;
	}

}
