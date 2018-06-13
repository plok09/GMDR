package org.epistasis.gui;

import java.awt.geom.Rectangle2D;
import java.util.EventObject;

public class SelectionEvent extends EventObject {
	private Rectangle2D viewport;
	private Rectangle2D selection;

	public SelectionEvent(SelectableComponent source, Rectangle2D viewport, Rectangle2D selection) {
		super(source);
		this.viewport = viewport;
		this.selection = selection;
	}

	public Rectangle2D getViewport() {
		return viewport;
	}

	public Rectangle2D getSelection() {
		return selection;
	}
}
