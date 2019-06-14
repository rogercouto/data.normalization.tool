package dmt.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

	private String driver = null;
	private String url;
	private String username = null;
	private String password = null;

	public ConnectionFactory(String url, String username, String password) {
		super();
		this.url = url;
		this.username = username;
		if (!password.isEmpty())
			this.password = password;
	}

	public ConnectionFactory(String driver, String url, String username, String password) {
		super();
		this.driver = driver;
		this.url = url;
		this.username = username;
		if (!password.isEmpty())
			this.password = password;
	}

	public ConnectionFactory(String url) {
		super();
		this.url = url;
	}

	public void testConnection() throws SQLException{
		try {
			if (driver != null)
				Class.forName(driver);
			Connection connection = DriverManager.getConnection(url, username, password);
			connection.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}

	public Connection getConnection(){
		try {
			if (driver != null)
				Class.forName(driver);
			if (username == null)
				return DriverManager.getConnection(url);
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}

	public String getDriver() {
		return driver;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
