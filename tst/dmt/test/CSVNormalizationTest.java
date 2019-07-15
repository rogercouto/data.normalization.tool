package dmt.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import dmt.input.CSVReader;
import dmt.model.Column;
import dmt.model.data.NormalForm;
import dmt.model.data.TableData;
import dmt.model.project.DataList;
import dmt.normalization.DataDivision;
import dmt.normalization.DataReplication;
import dmt.normalization.NormUtil;
import dmt.normalization.fd.FD;
import dmt.normalization.fd.FDMapper;
import dmt.preprocess.Cluster;
import dmt.preprocess.Preprocess;

public class CSVNormalizationTest {

	private String fileName;
	
	@Before
	public void setUp() {
		fileName = "data/test/enderecofunc.csv";
	}
	
	@Test
	public void testFileExists(){
		File file = new File(fileName);
		assertTrue("Arquivo não existe!", file.exists());
	}
	
	@Test
	public void testDataReaded() {
		File file = new File(fileName);
		TableData data = null;
		if (file.exists()){
			CSVReader reader = new CSVReader(fileName);
			data = reader.getData(';', '"');
			assertNotNull("data == null!", data);
			assertEquals("Número de registros deve ser 9!", data.getRowCount(), 9);
		}
	}
	
	@Test
	public void test1FN(){
		CSVReader reader = new CSVReader(fileName);
		TableData data = reader.getData(';', '"');
		if (data != null && data.getRowCount() == 9){
			Column columnEnderecos = data.getTable().getColumn("Enderecos");
			assertNotNull("Não existe coluna com o nome Enderecos!", columnEnderecos);
			DataReplication dr = new DataReplication(data);
			TableData newData = dr.splitColumn(columnEnderecos, new char[]{'\n'});
			Column columnEndereco = newData.getTable().getColumn("Endereco");
			assertNotNull("Após passagem a 1FN não existe coluna com o nome Endereco!", columnEndereco);
			assertEquals("Após passagem a 1FN o número de registros deve ser 12!", newData.getRowCount(), 12);
		}
	}
	
	@Test
	public void test1FN2(){
		CSVReader reader = new CSVReader(fileName);
		TableData data = reader.getData(';', '"');
		if (data != null){
			DataDivision dd = new DataDivision(data);
			DataList dl = dd.splitColumnsToList(new char[]{'\n'});
			assertEquals("Após passagem a 1FN o número de tabelas deve ser 2", dl.size(),2);
			TableData d1 = dl.getData("enderecofunc");
			assertNotNull("d1 == null!", d1);
			TableData d2 = dl.getData("Endereco");
			assertNotNull("d2 == null!", d2);
		}
	}
	
	@Test
	public void testPreprocess(){
		CSVReader reader = new CSVReader(fileName);
		TableData data = reader.getData(';', '"');
		if (data != null && data.getRowCount() == 9){
			DataReplication dr = new DataReplication(data);
			Column columnEnderecos = data.getTable().getColumn("Enderecos");
			if (columnEnderecos != null){
				TableData newData = dr.splitColumn(columnEnderecos, new char[]{'\n'});
				if (newData.getRowCount() == 12){
					Preprocess preprocess = new Preprocess(newData);
					TableData newData2 = preprocess.splitColumn("Endereco", "Endereco", "Cidade", ",");
					assertEquals("Wrong number of clumns after split!", newData2.getTable().getElementCount(),newData.getTable().getElementCount() + 1);
					if (newData2.getTable().getElementCount() == newData.getTable().getElementCount() + 1){
						preprocess = new Preprocess(newData2);
						TableData newData3 = preprocess.splitColumn("Cidade", "Cidade", "UF", "-");
						assertEquals("Wrong number of clumns after split!", newData3.getTable().getElementCount(), newData2.getTable().getElementCount() + 1);
						if (newData3.getTable().getElementCount() == newData2.getTable().getElementCount()+1){
							int set = newData3.getColumnSet("Cidade").size();
							assertEquals("Cidade set must be 5!", set, 5);
							Cluster cluster = new Cluster(2);
							cluster.addAll(newData3.getColumnValues("Cidade"));
							preprocess = new Preprocess(newData3);
							preprocess.clusterize("Cidade", cluster, Preprocess.CLUSTER_HIGGER);
							set = newData3.getColumnSet("Cidade").size();
							assertEquals("Now Cidade set must be 5!", set, 4);
						}
					}
				}
			}
		}
	}
	
	@Test
	public void test3FN(){
		CSVReader reader = new CSVReader(fileName);
		reader.createSurrogateKeys(true);
		TableData data = reader.getData(';', '"');
		if (data != null && data.getRowCount() == 9){
			DataReplication dr = new DataReplication(data);
			Column columnEnderecos = data.getTable().getColumn("Enderecos");
			if (columnEnderecos != null){
				TableData newData = dr.splitColumn(columnEnderecos, new char[]{'\n'});
				if (newData.getRowCount() == 12){
					Preprocess preprocess = new Preprocess(newData);
					TableData newData2 = preprocess.splitColumn("Endereco", "Endereco", "Cidade", ",");
					if (newData2.getTable().getElementCount() == newData.getTable().getElementCount() + 1){
						preprocess = new Preprocess(newData2);
						TableData newData3 = preprocess.splitColumn("Cidade", "Cidade", "UF", "-");
						if (newData3.getTable().getElementCount() == newData2.getTable().getElementCount()+1){
							int set = newData3.getColumnSet("Cidade").size();
							if (set == 5){
								Cluster cluster = new Cluster(2);
								cluster.addAll(newData3.getColumnValues("Cidade"));
								preprocess = new Preprocess(newData3);
								preprocess.clusterize("Cidade", cluster, Preprocess.CLUSTER_HIGGER);
								set = newData3.getColumnSet("Cidade").size();
								if (set == 4){
									NormUtil n = new NormUtil(newData3);
									n.checkNormalForm();
									assertEquals("Normal form must be Second Normal Form!", newData3.getNormalForm(), NormalForm.NF2);
									DataDivision dd = new DataDivision(newData3);
									FDMapper mapper = new FDMapper(newData3);
									mapper.setMaxLevel(1);
									List<FD> fds = mapper.getFDs();
									assertTrue("FDs size must be > 0", fds.size() > 0);
									if (fds.size() > 0){
										DataList dl = dd.splitDependences(fds.get(0), "Funcionario");
										assertEquals("Data List must be 2 tables!", dl.size(),2);
										if (dl.size() == 2){
											assertEquals("First table must be 5 columns!", 5, dl.get(0).getTable().getElementCount());
											assertEquals("Second table must be 8 columns!", 6, dl.get(1).getTable().getElementCount());
											mapper = new FDMapper(dl.get(0));
											fds = mapper.getFDs();
											if (fds.size() > 0){
												dd = new DataDivision(dl.get(0));
												DataList dl2 = dd.splitDependences(fds.get(0), "Localidade");
												if (dl2.size() == 2){
													n = new NormUtil(dl2.get(1));
													n.checkNormalForm();
													assertEquals("Normal form must be Third Normal Form!", dl2.get(1).getNormalForm(), NormalForm.NF3);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	
}
