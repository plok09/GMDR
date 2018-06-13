package org.epistasis;

public abstract class IsolatedAttributeScorer extends AbstractAttributeScorer {
	private boolean parallel;

	protected abstract double computeScore(int index);

	public IsolatedAttributeScorer(AbstractDataset data, boolean parallel) {
		super(data);
		this.parallel = parallel;
	}

	public IsolatedAttributeScorer(AbstractDataset data, boolean parallel, Runnable onIncrementProgress) {
		super(data, onIncrementProgress);
		this.parallel = parallel;
	}

	public int getTotalProgress() {
		return getData().getNumAttributes();
	}

	protected double[] computeScores() {
		double[] scores = new double[getData().getNumAttributes()];

		if (parallel) {
			ProducerConsumerThread thread = new ProducerConsumerThread();

			thread.setProducer(new Producer());

			for (int i = 0; i < Runtime.getRuntime().availableProcessors(); ++i) {
				thread.addConsumer(new Consumer(scores));
			}

			thread.run();
		} else {
			for (int i = 0; i < scores.length; ++i) {
				scores[i] = computeScore(i);
				incrementProgress();
			}
		}

		return scores;
	}

	private class Producer extends ProducerConsumerThread.Producer {
		private int i = 0;

		public Object produce() {
			if (i < getData().getNumAttributes()) {
				return new Integer(i++);
			} else {
				return null;
			}
		}
	}

	private class Consumer extends ProducerConsumerThread.Consumer {
		private double[] scores;

		public Consumer(double[] scores) {
			this.scores = scores;
		}

		public void consume(Object obj) {
			int i = ((Integer) obj).intValue();
			scores[i] = computeScore(i);
			incrementProgress();
		}
	}
}
