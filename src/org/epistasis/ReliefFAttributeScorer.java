package org.epistasis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class ReliefFAttributeScorer extends AbstractAttributeScorer {
	private Random rnd;
	private int m;
	private int k;
	private boolean all;
	private boolean parallel;
	private List samples;
	private Map attrDist;
	private Map instDist;
	private double[] weights;
	private Map config = new TreeMap();

	public ReliefFAttributeScorer(AbstractDataset data, int m, int k, boolean all, Random rnd, boolean parallel) {
		super(data);
		init(m, k, all, rnd, parallel);
	}

	public ReliefFAttributeScorer(AbstractDataset data, int m, int k, boolean all, Random rnd, boolean parallel, Runnable onIncrementProgress) {
		super(data, onIncrementProgress);
		init(m, k, all, rnd, parallel);
	}

	private void init(int m, int k, boolean all, Random rnd, boolean parallel) {
		this.m = m;
		this.k = k;
		this.all = all;
		this.rnd = rnd;
		this.parallel = false;
		config.put("FILTER", "RELIEFF");
		config.put("NEIGHBORS", Integer.toString(k));
		config.put("SAMPLES", all ? "ALL" : Integer.toString(m));
		config = Collections.unmodifiableMap(config);
	}

	public Map getConfig() {
		return config;
	}

	public int getTotalProgress() {
		return all ? getData().getNumInstances() : m;
	}

	public double[] computeScores() {
		weights = new double[getData().getNumAttributes()];

		if (all || m <= 0 || m >= getData().getNumInstances()) {
			samples = getData();
			m = samples.size();
		} else {
			samples = new ArrayList(getData());
			Collections.shuffle(samples, rnd);
			samples = samples.subList(0, m);
		}

		instDist = Collections.synchronizedMap(new HashMap());

		ProducerConsumerThread reliefThread = new ProducerConsumerThread();
		reliefThread.setProducer(new Producer());
		int maxConsumer = parallel ? Runtime.getRuntime().availableProcessors() : 1;

		for (int i = 0; i < maxConsumer; ++i) {
			reliefThread.addConsumer(new Consumer());
		}

		reliefThread.run();

		return weights;
	}

	private double diff(int index, List a, List b) {
		Object o1 = a.get(index);
		Object o2 = b.get(index);

		if (o1 instanceof Number && o2 instanceof Number) {
			if (attrDist == null) {
				attrDist = computeAttrDistMap();
			}

			double dist = ((Double) attrDist.get(new Integer(index))).doubleValue();

			return Math.abs(((Number) o1).doubleValue() - ((Number) o2).doubleValue()) / dist;
		}

		return o1.equals(o2) ? 0 : 1;
	}

	private Double diff(List a, List b) {
		if (a == b) {
			return new Double(0);
		}

		Object[] key = new Object[2];
		key[0] = b;
		key[1] = a;

		Double value = (Double) instDist.get(key);

		if (value == null) {
			key[0] = a;
			key[1] = b;
			value = (Double) instDist.get(key);

			if (value == null) {
				double sum = 0;

				for (int i = 0; i < a.size(); ++i) {
					sum += diff(i, a, b);
				}

				value = new Double(sum);
			}
		}

		return value;
	}

	private Map computeAttrDistMap() {
		if (getData().getNumInstances() <= 0) {
			return null;
		}

		Map attrDist = Collections.synchronizedMap(new HashMap());

		return attrDist;
	}

	private void computeNeighborhood(AbstractDataset.Instance inst, List hits, List misses) {
		for (Iterator i = getData().iterator(); i.hasNext();) {
			AbstractDataset.Instance neighbor = (AbstractDataset.Instance) i.next();

			if (inst == neighbor) {
				continue;
			}

			List neighborhood;

			if (inst.getStatus().equals(neighbor.getStatus())) {
				neighborhood = hits;
			} else {
				neighborhood = misses;
			}

			Double dist = diff(inst, neighbor);
			neighborhood.add(new NeighborhoodElement(dist, neighbor));
		}
	}

	private void processInstance(AbstractDataset.Instance inst) {
		List allHits = new ArrayList();
		List allMisses = new ArrayList();

		computeNeighborhood(inst, allHits, allMisses);

		allHits = Utility.lowestN(allHits, k);
		allMisses = Utility.lowestN(allMisses, k);

		List hits = new ArrayList(k);
		List misses = new ArrayList(k);

		for (Iterator i = allHits.iterator(); i.hasNext();) {
			hits.add(((NeighborhoodElement) i.next()).i);
		}

		for (Iterator i = allMisses.iterator(); i.hasNext();) {
			misses.add(((NeighborhoodElement) i.next()).i);
		}

		allHits = allMisses = null;

		double denominator = m * k;

		for (int i = 0; i < getData().getNumAttributes(); ++i) {
			double hitSum = 0;
			double missSum = 0;

			for (Iterator j = hits.iterator(); j.hasNext();) {
				hitSum += diff(i, inst, (List) j.next());
			}

			for (Iterator j = misses.iterator(); j.hasNext();) {
				missSum += diff(i, inst, (List) j.next());
			}

			alterWeight(i, (missSum - hitSum) / denominator);
		}

		incrementProgress();
	}

	private synchronized void alterWeight(int idx, double addend) {
		weights[idx] += addend;
	}

	private static class NeighborhoodElement implements Comparable {
		public Double d;
		public AbstractDataset.Instance i;

		public NeighborhoodElement() {
		}

		public NeighborhoodElement(Double d, AbstractDataset.Instance i) {
			this.d = d;
			this.i = i;
		}

		public int compareTo(Object o) {
			NeighborhoodElement n = (NeighborhoodElement) o;
			return d.compareTo(n.d);
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}

			try {
				NeighborhoodElement n = (NeighborhoodElement) o;
				return d.equals(n.d) && i.equals(n.i);
			} catch (ClassCastException ex) {
				return false;
			}
		}
	}

	private class AttrDistProducer extends ProducerConsumerThread.Producer {
		private int idx = 0;

		public Object produce() {
			if (idx < getData().getNumAttributes()) {
				return new Integer(idx++);
			}

			return null;
		}
	}

	private class AttrDistConsumer extends ProducerConsumerThread.Consumer {
		private Map attrDist;

		public AttrDistConsumer(Map attrDist) {
			this.attrDist = attrDist;
		}

		public void consume(Object obj) {
			int i = ((Integer) obj).intValue();

			if (!(((List) getData().get(0)).get(i) instanceof Number)) {
				return;
			}

			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;

			for (Iterator j = getData().iterator(); j.hasNext();) {
				double value = ((Number) ((List) j.next()).get(i)).doubleValue();

				if (value < min) {
					min = value;
				}

				if (value > max) {
					max = value;
				}
			}

			attrDist.put(obj, new Double(max - min));
		}
	}

	private class Producer extends ProducerConsumerThread.Producer {
		private Iterator i = samples.iterator();

		public Object produce() {
			if (i.hasNext()) {
				return i.next();
			} else {
				return null;
			}
		}
	}

	private class Consumer extends ProducerConsumerThread.Consumer {
		public void consume(Object obj) {
			processInstance((AbstractDataset.Instance) obj);
		}
	}
}
