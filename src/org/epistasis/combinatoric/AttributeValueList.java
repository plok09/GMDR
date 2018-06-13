package org.epistasis.combinatoric;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

public class AttributeValueList extends AbstractList implements Comparable, Cloneable {
	private Stack values = new Stack();

	public int size() {
		return values.size();
	}

	public Object get(int index) {
		return values.get(index);
	}

	public void push(Comparable c) {
		values.push(c);
	}

	public Comparable pop() {
		return (Comparable) values.pop();
	}

	public void set(Collection c) {
		values.clear();
		values.addAll(c);
	}

	public void clear() {
		values.clear();
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o == null) {
			return false;
		}

		AttributeValueList g = (AttributeValueList) o;

		return values.equals(g.values);
	}

	public int compareTo(Object o) {
		AttributeValueList g = (AttributeValueList) o;

		if (size() < g.size()) {
			return -1;
		}

		if (size() > g.size()) {
			return 1;
		}

		for (Iterator i = iterator(), j = g.iterator(); i.hasNext();) {
			Comparable c1 = (Comparable) i.next();
			Comparable c2 = (Comparable) j.next();

			int result = c1.compareTo(c2);

			if (result != 0) {
				return result;
			}
		}

		return 0;
	}

	public String toString() {
		StringBuffer b = new StringBuffer();

		for (Iterator i = values.iterator(); i.hasNext();) {
			if (b.length() > 0) {
				b.append(',');
			}

			b.append(i.next());
		}

		return b.toString();
	}

	public Object clone() {
		try {
			AttributeValueList a = (AttributeValueList) super.clone();

			a.values = (Stack) values.clone();

			return a;
		} catch (CloneNotSupportedException ex) {
			return null;
		}
	}
}
