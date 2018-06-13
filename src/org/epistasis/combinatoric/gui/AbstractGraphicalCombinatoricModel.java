package org.epistasis.combinatoric.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import org.epistasis.AbstractDataset;
import org.epistasis.NominalAttributeIndex;
import org.epistasis.combinatoric.AbstractCombinatoricModel;
import org.epistasis.combinatoric.AttributeValueList;

public abstract class AbstractGraphicalCombinatoricModel extends JComponent {
	private Dimension cellSize = new Dimension(100, 100);
	private AbstractDataset data;
	private AbstractCombinatoricModel model;
	private ModelStructure structure;
	private Font fntAxis;
	private Font fntValue;
	private FontMetrics fmAxis;
	private FontMetrics fmValue;
	private NominalAttributeIndex[] attributeIndices;

	public abstract void drawCell(Graphics2D g, Rectangle r, Object cell);

	public AbstractCombinatoricModel getModel() {
		return model;
	}

	public void setModel(AbstractCombinatoricModel model) {
		this.model = model;

		structure = model == null ? null : new ModelStructure(0, new AttributeValueList(), true, true);

		if (structure == null) {
			setPreferredSize(new Dimension());
		} else {
			setPreferredSize(structure.getSize());
		}
	}

	public void paint(Graphics g) {
		g.clearRect(0, 0, getSize().width, getSize().height);

		if (structure != null) {
			structure.draw((Graphics2D) g, new Point());
		}
	}

	public AbstractDataset getDataset() {
		return data;
	}

	public void setDataset(AbstractDataset data) {
		this.data = data;

		model = null;
		structure = null;

		if (data == null) {
			attributeIndices = null;
		} else {
			attributeIndices = new NominalAttributeIndex[data.getNumAttributes()];

			for (int i = 0; i < attributeIndices.length; ++i) {
				attributeIndices[i] = new NominalAttributeIndex(data.computeAttributeVector(i));
			}
		}
	}

	public Dimension getCellSize() {
		return cellSize;
	}

	public void setCellSize(Dimension cellSize) {
		this.cellSize.width = cellSize.width;
		this.cellSize.height = cellSize.height;

		structure.recalcSize();
	}

	private class ModelStructure {
		private boolean isLabeled;
		private int dimension;
		private Object cell;
		private List children;
		private Dimension size;
		private Insets insets;

		public int getDimension() {
			return dimension;
		}

		public boolean isHorizontal() {
			return getDimension() % 2 == 1;
		}

		public boolean isLabeled() {
			return isLabeled;
		}

		private int getGap() {
			return ((getDimension() + 1) / 2 - 1) * Math.min(cellSize.width, cellSize.height) / 2;
		}

		public Dimension getSize() {
			return size;
		}

		public Insets getInsets() {
			return insets;
		}

		public ModelStructure(int idxAttribute, AttributeValueList values, boolean isXLabel, boolean isYLabel) {
			this.dimension = model.getAttributes().size() - idxAttribute;
			this.isLabeled = dimension == 0 ? false : (isHorizontal() ? isYLabel : isXLabel);

			if (dimension == 0) {
				cell = model.get(values);
			} else {
				children = new ArrayList(attributeIndices[idxAttribute].size());

				boolean first = true;

				for (Iterator i = attributeIndices[idxAttribute].getValues().iterator(); i.hasNext(); first = false) {
					values.push((Comparable) i.next());

					boolean newXLabel = isXLabel;
					boolean newYLabel = isYLabel;

					if (isHorizontal()) {
						newYLabel = isYLabel && first;
					} else {
						newXLabel = isXLabel && first;
					}

					ModelStructure child = new ModelStructure(idxAttribute + 1, values, newXLabel, newYLabel);

					children.add(child);

					values.pop();
				}
			}

			recalcSize();
		}

		public void draw(Graphics2D g, Point p) {
			System.out.print(dimension);
			System.out.print('\t');
			System.out.print(isLabeled());
			System.out.print('\t');
			System.out.print(p);
			System.out.print('\t');
			System.out.println(insets);

			if (dimension == 0) {
				drawCell(g, new Rectangle(p, size), cell);
			} else {
				p = (Point) p.clone();
				p.translate(insets.left, insets.top);

				for (Iterator i = children.iterator(); i.hasNext();) {
					ModelStructure child = (ModelStructure) i.next();
					child.draw(g, p);

					if (isHorizontal()) {
						p.translate(child.getSize().width + getGap(), 0);
					} else {
						p.translate(0, child.getSize().height + getGap());
					}
				}
			}
		}

		public void recalcSize() {
			insets = new Insets(0, 0, 0, 0);

			if (dimension == 0) {
				size = cellSize;
				return;
			}

			size = new Dimension();

			if (isHorizontal() && isLabeled()) {
				size.height = insets.top = 50;
			} else if (isLabeled()) {
				size.width = insets.left = 50;
			}

			for (Iterator i = children.iterator(); i.hasNext();) {
				ModelStructure child = (ModelStructure) i.next();

				if (isHorizontal()) {
					size.height = child.getSize().height;
					size.width += child.getSize().width;
				} else {
					size.height += child.getSize().height;
					size.width = child.getSize().width;
				}
			}

			if (isHorizontal()) {
				size.width += (children.size() - 1) * getGap();
			} else {
				size.height += (children.size() - 1) * getGap();
			}
		}
	}
}
