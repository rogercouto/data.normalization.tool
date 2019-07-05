package dmt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import dmt.controller.CompModelEditorController;
import dmt.tools.Util;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.List;

public class Dialog1FN extends Dialog {

	protected Object result;
	protected Shell shell;
	protected Composite composite;
	protected CompModelEditorController modelEditor1;
	protected CompModelEditorController modelEditor2;
	protected Label lblMethod;
	protected Button checkSingle;
	protected Button chkMulti;
	protected Button btnConfirm;
	protected Label lblSeparators;
	protected Composite composite_1;
	protected Button chk1;
	protected Button chk2;
	protected Button chk3;
	protected Button chk4;
	protected Button chk5;
	protected Label lblNewTableName;
	protected Text txtNewTableName;
	protected Label lblColumn;
	protected List lstColumn;
	protected Label lblActual;
	protected Label lblRefatored;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public Dialog1FN(Shell parent) {
		super(parent, SWT.SHELL_TRIM | SWT.APPLICATION_MODAL);
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
		shell.setSize(720, 480);
		shell.setText("1FN Normalization");
		shell.setLayout(new GridLayout(2, false));
		composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		composite.setLayout(new GridLayout(5, false));
		lblSeparators = new Label(composite, SWT.NONE);
		lblSeparators.setText("Separators:");
		composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(5, false));
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		chk1 = new Button(composite_1, SWT.CHECK);
		chk1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dochk1widgetSelected(e);
			}
		});
		chk1.setSelection(true);
		chk1.setText(";");
		chk2 = new Button(composite_1, SWT.CHECK);
		chk2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dochk2widgetSelected(e);
			}
		});
		chk2.setSelection(true);
		chk2.setText("cr");
		chk3 = new Button(composite_1, SWT.CHECK);
		chk3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dochk3widgetSelected(e);
			}
		});
		chk3.setText(",");
		chk4 = new Button(composite_1, SWT.CHECK);
		chk4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dochk4widgetSelected(e);
			}
		});
		chk4.setText("space");
		chk5 = new Button(composite_1, SWT.CHECK);
		chk5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dochk5widgetSelected(e);
			}
		});
		chk5.setText(".");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		lblColumn = new Label(composite, SWT.NONE);
		lblColumn.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblColumn.setText("Column:");
		lstColumn = new List(composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		lstColumn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dolstColumnwidgetSelected(e);
			}
		});
		GridData gd_lstColumn = new GridData(SWT.LEFT, SWT.FILL, false, true, 4, 1);
		gd_lstColumn.widthHint = 150;
		gd_lstColumn.heightHint = 80;
		lstColumn.setLayoutData(gd_lstColumn);
		lblMethod = new Label(composite, SWT.NONE);
		lblMethod.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMethod.setText("Method:");
		checkSingle = new Button(composite, SWT.RADIO);
		checkSingle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				docheckSinglewidgetSelected(e);
			}
		});
		checkSingle.setText("Single Table (Recommended)");
		chkMulti = new Button(composite, SWT.RADIO);
		chkMulti.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dochkMultiwidgetSelected(e);
			}
		});
		chkMulti.setText("Multi table");
		lblNewTableName = new Label(composite, SWT.NONE);
		lblNewTableName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewTableName.setText("New table name: ");
		txtNewTableName = new Text(composite, SWT.BORDER);
		txtNewTableName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				dotxtNewTableNamemodifyText(arg0);
			}
		});
		txtNewTableName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblActual = new Label(shell, SWT.NONE);
		lblActual.setText("Actual");
		lblRefatored = new Label(shell, SWT.NONE);
		lblRefatored.setText("Refator");
		modelEditor1 = new CompModelEditorController(shell, SWT.BORDER, SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gd_modelEditor1 = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_modelEditor1.widthHint = 250;
		modelEditor1.setLayoutData(gd_modelEditor1);
		modelEditor2 = new CompModelEditorController(shell, SWT.BORDER, SWT.V_SCROLL | SWT.H_SCROLL);
		modelEditor2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		btnConfirm = new Button(shell, SWT.NONE);
		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnConfirmwidgetSelected(e);
			}
		});
		btnConfirm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
		btnConfirm.setText("Confirm");
		txtNewTableName.setEnabled(false);
	}
	protected void docheckSinglewidgetSelected(SelectionEvent e) {
	}
	protected void dochkMultiwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnConfirmwidgetSelected(SelectionEvent e) {
	}
	protected void dochk1widgetSelected(SelectionEvent e) {
		check();
		refresh();
	}
	protected void dochk2widgetSelected(SelectionEvent e) {
		check();
		refresh();
	}
	protected void dochk3widgetSelected(SelectionEvent e) {
		check();
		refresh();
	}
	protected void dochk4widgetSelected(SelectionEvent e) {
		check();
		refresh();
	}
	protected void dochk5widgetSelected(SelectionEvent e) {
		check();
		refresh();
	}
	
	protected void check(){
	}
	
	protected void refresh(){
	}

	public void setSeparators(char[] ca){
		chk1.setSelection(Util.contains(ca, ';'));
		chk2.setSelection(Util.contains(ca, '\n'));
		chk3.setSelection(Util.contains(ca, ','));
		chk4.setSelection(Util.contains(ca, ' '));
		chk5.setSelection(Util.contains(ca, '.'));
	}

	public char[] getSeparators(){
		StringBuilder builder = new StringBuilder();
		if (chk1.getSelection())
			builder.append(';');
		if (chk2.getSelection())
			builder.append('\n');
		if (chk3.getSelection())
			builder.append(',');
		if (chk4.getSelection())
			builder.append(' ');
		if (chk5.getSelection())
			builder.append('.');
		return builder.toString().toCharArray();
	}
	protected void dotxtNewTableNamemodifyText(ModifyEvent arg0) {
		refresh();
	}
	protected void dolstColumnwidgetSelected(SelectionEvent e) {
	}
}
