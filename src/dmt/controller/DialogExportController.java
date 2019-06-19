package dmt.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import dmt.database.Export;
import dmt.database.MysqlServer;
import dmt.database.PostgresServer;
import dmt.database.ReverseEng;
import dmt.database.Server;
import dmt.database.SqliteServer;
import dmt.model.project.DataList;
import dmt.tools.Util;
import dmt.view.DialogExport;

public class DialogExportController extends DialogExport{

	private Server server = null;
	private List<String> databaseNames = null;
	
	public DialogExportController(Shell parent) {
		super(parent);
		initialize();
	}

	private void initialize(){
		cmbDriver.add("Mysql");
		cmbDriver.add("PostgreSQL");
		cmbDriver.add("Sqlite");
		cmbDatabase.add("Mysql");
		cmbDatabase.add("PostgreSQL");
		cmbDatabase.add("Sqlite");
		txtPort.setEnabled(false);
		txtUser.setEnabled(false);
		txtPassword.setEnabled(false);
		txtDatabase.setEnabled(false);
	}
	
	private void testConnection(Server server){
		try {
			Connection conn = server.getConnection();
			if (conn != null){
				this.server = server;
				databaseNames = this.server.getDatabaseNames();
				databaseNames.forEach(name->{
					cmbDatabase.add(name);
				});
				lblTest.setText("Connection test successfully!");
			}
		} catch (java.lang.RuntimeException e) {
			lblTest.setText("Cannot connect with database!");
		}
	}
	
	protected void dobtnTestwidgetSelected(SelectionEvent e) {
		int index = cmbDriver.getSelectionIndex();
		Server server = null;
		int port = txtPort.getText().trim().length() > 0 ? Integer.parseInt(txtPort.getText()) : -1;
		switch (index) {
		case 0:
			server = new MysqlServer(txtHost.getText(), port, txtUser.getText(), txtPassword.getText());
			break;
		case 1:
			server = new PostgresServer(txtHost.getText(), port, txtUser.getText(), txtPassword.getText());
		case 2:
			server = new SqliteServer(txtHost.getText());	
		default:
			break;
		}
		testConnection(server);
	}
	
	protected void docmbDriverwidgetSelected(SelectionEvent e) {
		switch (cmbDriver.getSelectionIndex()) {
		case 0:
			txtHost.setText("localhost");
			txtPort.setText("3306");
			txtUser.setText("root");
			txtPort.setEnabled(true);
			txtUser.setEnabled(true);
			txtPassword.setEnabled(true);
			txtDatabase.setEnabled(true);
			break;
		case 1:
			txtHost.setText("localhost");
			txtPort.setText("5432");
			txtUser.setText("posrgres");
			txtPort.setEnabled(true);
			txtUser.setEnabled(true);
			txtPassword.setEnabled(true);
			txtDatabase.setEnabled(true);
			break;
		case 2:
			FileDialog dialog = new FileDialog(shell, SWT.OPEN);
			String fileName = dialog.open();
			if (fileName != null){
				txtHost.setText(fileName);
				txtPort.setText("");
				txtUser.setText("");
				txtPassword.setText("");
				txtPort.setEnabled(false);
				txtUser.setEnabled(false);
				txtPassword.setEnabled(false);
				txtDatabase.setEnabled(false);
			}
			break;
		default:
			break;
		}
	}
	
	protected void dobtnExportwidgetSelected(SelectionEvent e) {
		int index = tabFolder.getSelectionIndex();
		switch (index) {
		case 0:
			exportToSql();
			break;
		case 1:
			exportToDatabase();
			break;
		default:
			break;
		}
	}

	private void exportToSql() {
		int index = cmbDatabase.getSelectionIndex();
		Server server = null;
		switch (index) {
		case 0:
			server = new MysqlServer();
			break;
		case 1:
			server = new PostgresServer();
			break;
		case 2:
			server = new SqliteServer();	
		default:
			break;
		}
		DataList dl = Main.project.getDataList();
		dl.forEach(data->{
			if (!data.isLoaded()&&Main.project.getServer()!=null){
				ReverseEng re = new ReverseEng(Main.project.getServer());
				re.fillData(data);
			}
		});
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterExtensions(new String[]{"*.sql"});
		String fileName = dialog.open();
		if (fileName != null){
			try {
				File file = new File(fileName);
				String databaseName = Util.getTableName(fileName);
				Export export = new Export(databaseName, server, dl);
				String fulLSql = export.getFullSql();
				FileWriter writer = new FileWriter(file);
				writer.write(fulLSql);
				writer.close();
				showMessage("File exported successfully!");
				shell.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void exportToDatabase() {
		if (server != null){
			DataList dl = Main.project.getDataList();
			dl.forEach(data->{
				if (!data.isLoaded()&&Main.project.getServer()!=null){
					ReverseEng re = new ReverseEng(Main.project.getServer());
					re.fillData(data);
				}
			});
			List<String> otherDatabases = server.getDatabaseNames();
			String newDatabase = txtDatabase.getText();
			if (!otherDatabases.contains(newDatabase)){
				lblTest.setText("");
				try {
					Connection connection = server.getConnection();
					Statement stmt = connection.createStatement();
					stmt.executeUpdate(String.format("CREATE DATABASE %s", newDatabase));
					stmt.close();
					connection.close();
					server.setDatabaseName(newDatabase);
					Export export = new Export(newDatabase, server, dl);
					export.insertData();
					showMessage("Database created successfully!");
					shell.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else{
				lblTest.setText("Banco de dados já existe!");
			}
		}
	}
	
	private void showMessage(String message){
		MessageBox box = new MessageBox(shell);
		box.setText("Message");
		box.setMessage(message);
		box.open();
	}

}
