package dmt.normalization;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;

import dmt.tools.Util;

public class TypeMatch {

	public static final char[] ND_CHARS = {'(', ')', ' '};

	public static boolean haveLetters(String string){
		char[] ca = string.toCharArray();
		for (char c : ca) {
			if (Character.isLetter(c))
				return true;
			for (int i = 0; i < ND_CHARS.length; i++) {
				if (Character.compare(c, ND_CHARS[i]) == 0)
					return true;
			}
		}
		return false;
	}

	public static boolean numOnly(String string){
		char[] ca = string.toCharArray();
		for (char c : ca) {
			if (!Character.isDigit(c))
				return false;
		}
		return true;
	}

	public static boolean isBoolean(HashSet<Object> values){
		for (Object value : values) {
			if (value == null || value.toString().isEmpty())
				continue;
			char ca[] = value.toString().toCharArray();
			for (char c : ca) {
				if (!Character.isDigit(c))
					return false;
			}
			int i = Integer.valueOf(value.toString()).intValue();
			if (i != 0 && i != 1)
				return false;
		}
		return true;
	}

	public static boolean onlyDecimals(Class<?>[] types){
		for (Class<?> t : types) {
			if (!t.equals(Integer.class)&&
				!t.equals(Long.class)&&
				!t.equals(Float.class)&&
				!t.equals(Double.class))
				return false;
		}
		return true;
	}

	public static Class<?> getHigherDecimal(Class<?>[] types){
		boolean haveDouble = false;
		boolean haveFloat = false;
		boolean haveLong = false;
		for (Class<?> t : types) {
			if (t.equals(Double.class))
				haveDouble = true;
			else if (t.equals(Float.class))
				haveFloat = true;
			else if (t.equals(Long.class))
				haveLong = true;
		}
		if (haveDouble)
			return Double.class;
		else if (haveFloat)
			return Float.class;
		else if (haveLong)
			return Long.class;
		else
			return Integer.class;
	}

	public static Class<?> bestMatch(HashSet<Object> values){
		if (values.size() > 0){
			HashSet<Class<?>> types = new HashSet<>();
			for (Object value : values) {
				if (value == null)
					continue;
				String string = value.toString();
				if (haveLetters(string)){
					types.add(String.class);
				}else if (numOnly(string)){
					if (string.length() < 10){
						types.add(Integer.class);
					}else{
						long l = Long.valueOf(string);
						if (l > Integer.MAX_VALUE)
							types.add(Long.class);
						else
							types.add(Integer.class);
					}
				}else{
					if (string.length() > 1){
						string = string.replace(',', '.');
						int commas = Util.countChar('.', string);
						int ds = Math.max(Util.countChar('/', string),Util.countChar('-', string.substring(1,string.length()-2)));
						int hs = Util.countChar(':', string);
						if (commas == 1 && ds == 0 && hs == 0){
							double d = Double.valueOf(string);
							if (d > Float.MAX_VALUE)
								types.add(Double.class);
							else
								types.add(Float.class);
						}else if (ds == 2 && (hs == 1 || hs == 2) && (commas == 0 || commas == 1)){
							types.add(LocalDateTime.class);
						}else if (ds == 2 &&  hs == 0 && commas == 0){
							types.add(LocalDate.class);
						}else if (ds == 0 && (hs == 1 || hs == 2) && (commas == 0 || commas == 1)){
							types.add(LocalTime.class);
						}
					}
				}
			}
			if (types.contains(String.class))
				return String.class;
			Class<?>[] c = types.toArray(new Class<?>[types.size()]);
			if (c.length == 1){
				if (c[0].equals(Integer.class) && isBoolean(values))
					return Boolean.class;
				return c[0];
			}else{
				if (onlyDecimals(c))
					return getHigherDecimal(c);
			}
		}
		return Object.class;
	}

	public static Class<?> bestMatch(List<Object> values){
		return bestMatch(new HashSet<>(values));
	}

	private static final String DATE_PATTERN_BR = "dd/MM/yyyy";
	private static final String DATE_PATTERN_US = "yyyy-MM-dd";
	private static final String TIME_PATTERN_1 = "HH:mm";
	private static final String TIME_PATTERN_2 = "HH:mm:ss";
	private static final String TIME_PATTERN_3 = "HH:mm:ss.SSS";

	private static String getPattern(String value, Class<?> c){
		if (c.equals(LocalDate.class)){
			int sep = Util.countChar('/', value);
			if (sep == 2)
				return DATE_PATTERN_BR;
			sep = Util.countChar('-', value);
			if (sep == 2)
				return DATE_PATTERN_US;
		}else if (c.equals(LocalTime.class)){
			if (value.length() == TIME_PATTERN_1.length())
				return TIME_PATTERN_1;
			else if (value.length() == TIME_PATTERN_2.length())
				return TIME_PATTERN_2;
			else if (value.length() == TIME_PATTERN_3.length())
				return TIME_PATTERN_3;
		}else if (c.equals(LocalDateTime.class)){
			int sep = Util.countChar('/', value);
			if (sep == 2){
				if (value.length()-DATE_PATTERN_BR.length()-1 == TIME_PATTERN_1.length())
					return String.format("%s %s", DATE_PATTERN_BR, TIME_PATTERN_1);
				else if (value.length()-DATE_PATTERN_BR.length()-1 == TIME_PATTERN_2.length())
					return String.format("%s %s", DATE_PATTERN_BR, TIME_PATTERN_2);
				else if (value.length()-DATE_PATTERN_BR.length()-1 >= TIME_PATTERN_3.length())
					return String.format("%s %s", DATE_PATTERN_BR, TIME_PATTERN_3);
			}
			sep = Util.countChar('-', value);
			if (sep == 2){
				if (value.length()-DATE_PATTERN_US.length()-1 == TIME_PATTERN_1.length())
					return String.format("%s %s", DATE_PATTERN_US, TIME_PATTERN_1);
				else if (value.length()-DATE_PATTERN_US.length()-1 == TIME_PATTERN_2.length())
					return String.format("%s %s", DATE_PATTERN_US, TIME_PATTERN_2);
				else if (value.length()-DATE_PATTERN_US.length()-1 >= TIME_PATTERN_3.length())
					return String.format("%s %s", DATE_PATTERN_US, TIME_PATTERN_3);
			}
		}else{
			throw new RuntimeException("Cannot find date/time pattern!");
		}
		return null;
	}

	public static Object convert(Object value, Class<?> c){
		if (value.toString().trim().isEmpty())
			return value;
		if (c.equals(Integer.class)){
			return Integer.valueOf(value.toString());
		}else if (c.equals(Long.class)){
			return Long.valueOf(value.toString());
		}else if (c.equals(Float.class)){
			return Float.valueOf(value.toString().replace(',', '.'));
		}else if (c.equals(Double.class)){
			return Double.valueOf(value.toString().replace(',', '.'));
		}else if (c.equals(Boolean.class)){
			if (value.getClass().equals(String.class)){
				String s = value.toString();
				if (s.length() == 1 && s.compareTo("1") == 0)
					return new Boolean(true);
				else if(s.length() == 1 && s.compareTo("0") == 0)
					return new Boolean(false);
				else if(s.toLowerCase().compareTo("true") == 0)
					return new Boolean(true);
				else if(s.toLowerCase().compareTo("false") == 0)
					return new Boolean(false);
				else{
					System.err.println(value.toString());
					throw new RuntimeException("String cannot be converted to java.lang.Boolean");
				}
			}else{
				return Boolean.valueOf(value.toString());
			}
		}else if (c.equals(LocalDate.class)){
			String pattern = getPattern(value.toString(), LocalDate.class);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
			return LocalDate.parse(value.toString(), dtf);
		}else if (c.equals(LocalTime.class)){
			String pattern = getPattern(value.toString(), LocalTime.class);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
			return LocalTime.parse(value.toString(), dtf);
		}else if (c.equals(LocalDateTime.class)){
			String pattern = getPattern(value.toString(), LocalDateTime.class);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
			return LocalDateTime.parse(value.toString(), dtf);
		}else if(c.equals(String.class)){
			return value.toString();
		}
		return null;
	}

	public static void main(String[] args) {
		HashSet<Object> values = new HashSet<>();
		//values.add("0");
		//values.add("1");
		//values.add("300000000000132132132131321321132132132132100.00132100000313131");
		values.add("-23,13263268");
		//values.add("2019-01-01 01:00:00");
		System.out.println(bestMatch(values));

		//Boolean b = (Boolean)convert("false", Boolean.class);
		//System.out.println(b);

		System.out.println(convert("22:04:52", LocalTime.class));
		//o

	}

}
