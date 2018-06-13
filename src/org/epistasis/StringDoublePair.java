package org.epistasis;

import java.util.Comparator;

public class StringDoublePair {
	private String s;
	private double d;

	public StringDoublePair(String s, double d) {
		this.s = s;
		this.d = d;
	}

	public double getDouble() {
		return d;
	}

	public String toString() {
		return s;
	}

	public static class StringComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			StringDoublePair p1 = (StringDoublePair) o1;
			StringDoublePair p2 = (StringDoublePair) o2;

			return p1.toString().compareTo(p2.toString());
		}
	}

	public static class DoubleComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			StringDoublePair p1 = (StringDoublePair) o1;
			StringDoublePair p2 = (StringDoublePair) o2;

			if (p1.getDouble() < p2.getDouble()) {
				return -1;
			} else if (p1.getDouble() > p2.getDouble()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
