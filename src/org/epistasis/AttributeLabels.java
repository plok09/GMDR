package org.epistasis;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AttributeLabels extends AbstractList {
	private List labels;
	private String classLabel;
	private Map labelMap = new HashMap();

	public AttributeLabels() {
	}

	public AttributeLabels(String[] labels) {
		this(labels, false);
	}

	public AttributeLabels(String[] labels, String classLabel) {
		this(labels, false);
		this.classLabel = classLabel;
	}

	public AttributeLabels(String[] labels, boolean removeClass) {
		labelMap.clear();

		if (labels == null) {
			this.labels = null;
		} else {
			this.labels = Arrays.asList(labels);

			if (removeClass) {
				List newLabels = new ArrayList();
				classLabel = labels[labels.length - 1];
				newLabels.addAll(this.labels.subList(0, labels.length - 1));
				this.labels = newLabels;
			}
		}

		remap();
	}

	
	public boolean contains(Object o) {
		return labelMap.containsKey(o);
	}

	public boolean containsAll(Collection c) {
		for (Iterator i = c.iterator(); i.hasNext();) {
			if (!contains(i.next())) {
				return false;
			}
		}

		return true;
	}

	public void add(int idx, Object label) {
		labels.add(idx, label);
		remap();
	}

	public Object remove(int idx) {
		Object label = labels.remove(idx);
		remap();
		return label;
	}

	public int size() {
		return labels == null ? 0 : labels.size();
	}

	public Object get(int index) {
		return labels == null ? null : labels.get(index);
	}

	public Object get(String label) {
		return labelMap == null ? null : labelMap.get(label);
	}

	public String toString() {
		StringBuffer b = new StringBuffer();

		for (Iterator i = iterator(); i.hasNext();) {
			b.append(i.next());

			if (i.hasNext()) {
				b.append('\t');
			}
		}

		if (classLabel != null) {
			b.append('\t');
			b.append(classLabel);
		}

		return b.toString();
	}

	public String getClassLabel() {
		return classLabel;
	}

	private void remap() {
		labelMap.clear();

		for (int i = 0; labels != null && i < labels.size(); ++i) {
			Object label = labels.get(i);
			if (labelMap.containsKey(label)) {
				throw new IllegalArgumentException("Duplicate label '" + label + "' found.");
			}
			labelMap.put(label, new Integer(i));
		}
	}
}
