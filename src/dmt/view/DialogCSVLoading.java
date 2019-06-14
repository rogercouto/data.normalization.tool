package dmt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import dmt.tools.Benchmark;
import dmt.tools.Util;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class DialogCSVLoading extends Dialog {

	protected Object result;
	protected Shell shell;
	protected ProgressBar progressBar;
	protected Button btnLoad;
	protected String message;
	protected Label lblNewLabel;
	protected Text txtSeparator;
	protected Label lblStringDelimiter;
	protected Text txtDelimiter;
	protected Label lblNewLabel_1;
	
	private Benchmark benchmark;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DialogCSVLoading(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		benchmark = new Benchmark();
		benchmark.start();
		createContents();
	}

	protected void doTask(){}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		shell.pack();
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
		shell.setText(getText());
		GridLayout gl_shell = new GridLayout(5, false);
		gl_shell.marginHeight = 15;
		gl_shell.marginWidth = 15;
		shell.setLayout(gl_shell);
		lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Separator:");
		txtSeparator = new Text(shell, SWT.BORDER);
		txtSeparator.setText(";");
		GridData gd_txtSeparator = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtSeparator.widthHint = 10;
		txtSeparator.setLayoutData(gd_txtSeparator);
		lblStringDelimiter = new Label(shell, SWT.NONE);
		lblStringDelimiter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStringDelimiter.setText("String delimiter:");
		txtDelimiter = new Text(shell, SWT.BORDER);
		txtDelimiter.setText("\"");
		GridData gd_txtDelimiter = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtDelimiter.widthHint = 10;
		txtDelimiter.setLayoutData(gd_txtDelimiter);
		lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		progressBar = new ProgressBar(shell, SWT.NONE);
		GridData gd_progressBar = new GridData(SWT.LEFT, SWT.CENTER, true, false, 5, 1);
		gd_progressBar.widthHint = 300;
		progressBar.setLayoutData(gd_progressBar);
		if (message != null)
			shell.setText(message);
		Util.centralize(shell, shell.getParent());
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		btnLoad = new Button(shell, SWT.NONE);
		btnLoad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dobtnLoadwidgetSelected(e);
			}
		});
		btnLoad.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnLoad.setText("Carregar");
	}

	public void setMessage(String message){
		this.message = message;

	}

	public double getTaskTime(){
		double taskTime = benchmark.stop(false);
		return taskTime;
	}

	protected void dobtnLoadwidgetSelected(SelectionEvent e) {
		doTask();
	}
}
