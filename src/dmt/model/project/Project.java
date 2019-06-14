package dmt.model.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import dmt.model.Column;
import dmt.model.Table;
import dmt.model.data.TableData;

public class Project {

	private File file = null;

	private DataList dataList = new DataList();

	public Project() {
		super();
	}

	public Project(File file) {
		super();
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object obj = ois.readObject();
			if (obj.getClass().equals(DataList.class)){
				ois.close();
				dataList = (DataList)obj;
			}
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public void setDataList(DataList dataList){
		this.dataList = dataList;
	}

	public DataList getDataList(){
		return dataList;
	}

	public boolean saved(){
		return file != null;
	}

	public void save(){
		if (file == null)
			throw new RuntimeException("Project.save: File not set!");
		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(dataList);
			oos.close();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public void saveAs(File file){
		this.file = file;
		save();
	}

	private boolean haveTable(String tableName){
		Optional<TableData> op = dataList.stream().filter(d->d.getTable().getName().compareTo(tableName)==0).findAny();
		return op.isPresent();
	}

	/**
	 * Remake d1 references to d2 if exists
	 * @param d1 first data
	 * @param d2 second data
	 */
	public void remakeFks(TableData d1, TableData d2){
		Table t2 = d2.getTable();
		List<Column> c1 = d1.getTable().getColumns();
		c1.forEach(c->{
			if (c.getForeignKey() != null){
				String columnName = c.getForeignKey().getName();
				Column column = t2.getColumnIFExists(columnName);
				if (column != null){
					c.setForeignKey(column);
				}
			}
		});
	}

	public void checkFks(){
		dataList.forEach(d->{
			List<TableData> oList = dataList
					.stream().
					filter(od->od.getTable().getName().compareTo(d.getTable().getName()) != 0)
					.collect(Collectors.toList());
			oList.forEach(od->{
				remakeFks(d, od);
			});
		});
	}

	//replace if table have same name
	public void addOrReplace(DataList dataList){
		dataList.forEach(td->{
			if (haveTable(td.getTable().getName())){
				this.dataList.replace(td);
			}else{
				this.dataList.add(td);
			}
		});
		checkFks();
	}
	
	public void addOrReplace(TableData data){
		if (dataList.haveTable(data.getTable()))
			dataList.replace(data);
		else
			dataList.add(data);
		checkFks();
	}

	public TableData get(String tableName){
		Optional<TableData> op = dataList.stream().filter(d->d.getTable().getName().compareTo(tableName)==0).findFirst();
		if (op.isPresent())
			return op.get();
		return null;
	}
	
}
