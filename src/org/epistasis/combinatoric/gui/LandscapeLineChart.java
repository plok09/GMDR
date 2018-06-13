package org.epistasis.combinatoric.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.epistasis.StringDoublePair;
import org.epistasis.Utility;
import org.epistasis.gui.AbstractChart;
import org.epistasis.gui.SelectionEvent;
import org.epistasis.gui.SelectionListener;

public class LandscapeLineChart extends AbstractChart implements SelectionListener {
	private List landscape;
	private Map xLabels;

	private boolean xticks = true;
	private int yticks = 10;
	private int dotradius = 3;

	public LandscapeLineChart() {
		setYTicks(yticks, false);
		addSelectionListener(this);
		setToolTipText("");
	}

	public boolean isXTicks() {
		return xticks;
	}

	public void setXTicks(boolean xticks) {
		setXTicks(xticks, true);
	}

	public void setXTicks(boolean xticks, boolean repaint) {
		this.xticks = xticks;

		clearXLabels();

		if (xLabels != null && xticks && landscape != null && !landscape.isEmpty()) {
			for (Iterator i = xLabels.entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				addXLabel(((Double) entry.getValue()).doubleValue(), entry.getKey(), repaint);
			}
		}

		if (repaint && isVisible()) {
			repaint();
		}
	}

	public int getYTicks() {
		return yticks;
	}

	public void setYTicks(int yticks) {
		setYTicks(yticks, true);
	}

	private Map computeCombinatoricXTicks(int totalAttr, int minAttr, int maxAttr) {
		Map labels = new TreeMap();

		NumberFormat nf = new DecimalFormat("#");
		((DecimalFormat) nf).setGroupingSize(3);
		nf.setGroupingUsed(true);

		BigInteger runningsum = BigInteger.ZERO;

		for (int i = minAttr; i <= maxAttr; ++i) {
			Double value = new Double(runningsum.doubleValue() / (landscape.size() - 1));
			labels.put(nf.format(i), value);
			runningsum = runningsum.add(Utility.combinations(BigInteger.valueOf(totalAttr), BigInteger.valueOf(i)));
		}

		return labels;
	}

	public void setYTicks(int yticks, boolean repaint) {
		this.yticks = yticks;

		clearYLabels();

		if (yticks > 0) {
			NumberFormat nf = new DecimalFormat("0.0###");

			for (int i = 0; i <= yticks; ++i) {
				double y = (double) i / yticks * getViewport().getHeight() + getViewport().getMinY();
				addYLabel(y, nf.format(y), false);
			}
		}

		if (repaint && isVisible()) {
			repaint();
		}
	}

	public int getDotRadius() {
		return dotradius;
	}

	public void setDotRadius(int dotradius) {
		setDotRadius(dotradius, true);
	}

	public void setDotRadius(int dotradius, boolean repaint) {
		this.dotradius = dotradius;

		if (repaint && isVisible()) {
			repaint();
		}
	}

	public void setLandscape(List landscape) {
		setLandscape(landscape, true);
	}

	public void setLandscape(List landscape, boolean repaint) {
		Map xLabels = new TreeMap();

		if (landscape != null) {
			int i = 0;

			for (Iterator iter = landscape.iterator(); iter.hasNext(); ++i) {
				xLabels.put(iter.next().toString(), new Double((double) i / (landscape.size() - 1)));
			}
		}

		setLandscape(landscape, xLabels, repaint);
	}

	public void setLandscape(List landscape, Map xLabels) {
		setLandscape(landscape, xLabels, true);
	}

	public void setLandscape(List landscape, Map xLabels, boolean repaint) {
		this.landscape = landscape;
		this.xLabels = xLabels;

		setSelectionEnabled(landscape != null && !landscape.isEmpty());

		setXTicks(isXTicks(), false);

		if (repaint && isVisible()) {
			repaint();
		}
	}

	public void paint(Graphics g) {
		if (landscape == null || landscape.isEmpty()) {
			g.clearRect(0, 0, getWidth(), getHeight());
		} else {
			super.paint(g);
		}
	}

	public void drawContents(Graphics2D g, int width, int height) {
		if (landscape == null || landscape.isEmpty()) {
			return;
		}

		long first = (long) Math.floor(getViewport().getMinX() * (landscape.size() - 1));
		long last = (long) Math.min(Math.ceil(getViewport().getMaxX() * (landscape.size() - 1)), landscape.size() - 1);

		int[] xPoints = new int[(int) (last - first) + 1];
		int[] yPoints = new int[xPoints.length];
		boolean drawDots = dotradius > 0 && ((1.0 / (landscape.size() - 1)) / getViewport().getWidth() * width) >= dotradius * 2;

		for (int i = 0; i < xPoints.length; ++i) {
			double x;

			if (xPoints.length > 1) {
				x = (double) (i + first) / (landscape.size() - 1);
				x -= getViewport().getMinX();
				x /= getViewport().getWidth();
				x *= width;
			} else {
				x = width / 2.0;
			}

			xPoints[i] = (int) Math.round(x);

			double y = ((StringDoublePair) landscape.get((int) (i + first))).getDouble();

			y -= getViewport().getMinY();
			y /= getViewport().getHeight();
			y *= height;

			if (y == Double.POSITIVE_INFINITY) {
				yPoints[i] = Short.MAX_VALUE;
			} else if (y == Double.NEGATIVE_INFINITY) {
				yPoints[i] = Short.MIN_VALUE;
			} else {
				yPoints[i] = (int) Math.round(y);
			}

			if (drawDots) {
				g.fillOval(xPoints[i] - dotradius, yPoints[i] - dotradius - 1, dotradius * 2 + 1, dotradius * 2 + 1);
			}
		}

		g.setColor(getForeground());

		if (xPoints.length > 1) {
			g.drawPolyline(xPoints, yPoints, xPoints.length);
		}
	}

	public void setViewport(Rectangle2D viewport, boolean repaint) {
		super.setViewport(viewport, false);
		setYTicks(getYTicks(), repaint);
	}

	public void selectionChanged(SelectionEvent e) {
		Rectangle2D chart = getSelectable();

		AffineTransform xform = new AffineTransform();
		xform.translate(getViewport().getX(), getViewport().getY());
		xform.scale(getViewport().getWidth(), getViewport().getHeight());
		xform.translate(0, 1);
		xform.scale(1, -1);
		xform.scale(1.0 / chart.getWidth(), 1.0 / chart.getHeight());
		xform.translate(-chart.getX(), -chart.getY());

		Rectangle2D v = (Rectangle2D) xform.createTransformedShape(e.getSelection()).getBounds2D();

		setViewport(v);
	}

	public String getToolTipText(MouseEvent e) {
		int width = getWidth() - getBorderInsets().left - getBorderInsets().right - getChartInsets().left - getChartInsets().right;
		int height = getHeight() - getBorderInsets().top - getBorderInsets().bottom - getChartInsets().top - getChartInsets().bottom;

		boolean drawDots = landscape != null && !landscape.isEmpty() && dotradius > 0
				&& ((1.0 / (landscape.size() - 1)) / getViewport().getWidth() * width) >= dotradius * 2;

		if (!drawDots) {
			return null;
		}

		double x;
		int oldx;
		int idx;

		if (landscape.size() > 1) {
			x = e.getX() - getBorderInsets().left - getChartInsets().left;

			if (x < 0 || x > width) {
				return null;
			}

			x /= width;
			x *= getViewport().getWidth();
			x += getViewport().getMinX();
			x *= landscape.size() - 1;

			idx = (int) Math.round(x);

			oldx = (int) Math.round((((double) idx / (landscape.size() - 1)) - getViewport().getMinX()) / getViewport().getWidth() * width
					+ getChartInsets().left + getBorderInsets().left);
		} else {
			x = width / 2.0;
			oldx = (int) Math.round(x) + getChartInsets().left + getBorderInsets().left;

			idx = 0;
		}

		if (Math.abs(oldx - e.getX()) > dotradius) {
			return null;
		}

		double y = e.getY() - getBorderInsets().top - getChartInsets().top;

		if (y < 0 || y > height) {
			return null;
		}

		y /= height;
		y = 1 - y;
		y *= getViewport().getHeight();
		y += getViewport().getMinY();

		double value = ((StringDoublePair) landscape.get(idx)).getDouble();

		int oldvalue = (int) Math.round((1 - ((value - getViewport().getMinY()) / getViewport().getHeight())) * height + getChartInsets().top
				+ getBorderInsets().top);

		if (Math.abs(oldvalue - e.getY()) > dotradius) {
			return null;
		}

		NumberFormat nf = new DecimalFormat("0.0###");

		return landscape.get(idx).toString() + " " + nf.format(value);
	}
}
