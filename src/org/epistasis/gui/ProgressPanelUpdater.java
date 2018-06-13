package org.epistasis.gui;

public class ProgressPanelUpdater implements Runnable {
	private long max;
	private long value = 0;
	private ProgressPanel prgProgress;

	public ProgressPanelUpdater(ProgressPanel prgProgress, long max) {
		this.prgProgress = prgProgress;
		this.max = max;
	}

	public void run() {
		prgProgress.setValue(getNextValue());
	}

	private synchronized double getNextValue() {
		return (double) (++value) / max;
	}
}
