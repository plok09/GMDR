package org.epistasis.combinatoric.mdr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.epistasis.AbstractDataset;
import org.epistasis.combinatoric.AbstractCombinatoricModel;
import org.epistasis.combinatoric.AttributeCombination;
import org.epistasis.combinatoric.AttributeValueList;
import org.epistasis.combinatoric.CombinatoricResult;

import DataManage.Dataset;

public class Model extends AbstractCombinatoricModel {
	private double threshold;
	private int tieValue;

	public class Cell {
		private int affected;
		private int unaffected;
		private double ratio;
		private int status;

		// cgb begin
		private double affectedscore;
		private double unaffectedscore;

		public Cell(int affected, int unaffected, double affectedscore, double unaffectedscore) {
			this.affected = affected;
			this.unaffected = unaffected;
			this.affectedscore = affectedscore;
			this.unaffectedscore = unaffectedscore;

			ratio = (double) (affectedscore + unaffectedscore);

			if (ratio >= 0.0) {
				status = 1;
			} else if (ratio < 0.0) {
				status = 0;
			} else {
				status = tieValue;
			}
		}

		public double getAffectedScore() {
			return affectedscore;
		}

		public double getUnaffectedScore() {
			return unaffectedscore * (-1.0);
		}

		public String toString() {
			return Integer.toString(affected) + " " + Integer.toString(unaffected) + " " + Double.toString(affectedscore) + " "
					+ Double.toString(unaffectedscore);
		}

		// cgb end 2006y/6m/8d

		public Cell(int affected, int unaffected) {
			this.affected = affected;
			this.unaffected = unaffected;

			ratio = (double) affected / unaffected;

			if (ratio > threshold) {
				status = 1;
			} else if (ratio < threshold) {
				status = 0;
			} else {
				status = tieValue;
			}
		}

		public int getAffected() {
			return affected;
		}

		public int getUnaffected() {
			return unaffected;
		}

		public double getRatio() {
			return ratio;
		}

		public int getStatus() {
			return status;
		}
		/*
		 * public String toString() { return Integer.toString(affected) + " " +
		 * Integer.toString(unaffected); }
		 */
	}

	protected class TrainTarget implements MergeSearchTarget {
		// cgb begin
		public void run(Map model, AttributeValueList values, List instances) {
			int affected = 0;
			int unaffected = 0;
			double affectedscore = 0.0;
			double unaffectedscore = 0.0;

			for (Iterator i = instances.iterator(); i.hasNext();) {
				AbstractDataset.Instance instance = (AbstractDataset.Instance) i.next();

				double instscore = instance.getInstScore();

				if (instscore >= 0.0) {
					affected++;
					affectedscore += instscore;
				} else {
					unaffected++;
					unaffectedscore += instscore;
				}
			}

			Cell c = new Cell(affected, unaffected, affectedscore, unaffectedscore);

			((Result) trainResult).addInstances(instances, c.getStatus());

			model.put(values, c);
		}
		/*
		 * public void run(Map model, AttributeValueList values, List instances)
		 * { int affected = 0; int unaffected = 0;
		 * 
		 * for (Iterator i = instances.iterator(); i.hasNext(); ) {
		 * AbstractDataset.Instance instance = (AbstractDataset. Instance)
		 * i.next();
		 * 
		 * int status = ((Integer) instance.getStatus()).intValue();
		 * 
		 * if (status == 1) { affected++; } else { unaffected++; } }
		 * 
		 * Cell c = new Cell(affected, unaffected);
		 * 
		 * ((Result) trainResult).addInstances(instances, c.getStatus());
		 * 
		 * model.put(values, c); }
		 */
	}

	protected class TestTarget implements MergeSearchTarget {
		public void run(Map model, AttributeValueList values, List instances) {
			Cell c = (Cell) model.get(values);

			if (c != null) {
				((Result) testResult).addInstances(instances, c.getStatus());
			} else {
				((Result) testResult).addInstances(instances, -1);
			}
		}
	}

	public Model(AttributeCombination attributes, double threshold, int tieValue) {
		super(attributes);
		this.threshold = threshold;
		this.tieValue = tieValue;
	}

	public Model(AttributeCombination attributes, Result train, Result test) {
		super(attributes);
		this.trainResult = train;
		this.testResult = test;
		this.threshold = 0;
	}

	public Model(AttributeCombination attributes, double threshold, Result train, Result test) {
		super(attributes);
		this.trainResult = train;
		this.testResult = test;
		this.threshold = threshold;
	}

	public CombinatoricResult train(List instances) {
		if (threshold == 0) {
			throw new UnsupportedOperationException();
		}

		trainResult = new Result();
		getModel().clear();
		mergeSearch(instances, new TrainTarget());
		return trainResult;
	}

	public List constructAttribute(List instances) {
		ArrayList attribute = new ArrayList(instances.size());
		AttributeValueList cellidx = new AttributeValueList();

		for (Iterator i = instances.iterator(); i.hasNext();) {
			Dataset.Instance inst = (Dataset.Instance) i.next();

			for (Iterator j = getAttributes().iterator(); j.hasNext();) {
				cellidx.push((Comparable) inst.get(((Integer) j.next()).intValue()));
			}

			Cell c = (Cell) getModel().get(cellidx);

			if (c == null) {
				attribute.add("Unknown");
			} else {
				attribute.add("G" + c.getStatus());
			}

			cellidx.clear();
		}

		return attribute;
	}

	public CombinatoricResult test(List instances) {
		if (threshold == 0) {
			throw new UnsupportedOperationException();
		}

		testResult = new Result();
		mergeSearch(instances, new TestTarget());
		return testResult;
	}

	// cgb begin
	public void addCell(AttributeValueList values, int affected, int unaffected, double affectedscore, double unaffectedscore) {
		Cell c = new Cell(affected, unaffected, affectedscore, unaffectedscore);
		getModel().put(values, c);
	}

	// cgb end 2006y/6m/8d
	public void addCell(AttributeValueList values, int affected, int unaffected) {
		Cell c = new Cell(affected, unaffected);
		getModel().put(values, c);
	}

	public Object clone() {
		Model m = (Model) super.clone();

		return m;
	}
}
