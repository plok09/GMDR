package org.epistasis;

public class StringObjectPair {
	private String s;
	private Object o;

	public StringObjectPair(String s, Object o) {
		this.s = s;
		this.o = o;
	}

	public Object getObject() {
		return o;
	}

	public String toString() {
		return s;
	}
}
