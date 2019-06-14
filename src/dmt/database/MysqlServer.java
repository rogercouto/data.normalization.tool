package dmt.database;

import java.sql.Connection;

public class MysqlServer extends Server {

	public MysqlServer(String server, int port, String databaseName, String userName, String password){
		this.server = server;
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

}
