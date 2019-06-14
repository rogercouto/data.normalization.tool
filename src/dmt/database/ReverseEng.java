package dmt.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import dmt.model.Column;
import dmt.model.Table;

/**
 * Utilitario pra fzer engenharia reversa do banco de dados
 * @author Tecnico
 */
public class ReverseEng {

	/**
	 * Retorna os esquemas referente a um banco de dados
	 * @param server
	 * @return Lista de esquemas
	 */
	public static List<Table> getTables(Server server){
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
						ResultSet result2 = md.getColumns(null, null, tableName, null);
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
				result = md.getIndexInfo(null, null, schema.getName(), false, false);
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
					result = md.getCrossReference(null, null, ot.getName(), null, null, schema.getName());
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


	public static void main(String[] args) {
		test1();
	}

	public static void test1(){
		//Database database = new MysqlDatabase("biblioteca", "root", "");
		Server server = new MysqlServer("localhost", "tcc_test", "root", "admin");
		//Server server = new PostgresServer("localhost","tcc_test", "postgres", "admin");
		//database.getConnection();
		List<Table> schemas = getTables(server);
		//System.out.println(schemas.size());
		schemas.forEach(System.out::println);
	}

	public static void test2(){
		Server server = new PostgresServer("localhost", 5433, "?", "postgres", "admin");
		//Server server = new MysqlServer(null, "root", "");
		List<String> names = server.getDatabaseNames();
		names.forEach(System.out::println);
	}

}
