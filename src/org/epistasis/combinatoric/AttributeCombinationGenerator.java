package org.epistasis.combinatoric;

import org.epistasis.AttributeLabels;

public class AttributeCombinationGenerator extends CombinationGenerator {
	private AttributeLabels labels = null;

	public AttributeCombinationGenerator() {
	}

	public AttributeCombinationGenerator(AttributeLabels labels) {
		this.labels = labels;
	}

	public AttributeCombinationGenerator(int nVars, int nCombo) {
		super(nVars, nCombo);
	}

	public AttributeCombinationGenerator(AttributeLabels labels, int nCombo) {
		super(labels.size(), nCombo);
		this.labels = labels;
	}

	public void setLabels(AttributeLabels labels) {
		this.labels = labels;
	}

	public Object next() {
		int[] combo = (int[]) super.next();

		if (combo == null) {
			return null;
		}

		return new AttributeCombination(combo, labels);
	}
}
