package GUI;

import javax.swing.JComponent;

public class ComponentEnabler implements Runnable {
	private JComponent component;
	private boolean enabled;

	public ComponentEnabler(JComponent component, boolean enabled) {
		this.component = component;
		this.enabled = enabled;
	}

	public void run() {
		component.setEnabled(enabled);
	}
}
