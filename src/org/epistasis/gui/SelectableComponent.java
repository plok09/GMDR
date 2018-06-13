package org.epistasis.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.UIManager;

public class SelectableComponent extends JComponent implements MouseListener, MouseMotionListener, KeyListener {
	private Point2D dragStart = null;
	private Point2D dragEnd = null;
	private Rectangle2D selected = null;
	private java.util.List selectionListeners = new LinkedList();
	private Color selectionColor = UIManager.getLookAndFeelDefaults().getColor("TextField.selectionBackground");

	private boolean selectionEnabled = true;

	public Rectangle2D getSelectable() {
		return getBounds();
	}

	public SelectableComponent() {
		setFocusable(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		setBackground(Color.white);
	}

	public void paint(Graphics g) {
		g.clearRect(getInsets().left, getInsets().top, getWidth() - getInsets().left - getInsets().right, getHeight() - getInsets().top - getInsets().bottom);
	}

	public boolean isSelectionEnabled() {
		return selectionEnabled;
	}

	public void setSelectionEnabled(boolean enabled) {
		selectionEnabled = enabled;
	}

	public void addSelectionListener(SelectionListener l) {
		selectionListeners.add(l);
	}

	public void removeSelectionListener(SelectionListener l) {
		selectionListeners.remove(l);
	}

	public void mouseClicked(MouseEvent e) {
		if (!selectionEnabled) {
			return;
		}

		if (dragStart != null && e.getButton() == MouseEvent.BUTTON3) {
			dragEnd = e.getPoint();
			cancelSelection();
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void keyTyped(KeyEvent e) {
		if (!selectionEnabled) {
			return;
		}

		if (dragStart != null && e.getKeyChar() == KeyEvent.VK_ESCAPE) {
			cancelSelection();
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (!selectionEnabled) {
			return;
		}

		if (dragStart == null && e.getButton() == MouseEvent.BUTTON1) {
			dragStart = e.getPoint();
			requestFocusInWindow(true);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (!selectionEnabled) {
			return;
		}

		if (dragStart != null && e.getButton() == MouseEvent.BUTTON1) {
			Rectangle2D before = null;
			Rectangle2D after = new Rectangle();
			Rectangle2D selectable = getSelectable();

			if (dragEnd != null) {
				before = new Rectangle();
				before.setFrameFromDiagonal(dragStart, dragEnd);
				Rectangle2D.intersect(before, selectable, before);
			}

			dragEnd = e.getPoint();
			after.setFrameFromDiagonal(dragStart, dragEnd);
			Rectangle2D.intersect(after, selectable, after);

			selectionChanged(before, after, true);

			dragStart = dragEnd = null;
			selectable = null;
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (!selectionEnabled) {
			return;
		}

		if (dragStart != null) {
			Rectangle2D before = null;
			Rectangle2D after = new Rectangle();
			Rectangle2D selectable = getSelectable();

			if (dragEnd != null) {
				before = new Rectangle();
				before.setFrameFromDiagonal(dragStart, dragEnd);
				Rectangle2D.intersect(before, selectable, before);
			}

			dragEnd = e.getPoint();
			after.setFrameFromDiagonal(dragStart, dragEnd);
			Rectangle2D.intersect(after, selectable, after);

			selectionChanged(before, after, false);
		}
	}

	public Rectangle2D getViewport() {
		return getSelectable();
	}

	protected void fireSelectionEvent(SelectionEvent e) {
		Iterator i = selectionListeners.iterator();

		while (i.hasNext()) {
			((SelectionListener) i.next()).selectionChanged(e);
		}
	}

	protected void selectionChanged(Rectangle2D before, Rectangle2D after, boolean done) {
		Graphics2D g = (Graphics2D) getGraphics();

		g.setXORMode(Color.WHITE);
		g.setColor(selectionColor);

		if (before != null && after != null && after.getWidth() > 0 && after.getHeight() > 0 && done) {
			g.fill(before);
			fireSelectionEvent(new SelectionEvent(this, getViewport(), selected));
		} else if (before != null) {
			Area areaFill = new Area(before);
			areaFill.exclusiveOr(new Area(after));
			g.fill(areaFill);
		} else {
			g.fill(after);
		}

		selected = after;

		g.setColor(getForeground());
		g.setPaintMode();
	}

	protected void cancelSelection() {
		if (selected == null) {
			return;
		}

		Graphics2D g = (Graphics2D) getGraphics();
		g.setXORMode(Color.WHITE);
		g.setColor(selectionColor);
		g.fill(selected);
		g.setColor(getForeground());
		g.setPaintMode();

		dragStart = dragEnd = null;
		selected = null;
	}
}
