package dmt.view;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import dmt.tools.Util;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;

public class DialogCheckNormalForm extends Dialog {

	protected Object result;
	protected Shell shell;
	protected Group grpfn;
	protected Label lblCheckForMultiple;
	protected Label lblSeparator;
	protected Button chk1;
	protected Button chk2;
	protected Button chk3;
	protected Button chk4;
	protected Button chk5;
	protected Button btnCheck1;
	protected Label lblStatus;
	protected Text txt1FN;
	protected Group grpfn_1;
	protected Label lblPartialDependences;
	protected Text txt2FN;
	protected Button btnCheck2;
	protected Button btnNormalize;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DialogCheckNormalForm(Shell parent) {
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
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				doshellshellClosed(e);
			}
		});
		shell.setSize(386, 252);
		shell.setText("Check normal form");
		shell.setLayout(new GridLayout(1, false));
		grpfn = new Group(shell, SWT.NONE);
		grpfn.setLayout(new GridLayout(7, false));
		grpfn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpfn.setText("1FN");
		lblCheckForMultiple = new Label(grpfn, SWT.NONE);
		lblCheckForMultiple.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 6, 1));
		lblCheckForMultiple.setText("Check for multiple values");
		new Label(grpfn, SWT.NONE);
		lblSeparator = new Label(grpfn, SWT.NONE);
		lblSeparator.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSeparator.setText("Separators:");
		chk1 = new Button(grpfn, SWT.CHECK);
		chk1.setSelection(true);
		chk1.setText(";");
		chk2 = new Button(grpfn, SWT.CHECK);
		chk2.setSelection(true);
		chk2.setText("\\n");
		chk3 = new Button(grpfn, SWT.CHECK);
		chk3.setText(",");
		chk4 = new Button(grpfn, SWT.CHECK);
		chk4.setText("space");
		chk5 = new Button(grpfn, SWT.CHECK);
		chk5.setText(".");
		btnCheck1 = new Button(grpfn, SWT.NONE);
		btnCheck1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnCheckwidgetSelected(e);
			}
		});
		btnCheck1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnCheck1.setText("check");
		lblStatus = new Label(grpfn, SWT.NONE);
		lblStatus.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStatus.setText("Status:");
		txt1FN = new Text(grpfn, SWT.BORDER);
		txt1FN.setEditable(false);
		txt1FN.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
		grpfn_1 = new Group(shell, SWT.NONE);
		grpfn_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpfn_1.setText("2FN-3FN");
		grpfn_1.setLayout(new GridLayout(2, false));
		btnCheck2 = new Button(grpfn_1, SWT.NONE);
		btnCheck2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnCheck2widgetSelected(e);
			}
		});
		btnCheck2.setEnabled(false);
		btnCheck2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
		btnCheck2.setText("Check");
		lblPartialDependences = new Label(grpfn_1, SWT.NONE);
		lblPartialDependences.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPartialDependences.setText("Status:");
		txt2FN = new Text(grpfn_1, SWT.BORDER);
		txt2FN.setEditable(false);
		txt2FN.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnNormalize = new Button(shell, SWT.NONE);
		btnNormalize.setEnabled(false);
		btnNormalize.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnNormalizewidgetSelected(e);
			}
		});
		btnNormalize.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnNormalize.setText("Normalize");

	}
	protected void dobtnCheckwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnCheck2widgetSelected(SelectionEvent e) {
	}
	protected void doshellshellClosed(ShellEvent e) {
	}
	protected void dobtnNormalizewidgetSelected(SelectionEvent e) {
	}
}
