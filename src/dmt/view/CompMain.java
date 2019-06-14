package dmt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import dmt.controller.CompModelEditorController;

public class CompMain extends Composite {

	protected CTabFolder tabFolder;
	protected CTabItem tbtmModel;
	protected CompModelEditorController modelEditor;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompMain(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		tabFolder = new CTabFolder(this, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tbtmModel = new CTabItem(tabFolder, SWT.NONE);
		tbtmModel.setText("Model");
		modelEditor = new CompModelEditorController(tabFolder, SWT.NONE);
		tbtmModel.setControl(modelEditor);
		tabFolder.setSelection(tabFolder.getItem(0));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
