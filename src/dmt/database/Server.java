package dmt.database;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import dmt.model.Column;

/**
 * Classe que implementa o servidor de banco de dados utilizado
 * @author roger
 */
public abstract class Server implements Serializable{

	private static final long serialVersionUID = 5683012944851908599L;

	protected String name;
	protected String server;
	protected int port;
	protected String userName = null;
	protected String password = null;

	/**
	 * Conecta com o banco
	 * @return Conexao com o banco de dados
	 */
	public abstract Connection getConnection();

	/**
	 * Retorna os nomes dos bancos de dados existentes no servidor
	 * Necessário sobrescrever em alguns tipos de servidores como postgres
	 * @return Lista de nomes
	 */
	public List<String> getDatabaseNames(){
		List<String> names = new LinkedList<>();
		try {
			Connection connection = getConnection();
			DatabaseMetaData md = connection.getMetaData();
			ResultSet result = md.getCatalogs();
			ResultSetMetaData rmd = result.getMetaData();
			while (result.next()) {
				for (int i = 1; i <= rmd.getColumnCount(); i++) {
					names.add(result.getString(i));
				}
			}
			result.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return names;
	}

	public abstract String surrogateKeyDbType();

	public void setDatabaseName(String databaseName){
		this.name = databaseName;
	}
	
	public abstract String getDatabaseType(Column column);

}
