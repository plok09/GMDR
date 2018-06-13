package addon.imputation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PhenotypeImputation {
	private Random rand;
	private static String missing = ".";
	private int[] missingCount;
	private int total;
	private double[] sum;
	private double[] mean;
	private ArrayList<ArrayList<String>> variable = new ArrayList<ArrayList<String>>();

	public PhenotypeImputation(int len) {
		long seed = 2010;
		rand = new Random(seed);
		missingCount = new int[len];
		sum = new double[len];
		mean = new double[len];
		for (int i = 0; i < len; i++) {
			variable.add(new ArrayList<String>());
		}
	}

	public void addValues(String[] v) {
		for (int i = 0; i < v.length; i++) {
			ArrayList<String> p = variable.get(i);
			p.add(v[i]);
			if (v[i].compareTo(missing) == 0) {
				missingCount[i]++;
			} else {
				try {
					sum[i] += Double.parseDouble(v[i]);
				} catch (NumberFormatException ex) {
					System.err.println(ex.toString());
					System.err.println("Line " + total + " contains undefined value in the specified phenotype file.");
					System.exit(0);
				}
			}
		}
		total++;
	}

	public void ImplementImputation(int[] Idx) {
		for (int i = 0; i < Idx.length; i++) {
			mean[i] = sum[i] / (total - missingCount[Idx[i]]);
			ArrayList p = variable.get(Idx[i]);
			Collections.replaceAll(p, missing, Double.toString(mean[Idx[i]]));
		}
	}

	public void ImplementImputation(int pheIdx, boolean isBinary) {
		ArrayList p = variable.get(pheIdx);
		if (isBinary) {
			for (int i = 0; i < p.size(); i++) {
				if (((String) p.get(i)).compareTo(missing) == 0) {
					p.set(i, Integer.toString(rand.nextInt(2)));
				}
			}
		} else {
			Collections.replaceAll(p, missing, Double.toString(mean[pheIdx]));
		}
	}

	public double[] getVariable(int idx) {
		ArrayList<String> p = variable.get(idx);
		double[] v = new double[p.size()];
		for (int i = 0; i < v.length; i++) {
			v[i] = Double.parseDouble((String) p.get(i));
		}
		return v;
	}

	public double[][] getVariable(int[] idx) {
		double[][] v = new double[total][idx.length];
		for (int i = 0; i < idx.length; i++) {
			ArrayList p = variable.get(idx[i]);
			for (int j = 0; j < p.size(); j++) {
				v[j][i] = Double.parseDouble((String) p.get(j));
			}
		}
		return v;
	}
}
