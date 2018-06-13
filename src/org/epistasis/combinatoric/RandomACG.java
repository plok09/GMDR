package org.epistasis.combinatoric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.epistasis.AttributeLabels;

public abstract class RandomACG implements Iterator {
	private Random rand;
	private List attributes;
	private AttributeLabels labels;
	private int attrCount;

	public RandomACG(AttributeLabels labels, int attrCount, Random rand) {
		this.labels = labels;
		this.rand = rand;
		this.attrCount = attrCount;

		attributes = new ArrayList(labels.size());

		for (int i = 0; i < labels.size(); ++i) {
			attributes.add(new Integer(i));
		}
	}

	public synchronized Object next() {
		int[] attr = new int[attrCount];

		Collections.shuffle(attributes, rand);

		for (int i = 0; i < attr.length; ++i) {
			attr[i] = ((Integer) attributes.get(i)).intValue();
		}

		return new AttributeCombination(attr, labels);
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
