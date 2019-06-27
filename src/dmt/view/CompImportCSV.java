package dmt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;

public class CompImportCSV extends Composite {

	protected Table table;
	protected Composite composite;
	protected Button btnFirst;
	protected Button btnPrev;
	protected Spinner spinner;
	protected Button btnNext;
	protected Button btnLast;
	protected Group grpImportOptions;
	protected Label lblDataPreview;
	protected Label lblColumnNamesRow;
	protected Spinner spinnerNames;
	protected Label lblColumnDescriptionsRow;
	protected Button checkBox;
	protected Spinner spinnerDescriptions;
	protected Label lblStartOn;
	protected Spinner spinnerBegin;
	protected Spinner spinnerEnd;
	protected Button btnImport;
	protected Link linkSelectAll;
	protected Link linkUnselectAll;
	protected Label lblDataTypes;
	protected Combo comboTypes;
	protected Label lblTableName;
	protected Text txtTableName;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompImportCSV(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		grpImportOptions = new Group(this, SWT.NONE);
		grpImportOptions.setText("Import options");
		grpImportOptions.setLayout(new GridLayout(4, false));
		grpImportOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblTableName = new Label(grpImportOptions, SWT.NONE);
		lblTableName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTableName.setText("Table name:");
		txtTableName = new Text(grpImportOptions, SWT.BORDER);
		txtTableName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		lblColumnNamesRow = new Label(grpImportOptions, SWT.NONE);
		lblColumnNamesRow.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblColumnNamesRow.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		lblColumnNamesRow.setText("Column names row:");
		spinnerNames = new Spinner(grpImportOptions, SWT.BORDER);
		spinnerNames.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		spinnerNames.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dospinnerNameswidgetSelected(e);
			}
		});
		linkSelectAll = new Link(grpImportOptions, SWT.NONE);
		linkSelectAll.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		linkSelectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dolinkSelectAllwidgetSelected(e);
			}
		});
		linkSelectAll.setText("<a>Select all</a>");
		linkUnselectAll = new Link(grpImportOptions, SWT.NONE);
		linkUnselectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dolinkUnselectAllwidgetSelected(e);
			}
		});
		linkUnselectAll.setText("<a>Unselect all</a>");
		lblColumnDescriptionsRow = new Label(grpImportOptions, SWT.NONE);
		lblColumnDescriptionsRow.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblColumnDescriptionsRow.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		lblColumnDescriptionsRow.setText("Column descriptions row:");
		spinnerDescriptions = new Spinner(grpImportOptions, SWT.BORDER);
		spinnerDescriptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		spinnerDescriptions.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dospinnerDescriptionswidgetSelected(e);
			}
		});
		spinnerDescriptions.setEnabled(false);
		checkBox = new Button(grpImportOptions, SWT.CHECK);
		checkBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnCheckButtonwidgetSelected(e);
			}
		});
		checkBox.setSelection(true);
		checkBox.setText("Ignore");
		new Label(grpImportOptions, SWT.NONE);
		lblStartOn = new Label(grpImportOptions, SWT.NONE);
		lblStartOn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStartOn.setText("Data range (rows):");
		spinnerBegin = new Spinner(grpImportOptions, SWT.BORDER);
		spinnerBegin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		spinnerEnd = new Spinner(grpImportOptions, SWT.BORDER);
		new Label(grpImportOptions, SWT.NONE);
		lblDataTypes = new Label(grpImportOptions, SWT.NONE);
		lblDataTypes.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDataTypes.setText("Data types:");
		comboTypes = new Combo(grpImportOptions, SWT.READ_ONLY);
		comboTypes.add("Strings");
		comboTypes.add("Best match");
		comboTypes.select(0);
		comboTypes.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));

		lblDataPreview = new Label(this, SWT.NONE);
		lblDataPreview.setText("Data preview");
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setLinesVisible(true);
		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new GridLayout(5, false));
		btnFirst = new Button(composite, SWT.NONE);
		btnFirst.setToolTipText("First page");
		btnFirst.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnFirstwidgetSelected(e);
			}
		});
		btnFirst.setText("|<");
		btnPrev = new Button(composite, SWT.NONE);
		btnPrev.setToolTipText("Previows page");
		btnPrev.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnPrevwidgetSelected(e);
			}
		});
		btnPrev.setText("<");
		spinner = new Spinner(composite, SWT.BORDER);
		spinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dospinnerwidgetSelected(e);
			}
		});
		spinner.setMinimum(1);
		btnNext = new Button(composite, SWT.NONE);
		btnNext.setToolTipText("Next page");
		btnNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnNextwidgetSelected(e);
			}
		});
		btnNext.setText(">");
		btnLast = new Button(composite, SWT.NONE);
		btnLast.setToolTipText("Last page");
		btnLast.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnLastwidgetSelected(e);
			}
		});
		btnLast.setText(">|");
		btnImport = new Button(this, SWT.NONE);
		btnImport.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnImportwidgetSelected(e);
			}
		});
		btnImport.setText("Import");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	protected void dospinnerwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnFirstwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnPrevwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnNextwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnLastwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnCheckButtonwidgetSelected(SelectionEvent e) {
		spinnerDescriptions.setEnabled(!checkBox.getSelection());
	}
	protected void dospinnerNameswidgetSelected(SelectionEvent e) {
	}
	protected void dospinnerDescriptionswidgetSelected(SelectionEvent e) {
	}
	protected void dobtnImportwidgetSelected(SelectionEvent e) {
	}
	protected void dolinkSelectAllwidgetSelected(SelectionEvent e) {
	}
	protected void dolinkUnselectAllwidgetSelected(SelectionEvent e) {
	}
}
