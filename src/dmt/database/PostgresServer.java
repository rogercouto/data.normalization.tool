package dmt.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import dmt.model.Column;

public class PostgresServer extends Server {

	private static final long serialVersionUID = 2666926609387563596L;

	public PostgresServer() {
	}

	public PostgresServer(String server, int port, String userName, String password){
		this.server = server;
		if (port >= 0)
			this.port = port;
		this.userName = userName;
		this.password = password;
	}

	public PostgresServer(String server, int port, String databaseName, String userName, String password){
		this.server = server;
		if (port >= 0)
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

	@Override
	public String getDatabaseType(Column column) {
		if (column.isSurrogateKey())
			return "serial";
		else if (column.getType().equals(String.class))
			return "varchar(255)";
		else if (column.getType().equals(Character.class))
			return "char(1)";
		else if (column.getType().equals(Boolean.class))
			return "bool";
		else if (column.getType().equals(Integer.class)){
			if (column.isSurrogateKey())
				return "serial";
			return "int";
		}else if (column.getType().equals(Long.class))
			return "int8";
		else if (column.getType().equals(Float.class))
			return "float4";
		else if (column.getType().equals(Double.class))
			return "float8";
		else if (column.getType().equals(LocalDateTime.class)||column.getType().equals(Date.class))
			return "datetime";
		else if (column.getType().equals(LocalDate.class))
			return "date";
		else if (column.getType().equals(LocalTime.class))
			return "time";
		return "varchar(255)";
	}

	public static void main(String[] args) {
		PostgresServer server = new PostgresServer("localhost", 5432, "postgres", "admin");
		server.getDatabaseNames().forEach(System.out::println);
		Connection connection = server.getConnection();
		
		System.out.println(connection);
	}
	
}
