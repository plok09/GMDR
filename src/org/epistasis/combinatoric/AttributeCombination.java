package org.epistasis.combinatoric;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.epistasis.AttributeLabels;

public class AttributeCombination extends AbstractList implements Comparable {
	private List attributes;
	private AttributeLabels labels;

	public AttributeCombination() {
	}

	public AttributeCombination(int[] attributes) {
		setAttributes(attributes);
	}

	public AttributeCombination(int[] attributes, AttributeLabels labels) {
		this.labels = labels;
		setAttributes(attributes);
	}

	public AttributeCombination(String comboString) {
		setComboString(comboString);
	}

	public AttributeCombination(String comboString, AttributeLabels labels) {
		this.labels = labels;
		setComboString(comboString);
	}

	public void setAttributes(int[] attributes) {
		if (attributes == null) {
			this.attributes = null;
			return;
		}

		Set s = new TreeSet();

		for (int i = 0; i < attributes.length; ++i) {
			s.add(new Integer(attributes[i]));
		}

		convertSetToArray(s);
	}

	public void setLabels(AttributeLabels labels) {
		this.labels = labels;
	}

	public String getComboString() {
		StringBuffer b = new StringBuffer();

		for (int i = 0; i < size(); ++i) {
			if (i != 0) {
				b.append(',');
			}

			int attribute = ((Integer) get(i)).intValue();

			if (labels == null || attribute >= labels.size()) {
				b.append(attribute + 1);
			} else {
				b.append(labels.get(attribute));
			}
		}

		return b.toString();
	}

	public static int countAttributesInString(String attributes) {
		Pattern p = Pattern.compile("^\\s*\\[\\s+(.+?)\\s+]\\s*$");
		Matcher m = p.matcher(attributes);

		if (!m.matches()) {
			return -1;
		}

		return m.group(1).split("\\s+").length;
	}

	public void setComboString(String comboString) {
		if (comboString.trim().length() == 0) {
			attributes = null;
			return;
		}

		String[] fields = comboString.split(",");
		Set s = new TreeSet();

		for (int i = 0; i < fields.length; ++i) {
			fields[i] = fields[i].trim();
			Integer index = labels == null ? null : (Integer) labels.get(fields[i]);

			if (index == null) {
				try {
					index = Integer.valueOf(fields[i]);
					if (index.intValue() < 1) {
						throw new IllegalArgumentException(index.toString() + " is less than 1.");
					}
					index = new Integer(index.intValue() - 1);
				} catch (Exception ex) {
					throw new IllegalArgumentException("'" + fields[i] + "' is not a recognized attribute label.");
				}
			}

			s.add(index);
		}

		convertSetToArray(s);
	}

	/**
	 * Comparison function so that this object can serve as a map key.
	 * 
	 * @param o
	 *            Object to which compare this AttributeCombination
	 * @return < 0 if this object is less than the parameter, 0 if they are
	 *         equal, > 0 if this object is greater than the parameter
	 */
	public int compareTo(Object o) {
		AttributeCombination k = (AttributeCombination) o;

		// an object is always equal to itself
		if (this == k) {
			return 0;
		}

		// sort based on length
		if (size() < k.size()) {
			return -1;
		} else if (size() > k.size()) {
			return 1;
		}

		// same length, so sort lexicographically on the attributes
		// themselves
		else {
			for (int i = 0; i < size(); ++i) {
				int attribute = ((Integer) get(i)).intValue();
				int kattribute = ((Integer) k.get(i)).intValue();

				if (attribute < kattribute) {
					return -1;
				} else if (attribute > kattribute) {
					return 1;
				}
			}
		}

		// no differences, so they're equal
		return 0;
	}

	/**
	 * Convert an AttributeCombination to a String representation.
	 * 
	 * @return String representation of the AttributeCombination
	 */
	public String toString() {
		// use StringBuffer to build the string, to avoid needless
		// String reallocations
		StringBuffer b = new StringBuffer();

		// opening bracket
		b.append("[ ");

		// add each element, delimited by whitespace
		for (int i = 0; i < size(); ++i) {
			int attribute = ((Integer) get(i)).intValue();
			// next element
			if (labels != null && labels.size() > attribute) {
				b.append(labels.get(attribute));
			} else {
				b.append(attribute + 1);
			}

			// whitespace
			b.append(' ');
		}

		// closing bracket
		b.append(']');

		// return constructed String
		return b.toString();
	}

	/**
	 * Convert a Set of Integers to an array of ints and assign that array to
	 * the attribute combination.
	 * 
	 * @param s
	 *            Set
	 */
	private void convertSetToArray(Set s) {
		// if the set is empty, clear the forced attribute combination
		if (s.size() == 0) {
			attributes = null;
			return;
		}

		// allocate an array for the the AttributeCombination
		attributes = new ArrayList(s.size());

		// copy each value from the set to the array

		for (Iterator i = s.iterator(); i.hasNext();) {
			attributes.add(i.next());
		}
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		try {
			AttributeCombination a = (AttributeCombination) o;
			return super.equals(a);
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean trimToMax(int max) {
		boolean trimmed = false;

		for (ListIterator i = attributes.listIterator(); i.hasNext();) {
			Integer attribute = (Integer) i.next();

			if (attribute.intValue() >= max) {
				i.remove();
				trimmed = true;
			}
		}

		return trimmed;
	}

	public int size() {
		return attributes == null ? 0 : attributes.size();
	}

	public Object get(int index) {
		return attributes == null ? null : attributes.get(index);
	}
}
