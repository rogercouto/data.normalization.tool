package dmt.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import dmt.database.MysqlServer;
import dmt.database.ReverseEng;
import dmt.database.SqliteServer;
import dmt.input.CSVReader;
import dmt.model.Column;
import dmt.model.Table;
import dmt.model.data.TableData;
import dmt.normalization.Normalize;
import dmt.view.TableModel;

public class ModelEditorTest {

	protected Shell shell;
	protected CompModelEditorController modelEditor;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ModelEditorTest window = new ModelEditorTest();
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
		shell.setSize(960, 480);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		modelEditor = new CompModelEditorController(shell, SWT.NONE);
		createTestTables2();
	}

	public void createTestTables() {
		Table table = new Table("funcionario");
		table.createSurrogateKey();
		Column c0 = new Column("nome_func");
		c0.setType(String.class);
		c0.setDbType("VARCHAR");
		c0.setNotNull(true);
		c0.setSize(255);
		table.addColumn(c0);
		Column c1 = new Column("salario");
		c1.setType(Double.class);
		c1.setDbType("DOUBLE");
		c1.setNotNull(true);
		table.addColumn(c1);
		Table table1 = new Table("depto");
		table1.createSurrogateKey();
		Column c2 = new Column("nome_depto");
		c2.setType(String.class);
		c2.setDbType("VARCHAR");
		c2.setNotNull(true);
		c2.setSize(255);
		table1.addColumn(c2);
		Column c3 = new Column("depto_id");
		c3.setForeignKey(table1.getColumn(0));
		c3.setType(Integer.class);
		c3.setDbType("INTEGER");
		c3.setNotNull(true);
		table.addColumn(c3);
		//table.addTable(table1);
		modelEditor.addTableModel(new TableModel(table, new Point(150, 100)));
		modelEditor.addTableModel(new TableModel(table1, new Point(400, 150)));
		modelEditor.draw();
	}

	public void createTestTables2() {
		Table tableFunc = new Table("funcionario");
		tableFunc.createSurrogateKey();
		Column columnNomeFunc = new Column("nome_func");
		columnNomeFunc.setType(String.class);
		columnNomeFunc.setDbType("VARCHAR");
		columnNomeFunc.setNotNull(true);
		columnNomeFunc.setSize(255);
		tableFunc.addColumn(columnNomeFunc);
		Column columnSalario = new Column("salario");
		columnSalario.setType(Double.class);
		columnSalario.setDbType("DOUBLE");
		columnSalario.setNotNull(true);
		tableFunc.addColumn(columnSalario);

		Table tableDepto = new Table("depto");
		tableDepto.createSurrogateKey();
		Column columnNomeDepto = new Column("nome_depto");
		columnNomeDepto.setType(String.class);
		columnNomeDepto.setDbType("VARCHAR");
		columnNomeDepto.setNotNull(true);
		columnNomeDepto.setSize(255);
		tableDepto.addColumn(columnNomeDepto);

		Table tableFuncDepto = new Table("alocacao");
		Column columnFuncId = new Column("funcionario_id");
		columnFuncId.setType(Integer.class);
		columnFuncId.setPrimaryKey(true);
		columnFuncId.setForeignKey(tableFunc.getColumn(0));
		tableFuncDepto.addColumn(columnFuncId);

		Column columnDeptoId = new Column("depto_id");
		columnDeptoId.setType(Integer.class);
		columnDeptoId.setPrimaryKey(true);
		columnDeptoId.setForeignKey(tableDepto.getColumn(0));
		tableFuncDepto.addColumn(columnDeptoId);

		modelEditor.addTableModel(new TableModel(tableFunc, new Point(150, 100)));
		modelEditor.addTableModel(new TableModel(tableDepto, new Point(400, 150)));
		modelEditor.addTableModel(new TableModel(tableFuncDepto, new Point(300, 350)));
		modelEditor.draw();
		modelEditor.setDoubleClickListener(new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				if (arg0.data != null){
					TableModel model = (TableModel)arg0.data;
					System.err.println(model.getTable());
				}
			}
		});
	}

	public void createTestTables3() {
		CSVReader reader = new CSVReader("data/exemplo2.csv");
        TableData data = reader.getData(';','"');
        Normalize.matchBestTypes(data);
        Normalize.setStringSizes(data, 0);
        data.getTable().setName("Funcionario_Depto");
        data.getTable().getColumn("CodFuncionario").setPrimaryKey(true);
        data.getTable().getColumn("NumDepto").setPrimaryKey(true);
        System.out.println(data.getTable());
        int editDistance = 0;
        double perc = 1.0; //100%
        @SuppressWarnings("deprecation")
		HashMap<String, HashSet<Column>> deps = Normalize.getAllDependencies(data, editDistance, perc);
        List<TableData> datas = Normalize.splitDependences(data, "NumDepto", deps.get("NumDepto"));
        datas.forEach(d->{
        	int width = 100+(datas.indexOf(d)*200);
        	modelEditor.addTableModel(new TableModel(d.getTable(), new Point(width, 100)));
        });
	}

	public void createTestTables4() {
		MysqlServer server = new MysqlServer("biblioteca", "root", "");
		List<Table> tables = ReverseEng.getTables(server);
		for(Table table : tables){
			TableModel model = new TableModel(table,new Point(100, 100));
			System.out.println(table);
			modelEditor.addTableModel(model);
		}
	}

	public void createTestTables5() {
		SqliteServer server = new SqliteServer("C:/Dropbox/Java/rc.imobiliaria/rc.imobiliaria_data/imob_data.rdb");
		List<Table> tables = ReverseEng.getTables(server);
		for(Table table : tables){
			TableModel model = new TableModel(table,new Point(100, 100));
			modelEditor.addTableModel(model);
		}
	}

}
