package dmt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;

public class CompDataEditor extends Composite {

	protected Table table;
	protected Composite composite;
	protected Button btnFirst;
	protected Button btnPrev;
	protected Spinner spinner;
	protected Button btnNext;
	protected Button btnLast;
	protected Label lblActions;
	protected Combo cmbActions;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompDataEditor(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		lblActions = new Label(this, SWT.NONE);
		lblActions.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblActions.setText("Actions");
		cmbActions = new Combo(this, SWT.READ_ONLY);
		cmbActions.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				docomboActionswidgetSelected(e);
			}
		});
		GridData gd_comboActions = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_comboActions.widthHint = 100;
		cmbActions.setLayoutData(gd_comboActions);
		createTable();
		createNav();
	}
	
	protected void createTable(){
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}
	
	protected void removeTable(){
		table.dispose();
	}
	
	protected void createNav(){
		composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(5, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		btnFirst = new Button(composite, SWT.NONE);
		btnFirst.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnFirstwidgetSelected(e);
			}
		});
		btnFirst.setToolTipText("First page");
		btnFirst.setText("|<");
		btnPrev = new Button(composite, SWT.NONE);
		btnPrev.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnPrevwidgetSelected(e);
			}
		});
		btnPrev.setToolTipText("Last page");
		btnPrev.setText("<");
		spinner = new Spinner(composite, SWT.BORDER);
		spinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dospinnerwidgetSelected(e);
			}
		});
		spinner.setMinimum(1);
		spinner.setSelection(1);
		btnNext = new Button(composite, SWT.NONE);
		btnNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnNextwidgetSelected(e);
			}
		});
		btnNext.setToolTipText("Next page");
		btnNext.setText(">");
		btnLast = new Button(composite, SWT.NONE);
		btnLast.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnLastwidgetSelected(e);
			}
		});
		btnLast.setToolTipText("Next");
		btnLast.setText(">|");
	}
	
	protected void removeNav(){
		composite.dispose();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	protected void dobtnFirstwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnPrevwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnNextwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnLastwidgetSelected(SelectionEvent e) {
	}
	protected void dospinnerwidgetSelected(SelectionEvent e) {
	}
	protected void docomboActionswidgetSelected(SelectionEvent e) {
	}
}
