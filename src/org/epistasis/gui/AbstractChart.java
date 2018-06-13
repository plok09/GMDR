package org.epistasis.gui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.jibble.epsgraphics.EpsGraphics2D;

public abstract class AbstractChart extends SelectableComponent {

	private Insets borderInsets = new Insets(10, 10, 10, 10);
	private Insets chartInsets = new Insets(0, 0, 0, 0);
	private boolean dirtyInsets = true;
	private String xAxisLabel = "x-axis";
	private String yAxisLabel = "y-axis";
	private int xTickSize = 5;
	private int yTickSize = 5;
	private Font axisFont = new Font("Dialog", Font.BOLD, 20);
	private Font labelFont = new Font("Dialog", Font.PLAIN, 12);
	private Map xLabels = new TreeMap();
	private Map yLabels = new TreeMap();
	private Rectangle2D viewport = new Rectangle.Double(0, 0, 1, 1);

	public String getXAxisLabel() {
		return xAxisLabel;
	}

	public void setXAxisLabel(String xAxisLabel) {
		setXAxisLabel(xAxisLabel, true);
	}

	public void setXAxisLabel(String xAxisLabel, boolean repaint) {
		this.xAxisLabel = xAxisLabel;
		update(repaint);
	}

	public String getYAxisLabel() {
		return yAxisLabel;
	}

	public void setYAxisLabel(String yAxisLabel) {
		setYAxisLabel(yAxisLabel, true);
	}

	public void setYAxisLabel(String yAxisLabel, boolean repaint) {
		this.yAxisLabel = yAxisLabel;
		update(repaint);
	}

	public int getXTickSize() {
		return xTickSize;
	}

	public void setXTickSize(int xTickSize) {
		setXTickSize(xTickSize, true);
	}

	public void setXTickSize(int xTickSize, boolean repaint) {
		this.xTickSize = xTickSize;
		update(repaint);
	}

	public int getYTickSize() {
		return yTickSize;
	}

	public void setYTickSize(int yTickSize) {
		setYTickSize(yTickSize, true);
	}

	public void setYTickSize(int yTickSize, boolean repaint) {
		this.yTickSize = yTickSize;
		update(repaint);
	}

	public Font getAxisFont() {
		return axisFont;
	}

	public void setAxisFont(Font axisFont) {
		setAxisFont(axisFont, true);
	}

	public void setAxisFont(Font axisFont, boolean repaint) {
		this.axisFont = axisFont;
		update(repaint);
	}

	public Font getLabelFont() {
		return labelFont;
	}

	public void setLabelFont(Font labelFont) {
		setAxisFont(labelFont, true);
	}

	public void setLabelFont(Font labelFont, boolean repaint) {
		this.labelFont = labelFont;
		update(repaint);
	}

	public Insets getBorderInsets() {
		return borderInsets;
	}

	public void setBorderInsets(Insets borderInsets) {
		setBorderInsets(borderInsets, true);
	}

	public void setBorderInsets(Insets borderInsets, boolean repaint) {
		this.borderInsets = borderInsets;
		update(repaint);
	}

	public Rectangle2D getViewport() {
		return viewport;
	}

	public void setViewport(Rectangle2D viewport) {
		setViewport(viewport, true);
	}

	public void setViewport(Rectangle2D viewport, boolean repaint) {
		this.viewport = viewport;
		update(repaint);
	}

	public void paint(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		g.translate(borderInsets.left, borderInsets.right);
		draw((Graphics2D) g, getWidth() - borderInsets.left - borderInsets.right, getHeight() - borderInsets.top - borderInsets.bottom);
	}

	public String getEpsText(int width, int height) {
		EpsGraphics2D g = new EpsGraphics2D();

		dirtyInsets = true;
		draw(g, width, height);
		dirtyInsets = true;

		return g.toString();
	}

	protected void addXLabel(double x, Object label) {
		addXLabel(x, label, true);
	}

	protected void addXLabel(double x, Object label, boolean repaint) {
		xLabels.put(new Double(x), label);
		update(repaint);
	}

	protected void clearXLabels() {
		clearXLabels(true);
	}

	protected void clearXLabels(boolean repaint) {
		xLabels.clear();
		update(repaint);
	}

	protected void addYLabel(double y, Object label) {
		addYLabel(y, label, true);
	}

	protected void addYLabel(double y, Object label, boolean repaint) {
		yLabels.put(new Double(y), label);
		update(repaint);
	}

	protected void clearYLabels() {
		clearYLabels(true);
	}

	protected void clearYLabels(boolean repaint) {
		yLabels.clear();
		update(repaint);
	}

	private void update(boolean repaint) {
		dirtyInsets = true;

		if (repaint && isVisible()) {
			repaint();
		}
	}

	protected Insets getChartInsets() {
		if (dirtyInsets) {
			FontMetrics fmAxis = getGraphics().getFontMetrics(axisFont);
			FontMetrics fmLabel = getGraphics().getFontMetrics(labelFont);
			int width = getWidth() - borderInsets.left - borderInsets.right;
			int height = getHeight() - borderInsets.top - borderInsets.bottom;
			Graphics2D g = (Graphics2D) getGraphics();

			double xmin = Double.POSITIVE_INFINITY;
			double xmax = Double.NEGATIVE_INFINITY;
			double ymin = Double.NEGATIVE_INFINITY;
			double ymax = Double.POSITIVE_INFINITY;

			chartInsets.top = chartInsets.bottom = chartInsets.left = chartInsets.right = 0;

			if (xAxisLabel != null && xAxisLabel.length() > 0) {
				chartInsets.bottom += fmAxis.getHeight();
			}

			if (yAxisLabel != null && yAxisLabel.length() > 0) {
				chartInsets.left += 5 + fmAxis.getHeight();
			}

			if (!xLabels.isEmpty()) {
				chartInsets.bottom += 5 + xTickSize + fmLabel.getHeight();
			}

			if (!yLabels.isEmpty()) {
				Iterator i = yLabels.entrySet().iterator();
				double maxwidth = 0;

				while (i.hasNext()) {
					Map.Entry entry = (Map.Entry) i.next();
					double y = ((Double) entry.getKey()).doubleValue();

					if (y < viewport.getMinY()) {
						continue;
					}

					if (y > viewport.getMaxY()) {
						break;
					}

					String label = entry.getValue().toString();
					Rectangle2D r = fmLabel.getStringBounds(label, getGraphics());

					if (r.getWidth() > maxwidth) {
						maxwidth = r.getWidth();
					}
				}

				chartInsets.left += 5 + yTickSize + Math.ceil(maxwidth);
			}

			Iterator i = xLabels.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry entry = (Map.Entry) i.next();
				double x = ((Double) entry.getKey()).doubleValue();

				if (x < viewport.getMinX() || x > viewport.getMaxX()) {
					continue;
				}

				String label = entry.getValue().toString();
				Rectangle2D r = getXLabelBounds(g, width, height, x, label);

				if (r.getMinX() < xmin) {
					xmin = r.getMinX();
				}

				if (r.getMaxX() > xmax) {
					xmax = r.getMaxX();
				}
			}

			i = yLabels.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry entry = (Map.Entry) i.next();
				double y = ((Double) entry.getKey()).doubleValue();

				if (y < viewport.getMinY() || y > viewport.getMaxY()) {
					continue;
				}

				String label = entry.getValue().toString();
				Rectangle2D r = getYLabelBounds(g, width, height, y, label);

				if (r.getMinY() > ymin) {
					ymin = r.getMinY();
				}

				if (r.getMaxY() < ymax) {
					ymax = r.getMaxY();
				}
			}

			if (xmin != Double.POSITIVE_INFINITY && xmin < 0) {
				chartInsets.left -= (int) Math.floor(xmin);
			}

			if (xmax != Double.NEGATIVE_INFINITY && xmax > width) {
				chartInsets.right += (int) Math.floor(xmax - width + chartInsets.right);
			}

			if (ymin != Double.NEGATIVE_INFINITY && ymin < chartInsets.top) {
				chartInsets.top -= (int) Math.floor(ymin);
			}

			if (ymax != Double.POSITIVE_INFINITY && ymax > height - chartInsets.bottom) {
				chartInsets.bottom += (int) Math.floor(ymax - height + chartInsets.bottom);
			}

			dirtyInsets = false;
		}

		return chartInsets;
	}

	public Rectangle2D getSelectable() {
		return new Rectangle(getBorderInsets().left + getChartInsets().left, getBorderInsets().top + getChartInsets().top, getWidth() - getBorderInsets().left
				- getBorderInsets().right - getChartInsets().left - getChartInsets().right, getHeight() - getBorderInsets().top - getBorderInsets().bottom
				- getChartInsets().top - getChartInsets().bottom);
	}

	private Rectangle2D getXLabelBounds(Graphics2D g, int width, int height, double x, String label) {
		FontMetrics fmLabel = g.getFontMetrics(labelFont);

		x = (x - viewport.getMinX()) / viewport.getWidth();
		x *= width - chartInsets.left - chartInsets.right;
		x += chartInsets.left;

		Rectangle2D r = fmLabel.getStringBounds(label, g);
		r.setFrameFromCenter(x, height - chartInsets.bottom + xTickSize + r.getHeight() / 2.0, x - r.getWidth() / 2.0, height - chartInsets.bottom + xTickSize);

		return r;
	}

	private Rectangle2D getYLabelBounds(Graphics2D g, int width, int height, double y, String label) {
		FontMetrics fmLabel = g.getFontMetrics(labelFont);

		y = getViewport().getMaxY() - y;
		y /= viewport.getHeight();
		y *= height - chartInsets.top - chartInsets.bottom;
		y += chartInsets.top;

		Rectangle2D r = fmLabel.getStringBounds(label, g);
		r.setFrameFromCenter(chartInsets.left - 5 - yTickSize - r.getWidth() / 2.0, y, chartInsets.left - 5 - yTickSize, y - r.getHeight() / 2.0);

		return r;
	}

	public abstract void drawContents(Graphics2D g, int width, int height);

	private void draw(Graphics2D g, int width, int height) {
		FontMetrics fmAxis = g.getFontMetrics(axisFont);
		Font oldFont = g.getFont();

		g.drawLine(getChartInsets().left, getChartInsets().top, getChartInsets().left, height - getChartInsets().bottom);

		g.drawLine(getChartInsets().left, height - getChartInsets().bottom, width - getChartInsets().right, height - getChartInsets().bottom);

		g.setFont(labelFont);

		double last = Double.NEGATIVE_INFINITY;
		Iterator i = xLabels.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			double x = ((Double) entry.getKey()).doubleValue();

			if (x < viewport.getMinX() || x > viewport.getMaxX()) {
				continue;
			}

			String label = entry.getValue().toString();
			Rectangle2D r = getXLabelBounds(g, width, height, x, label);

			x = ((x - viewport.getMinX()) / viewport.getWidth()) * (width - getChartInsets().left - getChartInsets().right) + getChartInsets().left;

			g.drawLine((int) Math.round(x), height - getChartInsets().bottom, (int) Math.round(x), height - getChartInsets().bottom + xTickSize);

			if (r.getMinX() - last > 5) {
				g.drawString(label, (float) r.getMinX(), (float) r.getMaxY());
				last = r.getMaxX();
			}
		}

		last = Double.POSITIVE_INFINITY;
		i = yLabels.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			double y = ((Double) entry.getKey()).doubleValue();

			if (y < viewport.getMinY() || y > viewport.getMaxY()) {
				continue;
			}

			String label = entry.getValue().toString();
			Rectangle2D r = getYLabelBounds(g, width, height, y, label);

			y = ((viewport.getMaxY() - y) / viewport.getHeight()) * (height - getChartInsets().top - getChartInsets().bottom) + getChartInsets().top;

			g.drawLine(getChartInsets().left, (int) Math.round(y), getChartInsets().left - yTickSize, (int) Math.round(y));

			if (last - r.getMaxY() > 5) {
				g.drawString(label, (float) r.getMinX(), (float) r.getMaxY());
				last = r.getMinY();
			}
		}

		g.setFont(axisFont);

		if (xAxisLabel != null && xAxisLabel.length() > 0) {
			g.drawString(
					xAxisLabel,
					(float) (getChartInsets().left + (width - getChartInsets().left - getChartInsets().right - fmAxis.getStringBounds(xAxisLabel, g).getWidth()) / 2.0),
					height - fmAxis.getDescent());
		}

		if (yAxisLabel != null && yAxisLabel.length() > 0) {
			AffineTransform oldXForm = g.getTransform();
			g.translate(fmAxis.getHeight() - fmAxis.getLeading() - fmAxis.getDescent(), getChartInsets().top
					+ (height - getChartInsets().top - getChartInsets().bottom + fmAxis.getStringBounds(yAxisLabel, g).getWidth()) / 2.0);
			g.rotate(-Math.PI / 2.0);
			g.drawString(yAxisLabel, 0, 0);
			g.setTransform(oldXForm);
		}

		int width2 = width - getChartInsets().left - getChartInsets().right;
		int height2 = height - getChartInsets().top - getChartInsets().bottom;

		g.setFont(oldFont);

		AffineTransform oldXform = g.getTransform();
		g.translate(getChartInsets().left, height - getChartInsets().bottom);
		g.scale(1, -1);
		g.setClip(g.getClip().getBounds().intersection(new Rectangle(1, 0, width2, height2)));

		drawContents(g, width2, height2);

		g.setTransform(oldXform);
	}
}
