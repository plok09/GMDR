package org.epistasis;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

public class NominalAttributeIndex extends AbstractMap {
	private List list;
	private Map map;

	public NominalAttributeIndex(List attribute) {
		map = new TreeMap();

		for (Iterator i = attribute.iterator(); i.hasNext();) {
			Object value = i.next();

			if (!map.containsKey(value)) {
				map.put(value, null);
			}
		}

		int index = 0;
		list = new ArrayList(map.size());

		for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			entry.setValue(new Integer(index++));
			list.add(entry.getKey());
		}

		this.map = Collections.unmodifiableMap(map);
		this.list = Collections.unmodifiableList(list);
	}

	public Set entrySet() {
		return map.entrySet();
	}

	public int getIndex(Object value) {
		Integer index = (Integer) map.get(value);

		if (index == null) {
			throw new NoSuchElementException(value.toString());
		}

		return index.intValue();
	}

	public List getValues() {
		return list;
	}

	public Object getValue(int index) {
		return list.get(index);
	}
}
