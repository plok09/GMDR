package org.epistasis;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ChiSquaredScorer extends IsolatedAttributeScorer {
	private List classes;
	private NominalAttributeIndex classIndex;
	private boolean pvalue;
	private Map config = new TreeMap();

	public ChiSquaredScorer(AbstractDataset data, boolean pvalue, boolean parallel) {
		super(data, parallel);
		init(pvalue);
	}

	public ChiSquaredScorer(AbstractDataset data, boolean pvalue, boolean parallel, Runnable onIncrementProgress) {
		super(data, parallel, onIncrementProgress);
		init(pvalue);
	}

	private void init(boolean pvalue) {
		this.pvalue = pvalue;
		config.put("FILTER", "CHISQUARED");
		config.put("PVALUE", Boolean.toString(pvalue));
		config = Collections.unmodifiableMap(config);
	}

	public Map getConfig() {
		return config;
	}

	protected double[] computeScores() {
		classes = getData().computeStatusVector();
		classIndex = new NominalAttributeIndex(classes);

		return super.computeScores();
	}

	protected double computeScore(int index) {
		double[][] table = new double[classIndex.size()][];
		List attribute = getData().computeAttributeVector(index);
		NominalAttributeIndex attributeIndex = new NominalAttributeIndex(attribute);

		for (int i = 0; i < table.length; ++i) {
			table[i] = new double[attributeIndex.size()];
		}

		for (int i = 0; i < getData().getNumInstances(); ++i) {
			table[classIndex.getIndex(classes.get(i))][attributeIndex.getIndex(attribute.get(i))]++;
		}

		double chisq = Utility.computeChiSquared(table);

		chisq = pvalue ? Utility.pchisq(chisq, (classIndex.size() - 1) * (attributeIndex.size() - 1)) : chisq;

		return chisq;
	}
}
