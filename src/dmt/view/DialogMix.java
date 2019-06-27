package dmt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

public class DialogMix extends Dialog {

	protected Object result;
	protected Shell shell;
	protected Group grpOptions;
	protected Label lblColumn;
	protected Label lblOption;
	protected Table table;
	protected TableColumn tblclmnNewColumn;
	protected TableColumn tblclmnNewColumn_1;
	protected Button btnConfirm;
	protected Text txtDiv;
	protected TableColumn tblclmnNewColumn_2;
	protected Label lblFirstColumnName;
	protected Text txtColumnName;
	protected Label lblSecondColumn;
	protected Combo combo1;
	protected Combo combo2;
	protected Button btnKeepOriginalColumns;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DialogMix(Shell parent) {
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
		shell.setText("Join columns");
		shell.setSize(505, 420);
		shell.setLayout(new GridLayout(1, false));
		grpOptions = new Group(shell, SWT.NONE);
		grpOptions.setText("Options");
		grpOptions.setLayout(new GridLayout(2, false));
		grpOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblColumn = new Label(grpOptions, SWT.NONE);
		lblColumn.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblColumn.setText("First column:");
		combo1 = new Combo(grpOptions, SWT.READ_ONLY);
		combo1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				docombo1widgetSelected(e);
			}
		});
		combo1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblSecondColumn = new Label(grpOptions, SWT.NONE);
		lblSecondColumn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSecondColumn.setText("Second column:");
		combo2 = new Combo(grpOptions, SWT.READ_ONLY);
		combo2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				docombo2widgetSelected(e);
			}
		});
		combo2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblOption = new Label(grpOptions, SWT.NONE);
		lblOption.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblOption.setText("Divisor:");
		txtDiv = new Text(grpOptions, SWT.BORDER);
		txtDiv.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				dotxtDivmodifyText(arg0);
			}
		});
		txtDiv.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblFirstColumnName = new Label(grpOptions, SWT.NONE);
		lblFirstColumnName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFirstColumnName.setText("New column name:");
		txtColumnName = new Text(grpOptions, SWT.BORDER);
		txtColumnName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				dotxtColumnNamemodifyText(arg0);
			}
		});
		txtColumnName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(grpOptions, SWT.NONE);
		btnKeepOriginalColumns = new Button(grpOptions, SWT.CHECK);
		btnKeepOriginalColumns.setText("Keep original columns");
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		//tblValue.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(89);
		tblclmnNewColumn_1.setText("First Column");
		tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(100);
		tblclmnNewColumn_2.setText("Second column");
		tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(116);
		tblclmnNewColumn.setText("New Column");
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

	protected void docombo1widgetSelected(SelectionEvent e) {
	}
	protected void docombo2widgetSelected(SelectionEvent e) {
	}
	protected void dotxtDivmodifyText(ModifyEvent arg0) {
	}
	protected void dotxtColumnNamemodifyText(ModifyEvent arg0) {
	}
	protected void dobtnConfirmwidgetSelected(SelectionEvent e) {
	}
}
