package dmt.normalization;

import java.util.List;

import dmt.model.Column;
import dmt.model.Table;

public class TableReplication {

	private Table table;
	
	public TableReplication(Table table) {
		super();
		this.table = table;
	}

	/**
	 * Normaliza uma tabela com tabelas aninhadas para uma unica tabela
	 * @param table
	 * @return tabela resultante
	 */
	public Table mixNestedTables(){
		Table nTable = new Table(table.getName());
		table.getColumns().forEach(c->{
			nTable.addColumn(c.clone());
		});
		table.getSubtables().forEach(t->{
			TableDivision td = new TableDivision(t);
			List<Column> lc = td.getNestedColumns();
			lc.forEach(sc->{
				nTable.addColumn(sc.clone());
			});
		});
		return nTable;
	}

	

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

}
