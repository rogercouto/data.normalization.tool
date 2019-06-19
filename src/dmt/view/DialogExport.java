package dmt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import dmt.tools.Util;

public class DialogExport extends Dialog {

	protected Object result;
	protected Shell shell;
	protected Composite group;
	protected Label lblHostname;
	protected Label lblDriver;
	protected Label lblUser;
	protected Label lblPassword;
	protected Label lblNewLabel;
	protected Text txtHost;
	protected Combo cmbDriver;
	protected Text txtUser;
	protected Text txtPassword;
	protected Button btnExport;
	protected Button btnTest;
	protected Label lblPort;
	protected Text txtPort;
	protected Label lblTest;
	protected Text txtDatabase;
	protected CTabFolder tabFolder;
	protected CTabItem tbtmDatabase;
	protected CTabItem tbtmSqlScript;
	protected Composite composite;
	protected Label lblDatabase;
	protected Combo cmbDatabase;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DialogExport(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		createContents();
		setText("Export data");
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
		shell.setSize(359, 289);
		shell.setText("Export data");
		shell.setLayout(new GridLayout(1, false));
		tabFolder = new CTabFolder(shell, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tbtmSqlScript = new CTabItem(tabFolder, SWT.NONE);
		tbtmSqlScript.setText("Sql Script");
		composite = new Composite(tabFolder, SWT.NONE);
		tbtmSqlScript.setControl(composite);
		composite.setLayout(new GridLayout(2, false));
		lblDatabase = new Label(composite, SWT.NONE);
		lblDatabase.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDatabase.setText("Database:");
		cmbDatabase = new Combo(composite, SWT.READ_ONLY);
		cmbDatabase.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tbtmDatabase = new CTabItem(tabFolder, SWT.NONE);
		tbtmDatabase.setText("Database");
		group = new Composite(tabFolder, SWT.NONE);
		tbtmDatabase.setControl(group);
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
		txtDatabase = new Text(group, SWT.BORDER);
		txtDatabase.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		btnExport = new Button(shell, SWT.NONE);
		btnExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnExportwidgetSelected(e);
			}
		});
		GridData gd_btnExport = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnExport.widthHint = 75;
		btnExport.setLayoutData(gd_btnExport);
		btnExport.setText("Export");

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

	protected void dobtnTestwidgetSelected(SelectionEvent e) {
	}
	protected void docmbDriverwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnExportwidgetSelected(SelectionEvent e) {
	}
}
