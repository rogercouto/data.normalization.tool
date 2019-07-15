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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Tree;

import dmt.tools.Util;

public class DialogClusterize extends Dialog {

	protected Object result;
	protected Shell shell;
	protected Group grpOptions;
	protected Label lblEditDistance;
	protected Spinner spinner;
	protected Label lblColumn;
	protected Combo cmbColumn;
	protected Button btnConfirm;
	protected Tree tree;
	protected Label lblMethod;
	protected Combo cmbMethod;
	protected Button btnRemoveSel;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DialogClusterize(Shell parent) {
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
		shell.setSize(339, 384);
		shell.setText("Clusterize");
		shell.setLayout(new GridLayout(1, false));
		grpOptions = new Group(shell, SWT.NONE);
		grpOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpOptions.setLayout(new GridLayout(2, false));
		grpOptions.setText("Options");
		lblEditDistance = new Label(grpOptions, SWT.NONE);
		lblEditDistance.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEditDistance.setText("Edit distance:");
		spinner = new Spinner(grpOptions, SWT.BORDER);
		spinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dospinnerwidgetSelected(e);
			}
		});
		spinner.setSelection(1);
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
		lblMethod = new Label(grpOptions, SWT.NONE);
		lblMethod.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMethod.setText("Method:");
		cmbMethod = new Combo(grpOptions, SWT.READ_ONLY);
		cmbMethod.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				docmbMethodwidgetSelected(e);
			}
		});
		cmbMethod.add("Higher count");
		cmbMethod.add("First");
		cmbMethod.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(grpOptions, SWT.NONE);
		btnRemoveSel = new Button(grpOptions, SWT.NONE);
		btnRemoveSel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnRemoveSelwidgetSelected(e);
			}
		});
		btnRemoveSel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnRemoveSel.setText("Remove sel");
		tree = new Tree(shell, SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		btnConfirm = new Button(shell, SWT.NONE);
		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnConfirmwidgetSelected(e);
			}
		});
		btnConfirm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnConfirm.setText("Confirm");

	}
	
	protected void docmbColumnwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnConfirmwidgetSelected(SelectionEvent e) {
	}
	protected void dospinnerwidgetSelected(SelectionEvent e) {
	}
	protected void docmbMethodwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnRemoveSelwidgetSelected(SelectionEvent e) {
	}
}
