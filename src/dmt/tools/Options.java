package dmt.tools;

import dmt.tools.Util;

public class Options {

	private static boolean optionsLoaded = false;
	private static String skPrefix = null;
	private static String skSufix = "id";
	private static String valuePrefix = null;
	private static String valueSufix = "value";

	public static void loadOptions(){
		//Ler de arquivo
		System.out.println("Options loaded!");
		optionsLoaded = true;
	}

	public static String getKeyName(String tableName){
		if (!optionsLoaded)
			loadOptions();
		StringBuilder builder = new StringBuilder();
		if (skPrefix != null){
			builder.append(Util.isUpperCased(tableName)?skPrefix.toUpperCase():skPrefix);
			builder.append("_");
		}
		builder.append(tableName);
		if (skSufix != null){
			builder.append("_");
			builder.append(Util.isUpperCased(tableName)?skSufix.toUpperCase():skSufix);
		}
		return builder.toString();
	}

	public static String getValueName(String tableName){
		if (!optionsLoaded)
			loadOptions();
		StringBuilder builder = new StringBuilder();
		if (valuePrefix != null){
			builder.append(Util.isUpperCased(Util.toSingular(tableName))?valuePrefix.toUpperCase():valuePrefix);
			builder.append("_");
		}
		builder.append(Util.toSingular(tableName));
		if (valueSufix != null){
			builder.append("_");
			builder.append(Util.isUpperCased(Util.toSingular(tableName))?valueSufix.toUpperCase():valueSufix);
		}
		return builder.toString();
	}

	public static int getPageSize(){
		return 20;
	}

	public static int getShortStringLimit(){
		return 2;
	}

	@Deprecated
	public static boolean paralelize(){
		return true;
	}

	public static boolean testMode(){
		return false;
	}
	
	public static char[] getDefaultSeparators(){
		return new char[]{'\n',';'};
	}
	
	public static int getDefaultSampleSize(){
		return 5000;
	}
	
	public static double maxEmptyRate(){
		return 0.75;
	}

}
