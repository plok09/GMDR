package org.epistasis.combinatoric;

import java.util.Random;

import org.epistasis.AttributeLabels;

public class FixedRandomACG extends RandomACG {
	private long maxEval;
	private long nEval;

	public FixedRandomACG(AttributeLabels labels, int attrCount, Random rand, long maxEval) {
		super(labels, attrCount, rand);
		this.maxEval = maxEval;
		nEval = 0;
	}

	public boolean hasNext() {
		return nEval <= maxEval;
	}

	public Object next() {
		Object next = super.next();

		++nEval;

		return next;
	}
}
