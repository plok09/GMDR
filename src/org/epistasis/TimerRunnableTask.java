package org.epistasis;

import java.util.TimerTask;

public class TimerRunnableTask extends TimerTask {
	private Runnable target;

	public TimerRunnableTask(Runnable target) {
		this.target = target;
	}

	public void run() {
		target.run();
	}
}
