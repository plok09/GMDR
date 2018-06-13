package org.epistasis;

import java.util.LinkedList;

/**
 * A synchronized FIFO queue. If an operation cannot be performed (e.g. getting
 * an element from an empty queue, or putting an element into a full queue),
 * then the calling thread will block until the state of the queue changes.
 */
public class BlockingQueue {
	/** The maximum number of elements for the queue. */
	private final int capacity;

	/** Provides the underlying storage for the queue. */
	private LinkedList queue = new LinkedList();

	/**
	 * Default constructor. This sets the capacity to
	 * <code>Integer.MAX_VALUE</code>.
	 */
	public BlockingQueue() {
		this(Integer.MAX_VALUE);
	}

	/**
	 * Construct a <code>BlockingQueue</code> with a given capacity.
	 * 
	 * @param capacity
	 *            Maximum number of elements for this queue
	 */
	public BlockingQueue(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * Get the next value from the queue. If the queue is empty, this method
	 * blocks until an element is added, or its calling thread is interrupted.
	 * 
	 * @return Next value from queue
	 * @throws InterruptedException
	 *             If the calling thread is interrupted while blocked in this
	 *             method.
	 */
	public synchronized Object get() throws InterruptedException {
		// there may be multiple callers, and they are all woken when
		// the queue state changes, so repeat the check until we know the
		// queue isn't empty
		while (queue.isEmpty()) {
			wait();
		}

		// pop the element off the queue
		Object ret = queue.getLast();
		queue.removeLast();

		// notify anyone waiting on this queue
		notifyAll();

		// return the element
		return ret;
	}

	/**
	 * Put a value into the queue. If the queue is empty, this method blocks
	 * until an element is removed, or its calling thread is interrupted.
	 * 
	 * @param obj
	 *            The value to add to the queue
	 * @throws InterruptedException
	 *             If the calling thread is interrupted while blocked in this
	 *             method.
	 */
	public synchronized void put(Object obj) throws InterruptedException {
		// there may be multiple callers, and they are all woken when
		// the queue state changes, so repeat the check until we know the
		// queue isn't full
		while (queue.size() >= capacity) {
			wait();
		}

		// add the element
		queue.addFirst(obj);

		// notify anyone waiting on this queue
		notifyAll();
	}

	/**
	 * Clear the queue.
	 */
	public synchronized void clear() {
		// clear the queue
		queue.clear();

		// notify anyone waiting on this queue
		notifyAll();
	}
}
