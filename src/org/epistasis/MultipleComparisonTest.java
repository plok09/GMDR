package org.epistasis;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class MultipleComparisonTest extends AbstractList implements Cloneable {
	private ArrayList groups = new ArrayList();
	private Double sse;
	private Double mse;

	public static interface Group {
		public double getMean();

		public int getCount();
	}

	public Object set(int index, Object element) {
		sse = mse = null;
		return groups.set(index, (Group) element);
	}

	public void add(int index, Object element) {
		sse = mse = null;
		groups.add(index, (Group) element);
	}

	public Object remove(int index) {
		sse = mse = null;
		return groups.remove(index);
	}

	public int size() {
		return groups.size();
	}

	public Object get(int index) {
		return groups.get(index);
	}

	public double getSumSquaredError() {
		if (sse == null) {
			double mean = 0.0;

			for (Iterator i = iterator(); i.hasNext();) {
				mean += ((Group) i.next()).getMean();
			}

			mean /= size();

			double sse = 0.0;

			for (Iterator i = iterator(); i.hasNext();) {
				sse += Math.pow(mean - ((Group) i.next()).getMean(), 2);
			}

			this.sse = new Double(sse);
		}

		return sse.doubleValue();
	}

	public double getMeanSquaredError() {
		if (mse == null) {
			mse = new Double(getSumSquaredError() / size());
		}

		return mse.doubleValue();
	}

	public int getTotalCount() {
		int totalCount = 0;

		for (Iterator i = groups.iterator(); i.hasNext();) {
			Group g = (Group) i.next();
			totalCount += g.getCount();
		}

		return totalCount;
	}

	public Object clone() {
		try {
			MultipleComparisonTest mct = (MultipleComparisonTest) super.clone();

			mct.groups = (ArrayList) groups.clone();
			mct.sse = sse;
			mct.mse = mse;

			return mct;
		} catch (CloneNotSupportedException ex) {
			return null;
		}
	}

	public abstract double compute(int i, int j);

	public abstract double getCriticalValue();
}
