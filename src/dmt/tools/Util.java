package dmt.tools;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

public class Util {

	public static void boldFont(Control control){
		Font font = control.getFont();
		FontData[] fd = font.getFontData();
		fd[0].setStyle(SWT.BOLD);
		control.setFont(new Font(Display.getDefault(),fd));
	}

	public static void boldFont(TableItem item, int index){
		Font font = item.getFont(index);
		FontData[] fd = font.getFontData();
		fd[0].setStyle(SWT.BOLD);
		item.setFont(index, new Font(Display.getDefault(),fd));
	}
	
	/**
	 * Método que posiciona um Shell no centro do parent (se houver)
	 * @param shell
	 * @param parent
	 */
	public static synchronized void centralize(Shell shell,Composite parent) {
		if (parent != null){
			int x = parent.getLocation().x + ((parent.getSize().x- shell.getSize().x)/2);
			int y = parent.getLocation().y + ((parent.getSize().y- shell.getSize().y)/2);
			shell.setLocation(new Point(x,y));
		}else{
			Rectangle r = shell.getDisplay().getClientArea();
			int x = (r.width- shell.getSize().x)/2;
			int y = (r.height- shell.getSize().y)/2;
			shell.setLocation(new Point(x,y));
		}
	}

	public static synchronized void centralize(Shell shell) {
		centralize(shell, null);
	}

	public static boolean isUpperCased(String text){
		char[] ca = text.toCharArray();
		boolean lower = false;
		for (char c : ca) {
			if (Character.isLetter(c)&&Character.isLowerCase(c)){
				lower = true;
				break;
			}
		}
		return !lower;
	}

	public static String toSingular(String text){
		if (text.length() > 1 && text.toUpperCase().charAt(text.length()-1)=='S')
			return text.substring(0, text.length()-1);
		return text;
	}

	public static int countChar(char c, String string){
		int count = 0;
		char[] ca = string.toCharArray();
		for (char ac : ca) {
			if (Character.compare(ac, c) == 0)
				count++;
		}
		return count;
	}

	public static int countSpaces(String line[]){
		int count = 0;
		for (String string : line) {
			if (string.trim().length() == 0)
				count++;
		}
		return count;
	}

	public static int levenshteinDistance(String str1, String str2) {
		return levenshteinDistanceCS(str1.toUpperCase(), str2.toUpperCase());
	}
	/**
	 * https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
	 */
	public static int levenshteinDistanceCS(CharSequence lhs, CharSequence rhs) {
		int len0 = lhs.length() + 1;
	    int len1 = rhs.length() + 1;

	    // the array of distances
	    int[] cost = new int[len0];
	    int[] newcost = new int[len0];

	    // initial cost of skipping prefix in String s0
	    for (int i = 0; i < len0; i++) cost[i] = i;

	    // dynamically computing the array of distances

	    // transformation cost for each letter in s1
	    for (int j = 1; j < len1; j++) {
	        // initial cost of skipping prefix in String s1
	        newcost[0] = j;

	        // transformation cost for each letter in s0
	        for(int i = 1; i < len0; i++) {
	            // matching current letters in both strings
	            int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

	            // computing cost for each transformation
	            int cost_replace = cost[i - 1] + match;
	            int cost_insert  = cost[i] + 1;
	            int cost_delete  = newcost[i - 1] + 1;

	            // keep minimum cost
	            newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
	        }

	        // swap cost/newcost arrays
	        int[] swap = cost; cost = newcost; newcost = swap;
	    }

	    // the distance is the cost for transforming all letters in both strings
	    return cost[len0 - 1];
	}

	public boolean isNumeric(Class<?> type){
		HashSet<Class<?>> set = new HashSet<>();
		set.add(Short.class);
		set.add(Integer.class);
		set.add(Long.class);
		set.add(Float.class);
		set.add(Double.class);
		set.add(Number.class);
		set.add(BigDecimal.class);
		return set.contains(type);
	}

	public static Object copyObject(Object object){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos;
				oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
			} catch (IOException | ClassNotFoundException e) {
			}
		return null;
	}

	/**
	 * https://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
	 */
	public static int countLines(String fileName) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(fileName));
	    try {
	        byte[] c = new byte[1024];
	        int readChars = is.read(c);
	        if (readChars == -1) {
	            // bail out if nothing to read
	            return 0;
	        }
	        // make it easy for the optimizer to tune this loop
	        int count = 0;
	        while (readChars == 1024) {
	            for (int i=0; i<1024;) {
	                if (c[i++] == '\n') {
	                    ++count;
	                }
	            }
	            readChars = is.read(c);
	        }
	        // count remaining characters
	        while (readChars != -1) {
	            for (int i=0; i<readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	            readChars = is.read(c);
	        }
	        return count == 0 ? 1 : count;
	    } finally {
	        is.close();
	    }
	}

	public static String getTableName(String fileName) {
		String[] s1 = fileName.replace('\\' , '.').split("[.]");
		if (s1.length < 2)
			return "";
		return s1[s1.length-2];
	}

	public static boolean contains(char[] ca, char c){
		for (char ac : ca) {
			if (ac == c)
				return true;
		}
		return false;
	}
	
	public static String concat(Set<String> set){
		StringBuilder builder = new StringBuilder();
		set.forEach(e->{
			builder.append(e);
		});
		return builder.toString();
	}
	
	public static String getExtension(String fileName){
		String[] s = fileName.split("[.]");
		if (s.length > 0)
			return s[s.length-1];
		return null;	
	}
	
	public static void main(String[] args) {
		//String t = "C:\\Users\\roger\\Dropbox\\ArquivosSI\\TCC\\data.normalization.tool\\data\\exemplo1.csv";
		//System.out.println(getTableName(t));
		System.out.println(levenshteinDistance("Cachorro", "Cavalo"));
	}

}
