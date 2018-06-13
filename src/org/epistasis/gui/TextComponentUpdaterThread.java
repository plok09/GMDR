package org.epistasis.gui;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

public class TextComponentUpdaterThread extends Thread {
	private JTextComponent component;
	private Object textgen;
	private Runnable target;
	private Runnable target2;
	public TextComponentUpdaterThread(JTextComponent component, Object textgen) {
		this(component, textgen, null);
	}

	public TextComponentUpdaterThread(JTextComponent component, Object textgen, Runnable target) {
		this.component = component;
		this.textgen = textgen;
		this.target = target;
		setName("TextComponentUpdaterThread");
	}
	public TextComponentUpdaterThread(JTextComponent component, Object textgen, Runnable target1,Runnable target2) {
		this.component = component;
		this.textgen = textgen;
		this.target = target1;
		this.target2=target2;
		setName("TextComponentUpdaterThread");
	}

	public void run() {
		SwingUtilities.invokeLater(new TextSetter(textgen.toString()));
	}

	private class TextSetter implements Runnable {
		private String text;

		public TextSetter(String text) {
			this.text = text;
		}

		public void run() {
			component.setText(text);
			component.select(0, 0);

			if (target != null) {
				target.run();
			}
		}
	}
}
