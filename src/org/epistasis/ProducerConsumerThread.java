package org.epistasis;

public class ProducerConsumerThread extends Thread {
	private BlockingQueue queue = new BlockingQueue(100);
	private ThreadPool threads = new ThreadPool();
	private Producer producer = null;

	public void setProducer(Producer producer) {
		if (this.producer != null) {
			threads.remove(this.producer);
			this.producer.setQueue(null);
		}

		this.producer = producer;

		if (producer != null) {
			producer.setQueue(queue);
			threads.add(producer);
		}
	}

	public void addConsumer(Consumer consumer) {
		consumer.setQueue(queue);
		threads.add(consumer);
	}

	public void clearConsumers() {
		threads.clear();

		if (producer != null) {
			threads.add(producer);
		}
	}

	public void run() {
		if (producer == null) {
			throw new IllegalStateException("No Producer");
		}

		if (threads.size() < 2) {
			throw new IllegalStateException("No Consumers");
		}

		threads.run();
	}

	public void interrupt() {
		threads.interrupt();
		super.interrupt();
	}

	public abstract static class Producer implements Runnable {
		private BlockingQueue queue;

		public void run() {
			Object obj = null;

			try {
				while ((obj = produce()) != null && !Thread.currentThread().isInterrupted()) {
					queue.put(obj);
				}
				queue.put(null);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}

		public void setQueue(BlockingQueue queue) {
			this.queue = queue;
		}

		public abstract Object produce();
	}

	public abstract static class Consumer implements Runnable {
		private BlockingQueue queue;

		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					Object obj = queue.get();

					if (obj == null) {
						queue.put(null);
						return;
					}

					consume(obj);
				}
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}

		public void setQueue(BlockingQueue queue) {
			this.queue = queue;
		}

		public abstract void consume(Object obj);
	}
}
