package org.epistasis;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Manages a group of threads. The threads in the pool will be started at the
 * same time and interrupted at the same time, and the pool itself will wait
 * until all threads terminate before terminating itself.
 */
public class ThreadPool extends AbstractList implements Runnable {
	/** Indicates whether the pool is running */
	private boolean running = false;

	/** The list of threads in the pool */
	private List threads = Collections.synchronizedList(new ArrayList());

	/**
	 * Add code to the pool with the default priority. This should not be called
	 * while the pool is running.
	 * 
	 * @param r
	 *            Code to run on a thread in the pool
	 */
	public void add(Runnable r) {
		add(r, Thread.NORM_PRIORITY);
	}

	/**
	 * Add code to the pool. This should not be called while the pool is
	 * running.
	 * 
	 * @param r
	 *            Code to run on a thread in the pool
	 * @param priority
	 *            Priority for thread
	 */
	public void add(Runnable r, int priority) {
		// check to make sure we're not running yet
		if (running) {
			throw new IllegalStateException("ThreadPool is running.");
		}

		// creat the thread
		Thread t = new Thread(r);
		t.setPriority(priority);

		// add the thread to the pool
		threads.add(t);
	}

	/**
	 * Clear the threads from the pool. This should not be called while the pool
	 * is running.
	 */
	public void clear() {
		// check to make sure we're not running yet
		if (running) {
			throw new IllegalStateException("ThreadPool is running.");
		}

		// clear the pool
		threads.clear();
	}

	/**
	 * Start the thread pool.
	 */
	public void run() {
		// we'll now start the pool
		running = true;

		// iterate over threads, starting each one

		for (Iterator i = threads.iterator(); i.hasNext();) {
			((Thread) i.next()).start();
		}

		try {
			// iterate over threads, waiting for each one to finish
			for (Iterator i = threads.iterator(); i.hasNext();) {
				((Thread) i.next()).join();
			}
		} catch (InterruptedException ex) {
			// if the current thread is interrupted while waiting for the
			// child threads to end, interrupt all the child threads
			interrupt();

			// propagate the interrupt
			Thread.currentThread().interrupt();
		}

		// we're done now
		running = false;
	}

	/**
	 * Interrupt all threads in the pool. This may not stop the threads if they
	 * mask interrupts.
	 */
	public void interrupt() {
		// if we're not running, there's nothing to interrupt
		if (!running) {
			return;
		}

		// iterate over threads, calling interrupt() on each
		for (Iterator i = threads.iterator(); i.hasNext();) {
			((Thread) i.next()).interrupt();
		}

		// we're no longer running
		running = false;
	}

	public int size() {
		return threads.size();
	}

	public Object get(int index) {
		return threads.get(index);
	}

	public Object remove(int index) {
		return threads.remove(index);
	}
}
