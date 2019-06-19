package dmt.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import dmt.model.Column;
import dmt.model.Table;
import dmt.model.project.DataList;
import dmt.tools.IntCounter;

public class Export {

	public String dbName;
	public Server server;
	public DataList list;
	
	public Export(String dbName, Server server, DataList list) {
		super();
		this.dbName = dbName;
		this.server = server;
		this.list = list;
	}
	
	public String getTableCreateSql(){
		StringBuilder builder = new StringBuilder();
		list.forEach(data->{
			Table table = data.getTable();
			builder.append("CREATE TABLE ");
			builder.append(table.getName());
			builder.append("(");
			table.getColumns().forEach(column->{
				if (table.getColumns().indexOf(column) > 0)
					builder.append(",\n");
				else
					builder.append("\n");
				builder.append('\t');
				builder.append(column.getName());
				builder.append(' ');
				if (column.getDbType() == null){
					column.setDbType(server.getDatabaseType(column));
				}
				if(column.getDbType().compareTo("VARCHAR") == 0){
					column.setDbType("VARCHAR(255)");
				}
				builder.append(column.getDbType());
				if (column.isNotNull())
					builder.append(" NOT NULL");
				if (column.isUnique()&&!column.isPrimaryKey())
					builder.append(" UNIQUE");
			});
			List<Column> pks = table.getPrimaryKeys();
			if (pks.size() > 0)
				builder.append(",\n\tPRIMARY KEY(");
			pks.forEach(pk->{
				if (pks.indexOf(pk) > 0)
					builder.append(", ");
				builder.append(pk.getName());
			});
			if (pks.size() > 0)
				builder.append(")");
			builder.append("\n);\n\n");
		});
		return builder.toString();
	}
	
	public String getFkCreateSql(){
		StringBuilder builder = new StringBuilder();
		list.forEach(data->{
			Table table = data.getTable();
			List <Column> fks = table.getForeignKeys();
			fks.forEach(fk->{
				builder.append("ALTER TABLE ");
				builder.append(table.getName());
				builder.append(" ADD FOREIGN KEY(");
				builder.append(fk.getName());
				builder.append(")");
				builder.append(" REFERENCES ");
				builder.append(fk.getForeignKey().getTable().getName());
				builder.append("(");
				builder.append(fk.getForeignKey().getName());
				builder.append(");\n\n");
			});
		});
		return builder.toString();
	}
	
	public String getDataSql(){
		StringBuilder builder = new StringBuilder();
		list.forEach(data->{
			if (data.isLoaded()){
				builder.append("INSERT INTO ");
				Table table = data.getTable();
				builder.append(table.getName());
				builder.append("(");
				List<Column> columns = table.getColumns(false);
				columns.forEach(column->{
					if (columns.indexOf(column) > 0)
						builder.append(", ");
					builder.append(column.getName());
				});
				builder.append(") VALUES\n");
				IntCounter i = new IntCounter();
				data.getRows().forEach(row->{
					if (i.getValue() > 0){
						builder.append(",\n");
					}
					builder.append("(");
					columns.forEach(column->{
						if (columns.indexOf(column) > 0)
							builder.append(", ");
						builder.append(row.getDbValue(column.getName()));
					});
					builder.append(")");
					i.inc();
				});
				builder.append(";\n\n");
			}
		});
		return builder.toString();
	}

	public String getFullSql(){
		StringBuilder builder = new StringBuilder();
		builder.append("DROP DATABASE IF EXISTS ");
		builder.append(dbName);
		builder.append(";\n\n");
		builder.append("CREATE DATABASE ");
		builder.append(dbName);
		builder.append(";\n\n");
		builder.append("USE ");
		builder.append(dbName);
		builder.append(";\n\n");
		builder.append(getTableCreateSql());
		builder.append(getDataSql());
		builder.append(getFkCreateSql());
		return builder.toString();
	}
	
	public void insertData(){
		try {
			String fullSql = getFullSql();
			String[] commands = fullSql.split(";");
			Connection conn = server.getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 3; i < commands.length; i++) {
				String command = commands[i].trim();
				if (command.trim().length() > 0)
					stmt.executeUpdate(command);
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
