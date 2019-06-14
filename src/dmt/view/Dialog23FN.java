package dmt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import dmt.controller.CompModelEditorController;
import dmt.tools.Util;

public class Dialog23FN extends Dialog {

	protected Object result;
	protected Shell shell;
	protected Group grpColumnsUsed;
	protected List lstAval;
	protected Composite composite;
	protected Button btnAdd;
	protected Button btnAddAll;
	protected Button btnRem;
	protected Button btnRemAll;
	protected List lstSel;
	protected Label lblAvaliable;
	protected Label lblSelected;
	protected Group grpDependences;
	protected Button btnSearch;
	protected List lstFD;
	protected Button btnPreview;
	protected Label lblCominate;
	protected Spinner spnCombin;
	protected CTabFolder tabFolder;
	protected CTabItem tbtmFunctionalDependences;
	protected Composite composite_1;
	protected CTabItem tbtmPreview;
	protected Composite composite_2;
	protected CompModelEditorController modelBefore;
	protected Label lblNewLabel;
	protected Label lblNewLabel_1;
	protected CompModelEditorController modelAfter;
	protected Button btnConfirmChanges;
	protected Group group;
	protected Label lblDataSample;
	protected Spinner spnSample;
	protected Label lblTolerance;
	protected Spinner spnTolerance;
	protected Label label;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public Dialog23FN(Shell parent) {
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
		shell.setSize(746, 443);
		shell.setText("3FN Normalization");
		shell.setLayout(new GridLayout(2, false));
		tabFolder = new CTabFolder(shell, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tbtmFunctionalDependences = new CTabItem(tabFolder, SWT.NONE);
		tbtmFunctionalDependences.setText("Functional dependences");
		composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmFunctionalDependences.setControl(composite_1);
		composite_1.setLayout(new GridLayout(2, false));
		grpColumnsUsed = new Group(composite_1, SWT.NONE);
		grpColumnsUsed.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 2));
		grpColumnsUsed.setText("Columns used");
		grpColumnsUsed.setLayout(new GridLayout(3, false));
		lblAvaliable = new Label(grpColumnsUsed, SWT.NONE);
		lblAvaliable.setText("Avaliable");
		new Label(grpColumnsUsed, SWT.NONE);
		lblSelected = new Label(grpColumnsUsed, SWT.NONE);
		lblSelected.setText("Selected");
		lstAval = new List(grpColumnsUsed, SWT.BORDER | SWT.V_SCROLL);
		GridData gd_lstAval = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_lstAval.widthHint = 150;
		lstAval.setLayoutData(gd_lstAval);
		composite = new Composite(grpColumnsUsed, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		composite.setLayout(new GridLayout(1, false));
		btnAdd = new Button(composite, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnAddwidgetSelected(e);
			}
		});
		btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnAdd.setText(">");
		btnAddAll = new Button(composite, SWT.NONE);
		btnAddAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnAddAllwidgetSelected(e);
			}
		});
		btnAddAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnAddAll.setText(">>");
		btnRem = new Button(composite, SWT.NONE);
		btnRem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnRemwidgetSelected(e);
			}
		});
		btnRem.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnRem.setText("<");
		btnRemAll = new Button(composite, SWT.NONE);
		btnRemAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnRemAllwidgetSelected(e);
			}
		});
		btnRemAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnRemAll.setText("<<");
		lstSel = new List(grpColumnsUsed, SWT.BORDER | SWT.V_SCROLL);
		GridData gd_lstSel = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_lstSel.widthHint = 150;
		lstSel.setLayoutData(gd_lstSel);
		group = new Group(composite_1, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		group.setLayout(new GridLayout(3, false));
		lblCominate = new Label(group, SWT.NONE);
		lblCominate.setText("Max combination:");
		spnCombin = new Spinner(group, SWT.BORDER);
		spnCombin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		spnCombin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dospnCombinwidgetSelected(e);
			}
		});
		spnCombin.setMaximum(5);
		spnCombin.setMinimum(1);
		spnCombin.setSelection(1);
		new Label(group, SWT.NONE);
		lblDataSample = new Label(group, SWT.NONE);
		lblDataSample.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDataSample.setText("Data sample:");
		spnSample = new Spinner(group, SWT.BORDER);
		spnSample.setMaximum(500);
		spnSample.setMinimum(50);
		spnSample.setSelection(50);
		new Label(group, SWT.NONE);
		lblTolerance = new Label(group, SWT.NONE);
		lblTolerance.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTolerance.setText("Tolerance:");
		spnTolerance = new Spinner(group, SWT.BORDER);
		spnTolerance.setSelection(100);
		label = new Label(group, SWT.NONE);
		label.setText("%");
		grpDependences = new Group(composite_1, SWT.NONE);
		grpDependences.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpDependences.setLayout(new GridLayout(1, false));
		grpDependences.setText("Dependences");
		btnSearch = new Button(grpDependences, SWT.NONE);
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnSearchwidgetSelected(e);
			}
		});
		btnSearch.setText("Search");
		lstFD = new List(grpDependences, SWT.BORDER | SWT.V_SCROLL);
		lstFD.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		btnPreview = new Button(grpDependences, SWT.NONE);
		btnPreview.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnNormalizewidgetSelected(e);
			}
		});
		GridData gd_btnPreview = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnPreview.widthHint = 100;
		btnPreview.setLayoutData(gd_btnPreview);
		btnPreview.setText("Preview");
	}
	
	protected void createPreview(){
		if (tbtmPreview == null){
			tbtmPreview = new CTabItem(tabFolder, SWT.NONE);
			tbtmPreview.setText("Preview");
			composite_2 = new Composite(tabFolder, SWT.NONE);
			tbtmPreview.setControl(composite_2);
			composite_2.setLayout(new GridLayout(2, false));
			lblNewLabel = new Label(composite_2, SWT.NONE);
			lblNewLabel.setText("Actual");
			lblNewLabel_1 = new Label(composite_2, SWT.NONE);
			lblNewLabel_1.setText("Refactor");
			modelBefore = new CompModelEditorController(composite_2, SWT.BORDER);
			modelBefore.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			modelAfter = new CompModelEditorController(composite_2, SWT.BORDER);
			modelAfter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			new Label(composite_2, SWT.NONE);
			btnConfirmChanges = new Button(composite_2, SWT.NONE);
			btnConfirmChanges.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			btnConfirmChanges.setText("Confirm");
			btnConfirmChanges.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					dobtnConfirmChangeswidgetSelected(e);
				}
			});
		}
	}
	
	protected void dobtnAddwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnAddAllwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnRemwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnRemAllwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnSearchwidgetSelected(SelectionEvent e) {
	}
	protected void dospnCombinwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnNormalizewidgetSelected(SelectionEvent e) {
	}
	protected void dobtnConfirmChangeswidgetSelected(SelectionEvent e) {
	}
}
