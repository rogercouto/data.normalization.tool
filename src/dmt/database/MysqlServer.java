package dmt.database;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import dmt.model.Column;

public class MysqlServer extends Server {

	private static final long serialVersionUID = -7204815227892538768L;

	public MysqlServer() {
	}

	public MysqlServer(String server, int port, String userName, String password){
		this.server = server;
		if (port >= 0)
			this.port = port;
		this.userName = userName;
		this.password = password;
	}

	public MysqlServer(String server, int port, String databaseName, String userName, String password){
		this.server = server;
		if (port >= 0)
			this.port = port;
		this.name = databaseName;
		this.userName = userName;
		this.password = password;
	}

	public MysqlServer(String server, String databaseName, String userName, String password){
		this.server = server;
		this.port = 3306;
		this.name = databaseName;
		this.userName = userName;
		this.password = password;
	}

	public MysqlServer(String databaseName, String userName, String password){
		this.server = "localhost";
		this.port = 3306;
		this.name = databaseName;
		this.userName = userName;
		this.password = password;
	}

	@Override
	public Connection getConnection() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("jdbc:mysql://");
		urlBuilder.append(server);
		urlBuilder.append(":");
		urlBuilder.append(port);
		urlBuilder.append("/");
		if (name != null)
			urlBuilder.append(name);
		urlBuilder.append("?useTimezone=true&serverTimezone=UTC");
		ConnectionFactory connectionFactory = new ConnectionFactory(urlBuilder.toString(), userName, password);
		return connectionFactory.getConnection();
	}

	@Override
	public String surrogateKeyDbType() {
		return "INTEGER";
	}

	@Override
	public String getDatabaseType(Column column) {
		if (column.getType().equals(String.class))
			return "VARCHAR(255)";
		else if (column.getType().equals(Character.class))
			return "CHAR(1)";
		else if (column.getType().equals(Boolean.class))
			return "TINYINT(1)";
		else if (column.getType().equals(Integer.class)){
			if (column.isSurrogateKey())
				return "INTEGER AUTO_INCREMENT";
			return "INTEGER";
		}else if (column.getType().equals(Long.class))
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
