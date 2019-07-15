package dmt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import dmt.tools.Util;

public class DialogRefine extends Dialog {

	protected Object result;
	protected Shell shell;
	protected Group grpOptions;
	protected Label lblColumn;
	protected Combo cmbColumn;
	protected Label lblOption;
	protected Table tblValue;
	protected TableColumn tblclmnNewColumn;
	protected TableColumn tblclmnNewColumn_1;
	protected Button btnConfirm;
	protected Composite composite;
	protected Button rdUpper;
	protected Button rdLower;
	protected Button rdUpperFirst;
	protected Button chkIgnoreShort;
	protected Composite composite_1;
	protected Button chkCleanSpaces;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DialogRefine(Shell parent) {
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
		shell.setText("Refine column");
		shell.setSize(440, 420);
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
		lblOption.setText("Transform:");
		composite_1 = new Composite(grpOptions, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		chkCleanSpaces = new Button(composite_1, SWT.CHECK);
		chkCleanSpaces.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dochkCleanSpaceswidgetSelected(e);
			}
		});
		chkCleanSpaces.setText("Clean trailing spaces ( show as _ )");
		new Label(grpOptions, SWT.NONE);
		composite = new Composite(grpOptions, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new GridLayout(3, false));
		rdUpper = new Button(composite, SWT.RADIO);
		rdUpper.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dordUpperwidgetSelected(e);
			}
		});
		rdUpper.setText("To upper case");
		rdLower = new Button(composite, SWT.RADIO);
		rdLower.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dordLowerwidgetSelected(e);
			}
		});
		rdLower.setText("To lower case");
		rdUpperFirst = new Button(composite, SWT.RADIO);
		rdUpperFirst.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dordUpperFirstwidgetSelected(e);
			}
		});
		rdUpperFirst.setText("Upper first char");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		chkIgnoreShort = new Button(composite, SWT.CHECK);
		chkIgnoreShort.setEnabled(false);
		chkIgnoreShort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dochkIgnoreShortwidgetSelected(e);
			}
		});
		chkIgnoreShort.setText("Ingnore short strings");
		tblValue = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		//tblValue.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		tblValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tblValue.setHeaderVisible(true);
		tblValue.setLinesVisible(true);
		tblclmnNewColumn = new TableColumn(tblValue, SWT.NONE);
		tblclmnNewColumn.setWidth(184);
		tblclmnNewColumn.setText("Old Value");
		tblclmnNewColumn_1 = new TableColumn(tblValue, SWT.NONE);
		tblclmnNewColumn_1.setWidth(197);
		tblclmnNewColumn_1.setText("New Value");
		btnConfirm = new Button(shell, SWT.NONE);
		btnConfirm.setEnabled(false);
		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnConfirmwidgetSelected(e);
			}
		});
		btnConfirm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnConfirm.setText("Confirm");

	}
	protected void dochkCleanSpaceswidgetSelected(SelectionEvent e) {
		refresh();
	}
	protected void dordUpperwidgetSelected(SelectionEvent e) {
		refresh();
	}
	protected void dordLowerwidgetSelected(SelectionEvent e) {
		refresh();
	}
	protected void dordUpperFirstwidgetSelected(SelectionEvent e) {
		refresh();
	}
	protected void dochkIgnoreShortwidgetSelected(SelectionEvent e) {
		refresh();
	}
	protected void docmbColumnwidgetSelected(SelectionEvent e) {
		refresh();
	}
	protected void refresh(){
		chkIgnoreShort.setEnabled(rdUpperFirst.getSelection());
	}
	protected void dobtnConfirmwidgetSelected(SelectionEvent e) {
		refresh();
	}
}
