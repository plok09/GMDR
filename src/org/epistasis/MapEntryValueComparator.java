package org.epistasis;

import java.util.Comparator;
import java.util.Map;

public class MapEntryValueComparator implements Comparator {
	private boolean ascending = true;

	public MapEntryValueComparator() {
	}

	public MapEntryValueComparator(boolean ascending) {
		this.ascending = ascending;
	}

	public int compare(Object o1, Object o2) {
		Comparable c1 = (Comparable) ((Map.Entry) o1).getValue();
		Comparable c2 = (Comparable) ((Map.Entry) o2).getValue();

		return (ascending ? 1 : -1) * c1.compareTo(c2);
	}
}
