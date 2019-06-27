package dmt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import dmt.tools.Util;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class DialogSplit extends Dialog {

	protected Object result;
	protected Shell shell;
	protected Group grpOptions;
	protected Label lblColumn;
	protected Combo cmbColumn;
	protected Label lblOption;
	protected Table table;
	protected TableColumn tblclmnNewColumn;
	protected TableColumn tblclmnNewColumn_1;
	protected Button btnConfirm;
	protected Text txtDelimiter;
	protected TableColumn tblclmnNewColumn_2;
	protected Label lblFirstColumnName;
	protected Label lblSecondColumnName;
	protected Text txtFirstColumn;
	protected Text txtSecondColumn;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DialogSplit(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		createContents();
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		Util.centralize(shell, getParent());
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
		shell.setText("Split column");
		shell.setSize(553, 420);
		shell.setLayout(new GridLayout(1, false));
		grpOptions = new Group(shell, SWT.NONE);
		grpOptions.setText("Options");
		grpOptions.setLayout(new GridLayout(2, false));
		grpOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblColumn = new Label(grpOptions, SWT.NONE);
		lblColumn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblColumn.setText("Column:");
		cmbColumn = new Combo(grpOptions, SWT.READ_ONLY);
		cmbColumn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				docmbColumnwidgetSelected(e);
			}
		});
		cmbColumn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblOption = new Label(grpOptions, SWT.NONE);
		lblOption.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblOption.setText("Delimiter");
		txtDelimiter = new Text(grpOptions, SWT.BORDER);
		txtDelimiter.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				dotxtDelimitermodifyText(arg0);
			}
		});
		txtDelimiter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblFirstColumnName = new Label(grpOptions, SWT.NONE);
		lblFirstColumnName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFirstColumnName.setText("First column name:");
		txtFirstColumn = new Text(grpOptions, SWT.BORDER);
		txtFirstColumn.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				dotxtFirstColumnmodifyText(arg0);
			}
		});
		txtFirstColumn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblSecondColumnName = new Label(grpOptions, SWT.NONE);
		lblSecondColumnName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSecondColumnName.setText("Second column name:");
		txtSecondColumn = new Text(grpOptions, SWT.BORDER);
		txtSecondColumn.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				dotxtSecondColumnmodifyText(arg0);
			}
		});
		txtSecondColumn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		//tblValue.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(98);
		tblclmnNewColumn.setText("Original Column");
		tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(89);
		tblclmnNewColumn_1.setText("First Column");
		tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(100);
		tblclmnNewColumn_2.setText("Second column");
		btnConfirm = new Button(shell, SWT.NONE);
		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnConfirmwidgetSelected(e);
			}
		});
		btnConfirm.setEnabled(false);
		btnConfirm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnConfirm.setText("Confirm");

	}
	protected void docmbColumnwidgetSelected(SelectionEvent e) {
		if (cmbColumn.getText().trim().length() > 0 && txtFirstColumn.getText().trim().length() == 0)
			txtFirstColumn.setText(cmbColumn.getText());
		refresh();
	}
	protected void dotxtDelimitermodifyText(ModifyEvent arg0) {
		refresh();
	}
	protected void dotxtFirstColumnmodifyText(ModifyEvent arg0) {
		refresh();
	}
	protected void dotxtSecondColumnmodifyText(ModifyEvent arg0) {
		refresh();
	}

	protected void refresh() {
	}
	protected void dobtnConfirmwidgetSelected(SelectionEvent e) {
	}
}
