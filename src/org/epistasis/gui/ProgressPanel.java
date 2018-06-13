package org.epistasis.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JProgressBar;

public class ProgressPanel extends TitledPanel {
	private JProgressBar prgProgress = new JProgressBar();
	private GridBagLayout gblLayout = new GridBagLayout();
	private double value = 0;

	public ProgressPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
		prgProgress.setValue(Math.round((float) value * 100.0f));
	}

	private void jbInit() throws Exception {
		this.setTitle("Progress Completed");
		this.setLayout(gblLayout);
		this.add(prgProgress,
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 240, 253));
		this.add(prgProgress, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		prgProgress.setStringPainted(true);
	}
}
