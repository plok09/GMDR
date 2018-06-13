package org.epistasis;

public class IntObjectPair {
	private int i;
	private Object obj;

	public IntObjectPair(int i, Object obj) {
		this.i = i;
		this.obj = obj;
	}

	public int getInt() {
		return i;
	}

	public Object getObject() {
		return obj;
	}
}
