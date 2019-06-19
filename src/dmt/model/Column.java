package dmt.model;

/**
 * Classe que representa uma coluna de uma tabela
 * @author Roger
 */
public class Column extends Element implements Cloneable{

	private static final long serialVersionUID = 6938768670487150816L;

	private Table table;
	private String description;
	private Class<?> type;
	private String dbType;
	private boolean primaryKey;
	private boolean surrogateKey;
	private boolean notNull;
	private boolean unique = false;
	private Column foreignKey;
	private Integer size;

	public Column() {
		super();
	}
	public Column(String name) {
		super(name);
	}
	public Table getTable() {
		return table;
	}
	public void setTable(Table table) {
		this.table = table;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Class<?> getType() {
		return type;
	}
	public void setType(Class<?> type) {
		this.type = type;
	}
	public String getDbType() {
		return dbType;
	}
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
	public boolean isPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
		if (primaryKey){
			this.notNull = true;
			this.unique = true;
			if (table != null)
				table.havePrimaryKeys(true);
		}
	}
	public boolean isSurrogateKey() {
		return surrogateKey;
	}
	public void setSurrogateKey(boolean surrogateKey) {
		this.surrogateKey = surrogateKey;
		if (surrogateKey){
			this.primaryKey = true;
			this.notNull = true;
			this.unique = true;
		}
	}
	public boolean isNotNull() {
		return notNull;
	}
	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}
	public boolean isUnique() {
		return unique;
	}
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	public Column getForeignKey() {
		return foreignKey;
	}
	public void setForeignKey(Column foreignKey) {
		this.foreignKey = foreignKey;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}

	@Override
	public Column clone(){
		Column column = new Column(getName());
		column.setPrimaryKey(isPrimaryKey());
		column.setSurrogateKey(isSurrogateKey());
		column.setForeignKey(getForeignKey());
		column.setNotNull(isNotNull());
		column.setUnique(isUnique());
		column.setSize(getSize());
		column.setType(getType());
		return column;
	}

}
