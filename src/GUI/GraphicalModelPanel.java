package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import org.epistasis.AttributeLabels;
import org.epistasis.combinatoric.AttributeValueList;
import org.epistasis.combinatoric.mdr.Model;
import org.jibble.epsgraphics.EpsGraphics2D;

/**
 * Swing component that draws a graphical chart depicting a given
 * <code>MDRModel</code>. Provides functionality for breaking up a model chart
 * into smaller-dimensional chunks, which may be paged through, and export of
 * graphics to EPS format, using the Jibble <code>EPSGraphics2D</code> class.
 */
public class GraphicalModelPanel extends JComponent {
	public GraphicalModelPanel() {
	}
	/** Model to draw. */
	private Model model = null;

	/** Color to use to fill in "affected" cells. */
	private Color affectedCellColor = new Color(0x80, 0x80, 0x80);

	/** Color to use to fill in "unaffected cells. */
	private Color unaffectedCellColor = new Color(0xBF, 0xBF, 0xBF);

	/** Color to use to fill in "unknown" cells. */
	private Color unknownCellColor = Color.WHITE;

	/** Color to use to fill in empty cells. */
	private Color emptyCellColor = Color.WHITE;

	/** Font to use when drawing counts inside cells. */
	private Font cellFont = new Font("Dialog", Font.PLAIN, 12);

	/** Font to use when drawing value labels. */
	private Font valueFont = new Font("Dialog", Font.PLAIN, 12);

	/** Font to use when drawing attribute labels. */
	private Font axisFont = new Font("Dialog", Font.PLAIN, 12);

	/** Size of cells, in pixels */
	private int cellSize = 100;

	/**
	 * Value of largest count inside a cell in the current model. This is used
	 * to scale the bars inside the cells.
	 */
	private int modelMax;

	private static final BufferedImage biImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

	private AttributeLabels labels;

	/**
	 * A <code>Vector of <code>Set</code>s of <code>Comparable</code>s that
	 * represent the values possible at each attribute.
	 */
	private ArrayList levels;
	private GraphicalModel gm;
	private ArrayList pages = new ArrayList();

	/** Maximum dimensionality to be shown on a single page. */
	private int maxDim = 3;

	/** Index of page currently showing. */
	private int page = 0;

	private List drawnVar = null;

	/**
	 * Get the number of pages that the current model fills, given the maximum
	 * dimension per page constraint.
	 * 
	 * @return Number of pages for current model
	 */
	public int getNumPages() {
		return pages.size();
	}

	/**
	 * Get the index of the page that is currently showing.
	 * 
	 * @return Index of current page
	 */
	public int getPage() {
		return page;
	}

	/**
	 * Show a given page of the current model.
	 * 
	 * @param page
	 *            Index of page to show
	 */
	public void setPage(int page) {
		if (page < 0 || page >= getNumPages()) {
			return;
		}

		this.page = page;

		gm = new GraphicalModel((Graphics2D) biImage.getGraphics(), drawnVar, (String[]) pages.get(page));

		getPageLabel(page);

		recalculateSize();
		repaint();
	}

	/**
	 * Generate a header label for a given page, indicating the values for each
	 * attribute that has been split.
	 * 
	 * @param page
	 *            Page for which to generate the label
	 * @return Header label for given page
	 */
	private String getPageLabel(int page) {
		String ret = null;

		if (getNumPages() > 1) {
			ret = "";
			String[] values = (String[]) pages.get(page);
			List variables = model.getAttributes();

			for (int i = values.length - 1; i >= 0; --i) {
				if (i != values.length - 1) {
					ret += ", ";
				}

				ret += (String) labels.get(((Integer) variables.get(i + maxDim)).intValue());
				ret += " = " + values[i];
			}
		}

		return ret;
	}

	/**
	 * Get the maximum number of dimensions per page that this panel will
	 * display.
	 * 
	 * @return Maximum dimension of pages
	 */
	public int getMaxDim() {
		return maxDim;
	}

	/**
	 * set the maximum number of dimensions per page that this panel will
	 * display.
	 * 
	 * @param maxDim
	 *            Maximum dimension of pages
	 */
	public void setMaxDim(int maxDim) {
		this.maxDim = maxDim;

		if (model == null) {
			return;
		}

		recalculatePages();
	}

	/**
	 * 
	 */
	private void recalculatePages() {
		pages.clear();
		drawnVar = null;

		if (model == null) {
			page = 0;
			return;
		}

		addPage(model.getAttributes(), null);
		setPage(0);
	}

	private void addPage(List variables, String[] constants) {
		if (maxDim > 0 && variables.size() > maxDim) {
			List newVar = variables.subList(0, variables.size() - 1);

			String[] newConst = new String[constants == null ? 1 : (constants.length + 1)];
			for (int i = 1; i < newConst.length; ++i) {
				newConst[i] = constants[i - 1];
			}

			Set values = (Set) levels.get(((Integer) variables.get(variables.size() - 1)).intValue());
			Iterator value = values.iterator();
			while (value.hasNext()) {
				newConst[0] = (String) value.next();
				addPage(newVar, (String[]) newConst.clone());
			}
		} else {
			if (drawnVar == null) {
				drawnVar = variables;
			}

			pages.add(constants);
		}
	}

	public AttributeLabels getLabels() {
		return labels;
	}

	public void setLabels(AttributeLabels labels) {
		this.labels = labels;
	}

	public List getLevels() {
		return levels;
	}

	public void setLevels(List levels) {
		this.levels = new ArrayList(levels);
	}

	public Model getModel() {
		return model;
	}

	/**
	 * Set the model to be displayed as a graphical chart.
	 * 
	 * @param model
	 *            The model to be drawn
	 */
	public void setModel(Model model) {
		this.model = model;

		analyzeModel();
		recalculatePages();
		recalculateSize();
		repaint();
	}

	/**
	 * Get the size in pixels of an individual cell.
	 * 
	 * @return Cell size in pixels
	 */
	public int getCellSize() {
		return cellSize;
	}

	/**
	 * Set the size in pixels of individual cells.
	 * 
	 * @param cellSize
	 *            Cell size in pixels
	 */
	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
		recalculateSize();
	}

	public Color getAffectedCellColor() {
		return affectedCellColor;
	}

	public void setAffectedCellColor(Color affectedCellColor) {
		this.affectedCellColor = affectedCellColor;
	}

	public Color getUnaffectedCellColor() {
		return unaffectedCellColor;
	}

	public void setUnaffectedCellColor(Color unaffectedCellColor) {
		this.unaffectedCellColor = unaffectedCellColor;
	}

	public Color getUnknownCellColor() {
		return unknownCellColor;
	}

	public void setUnknownCellColor(Color unknownCellColor) {
		this.unknownCellColor = unknownCellColor;
	}

	public Color getEmptyCellColor() {
		return emptyCellColor;
	}

	public void setEmptyCellColor(Color emptyCellColor) {
		this.emptyCellColor = emptyCellColor;
	}

	public Font getCellFont() {
		return cellFont;
	}

	public void setCellFont(Font cellFont) {
		this.cellFont = cellFont;
	}

	public Font getValueFont() {
		return valueFont;
	}

	public void setValueFont(Font valueFont) {
		this.valueFont = valueFont;
	}

	public Font getAxisFont() {
		return axisFont;
	}

	public void setAxisFont(Font axisFont) {
		this.axisFont = axisFont;
	}

	public void paint(Graphics g) {
		super.paint(g);

		g.clearRect(0, 0, getWidth(), getHeight());

		if (model == null || gm == null || labels == null || levels == null) {
			return;
		}

		String pageLabel = getPageLabel(page);

		if (pageLabel != null) {
			g.setFont(axisFont);
			g.translate(0, g.getFontMetrics().getHeight());
			g.drawString(pageLabel, 5, 0);
		}

		g.translate(5, 5);
		gm.draw((Graphics2D) g);
	}

	public String getPageEPS(int page) {
		if (page >= getNumPages()) {
			return null;
		}

		Graphics2D g = new EpsGraphics2D();
		GraphicalModel gm = new GraphicalModel(g, drawnVar, (String[]) pages.get(page));

		String pageLabel = getPageLabel(page);

		if (pageLabel != null) {
			g.setFont(axisFont);
			g.translate(0, g.getFontMetrics().getHeight());
			g.drawString(pageLabel, 5, 0);
		}

		gm.draw(g);

		return g.toString();
	}

	private void analyzeModel() {
		modelMax = 0;

		if (model == null) {
			gm = null;
			return;
		}

		Iterator i = model.values().iterator();
		while (i.hasNext()) {
			Model.Cell c = (Model.Cell) i.next();
			// cgb begin
			if (((int) (c.getAffectedScore() + 0.5)) > modelMax) {
				modelMax = ((int) (c.getAffectedScore() + 0.5));
			}
			if (((int) (c.getUnaffectedScore() + 0.5)) > modelMax) {
				modelMax = ((int) (c.getUnaffectedScore() + 0.5));
			}
			// cgb end 2006y/6m/15d
			// if (c.getAffected() > modelMax) {
			// modelMax = c.getAffected();
			// }
			// if (c.getUnaffected() > modelMax) {
			// modelMax = c.getUnaffected();
			// }
		}

		gm = new GraphicalModel((Graphics2D) biImage.getGraphics(), model.getAttributes());
	}

	private void recalculateSize() {
		if (model == null) {
			setPreferredSize(new Dimension(0, 0));
		} else {
			Dimension d = new Dimension(gm.getSize());
			String pageLabel = getPageLabel(page);

			if (pageLabel != null) {
				Rectangle r = getFontMetrics(axisFont).getStringBounds(pageLabel, biImage.getGraphics()).getBounds();
				d.height += r.height;
				if (d.width < r.width) {
					d.width = r.width;
				}
			}

			d.width += 10;
			d.height += 10;

			setPreferredSize(d);
		}

		revalidate();
	}

	public class GraphicalModel {
		public int getDimension() {
			return variables.size();
		}

		public int getLabelSpace() {
			return labelSpace;
		}

		public boolean isHorizontal() {
			return (getDimension() % 2) == 1;
		}

		public int getGap() {
			return ((getDimension() + 1) / 2 - 1) * cellSize / 2;
		}

		public Dimension getSize() {
			return size;
		}

		public Dimension getTrimSize() {
			return trimSize;
		}

		public boolean isLabeled() {
			if (getDimension() == 0) {
				return false;
			}

			if (isHorizontal()) {
				return firsty;
			}

			return firstx;
		}

		public void setLabelSpace(int labelSpace) {
			if (isHorizontal()) {
				size.width -= this.labelSpace;
				this.labelSpace = labelSpace;
				size.width += labelSpace;
			} else {
				size.height -= this.labelSpace;
				this.labelSpace = labelSpace;
				size.height += labelSpace;
			}
		}

		public Point getLabelCenter() {
			Point ret = new Point();
			ret.x = size.width - trimSize.width / 2;
			ret.y = size.height - trimSize.height / 2;
			return ret;
		}

		public void draw(Graphics2D g) {
			if (getDimension() == 0) {
				AttributeValueList gtype = new AttributeValueList();
				gtype.set(Arrays.asList(constants));
				Model.Cell c = (Model.Cell) model.get(gtype);
				if (c == null) {
					drawEmptyCell(g, loc);
				} else {
					// cgb begin
					// drawCell(g, loc, ((int) (c.getAffectedScore() + 0.5)),
					// ((int) (c.getUnaffectedScore() + 0.5)), c.getStatus());
					drawCell(g, loc, c.getAffectedScore(), c.getUnaffectedScore(), c.getStatus());
					// drawCell(g, loc, c.getAffected(), c.getUnaffected(),
					// c.getStatus());
					// cgb end
				}
			} else {
				Iterator child = children.iterator();
				while (child.hasNext()) {
					GraphicalModel m = (GraphicalModel) child.next();
					m.draw(g);
				}

				if (isLabeled()) {
					drawLabels(g);
				}
			}
		}

		private void drawLabels(Graphics2D g) {
			Set values = (Set) levels.get(((Integer) variables.get(variables.size() - 1)).intValue());
			String label = (String) labels.get(((Integer) variables.get(variables.size() - 1)).intValue());
			g.setFont(axisFont);
			Rectangle r = g.getFontMetrics().getStringBounds(label, g).getBounds();
			Point center = getLabelCenter();

			if (isHorizontal()) {
				g.drawString(label, loc.x + center.x - r.width / 2, loc.y + r.height);
				g.setFont(valueFont);
				int i = 0;
				Iterator value = values.iterator();
				while (value.hasNext()) {
					GraphicalModel m = (GraphicalModel) children.get(i++);
					center = m.getLabelCenter();
					label = (String) value.next();
					Rectangle r2 = g.getFontMetrics().getStringBounds(label, g).getBounds();
					g.drawString(label, m.loc.x + center.x - r2.width / 2, loc.y + 5 + r.height + r2.height);
				}
			} else {
				drawVerticalString(g, label, loc.x + r.height, loc.y + center.y + r.width / 2);
				g.setFont(valueFont);
				int i = 0;
				Iterator value = values.iterator();
				while (value.hasNext()) {
					GraphicalModel m = (GraphicalModel) children.get(i++);
					center = m.getLabelCenter();
					label = (String) value.next();
					Rectangle r2 = g.getFontMetrics().getStringBounds(label, g).getBounds();
					g.drawString(label, loc.x + getLabelSpace() - 5 - r2.width, m.loc.y + center.y + r2.height / 2);
				}
			}
		}

		public GraphicalModel(Graphics2D g, List variables) {
			this(g, new Point(), true, true, variables, null);
		}

		public GraphicalModel(Graphics2D g, List variables, String[] constants) {
			this(g, new Point(), true, true, variables, constants);
		}

		private GraphicalModel(Graphics2D g, Point loc, boolean firstx, boolean firsty, List variables, String[] constants) {
			this.variables = variables;
			this.constants = constants;
			this.loc = new Point(loc);
			this.firstx = firstx;
			this.firsty = firsty;

			if (getDimension() == 0) {
				trimSize.width = trimSize.height = size.width = size.height = cellSize;
				return;
			}

			List newVar = variables.subList(0, variables.size() - 1);

			String[] newConst = new String[constants == null ? 1 : (constants.length + 1)];
			for (int i = 1; i < newConst.length; ++i) {
				newConst[i] = constants[i - 1];
			}

			Set values = (Set) levels.get(((Integer) variables.get(variables.size() - 1)).intValue());
			Iterator value = values.iterator();

			calcLabelSpace(g);

			children = new ArrayList(values.size());
			Point p = new Point(loc);

			if (isHorizontal()) {
				p.y += labelSpace;
			} else {
				p.x += labelSpace;
			}

			boolean fx = firstx;
			boolean fy = firsty;

			while (value.hasNext()) {
				newConst[0] = (String) value.next();
				GraphicalModel m = new GraphicalModel(g, p, fx, fy, newVar, (String[]) newConst.clone());

				children.add(m);

				if (isHorizontal()) {
					p.x += m.getSize().width + getGap();
					fx = false;
				} else {
					p.y += m.getSize().height + getGap();
					fy = false;
				}
			}

			Iterator child = children.iterator();
			int maxLabelSpace = 0;
			while (child.hasNext()) {
				GraphicalModel m = (GraphicalModel) child.next();
				if (m.getLabelSpace() > maxLabelSpace) {
					maxLabelSpace = m.getLabelSpace();
				}
			}

			child = children.iterator();
			while (child.hasNext()) {
				GraphicalModel m = (GraphicalModel) child.next();
				m.setLabelSpace(maxLabelSpace);
			}

			if (isHorizontal()) {
				GraphicalModel m;

				trimSize.width = size.width = (children.size() - 1) * getGap();
				child = children.iterator();
				while (child.hasNext()) {
					m = (GraphicalModel) child.next();
					size.width += m.getSize().width;
					trimSize.width += m.getTrimSize().width;
				}
				m = (GraphicalModel) children.get(0);
				trimSize.height = m.getTrimSize().height;
				size.height = m.getSize().height + labelSpace;
			} else {
				GraphicalModel m;

				trimSize.height = size.height = (children.size() - 1) * getGap();
				child = children.iterator();
				while (child.hasNext()) {
					m = (GraphicalModel) child.next();
					size.height += m.getSize().height;
					trimSize.height += m.getTrimSize().height;
				}
				m = (GraphicalModel) children.get(0);
				trimSize.width = m.getTrimSize().width;
				size.width = m.getSize().width + labelSpace;
			}
		}

		/*
		 * private void drawCell(Graphics2D g, Point p, int affected, int
		 * unaffected, int status) { switch (status) { case 1:
		 * g.setColor(affectedCellColor); break; case 0:
		 * g.setColor(unaffectedCellColor); break; case -1:
		 * g.setColor(unknownCellColor); break; }
		 * 
		 * g.fillRect(p.x, p.y, cellSize, cellSize);
		 * g.setColor(getForeground()); String s = Integer.toString(affected);
		 * g.setFont(cellFont); Rectangle2D bounds =
		 * g.getFontMetrics().getStringBounds(s, g); int height = (int)
		 * ((double) Math.min(affected, modelMax) / (double) modelMax (cellSize
		 * - 5 - bounds.getHeight())); g.fillRect(p.x + (int) (cellSize / 6.0),
		 * p.y + cellSize - height, (int) (cellSize / 4.0), height);
		 * g.drawString(s, p.x + (int) (cellSize / 6.0 + (((cellSize / 4.0) -
		 * bounds.getWidth()) / 2.0)), p.y + cellSize - 5 - height); s =
		 * Integer.toString(unaffected); bounds =
		 * g.getFontMetrics().getStringBounds(s, g); height = (int) ((double)
		 * Math.min(unaffected, modelMax) / (double) modelMax (cellSize - 5 -
		 * bounds.getHeight())); g.fillRect(p.x + (int) (cellSize / 3.0 +
		 * cellSize / 4.0), p.y + cellSize - height, (int) (cellSize / 4.0),
		 * height); g.drawString(s, p.x + (int) (cellSize / 3.0 + cellSize / 4.0
		 * + (((cellSize / 4.0) - bounds.getWidth()) / 2.0)), p.y + cellSize - 5
		 * - height); g.drawRect(p.x, p.y, cellSize, cellSize); }
		 */

		// add by leiyan begin 2007-01-09
		private void drawCell(Graphics2D g, Point p, double affected, double unaffected, int status) {
			switch (status) {
			case 1:
				g.setColor(affectedCellColor);
				break;
			case 0:
				g.setColor(unaffectedCellColor);
				break;
			case -1:
				g.setColor(unknownCellColor);
				break;
			}

			g.fillRect(p.x, p.y, cellSize, cellSize);
			g.setColor(getForeground());
			String s = new DecimalFormat("0.0").format(affected);
			g.setFont(cellFont);
			Rectangle2D bounds = g.getFontMetrics().getStringBounds(s, g);
			int height = (int) (Math.min(affected, modelMax) / (double) modelMax * (cellSize - 5 - bounds.getHeight()));
			g.fillRect(p.x + (int) (cellSize / 6.0), p.y + cellSize - height, (int) (cellSize / 4.0), height);
			g.drawString(s, p.x + (int) (cellSize / 6.0 + (((cellSize / 4.0) - bounds.getWidth()) / 2.0)), p.y + cellSize - 5 - height);
			if (unaffected == 0.0) {
				s = "0.0";
			} else {
				s = "-" + new DecimalFormat("0.0").format(unaffected);
			}
			bounds = g.getFontMetrics().getStringBounds(s, g);
			height = (int) (Math.min(unaffected, modelMax) / (double) modelMax * (cellSize - 5 - bounds.getHeight()));
			g.fillRect(p.x + (int) (cellSize / 3.0 + cellSize / 4.0), p.y + cellSize - height, (int) (cellSize / 4.0), height);
			g.drawString(s, p.x + (int) (cellSize / 3.0 + cellSize / 4.0 + (((cellSize / 4.0) - bounds.getWidth()) / 2.0)), p.y + cellSize - 5 - height);
			g.drawRect(p.x, p.y, cellSize, cellSize);
		}

		// add by leiyan end 2007-01-09

		private void drawEmptyCell(Graphics2D g, Point p) {
			g.setColor(emptyCellColor);
			g.fillRect(p.x, p.y, cellSize, cellSize);
			g.setColor(getForeground());
			g.drawRect(p.x, p.y, cellSize, cellSize);
		}

		private void calcLabelSpace(Graphics2D g) {
			if (!isLabeled()) {
				labelSpace = 0;
				return;
			}

			if (isHorizontal()) {
				labelSpace = g.getFontMetrics(axisFont).getHeight() + g.getFontMetrics(valueFont).getHeight() + 10;
				return;
			}

			Set values = (Set) levels.get(((Integer) variables.get(variables.size() - 1)).intValue());
			int maxWidth = 0;
			Iterator value = values.iterator();
			while (value.hasNext()) {
				String s = (String) value.next();
				int width = g.getFontMetrics(valueFont).getStringBounds(s, g).getBounds().width;
				if (width > maxWidth) {
					maxWidth = width;
				}
			}

			labelSpace = g.getFontMetrics(axisFont).getHeight() + maxWidth + 10;
		}

		private void drawVerticalString(Graphics2D g, String s, int x, int y) {
			g.translate(x, y);
			g.rotate(-Math.PI / 2);
			g.drawString(s, 0, 0);
			g.rotate(Math.PI / 2);
			g.translate(-x, -y);
		}

		private Dimension size = new Dimension();
		private Dimension trimSize = new Dimension();
		private Point loc = null;
		private int labelSpace = 0;
		private List variables = null;
		private String[] constants = null;
		private ArrayList children = null;
		private boolean firstx;
		private boolean firsty;
	}
}
