package dmt.model;

import java.io.Serializable;

/**
 * Classe base para os tipos Table e Column
 * @author Roger
 */
public abstract class Element implements Serializable {

	private static final long serialVersionUID = 7839635371463600807L;

	private String name;

	public Element() {
		super();
	}
	public Element(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNameWidth(){
		char[] ca = name.toCharArray();
		int width = 0;
		for (char c : ca) {
			width += Character.isUpperCase(c) ? 7 : 5;
		}
		return width;
	}
		
}
