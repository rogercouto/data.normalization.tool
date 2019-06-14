package dmt.view;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import dmt.model.Column;
import dmt.tools.Util;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class DialogKeepColumns extends Dialog {

	protected List<Column> result;
	protected Shell shell;
	protected Table tblColumns;
	protected Button btnRemove;
	protected TableColumn tblclmnNewColumn;
	protected Label lblSelectColumnsTo;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DialogKeepColumns(Shell parent) {
		super(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		createContents();
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public List<Column> open() {
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
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setSize(203, 300);
		shell.setText("Columns");
		shell.setLayout(new GridLayout(1, false));
		lblSelectColumnsTo = new Label(shell, SWT.NONE);
		lblSelectColumnsTo.setText("Select columns to import");
		tblColumns = new Table(shell, SWT.BORDER | SWT.CHECK);
		tblColumns.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tblColumns.setLinesVisible(true);
		tblclmnNewColumn = new TableColumn(tblColumns, SWT.NONE);
		tblclmnNewColumn.setWidth(155);
		btnRemove = new Button(shell, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnRemovewidgetSelected(e);
			}
		});
		btnRemove.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnRemove.setText("Confirm");

	}
	
	protected void dobtnRemovewidgetSelected(SelectionEvent e) {
	}
}
