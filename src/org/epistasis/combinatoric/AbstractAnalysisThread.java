package org.epistasis.combinatoric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.epistasis.AbstractDataset;
import org.epistasis.ProducerConsumerThread;

public abstract class AbstractAnalysisThread extends Thread {
	private AbstractDataset data;
	private CombinatoricModelFilter filter;
	private long seed;
	private int intervals;
	private int nConsumers;
	private Runnable onEndModel;
	private Runnable onEndAttribute;
	private Runnable onEnd;
	private boolean complete = false;
	private List producers = new ArrayList();

	public static class QueueEntry {
		private AttributeCombination attributes;
		private int interval;

		public QueueEntry(AttributeCombination attributes, int interval) {
			this.attributes = attributes;
			this.interval = interval;
		}

		public AttributeCombination getAttributes() {
			return attributes;
		}

		public int getInterval() {
			return interval;
		}
	}

	public abstract static class Producer extends ProducerConsumerThread.Producer {
		private int intervals;

		public Producer(int intervals) {
			this.intervals = intervals;
		}

		public int getIntervals() {
			return intervals;
		}
	}

	public static class ForcedCombinationProducer extends Producer {
		private AttributeCombination attributes;
		private int interval = 0;

		public ForcedCombinationProducer(AttributeCombination attributes, int intervals) {
			super(intervals);
			this.attributes = attributes;
		}

		public Object produce() {
			if (interval >= getIntervals()) {
				return null;
			}

			return new QueueEntry(attributes, interval++);
		}
	}

	public static class CombinatoricProducer extends Producer {
		private AttributeCombination attributes;
		private Iterator acg;
		private int interval = 0;

		public CombinatoricProducer(Iterator acg, int intervals) {
			super(intervals);
			this.acg = acg;
		}

		public Object produce() {
			if (interval == 0 && !acg.hasNext()) {
				return null;
			}

			if (interval == 0) {
				attributes = (AttributeCombination) acg.next();
			}

			QueueEntry entry = new QueueEntry(attributes, interval);

			interval = (interval + 1) % getIntervals();

			return entry;
		}
	}

	protected class ModelAnalyzer extends ProducerConsumerThread.Consumer {
		public void consume(Object obj) {
			QueueEntry entry = (QueueEntry) obj;

			AbstractCombinatoricModel model = createModel(entry.getAttributes());

			model.train((List) data.getTrainSets().get(entry.getInterval()));
			filter.add(model, entry.getInterval());

			if (onEndModel != null) {
				onEndModel.run();
			}
		}
	}

	public int getIntervals() {
		return intervals;
	}

	public long getSeed() {
		return seed;
	}

	public boolean isComplete() {
		return complete;
	}

	public CombinatoricModelFilter getModelFilter() {
		return filter;
	}

	public void run() {
		ProducerConsumerThread pct = null;

		filter.getData().partition(intervals, new Random(seed));

		for (Iterator i = producers.iterator(); i.hasNext() && !isInterrupted();) {
			pct = new ProducerConsumerThread();
			pct.setProducer((ProducerConsumerThread.Producer) i.next());

			for (int j = 0; j < nConsumers; ++j) {
				pct.addConsumer(new ModelAnalyzer());
			}

			pct.run();

			if (!isInterrupted() && onEndAttribute != null) {
				onEndAttribute.run();
			}
		}

		complete = !isInterrupted();

		if (onEnd != null) {
			onEnd.run();
		}
	}

	public void addProducer(Producer producer) {
		producers.add(producer);
	}

	protected AbstractAnalysisThread(CombinatoricModelFilter filter, AbstractDataset data, int intervals, long seed, Runnable onEndModel,
			Runnable onEndAttribute, Runnable onEnd, boolean parallel) {
		this.filter = filter;
		this.data = data;
		this.seed = seed;
		this.intervals = intervals;
		this.onEndModel = onEndModel;
		this.onEndAttribute = onEndAttribute;
		this.onEnd = onEnd;

		this.nConsumers = parallel ? Runtime.getRuntime().availableProcessors() : 1;
	}

	protected abstract AbstractCombinatoricModel createModel(AttributeCombination attributes);
}
