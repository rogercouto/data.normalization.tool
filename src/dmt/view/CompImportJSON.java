package dmt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import dmt.controller.CompModelEditorController;

public class CompImportJSON extends Composite {
	protected Composite composite;
	protected Label lblNestedTables;
	protected Button btnReplicate;
	protected Button btnSplit;
	protected CompModelEditorController modelEditor1;
	protected CompModelEditorController modelEditor2;
	protected Button btnImport;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompImportJSON(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		composite.setLayout(new GridLayout(3, false));
		lblNestedTables = new Label(composite, SWT.NONE);
		lblNestedTables.setText("Nested tables:");
		btnReplicate = new Button(composite, SWT.RADIO);
		btnReplicate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnReplicatewidgetSelected(e);
			}
		});
		btnReplicate.setSelection(true);
		btnReplicate.setText("Single table (Recommended)");
		btnSplit = new Button(composite, SWT.RADIO);
		btnSplit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnSplitwidgetSelected(e);
			}
		});
		btnSplit.setText("Split tables");
		modelEditor1 = new CompModelEditorController(this, SWT.BORDER, SWT.NONE);
		GridData gd_modelEditor1 = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_modelEditor1.widthHint = 220;
		modelEditor1.setLayoutData(gd_modelEditor1);
		modelEditor2 = new CompModelEditorController(this, SWT.BORDER, SWT.NONE);
		modelEditor2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		new Label(this, SWT.NONE);
		btnImport = new Button(this, SWT.NONE);
		btnImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnImportwidgetSelected(e);
			}
		});
		btnImport.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnImport.setText("Import");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	protected void dobtnReplicatewidgetSelected(SelectionEvent e) {
	}
	protected void dobtnSplitwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnImportwidgetSelected(SelectionEvent e) {
	}
}
