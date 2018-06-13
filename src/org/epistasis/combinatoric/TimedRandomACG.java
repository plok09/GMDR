package org.epistasis.combinatoric;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.epistasis.AttributeLabels;

public class TimedRandomACG extends RandomACG {
	private Timer t;
	private long millis;
	private boolean hasNext = true;

	public TimedRandomACG(AttributeLabels labels, int attrCount, Random rand, long millis) {
		super(labels, attrCount, rand);
		this.millis = millis;
	}

	public Object next() {
		if (t == null) {
			t = new Timer(true);
			t.schedule(new SetDoneTask(), millis);
		}

		return super.next();
	}

	public boolean hasNext() {
		return hasNext;
	}

	private synchronized void setDone() {
		hasNext = false;
	}

	private class SetDoneTask extends TimerTask {
		public void run() {
			setDone();
		}
	}
}
