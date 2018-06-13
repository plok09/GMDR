package org.epistasis;

public class RunningTotal {
	private double sum = 0;
	private Double mean = new Double(0);
	private int size = 0;

	public void add(double value) {
		sum += value;
		mean = null;
		size++;
	}

	public double getSum() {
		return sum;
	}

	public double getMean() {
		if (mean == null) {
			mean = new Double(sum / size);
		}

		return mean.doubleValue();
	}

	public int size() {
		return size;
	}

	public void clear() {
		sum = 0;
		mean = new Double(0);
		size = 0;
	}
}
