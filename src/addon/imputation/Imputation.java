package addon.imputation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class Imputation {
	private static String missing = ".";
	private ArrayList<HashMap> genotypeHub;
	private Random rand;
	private long seed = 20101111;
	private double[] missingCount;
	private int total;

	public Imputation() throws IOException {
		rand = new Random(seed);
		genotypeHub = new ArrayList();
		total = 0;
	}

	public void initial(int len) {
		for (int i = 0; i < len; i++) {
			genotypeHub.add(new HashMap());
		}
		missingCount = new double[len];
	}

	public ArrayList MarkerCensus(String[] fields) {
		ArrayList missing_infor = null;
		for (int j = 0; j < fields.length; j++) {
			HashMap hm = genotypeHub.get(j);
			if (fields[j].compareTo(missing) == 0) {
				if (missing_infor == null) {
					missing_infor = new ArrayList();
					missing_infor.add(new Integer(total));
				}
				missingCount[j] += 1;
				missing_infor.add(new Integer(j));
				continue;
			}
			int c;
			if (hm.containsKey(fields[j])) {
				c = ((Integer) hm.get(fields[j])).intValue();
				c++;
			} else {
				c = 1;
			}
			hm.put(fields[j], new Integer(c));
		}
		total++;
		return missing_infor;
	}

	public void CalculateGenotypeFrequency() {
		for (int i = 0; i < genotypeHub.size(); i++) {
			HashMap hm = genotypeHub.get(i);
			Set keys = (Set) hm.keySet();
			ArrayList gc = new ArrayList();
			for (Iterator e = keys.iterator(); e.hasNext();) {
				String geno = (String) e.next();
				Integer count = (Integer) hm.get(geno);
				double f = count.doubleValue() / (total - missingCount[i]);
				hm.put(geno, new Double(f));
			}
		}
	}

	public String[] implementImputation(String[] geno) {
		String[] g = new String[geno.length];
		for (int i = 0; i < geno.length; i++) {
			if (geno[i].compareTo(missing) == 0) {
				g[i] = generateGenotype(i);
			} else {
				g[i] = geno[i];
			}
		}
		return g;
	}

	public String generateGenotype(int idx) {
		double f = rand.nextDouble();
		HashMap hm = (HashMap) genotypeHub.get(idx);
		Set keys = (Set) hm.keySet();
		String g = null;
		for (Iterator e = keys.iterator(); e.hasNext();) {
			String key = (String) e.next();
			g = key;
			double d = ((Double) hm.get(key)).doubleValue();
			f -= d;
			if (f <= 0) {
				break;
			}
		}
		return g;
	}
}
