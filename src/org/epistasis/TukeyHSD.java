package org.epistasis;

public class TukeyHSD extends MultipleComparisonTest {
	private double alpha;

	public TukeyHSD(double alpha) {
		this.alpha = alpha;
	}

	public double compute(int i, int j) {
		double[] n = new double[2];
		Group a = (Group) get(i);
		Group b = (Group) get(j);
		n[0] = a.getCount();
		n[1] = b.getCount();

		return (a.getMean() - b.getMean()) / Math.sqrt(getMeanSquaredError() / Utility.powerMean(-1, n));
	}

	public double getCriticalValue() {
		return APStat.qtrng(1 - alpha, getTotalCount() - size(), size());
	}

	public Object clone() {
		TukeyHSD hsd = (TukeyHSD) super.clone();

		hsd.alpha = alpha;

		return hsd;
	}
}
