package dmt.database;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import dmt.model.Column;

public class SqliteServer extends Server {

	private static final long serialVersionUID = -4610335261230701855L;

	public SqliteServer() {
	}

	public SqliteServer(String databaseName){
		this.name = databaseName;
	}

	@Override
	public Connection getConnection() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("jdbc:sqlite:");
		urlBuilder.append(name);
		ConnectionFactory connectionFactory = new ConnectionFactory(urlBuilder.toString());
		return connectionFactory.getConnection();
	}

	@Override
	public String surrogateKeyDbType() {
		return "INTEGER";
	}

	@Override
	public List<String> getDatabaseNames(){
		return new LinkedList<>();
	}

	@Override
	public String getDatabaseType(Column column) {
		if (column.getType().equals(String.class))
			return "VARCHAR(255)";
		else if (column.getType().equals(Character.class))
			return "CHAR(1)";
		else if (column.getType().equals(Boolean.class))
			return "TINYINT(1)";
		else if (column.getType().equals(Integer.class))
			return "INTEGER";
		else if (column.getType().equals(Long.class))
			return "BIGINT";
		else if (column.getType().equals(Float.class))
			return "FLOAT";
		else if (column.getType().equals(Double.class))
			return "DOUBLE";
		else if (column.getType().equals(LocalDateTime.class)||column.getType().equals(Date.class))
			return "DATETIME";
		else if (column.getType().equals(LocalDate.class))
			return "DATE";
		else if (column.getType().equals(LocalTime.class))
			return "TIME";
		return "VARCHAR(255)";
	}

}
