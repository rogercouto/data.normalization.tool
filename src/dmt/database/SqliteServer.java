package dmt.database;

import java.sql.Connection;

public class SqliteServer extends Server {

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

}
