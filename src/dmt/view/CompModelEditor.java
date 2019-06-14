package dmt.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

public class CompModelEditor extends Composite {
	
	protected Canvas canvas;
	protected int hSelection;
	protected int vSelection;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompModelEditor(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		canvas = new Canvas(this, SWT.H_SCROLL | SWT.V_SCROLL);
		initialize();
		
	}
	public CompModelEditor(Composite parent, int style, int canvasStyle) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		canvas = new Canvas(this, canvasStyle);
		initialize();
		
	}
	
	private void initialize() {
		if (canvas.getStyle() == (SWT.H_SCROLL | SWT.V_SCROLL)){
			canvas.getHorizontalBar().addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					scrollMoved();
				}
			});
			canvas.getVerticalBar().addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					scrollMoved();
				}
			});
		}
		canvas.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
	}

	@Override
	protected void checkSubclass() {
	}
	
	protected void scrollMoved(){
		hSelection = canvas.getHorizontalBar().getSelection()*5;
		vSelection = canvas.getVerticalBar().getSelection()*5;
	}

}
