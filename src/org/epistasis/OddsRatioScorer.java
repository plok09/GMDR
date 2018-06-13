package org.epistasis;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class OddsRatioScorer extends IsolatedAttributeScorer {
	private Map config = new TreeMap();

	public OddsRatioScorer(AbstractDataset data, boolean parallel) {
		super(data, parallel);
		init();
	}

	public OddsRatioScorer(AbstractDataset data, boolean parallel, Runnable onIncrementProgress) {
		super(data, parallel, onIncrementProgress);
		init();
	}

	private void init() {
		config.put("FILTER", "ODDSRATIO");
		config = Collections.unmodifiableMap(config);
	}

	protected double computeScore(int index) {
		int[] total = new int[2];
		double best = 0;
		Map m = new TreeMap();

		for (Iterator i = getData().iterator(); i.hasNext();) {
			AbstractDataset.Instance inst = (AbstractDataset.Instance) i.next();
			Object value = inst.get(index);
			int[] counts = (int[]) m.get(value);

			if (counts == null) {
				counts = new int[2];
				m.put(value, counts);
			}

			int status = ((Integer) inst.getStatus()).intValue();
			counts[status]++;
			total[status]++;
		}

		for (Iterator i = m.keySet().iterator(); i.hasNext();) {
			int[] counts = (int[]) m.get(i.next());

			double ratio = computeOddsRatio(counts[1], counts[0], total[1] - counts[1], total[0] - counts[0]);

			if (ratio > best) {
				best = ratio;
			}

			for (Iterator j = m.keySet().iterator(); j.hasNext();) {
				int[] jcounts = (int[]) m.get(j.next());

				if (jcounts == counts) {
					continue;
				}

				ratio = computeOddsRatio(counts[1], counts[0], jcounts[1], jcounts[0]);

				if (ratio > best) {
					best = ratio;
				}
			}
		}

		return best;
	}

	public Map getConfig() {
		return config;
	}

	private static double computeOddsRatio(double a, double b, double c, double d) {
		if (b == 0) {
			b = 1;
		}

		if (c == 0) {
			c = 1;
		}

		return a * d / (b * c);
	}
}
