package org.epistasis.combinatoric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.epistasis.AbstractAttributeScorer;
import org.epistasis.AbstractDataset;
import org.epistasis.StringDoublePair;

public class AttributeRanker {

	private AbstractAttributeScorer scorer;
	private List attributes;
	private List scores;

	public AbstractAttributeScorer getScorer() {
		return scorer;
	}

	public List getSortedScores() {
		return scores;
	}

	public void setScorer(AbstractAttributeScorer scorer) {
		this.scorer = scorer;

		if (scorer == null) {
			attributes = null;
		} else {
			attributes = new ArrayList(scorer.getData().getNumAttributes());

			for (int i = 0; i < scorer.getData().getNumAttributes(); ++i) {
				attributes.add(new Integer(i));
			}
		}
	}

	public AbstractDataset getData() {
		return scorer == null ? null : scorer.getData();
	}

	public AttributeCombination selectThreshold(double threshold, boolean above, boolean closed) {
		if (scorer == null) {
			throw new IllegalStateException("No Scorer");
		}

		if (getData() == null) {
			throw new IllegalStateException("No Dataset");
		}

		rank(!above);

		List selected = new ArrayList();

		for (Iterator i = attributes.iterator(); i.hasNext();) {
			Integer attr = (Integer) i.next();
			int idx = attr.intValue();
			double value = scorer.getScore(idx);

			if (!((above && value > threshold) || ((!above) && value < threshold) || (closed && value == threshold))) {
				break;
			}

			selected.add(attr);
		}

		int[] attr = new int[selected.size()];

		for (int i = 0; i < attr.length; ++i) {
			attr[i] = ((Integer) selected.get(i)).intValue();
		}

		return new AttributeCombination(attr, getData().getLabels());
	}

	public AttributeCombination selectPct(double pct, boolean ascending) {
		return selectN((int) Math.floor(pct * (attributes == null ? 0 : attributes.size())), ascending);
	}

	public AttributeCombination selectN(int n, boolean ascending) {
		if (scorer == null) {
			throw new IllegalStateException("No Scorer");
		}

		if (getData() == null) {
			throw new IllegalStateException("No Dataset");
		}

		rank(ascending);

		int[] attr = new int[n];
		for (int i = 0; i < attr.length; ++i) {
			attr[i] = ((Integer) attributes.get(i)).intValue();
		}

		return new AttributeCombination(attr, getData().getLabels());
	}

	public List rank(boolean ascending) {
		scorer.compute();
		Collections.sort(attributes, new RankOrder(scorer, ascending));

		scores = new ArrayList(attributes.size());

		for (Iterator i = attributes.iterator(); i.hasNext();) {
			int attr = ((Integer) i.next()).intValue();
			scores.add(new StringDoublePair(getData().getLabels().get(attr).toString(), scorer.getScore(attr)));
		}

		return scores;
	}

	private static class RankOrder implements Comparator {
		private List scores;
		private boolean ascending;

		public RankOrder(List scores, boolean ascending) {
			this.scores = scores;
			this.ascending = ascending;
		}

		public int compare(Object o1, Object o2) {
			double a = ((Double) scores.get(((Integer) o1).intValue())).doubleValue();

			double b = ((Double) scores.get(((Integer) o2).intValue())).doubleValue();

			int ret = a < b ? -1 : a > b ? 1 : 0;

			return ascending ? ret : -ret;
		}
	}
}
