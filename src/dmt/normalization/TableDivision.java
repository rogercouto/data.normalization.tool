package dmt.normalization;

import java.util.LinkedList;
import java.util.List;

import dmt.model.Column;
import dmt.model.Element;
import dmt.model.Table;

public class TableDivision {
	
	private Table table;

	public TableDivision(Table table) {
		super();
		this.table = table;
	}

	/**
	 * Normaliza uma tabela com tabelas aninhadas para uma lista de tabelas
	 * @param table: tabela a ser normalizada
	 * @param parentPKs: chave primaria da tabela pai
	 * @return lista de tabelas
	 */
	public List<Table> splitNestedTables(List<Column> parentPKs){
	    List<Table> list = new LinkedList<>();
	    Table t1 = new Table(table.getName());
	    if (parentPKs != null){
	        for (Column key : parentPKs) {
	            Column fk = key.clone();
	            fk.setPrimaryKey(false);
	            fk.setSurrogateKey(false);
	            fk.setUnique(false);
	            fk.setForeignKey(key);
	            t1.addColumn(fk);
	        }
	    }
	    list.add(t1);
	    for (Element element : table.getElements()) {
	        if (element.getClass().equals(Column.class)){
	            Column c = (Column)element;
	            t1.addColumn(c);//Removed clone
	        }else if (element.getClass().equals(Table.class)){
	            Table t = ((Table)element).clone();
	            if (t1.haveSurrogateKey())
	                t.createSurrogateKey();
	            list.addAll(splitNestedTables(table.getPrimaryKeys()));
	        }
	    }
	    return list;
	}

	/**
	 * Normaliza uma tabela com tabelas aninhadas para uma lista de tabelas
	 * @param table: tabela a ser normalizada
	 * @return lista de tabelas
	 */
	public List<Table> splitNestedTables(){
	    return splitNestedTables(null);
	}

	private static List<Column> getNestedColumns(Table table){
		List<Column> columns = new LinkedList<>();
		table.getElements().forEach(e->{
			if (e.getClass().equals(Column.class)){
				columns.add((Column)e);
			}else{
				columns.addAll(getNestedColumns((Table)e));
			}
		});
		return columns;
	}

	public List<Column> getNestedColumns(){
		return getNestedColumns(table);
	}
	
	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

}
