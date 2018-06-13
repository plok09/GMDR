package org.epistasis;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Several utility functions for epistasis.org programs.
 */
public final class Utility {
	/**
	 * Prevent the Utility class from being instantiated.
	 */
	private Utility() {
	}

	/**
	 * Compute the one-tailed sign test p-value for a given n with c elements
	 * greater than the mean.
	 * 
	 * @param n
	 *            Total number of elements
	 * @param c
	 *            Number of elements greater than the mean
	 * @return P-value for sign test
	 */
	public static double computeOneTailedSignTest(int n, int c) {
		return signTestSummation(n, c, n);
	}

	/**
	 * Compute the two-tailed sign test p-value for a given n with c elements
	 * greater than the mean.
	 * 
	 * @param n
	 *            Total number of elements
	 * @param c
	 *            Number of elements greater than the mean
	 * @return P-value for sign test
	 */
	public static double computeTwoTailedSignTest(int n, int c) {
		int n_2 = n / 2;

		if (c > n_2) {
			return 2.0 * signTestSummation(n, c, n);
		} else if (c < n_2) {
			return 2.0 * signTestSummation(n, 0, c);
		} else {
			return 1;
		}
	}

	/**
	 * Perform a specific summation used to compute the sign test.
	 * 
	 * @param n
	 *            Total number of samples
	 * @param min
	 *            Lower limit for the summation
	 * @param max
	 *            Upper limit of the summation
	 * @return Value of summation
	 */
	private static double signTestSummation(int n, int min, int max) {
		double sum = 0.0;
		double half_tothe_n = Math.pow(0.5, n);

		for (int k = min; k <= max; ++k) {
			sum += combinations(BigInteger.valueOf(n), BigInteger.valueOf(k)).doubleValue() * half_tothe_n;
		}

		return sum;
	}

	/**
	 * Compute the power mean of an array of doubles for a given power (p). Many
	 * means are special cases of the power mean: Harmonic Mean => p = -1,
	 * Geometric Mean => p = 0, Arithmetic Mean => p = 1, Root-Mean-Square => p
	 * = 2.
	 * 
	 * @param p
	 *            Power of the mean
	 * @param x
	 *            Array of doubles for which to compute the power mean
	 * @return Power mean of input values
	 */
	public static double powerMean(double p, double[] x) {
		double sum = 0;

		for (int i = 0; i < x.length; ++i) {
			sum += Math.pow(x[i], p);
		}

		return Math.pow(sum / x.length, 1.0 / p);
	}

	/**
	 * Compute the number of combinations possible when choosing r from n.
	 * 
	 * @param n
	 *            The total number of items
	 * @param r
	 *            The number of items to choose
	 * @return Number of possible combinations
	 */
	public static long combinations(long n, long r) {
		if (r > n / 2) {
			r = n - r;
		}

		return product(BigInteger.valueOf(n - r + 1), BigInteger.valueOf(n)).divide(factorial(BigInteger.valueOf(r))).longValue();
	}

	/**
	 * Compute the number of combinations possible when choosing r from n.
	 * 
	 * @param n
	 *            The total number of items
	 * @param r
	 *            The number of items to choose
	 * @return Number of possible combinations
	 */
	public static BigInteger combinations(BigInteger n, BigInteger r) {
		if (r.compareTo(n.shiftRight(1)) < 0) {
			r = n.subtract(r);
		}

		return product(n.subtract(r).add(BigInteger.ONE), n).divide(factorial(r));
	}

	/**
	 * Compute the factorial of a long integer.
	 * 
	 * @param n
	 *            Number of which to compute the factorial
	 * @return Factorial of the parameter
	 */
	public static long factorial(long n) {
		return product(2, n);
	}

	/**
	 * Compute the factorial of a long integer.
	 * 
	 * @param n
	 *            Number of which to compute the factorial
	 * @return Factorial of the parameter
	 */
	public static BigInteger factorial(BigInteger n) {
		return product(BigInteger.valueOf(2), n);
	}

	/**
	 * Compute the product of all integers in the range [min,max].
	 * 
	 * @param min
	 *            First number to include in product
	 * @param max
	 *            Last number to include in product
	 * @return Product of all integers in range.
	 */
	public static long product(long min, long max) {
		long ret = 1;

		for (long i = min; i <= max; ++i) {
			ret *= i;
		}

		return ret;
	}

	/**
	 * Compute the product of all integers in the range [min,max].
	 * 
	 * @param min
	 *            First number to include in product
	 * @param max
	 *            Last number to include in product
	 * @return Product of all integers in range.
	 */
	public static BigInteger product(BigInteger min, BigInteger max) {
		BigInteger ret = BigInteger.ONE;

		for (BigInteger i = min; i.compareTo(max) <= 0; i = i.add(BigInteger.ONE)) {
			ret = ret.multiply(i);
		}

		return ret;
	}

	public static double computeChiSquared(double[][] table) {
		int rows = table.length;
		int cols = rows > 0 ? table[0].length : 0;

		double[] rtotal = new double[rows];
		double[] ctotal = new double[cols];
		double total = 0;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				rtotal[i] += table[i][j];
				ctotal[j] += table[i][j];
			}

			total += rtotal[i];
		}

		double chisq = 0;

		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < cols; ++j) {
				double expected = ctotal[j] * rtotal[i] / total;

				double diff = table[i][j] - expected;
				chisq += diff * diff / expected;
			}
		}

		return chisq;
	}

	/**
	 * Returns value of kappa statistic if class is nominal. This was adapted
	 * from Weka 3.4.4.
	 * 
	 * @param confusion
	 *            A square matrix of doubles with actual classes as the row
	 *            indices and predicted classes as the column indices.
	 * 
	 * @return the value of the kappa statistic
	 */
	public static double computeKappaStatistic(double[][] confusion) {
		double[] sumRows = new double[confusion.length];
		double[] sumColumns = new double[confusion.length];
		double sumOfWeights = 0;

		for (int i = 0; i < confusion.length; i++) {
			for (int j = 0; j < confusion.length; j++) {
				sumRows[i] += confusion[i][j];
				sumColumns[j] += confusion[i][j];
				sumOfWeights += confusion[i][j];
			}
		}

		double correct = 0, chanceAgreement = 0;

		for (int i = 0; i < confusion.length; i++) {
			chanceAgreement += (sumRows[i] * sumColumns[i]);
			correct += confusion[i][i];
		}

		chanceAgreement /= (sumOfWeights * sumOfWeights);
		correct /= sumOfWeights;

		if (chanceAgreement < 1) {
			return (correct - chanceAgreement) / (1 - chanceAgreement);
		} else {
			return 1;
		}
	}

	public static int compareRanges(Iterator i, Iterator j) {
		while (i.hasNext() && j.hasNext()) {
			Comparable a = (Comparable) i.next();
			Comparable b = (Comparable) j.next();
			int ret = a.compareTo(b);

			if (ret != 0) {
				return ret;
			}
		}

		if (i.hasNext()) {
			return 1;
		}

		if (j.hasNext()) {
			return -1;
		}

		return 0;
	}

	public static double pchisq(double chisq, int df) {
		if (chisq == 0) {
			return 1;
		}

		return 1 - APStat.gammad(chisq / 2.0, df / 2.0);
	}

	public static int maxIndex(List list) {
		return maxIndex(list, null);
	}

	public static int maxIndex(List list, Comparator c) {
		int maxIndex = -1;
		Object maxObj = null;

		int i = 0;

		for (Iterator iter = list.iterator(); iter.hasNext(); ++i) {
			Object o = iter.next();

			if (maxObj == null) {
				maxIndex = i;
				maxObj = o;
				continue;
			}

			if (c == null) {
				if (((Comparable) o).compareTo(maxObj) > 0) {
					maxIndex = i;
					maxObj = o;
				}
			} else {
				if (c.compare(o, maxObj) > 0) {
					maxIndex = i;
					maxObj = o;
				}
			}
		}

		return maxIndex;
	}

	public static List lowestN(List list, int n) {
		return lowestN(list, n, null);
	}

	public static List lowestN(List list, int n, Comparator c) {
		if (list.size() <= n) {
			return new ArrayList(list);
		}

		List lowest = new ArrayList(n);
		int[] indices = new int[n];
		int maxindex = 0;
		int i = 0;

		for (Iterator iter = list.iterator(); iter.hasNext(); ++i) {
			Object o = iter.next();

			if (lowest.size() < n) {
				indices[maxindex] = i;
				lowest.add(o);

				if (lowest.size() == n) {
					maxindex = maxIndex(lowest, c);
				} else {
					maxindex++;
				}

				continue;
			}

			Object max = lowest.get(maxindex);

			if (c == null) {
				if (((Comparable) o).compareTo(max) < 0) {
					indices[maxindex] = i;
					lowest.set(maxindex, o);
					maxindex = maxIndex(lowest, c);
				}
			} else {
				if (c.compare(o, max) < 0) {
					indices[maxindex] = i;
					lowest.set(maxindex, o);
					maxindex = maxIndex(lowest, c);
				}
			}
		}

		return lowest;
	}
}
