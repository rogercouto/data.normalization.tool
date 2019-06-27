package dmt.controller;

import java.sql.Connection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import dmt.database.MysqlServer;
import dmt.database.PostgresServer;
import dmt.database.Server;
import dmt.database.SqliteServer;
import dmt.view.DialogConnection;

public class DialogConnectionController extends DialogConnection {

	private Server server = null;
	private List<String> databaseNames = null;

	public DialogConnectionController(Shell parent) {
		super(parent);
		initialize();
	}

	private void initialize(){
		cmbDriver.add("Mysql");
		cmbDriver.add("PostgreSQL");
		cmbDriver.add("Sqlite");
		txtPort.setEnabled(false);
		txtUser.setEnabled(false);
		txtPassword.setEnabled(false);
		cmbDatabase.setEnabled(false);
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
			cmbDatabase.setEnabled(true);
			break;
		case 1:
			txtHost.setText("localhost");
			txtPort.setText("5432");
			txtUser.setText("postgres");
			txtPort.setEnabled(true);
			txtUser.setEnabled(true);
			txtPassword.setEnabled(true);
			cmbDatabase.setEnabled(true);
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
				cmbDatabase.setEnabled(false);
			}
			break;
		default:
			break;
		}
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
			break;
		case 2:
			server = new SqliteServer(txtHost.getText());
			break;
		default:
			break;
		}
		testConnection(server);
	}

	protected void dobtnImportwidgetSelected(SelectionEvent e) {
		try{
			if (cmbDatabase.getText().trim().length() > 0){
				dobtnTestwidgetSelected(e);
				server.setDatabaseName(cmbDatabase.getText());
				Connection conn = server.getConnection();
				if (conn != null){
					result = server;
					shell.close();
				}
			}else if (cmbDriver.getSelectionIndex() == 2){
				dobtnTestwidgetSelected(e);
				Connection conn = server.getConnection();
				if (conn != null){
					result = server;
					shell.close();
				}
			}
		} catch (java.lang.RuntimeException ex) {
			lblTest.setText("Cannot connect with database!");
		}
	}

}
