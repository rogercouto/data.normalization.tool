package dmt.controller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;

import dmt.model.Column;
import dmt.model.data.NormalForm;
import dmt.model.data.TableData;
import dmt.normalization.Normalize;
import dmt.normalization.fd.FD;
import dmt.normalization.fd.FDMapper;
import dmt.view.DialogCheckNormalForm;

public class DialogCheckNormalFormController extends DialogCheckNormalForm{

	private TableData data = null;
	private List<FD> fds = null;
	private FDMapper mapper = null;
	private char[] seps = null;

	public DialogCheckNormalFormController(Shell parent, TableData data) {
		super(parent);
		this.data = data;
		mapper = new FDMapper(data);
		mapper.setMaxLevel(1);
		mapper.setMaxData(50);
		mapper.setTolerance(1.0);
	}

	private char[] getSeparators(){
		List<Character> l = new ArrayList<>();
		if (chk1.getSelection())
			l.add(';');
		if (chk2.getSelection())
			l.add('\n');
		if (chk3.getSelection())
			l.add(',');
		if (chk4.getSelection())
			l.add(' ');
		if (chk5.getSelection())
			l.add('.');
		char[] seps = new char[l.size()];
		for (int i = 0; i < seps.length; i++) {
			seps[i] = l.get(i);
		}
		return seps;
	}

	protected void dobtnCheckwidgetSelected(SelectionEvent e) {
		seps = getSeparators();
		List<Column> columns = Normalize.findMultiValuedColumns(data, seps);
		if (columns.size() == 0){
			txt1FN.setText("table in 1FN: no multiple values found");
			data.setNormalForm(NormalForm.NF1);
			btnCheck2.setEnabled(true);
		}else{
			txt1FN.setText("table denormalizated: multiple values found!");
			txt2FN.setText("");
			data.setNormalForm(NormalForm.NN);
			btnCheck2.setEnabled(false);
			btnNormalize.setEnabled(true);
		}
	}

	protected void dobtnCheck2widgetSelected(SelectionEvent e) {
		if (fds == null)
			fds = mapper.getFDs();
		if (data.getTable().getPrimaryKeys().size() < 2){
			if (fds.size() == 0){
				txt2FN.setText("table in 3FN: no dependences found!");
				data.setNormalForm(NormalForm.NF3);
			}else{
				txt2FN.setText("table in 2FN: transitory dependences found!");
				data.setNormalForm(NormalForm.NF2);
				btnNormalize.setEnabled(true);
			}
		}//multiple primary keys don't allowed yet
	}

	protected void dobtnNormalizewidgetSelected(SelectionEvent e) {
		if (seps != null){
			result = seps;
			shell.close();
		}
	}
}
