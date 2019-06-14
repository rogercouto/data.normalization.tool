package dmt.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

public class PostgresServer extends Server {

	public PostgresServer(String server, int port, String databaseName, String userName, String password){
		this.server = server;
		this.port = port;
		this.name = databaseName;
		this.userName = userName;
		this.password = password;
	}

	public PostgresServer(String server, String databaseName, String userName, String password){
		this.server = server;
		this.port = 5432;
		this.name = databaseName;
		this.userName = userName;
		this.password = password;
	}

	public PostgresServer(String databaseName, String userName, String password){
		this.server = "localhost";
		this.port = 5432;
		this.name = databaseName;
		this.userName = userName;
		this.password = password;
	}

	@Override
	public Connection getConnection() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("jdbc:postgresql://");
		urlBuilder.append(server);
		urlBuilder.append(":");
		urlBuilder.append(port);
		urlBuilder.append("/");
		if (name != null)
			urlBuilder.append(name);
		ConnectionFactory connectionFactory = new ConnectionFactory(urlBuilder.toString(), userName, password);
		return connectionFactory.getConnection();
	}

	@Override
	public List<String> getDatabaseNames(){
		List<String> names = new LinkedList<>();
		try {
			Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT datname FROM pg_database WHERE datistemplate = false;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                names.add(rs.getString(1));
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return names;
	}

	@Override
	public String surrogateKeyDbType() {
		return "serial";
	}

}
