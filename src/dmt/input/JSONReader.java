package dmt.input;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import dmt.model.Column;
import dmt.model.Table;
import dmt.model.data.RowData;
import dmt.model.data.TableData;
import dmt.tools.Util;

/**
 * Classe utilizada para retornar os dados de um JSON
 * @author Roger
 */
public class JSONReader {

	private String content;
	private boolean firstAsModel = true;
	private boolean createSurrogateKeys = false;
	private boolean xml = false;
	
	public JSONReader(String filePath) {
		try {
			if (Util.getExtension(filePath).toLowerCase().compareTo("xml") == 0)
				xml = true;
			content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isFirstAsModel() {
		return firstAsModel;
	}

	public void setFirstAsModel(boolean firstAsModel) {
		this.firstAsModel = firstAsModel;
	}

	public boolean creatingSurrogateKeys() {
		return createSurrogateKeys;
	}

	public void createSurrogateKeys(boolean createSurrogateKeys) {
		this.createSurrogateKeys = createSurrogateKeys;
	}

	public String getContent() {
		if (!xml)
			return content;
		else
			return getStart(content).toString();
	}
	
	private JSONObject getJsonObject(){
		if (!xml)
			return new JSONObject(content);
		else
			return getStart(content);
	}
	/**
	 * Le um arquivo em JSon e recupera suas informacoes
	 * @param parentTable: utilizada para recursao
	 * @param jsonObj: utilziada para recursao
	 * @param useFirst: indica que o primeiro registro define a tabela, caso contrario busca o que tiver mais colunas
	 * @return Tabela resultante
	 */
	private Table readTable(Table parentTable, JSONObject jsonObj){
		if (jsonObj == null)
			jsonObj = getJsonObject();
		Table table = new Table();
		Iterator<String> iterator = jsonObj.keys();
		while (iterator.hasNext()){
			String key = iterator.next();
			Object obj = jsonObj.get(key);
			if (obj.getClass().equals(JSONArray.class)){
				table.setName(Util.toSingular(key));
				if (createSurrogateKeys)
					table.createSurrogateKey();
				JSONArray jsonArray = (JSONArray)obj;
				if (firstAsModel){
					if (jsonArray.length() > 0 && jsonArray.get(0).getClass().equals(JSONObject.class)){
						JSONObject subObj = jsonArray.getJSONObject(0);
						Table subTable = readTable(table, subObj);
						if (subTable.getName() != null)
							table.addTable(subTable);
					}
				}else{
					int maxColumns = 0;
					Table addTable = null;
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject subObj = jsonArray.getJSONObject(i);
						Table subTable = readTable(table, subObj);
						if (subTable.getName() != null && subTable.getElementCount() > maxColumns){
							maxColumns = subTable.getElementCount();
							addTable = subTable.clone();
						}
					}
					if (addTable != null)
						table.addTable(addTable);
				}
			}else{
				if (parentTable != null){
					Column column = new Column(key);
					column.setType(obj.getClass());
					parentTable.addColumn(column);
				}
			}
		}
		return table;
	}
	
	public Table readTable(){
		return readTable(null, null);
	}
	
	public TableData readData(){
		Table table = readTable(null, null);
		if (table.haveNestedTables())
			System.err.println(String.format("Warning: Table %s have nested tables!", table.getName()));
		TableData data = new TableData(table);
		JSONObject jsonObj = getJsonObject();
		JSONArray jsonArray = null;
		if (jsonObj.has(table.getName()))
			jsonArray = jsonObj.getJSONArray(table.getName());
		else if (jsonObj.has(table.getName().concat("s")))	
			jsonArray = jsonObj.getJSONArray(table.getName().concat("s"));
		if (jsonArray != null){
			for (int i = 0; i < jsonArray.length(); i++) {
				RowData row = new RowData(table);
				JSONObject jObj = jsonArray.getJSONObject(i);
				for (Column column : table.getColumns(false)) {
					Object value = jObj.get(column.getName());
					row.setValue(column.getName(), value);
				}
				data.addRow(row);
			}
		}
		return data;
	}
	
	private static JSONObject getStart(String xmlContent){
		JSONObject jsonObj = XML.toJSONObject(xmlContent);
		Optional<String> key = jsonObj.keySet().stream().findFirst();
		if (key.isPresent()){
			Object object = jsonObj.get(key.get());
			if (object.getClass().equals(JSONArray.class))
				return jsonObj;
			else
				return jsonObj.getJSONObject(key.get());
		}
		return null;
	}
	
	public static void main(String[] args) {
		try {
			//String jsonFilePath = "data/test/pessoasJuridicas.json";
			String xmlFilePath = "data/test/pessoasJuridicas.xml";
			//String jsonContent = new String ( Files.readAllBytes( Paths.get(jsonFilePath) ) );
			String xmlContent = new String ( Files.readAllBytes( Paths.get(xmlFilePath) ) );
			//JSONObject jsonObject = new JSONObject(jsonContent);
			JSONObject xmlObject = getStart(xmlContent);
			System.out.println(xmlObject);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
