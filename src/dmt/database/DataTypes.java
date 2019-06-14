package dmt.database;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Enumeracao utilizada pra mapear os tipos de dados do banco de dados para classes do java v.v
 * @author roger
 *
 */
public enum DataTypes {

	SERIAL("SERIAL", -1, Integer.class),
	INT2("INT2", -1, Integer.class),
	INT4("INT4", -1, Integer.class),
	INT8("INT8", -1, Long.class),
	INT("INT", -1, Integer.class),
	INTEGER("INTEGER", -1, Integer.class),
	BINGINT("BIGINT", -1, Long.class),
	NUMERIC("NUMERIC", -1, Number.class),
	FLOAT4("FLOAT4", -1, Float.class),
	FLOAT8("FLOAT8", -1, Double.class),
	FLOAT("FLOAT", -1, Float.class),
	DOUBLE("DOUBLE", -1, Double.class),
	BOOL ("BOOL", -1, Boolean.class),
	BOOLEAN ("BOOLEAN", -1, Boolean.class),
	TBOOL ("TINYINT", -1, Boolean.class),
	CHAR ("CHAR", 1, Character.class),
	BPCHAR ("BPCHAR", 1, Character.class),
	BPCHARs ("BPCHAR", -1, String.class),
	CHARS ("CHAR", -1, String.class),
	VARCHARS ("VARCHAR", 255, String.class),
	VARCHAR ("VARCHAR", -1, String.class),
	TEXT("TEXT", -1, String.class),
	TIMESTAMP ("TIMESTAMP", -1, LocalDateTime.class),
	DATETIME ("DATETIME", -1, LocalDateTime.class),
	TIMESTAMPTZ ("TIMESTAMPTZ", -1, LocalDateTime.class),
	TIME ("TIME", -1, LocalTime.class),
	TIMETZ ("TIMETZ", -1, LocalTime.class),
	DATE ("DATE", -1, LocalDate.class);

	private final String dbType;
	private final int dbTypeSize;
	private final Class<?> javaClass;

	private DataTypes(String dbType, int dbTypeSize, Class<?> javaClass) {
		this.dbType = dbType;
		this.dbTypeSize = dbTypeSize;
		this.javaClass = javaClass;
	}
	public String getDbType() {
		return dbType;
	}
	public int getDbTypeSyze() {
		return dbTypeSize;
	}
	public Class<?> getJavaClass() {
		return javaClass;
	}

	/**
	 * Retorna o tipo de classe utilizada no java conforme o tipo e o tamanho no banco
	 * @param dbType tipo de dado do banco
	 * @param dbTypeSyze tamanho do campo no banco, -1 para qualquer tamanho
	 * @return Classe do java
	 */
	public static Class<?> getJavaType(String dbType, int dbTypeSyze){
		for (DataTypes dt : DataTypes.values()) {
			if (dt.dbType.toUpperCase().compareTo(dbType.toUpperCase())==0&&(dt.dbTypeSize==dbTypeSyze||dt.dbTypeSize==-1))
				return dt.javaClass;
		}
		return Object.class;
	}

	/**
	 * Retorna o tipo de dados utilizado no banco conforme a classe do java
	 * @param javaClass classe
	 * @return String
	 */
	public static String getDbType(Class<?> javaClass){
		for (DataTypes dt : DataTypes.values()) {
			if (dt.getClass().equals(javaClass))
				return dt.getDbType();
		}
		return "VARCHAR";
	}

	/**
	 * Retonra o tamanho do tipo de dados conforme a classe do java
	 * @param javaClass classe
	 * @return int
	 */
	public static int getDbTypeSize(Class<?> javaClass){
		for (DataTypes dt : DataTypes.values()) {
			if (dt.getClass().equals(javaClass))
				return dt.getDbTypeSyze();
		}
		return -1;
	}
	
	public static List<Class<?>> getAllTypes(){
		List<Class<?>> types = new LinkedList<>();
		types.add(String.class);
		types.add(Integer.class);
		types.add(Long.class);
		types.add(Number.class);
		types.add(Float.class);
		types.add(Double.class);
		types.add(Boolean.class);
		types.add(Character.class);
		types.add(LocalDate.class);
		types.add(LocalTime.class);
		types.add(LocalDateTime.class);
		return types;
	}

}
