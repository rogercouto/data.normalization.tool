package dmt.input;

import dmt.model.Column;
import dmt.model.Table;
import dmt.model.data.RowData;
import dmt.model.data.TableData;

public class MatrixReader {

	private String tableName;
	private int columnNamesIndex = 0;
	private int columnDescriptionsIndex = -1;
	private boolean createSurrogateKeys = false;
	private int start;
	private int end;

	public MatrixReader() {
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
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

	public boolean isCreateSurrogateKeys() {
		return createSurrogateKeys;
	}

	public void setCreateSurrogateKeys(boolean createSurrogateKeys) {
		this.createSurrogateKeys = createSurrogateKeys;
	}

	public int getStart() {
		return start;
	}

	public void setBegin(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public TableData getData(String[][] matrix){
		Table table = new Table(tableName);
		int jInc = 0;
		if (createSurrogateKeys){
			table.createSurrogateKey();
			jInc = 1;
		}
		int numColumns = matrix[columnNamesIndex].length;
		//corrigir nomes das colunas
		for (int j = 1; j < numColumns; j++){
			int inc = 1;
			for (int k = 0; k < j; k++){
				if (matrix[columnNamesIndex][j].compareTo(matrix[columnNamesIndex][k])==0){
					inc++;
					matrix[columnNamesIndex][j] = matrix[columnNamesIndex][j].concat(String.valueOf(inc));
				}
			}
		}
		for (int j = 0; j < numColumns; j++) {
			Column column = new Column(matrix[columnNamesIndex][j]);
			//TPDO Procurar aqui o tipo certo
			if (columnDescriptionsIndex >= 0){
				column.setDescription(matrix[columnDescriptionsIndex][j]);
			}
			column.setType(String.class);
			table.addColumn(column);
		}
		//System.out.println(table);
		TableData data = new TableData(table);
		for (int i = start; i <= end; i++) {
			RowData row = new RowData(table);
			for (int j = 0; j < matrix[i].length; j++) {
				row.setValue(j+jInc, matrix[i][j]);
			}
			data.addRow(row);
		}
		return data;
	}

}
