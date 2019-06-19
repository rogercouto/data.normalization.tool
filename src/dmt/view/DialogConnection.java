package dmt.view;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import dmt.tools.Util;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.events.VerifyEvent;

public class DialogConnection extends Dialog {

	protected Object result;
	protected Shell shell;
	protected Group group;
	protected Label lblHostname;
	protected Label lblDriver;
	protected Label lblUser;
	protected Label lblPassword;
	protected Label lblNewLabel;
	protected Text txtHost;
	protected Combo cmbDriver;
	protected Combo cmbDatabase;
	protected Text txtUser;
	protected Text txtPassword;
	protected Button btnImport;
	protected Button btnTest;
	protected Label lblPort;
	protected Text txtPort;
	protected Label lblTest;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DialogConnection(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		createContents();
		setText("Import from database");
		Util.centralize(shell, getParent());
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(359, 284);
		shell.setText("Connect to database...");
		shell.setLayout(new GridLayout(1, false));
		group = new Group(shell, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		group.setLayout(new GridLayout(4, false));
		lblDriver = new Label(group, SWT.NONE);
		lblDriver.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDriver.setText("Driver:");
		cmbDriver = new Combo(group, SWT.READ_ONLY);
		cmbDriver.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				docmbDriverwidgetSelected(e);
			}
		});
		cmbDriver.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		lblHostname = new Label(group, SWT.NONE);
		lblHostname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHostname.setText("Hostname:");
		txtHost = new Text(group, SWT.BORDER);
		txtHost.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		lblPort = new Label(group, SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPort.setText("Port:");
		txtPort = new Text(group, SWT.BORDER);
		txtPort.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent arg0) {
				dotxtPortverifyText(arg0);
			}
		});
		txtPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		lblUser = new Label(group, SWT.NONE);
		lblUser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUser.setText("User:");
		txtUser = new Text(group, SWT.BORDER);
		txtUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		lblPassword = new Label(group, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPassword.setText("Password:");
		txtPassword = new Text(group, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
		new Label(group, SWT.NONE);
		lblTest = new Label(group, SWT.NONE);
		lblTest.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		btnTest = new Button(group, SWT.NONE);
		btnTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnTestwidgetSelected(e);
			}
		});
		GridData gd_btnTest = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnTest.widthHint = 75;
		btnTest.setLayoutData(gd_btnTest);
		btnTest.setText("Test");
		lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Database:");
		cmbDatabase = new Combo(group, SWT.NONE);
		cmbDatabase.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		btnImport = new Button(shell, SWT.NONE);
		btnImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnImportwidgetSelected(e);
			}
		});
		GridData gd_btnImport = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnImport.widthHint = 75;
		btnImport.setLayoutData(gd_btnImport);
		btnImport.setText("Import");

	}
	protected void dobtnTestwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnImportwidgetSelected(SelectionEvent e) {
	}
	protected void dotxtPortverifyText(VerifyEvent arg0) {
		if (arg0.text.trim().length() > 0){
			char[] ca = arg0.text.toCharArray();
			for (char c : ca) {
				if (!Character.isDigit(c))
					arg0.doit = false;
			}
		}
	}

	protected void docmbDriverwidgetSelected(SelectionEvent e) {
	}
}
