package org.epistasis.combinatoric;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.epistasis.AbstractAttributeScorer;

public class AttributeRankerThread extends Thread {

	private static final int SELECTN = 1;
	private static final int SELECTPCT = 2;
	private static final int SELECTTHRESHOLD = 3;

	private AttributeRanker ranker = new AttributeRanker();
	private AttributeCombination combo;
	private Double value;
	private boolean ascending;
	private boolean closed;
	private int mode;
	private Runnable onEnd;
	private Map config;

	public AttributeRankerThread(AbstractAttributeScorer scorer, int n, boolean ascending, Runnable onEnd) {
		ranker.setScorer(scorer);
		value = new Double(n);
		mode = SELECTN;
		this.ascending = ascending;
		this.onEnd = onEnd;
		config = new TreeMap(scorer.getConfig());
		config.put("SELECTION", "TOPN");
		config.put("SELECTIONVALUE", Integer.toString(n));
		config = Collections.unmodifiableMap(config);
	}

	public AttributeRankerThread(AbstractAttributeScorer scorer, double pct, boolean ascending, Runnable onEnd) {
		ranker.setScorer(scorer);
		value = new Double(pct);
		mode = SELECTPCT;
		this.ascending = ascending;
		this.onEnd = onEnd;
		config = new TreeMap(scorer.getConfig());
		config.put("SELECTION", "TOP%");
		config.put("SELECTIONVALUE", value.toString());
		config = Collections.unmodifiableMap(config);
	}

	public AttributeRankerThread(AbstractAttributeScorer scorer, double threshold, boolean above, boolean closed, Runnable onEnd) {
		ranker.setScorer(scorer);
		value = new Double(threshold);
		mode = SELECTTHRESHOLD;
		this.ascending = !above;
		this.closed = closed;
		this.onEnd = onEnd;
		config = new TreeMap(scorer.getConfig());
		config.put("SELECTION", "THRESHOLD");
		config.put("SELECTIONVALUE", value.toString());
		config = Collections.unmodifiableMap(config);
	}

	public void run() {
		switch (mode) {
		case SELECTN:
			combo = ranker.selectN(value.intValue(), ascending);
			break;

		case SELECTPCT:
			combo = ranker.selectPct(value.doubleValue(), ascending);
			break;

		case SELECTTHRESHOLD:
			combo = ranker.selectThreshold(value.doubleValue(), !ascending, closed);
			break;
		}

		if (onEnd != null) {
			onEnd.run();
		}
	}

	public Map getConfig() {
		return config;
	}

	public AttributeCombination getCombo() {
		return combo;
	}

	public AttributeRanker getRanker() {
		return ranker;
	}
}
