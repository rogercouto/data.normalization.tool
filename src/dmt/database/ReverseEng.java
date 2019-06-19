package dmt.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.ProgressBar;

import dmt.model.Column;
import dmt.model.Table;
import dmt.model.data.RowData;
import dmt.model.data.TableData;
import dmt.tools.IntCounter;

/**
 * Utilitario pra fzer engenharia reversa do banco de dados
 * @author Tecnico
 */
public class ReverseEng {

	private Server server = null;

	public ReverseEng(Server server) {
		super();
		this.server = server;
	}

	/**
	 * Retorna os esquemas referente a um banco de dados
	 * @return Lista de esquemas
	 */
	public List<Table> getTables(){
		List<Table> list = new LinkedList<>();
		try {
			Connection connection = server.getConnection();
			DatabaseMetaData md = connection.getMetaData();
			//Get all tables of database
			ResultSet result = md.getTables(server.name, null, null, new String[] { "TABLE" });
			ResultSetMetaData rmd = result.getMetaData();
			while (result.next()){
				Table schema = new Table();
				for (int i = 1; i <= rmd.getColumnCount(); i++) {
					if (rmd.getColumnName(i).toUpperCase().compareTo("TABLE_NAME") == 0){
						//get table name
						String tableName = result.getString(i);
						schema.setName(tableName);
						ResultSet result2 = md.getColumns(server.name, null, tableName, null);
						ResultSetMetaData rmd2 = result2.getMetaData();
						while (result2.next()){
							String columnName = null;
							int columnSize = -1;
							String dbTypeName = null;
							for (int j = 1; j <= rmd2.getColumnCount(); j++) {
								if (rmd2.getColumnName(j).toUpperCase().compareTo("COLUMN_NAME") == 0){
									columnName = result2.getString(j);
								}else if (rmd2.getColumnName(j).toUpperCase().compareTo("TYPE_NAME") == 0){
									dbTypeName = result2.getString(j);
								}else if (rmd2.getColumnName(j).toUpperCase().compareTo("COLUMN_SIZE") == 0){
									columnSize = result2.getInt(j);
								}
							}
							if (columnName != null && columnSize >= 0 && dbTypeName !=null){
								Column column = new Column();
								column.setName(columnName);
								column.setType(DataTypes.getJavaType(dbTypeName, columnSize));
								column.setDbType(dbTypeName);
								schema.addColumn(column);
							}else{
								throw new RuntimeException("Somthing wrong is not right!");
							}
						}
						result2.close();
					}
				}
				list.add(schema);
			}
			//Get prinary keys and unique indexes
			result.close();
			for (Table schema : list) {
				result = md.getIndexInfo(server.name, null, schema.getName(), false, false);
				rmd = result.getMetaData();
				while (result.next()){
					String columnName = null;
					boolean pk = false;
					boolean un = false;
					for (int i = 1; i <= rmd.getColumnCount(); i++) {
						if (rmd.getColumnName(i).toUpperCase().compareTo("COLUMN_NAME") == 0)
							columnName = result.getString(i);
						else if (rmd.getColumnName(i).toUpperCase().compareTo("INDEX_NAME") == 0){
							String indexName = result.getString(i);
							if (indexName.toUpperCase().compareTo("PRIMARY") == 0 || indexName.toUpperCase().contains("_PKEY"))
								pk = true;
							else if (indexName.toUpperCase().contains("_UNIQUE"))
								un = true;
						}
					}
					if (columnName != null){
						if (pk)
							schema.getColumn(columnName).setPrimaryKey(true);
						if (un)
							schema.getColumn(columnName).setUnique(true);
					}
				}
			}
			result.close();
			//get foreign keys
			for (Table schema : list) {
				List<Table> otherTables = list.stream().filter(s -> !s.equals(schema)).collect(Collectors.toList());
				for (Table ot : otherTables) {
					result = md.getCrossReference(server.name, null, ot.getName(), null, null, schema.getName());
					rmd = result.getMetaData();
					while (result.next()){
						String fkColumnName = null;
						String pkColumnName = null;
						for (int i = 1; i <= rmd.getColumnCount(); i++) {
							if (rmd.getColumnName(i).toUpperCase().compareTo("PKCOLUMN_NAME") == 0){
								pkColumnName = result.getString(i);
							}else if (rmd.getColumnName(i).toUpperCase().compareTo("FKCOLUMN_NAME") == 0){
								fkColumnName = result.getString(i);
							}

						}
						Column ref = ot.getColumn(pkColumnName);
						Column fk = schema.getColumn(fkColumnName);
						if (fk != null)
							schema.getColumn(fkColumnName).setForeignKey(ref);
					}
					result.close();
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static final int INC = 1000;
	/**
	 * Retorna os esquemas referente a um banco de dados
	 * @return Lista de esquemas
	 */
	public List<Table> getTables(ProgressBar bar){
		bar.setMaximum(50);
		List<Table> list = new LinkedList<>();
		try {
			Connection connection = server.getConnection();
			DatabaseMetaData md = connection.getMetaData();
			//Get all tables of database
			ResultSet result = md.getTables(server.name, null, null, new String[] { "TABLE" });
			ResultSetMetaData rmd = result.getMetaData();
			while (result.next()){
				Table schema = new Table();
				for (int i = 1; i <= rmd.getColumnCount(); i++) {
					if (rmd.getColumnName(i).toUpperCase().compareTo("TABLE_NAME") == 0){
						//get table name
						String tableName = result.getString(i);
						schema.setName(tableName);
						ResultSet result2 = md.getColumns(server.name, null, tableName, null);
						ResultSetMetaData rmd2 = result2.getMetaData();
						while (result2.next()){
							String columnName = null;
							int columnSize = -1;
							String dbTypeName = null;
							for (int j = 1; j <= rmd2.getColumnCount(); j++) {
								if (rmd2.getColumnName(j).toUpperCase().compareTo("COLUMN_NAME") == 0){
									columnName = result2.getString(j);
								}else if (rmd2.getColumnName(j).toUpperCase().compareTo("TYPE_NAME") == 0){
									dbTypeName = result2.getString(j);
								}else if (rmd2.getColumnName(j).toUpperCase().compareTo("COLUMN_SIZE") == 0){
									columnSize = result2.getInt(j);
								}
							}
							if (columnName != null && columnSize >= 0 && dbTypeName !=null){
								Column column = new Column();
								column.setName(columnName);
								column.setType(DataTypes.getJavaType(dbTypeName, columnSize));
								column.setDbType(dbTypeName);
								schema.addColumn(column);
							}else{
								throw new RuntimeException("Somthing wrong is not right!");
							}
						}
						result2.close();
					}
				}
				list.add(schema);
			}
			bar.setSelection(bar.getSelection()+INC);
			//Get prinary keys and unique indexes
			result.close();
			for (Table schema : list) {
				result = md.getIndexInfo(server.name, null, schema.getName(), false, false);
				rmd = result.getMetaData();
				while (result.next()){
					String columnName = null;
					boolean pk = false;
					boolean un = false;
					for (int i = 1; i <= rmd.getColumnCount(); i++) {
						if (rmd.getColumnName(i).toUpperCase().compareTo("COLUMN_NAME") == 0)
							columnName = result.getString(i);
						else if (rmd.getColumnName(i).toUpperCase().compareTo("INDEX_NAME") == 0){
							String indexName = result.getString(i);
							if (indexName.toUpperCase().compareTo("PRIMARY") == 0 || indexName.toUpperCase().contains("_PKEY"))
								pk = true;
							else if (indexName.toUpperCase().contains("_UNIQUE"))
								un = true;
						}
					}
					if (columnName != null){
						if (pk)
							schema.getColumn(columnName).setPrimaryKey(true);
						if (un)
							schema.getColumn(columnName).setUnique(true);
					}
				}
			}
			bar.setSelection(bar.getSelection()+INC);
			result.close();
			//get foreign keys
			for (Table schema : list) {
				List<Table> otherTables = list.stream().filter(s -> !s.equals(schema)).collect(Collectors.toList());
				for (Table ot : otherTables) {
					result = md.getCrossReference(server.name, null, ot.getName(), null, null, schema.getName());
					rmd = result.getMetaData();
					while (result.next()){
						String fkColumnName = null;
						String pkColumnName = null;
						for (int i = 1; i <= rmd.getColumnCount(); i++) {
							if (rmd.getColumnName(i).toUpperCase().compareTo("PKCOLUMN_NAME") == 0){
								pkColumnName = result.getString(i);
							}else if (rmd.getColumnName(i).toUpperCase().compareTo("FKCOLUMN_NAME") == 0){
								fkColumnName = result.getString(i);
							}

						}
						Column ref = ot.getColumn(pkColumnName);
						Column fk = schema.getColumn(fkColumnName);
						if (fk != null)
							schema.getColumn(fkColumnName).setForeignKey(ref);
					}
					result.close();
				}
				bar.setSelection(bar.getSelection()+INC);
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public void fillData(TableData data){
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT ");
			IntCounter ic = new IntCounter();
			data.getTable().getColumns()
				.stream()
				.filter(column->!column.isSurrogateKey())
				.forEach(column->{
				if (ic.getValue() > 0)
					builder.append(", ");
				builder.append(column.getName());
				ic.inc();
			});
			builder.append(" FROM ");
			builder.append(data.getTable().getName());
			Connection conn = server.getConnection();
			Statement st = conn.createStatement();
			ResultSet res = st.executeQuery(builder.toString());
			while (res.next()){
				RowData row = new RowData(data.getTable());
				for(int i = 0; i < ic.getValue(); i++){
					row.setValue(i, res.getObject(i+1));
				}
				data.addRow(row);
			}
			res.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Server server = new MysqlServer("biblioteca", "root", "");
		ReverseEng re = new ReverseEng(server);
		re.getTables()
		.stream()
		.filter(t->t.getName().compareTo("cidade") == 0)
		.forEach(t->{
			System.out.println(t);
		});
	}

}
