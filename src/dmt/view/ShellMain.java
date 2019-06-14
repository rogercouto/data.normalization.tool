package dmt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import dmt.tools.Util;

public class ShellMain {

	protected Shell shell;
	protected Menu menu;
	protected MenuItem mntmNewSubmenu;
	protected Menu menu_1;
	protected MenuItem menuArqCSV;
	protected Composite compStatus;
	protected Label lblStatus;
	protected ToolItem btnCsv;
	protected ToolBar toolBar_1;
	protected ToolItem btnJson;
	protected Composite compCenter;
	protected MenuItem menuProject;
	protected Menu menu_2;
	protected MenuItem mntmNewItem;
	protected ToolItem btnOpen;
	protected ToolItem btnSave;
	protected ToolItem btnDB;
	protected ToolItem toolItem;
	protected MenuItem mntmJsonFile;
	protected MenuItem mntmOpen;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ShellMain window = new ShellMain();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		//shell.setMaximized(true);
		Util.centralize(shell);
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(706, 537);
		shell.setText("Data Normalization Tool");
		GridLayout gl_shell = new GridLayout(1, false);
		gl_shell.verticalSpacing = 0;
		gl_shell.horizontalSpacing = 0;
		gl_shell.marginHeight = 0;
		gl_shell.marginWidth = 0;
		shell.setLayout(gl_shell);
		menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		menuProject = new MenuItem(menu, SWT.CASCADE);
		menuProject.setText("Project");
		menu_2 = new Menu(menuProject);
		menuProject.setMenu(menu_2);
		mntmNewItem = new MenuItem(menu_2, SWT.NONE);
		mntmNewItem.setText("Save...");
		mntmOpen = new MenuItem(menu_2, SWT.NONE);
		mntmOpen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				domntmOpenwidgetSelected(e);
			}
		});
		mntmOpen.setText("Open...");
		mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("Import");
		menu_1 = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(menu_1);
		menuArqCSV = new MenuItem(menu_1, SWT.NONE);
		menuArqCSV.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				domenuArqCSVwidgetSelected(e);
			}
		});
		menuArqCSV.setText("CSV File...");
		mntmJsonFile = new MenuItem(menu_1, SWT.NONE);
		mntmJsonFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				domntmJsonFilewidgetSelected(e);
			}
		});
		mntmJsonFile.setText("JSON File...");
		toolBar_1 = new ToolBar(shell, SWT.FLAT);
		toolBar_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		toolBar_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		btnOpen = new ToolItem(toolBar_1, SWT.NONE);
		btnOpen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnOpenwidgetSelected(e);
			}
		});
		btnOpen.setToolTipText("Open Project");
		btnOpen.setImage(SWTResourceManager.getImage(ShellMain.class, "/icon/open32.png"));
		btnSave = new ToolItem(toolBar_1, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnSavewidgetSelected(e);
			}
		});
		btnSave.setEnabled(false);
		btnSave.setToolTipText("Save Project");
		btnSave.setImage(SWTResourceManager.getImage(ShellMain.class, "/icon/save32.png"));
		toolItem = new ToolItem(toolBar_1, SWT.SEPARATOR);
		btnCsv = new ToolItem(toolBar_1, SWT.NONE);
		btnCsv.setToolTipText("Import from CSV");
		btnCsv.setImage(SWTResourceManager.getImage(ShellMain.class, "/icon/csv32.png"));
		btnJson = new ToolItem(toolBar_1, SWT.NONE);
		btnJson.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnJsonwidgetSelected(e);
			}
		});
		btnJson.setToolTipText("Import from JSON");
		btnJson.setImage(SWTResourceManager.getImage(ShellMain.class, "/icon/json32.png"));
		btnDB = new ToolItem(toolBar_1, SWT.NONE);
		btnDB.setImage(SWTResourceManager.getImage(ShellMain.class, "/icon/database32.png"));
		btnDB.setToolTipText("Import From SGBD");
		btnCsv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dotltmCsvwidgetSelected(e);
			}
		});
		compCenter = new Composite(shell, SWT.NONE);
		compCenter.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		compCenter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compCenter.setLayout(new FillLayout(SWT.HORIZONTAL));
		compStatus = new Composite(shell, SWT.NONE);
		compStatus.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		compStatus.setLayout(new GridLayout(1, false));
		lblStatus = new Label(compStatus, SWT.NONE);
		lblStatus.setText(" ");
		GridLayout gl_compositeMain = new GridLayout(3, false);
		gl_compositeMain.verticalSpacing = 0;
		gl_compositeMain.marginHeight = 0;
		gl_compositeMain.marginWidth = 0;
	}

	protected void domenuArqCSVwidgetSelected(SelectionEvent e) {
	}
	protected void dotltmCsvwidgetSelected(SelectionEvent e) {
		domenuArqCSVwidgetSelected(e);
	}
	protected void dobtnSavewidgetSelected(SelectionEvent e) {
	}
	protected void dobtnJsonwidgetSelected(SelectionEvent e) {
		domntmJsonFilewidgetSelected(e);
	}
	protected void domntmJsonFilewidgetSelected(SelectionEvent e) {
	}
	protected void domntmOpenwidgetSelected(SelectionEvent e) {
	}
	protected void dobtnOpenwidgetSelected(SelectionEvent e) {
		domntmOpenwidgetSelected(e);
	}
}
