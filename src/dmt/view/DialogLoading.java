package dmt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import dmt.tools.Benchmark;
import dmt.tools.Util;

public class DialogLoading extends Dialog {

	protected Object result;
	protected Shell shell;
	protected ProgressBar progressBar;
	protected String message;
	
	private Benchmark benchmark;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DialogLoading(Shell parent) {
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
		doTask();
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
		shell.setSize(284, 82);
		shell.setText(getText());
		GridLayout gl_shell = new GridLayout(1, false);
		gl_shell.marginHeight = 20;
		gl_shell.marginWidth = 10;
		shell.setLayout(gl_shell);
		progressBar = new ProgressBar(shell, SWT.INDETERMINATE);
		GridData gd_progressBar = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_progressBar.widthHint = 300;
		progressBar.setLayoutData(gd_progressBar);
		if (message != null)
			shell.setText(message);
		Util.centralize(shell, shell.getParent());
	}

	public void setMessage(String message){
		this.message = message;

	}

	public double getTaskTime(){
		double taskTime = benchmark.stop(false);
		return taskTime;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}
	
}
