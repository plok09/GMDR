package org.epistasis;

import java.util.AbstractList;
import java.util.Map;

public abstract class AbstractAttributeScorer extends AbstractList {

	private Runnable onIncrementProgress;
	private AbstractDataset data;
	private double[] scores;

	protected abstract double[] computeScores();

	public abstract int getTotalProgress();

	public abstract Map getConfig();

	public AbstractAttributeScorer(AbstractDataset data) {
		this.data = data;
	}

	public AbstractAttributeScorer(AbstractDataset data, Runnable onIncrementProgress) {
		this.data = data;
		this.onIncrementProgress = onIncrementProgress;
	}

	protected void incrementProgress() {
		if (onIncrementProgress != null) {
			onIncrementProgress.run();
		}
	}

	public void compute() {
		if (scores != null) {
			return;
		}

		scores = computeScores();
	}

	public AbstractDataset getData() {
		return data;
	}

	public Object get(int index) {
		return new Double(scores[index]);
	}

	public double getScore(int index) {
		return scores[index];
	}

	public int size() {
		return scores == null ? 0 : scores.length;
	}
}
