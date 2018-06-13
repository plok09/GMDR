package org.epistasis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DummyRegressionCalculator {
	private RunningTotal total = new RunningTotal();
	private RunningTotal[] categories;
	private List[] values;
	private Double sse = new Double(0);
	private Double ssyy = new Double(0);
	private double[] coeff = null;

	public DummyRegressionCalculator(int categories) {
		this.categories = new RunningTotal[categories];
		values = new List[categories];

		for (int i = 0; i < this.categories.length; ++i) {
			this.categories[i] = new RunningTotal();
			values[i] = new ArrayList();
		}
	}

	public void add(int category, double value) {
		sse = ssyy = null;
		total.add(value);
		categories[category].add(value);
		values[category].add(new Double(value));
	}

	public double getMean(int category) {
		return categories[category].getMean();
	}

	public double getMeanY() {
		return total.getMean();
	}

	public double getCoeff(int category) {
		if (coeff != null) {
			return coeff[category];
		}

		if (category == 0) {
			return getMean(0);
		}

		return getMean(category) - getMean(0);
	}

	public double[] computeCoeffArray() {
		double[] coeff = new double[categories.length];

		coeff[0] = getMean(0);

		for (int i = 1; i < coeff.length; ++i) {
			coeff[i] = getMean(i) - coeff[0];
		}

		return coeff;
	}

	public void setCoeffArray(double[] coeff) {
		this.coeff = coeff;
		sse = ssyy = null;
	}

	public double getSSE() {
		computeSumSquares();
		return sse.doubleValue();
	}

	public double getSSyy() {
		computeSumSquares();
		return ssyy.doubleValue();
	}

	public double getR2() {
		return 1.0 - getSSE() / getSSyy();
	}

	public void clear() {
		for (int i = 0; i < categories.length; ++i) {
			categories[i].clear();
			values[i].clear();
			coeff = null;
			sse = ssyy = new Double(0);
		}
	}

	private void computeSumSquares() {
		if (sse != null) {
			return;
		}

		double sse = 0;
		double ssyy = 0;

		for (int i = 0; i < values.length; ++i) {
			double y = i == 0 ? getCoeff(i) : (getCoeff(0) + getCoeff(i));
			Iterator j = values[i].iterator();

			while (j.hasNext()) {
				double value = ((Double) j.next()).doubleValue();
				double diffsse = value - y;
				double diffssyy = value - getMeanY();

				sse += diffsse * diffsse;
				ssyy += diffssyy * diffssyy;
			}
		}

		this.sse = new Double(sse);
		this.ssyy = new Double(ssyy);
	}
}
