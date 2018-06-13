package DataManage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.epistasis.AbstractDataset;



public class Dataset extends AbstractDataset {

	private boolean paired;
	private Double ratio;
	
	public Object set(int index, Object element) {
		ratio = null;
		return super.set(index, element);
	}

	public void add(int index, Object element) {
		ratio = null;
		super.add(index, element);
	}

	public Object remove(int index) {
		ratio = null;
		return super.remove(index);
	}

	public void partition(int intervals, Random rnd) {
		train.clear();
		test.clear();

		if (intervals < 1 || intervals > size()) {
			return;
		}

		if (intervals == 1) {
			train.add(this);
			return;
		}

		train.ensureCapacity(intervals);
		test.ensureCapacity(intervals);

		for (int i = 0; i < intervals; ++i) {
			Dataset d = (Dataset) clone();
			d.clear();
			train.add(d);
			d = (Dataset) clone();
			d.clear();
			test.add(d);
		}

		if (paired) {
			pairedPartition(intervals, rnd);
		} else {
			unpairedPartition(intervals, rnd);
		}

		for (int i = 0; i < intervals; ++i) {
			for (int j = 0; j < intervals; ++j) {
				if (i != j) {
					((Dataset) train.get(i)).addAll((Dataset) test.get(j));
				}
			}
		}
	}

	public boolean isPaired() {
		return paired;
	}

	public void setPaired(boolean paired) {
		this.paired = paired;
	}

	private void unpairedPartition(int intervals, Random rnd) {
		ArrayList<Instance> population = new ArrayList<Instance>();
		for (Iterator<Instance> e = iterator(); e.hasNext();) {
			population.add(e.next());
		}

		if (rnd != null) {
			Collections.shuffle(population, rnd);
		}

		for (int i = 0; i < population.size(); i++) {
			((Dataset) test.get(i % intervals)).add(population.get(i));
		}
	}

	//	private void unpairedPartition(int intervals, Random rnd) {
	//		ArrayList affecteds = new ArrayList();
	//		ArrayList unaffecteds = new ArrayList();
	//
	//		for (Iterator i = iterator(); i.hasNext();) {
	//			Instance inst = (Instance) i.next();
	//
	//			if (((Integer) inst.getStatus()).intValue() == 1) {
	//				affecteds.add(inst);
	//			} else {
	//				unaffecteds.add(inst);
	//			}
	//		}
	//
	//		if (rnd != null) {
	//			Collections.shuffle(affecteds, rnd);
	//			Collections.shuffle(unaffecteds, rnd);
	//		}
	//
	//		for (int i = 0; i < affecteds.size(); ++i) {
	//			((Dataset) test.get(i % intervals)).add(affecteds.get(i));
	//		}
	//
	//		for (int i = 0; i < unaffecteds.size(); ++i) {
	//			((Dataset) test.get((i + affecteds.size()) % intervals))
	//					.add(unaffecteds.get(i));
	//		}
	//	}

	private void pairedPartition(int intervals, Random rnd) {
		ArrayList pairs = new ArrayList(size() / 2);

		for (int i = 0; i < size(); i += 2) {
			Object[] pair = new Object[2];
			pair[0] = get(i);
			pair[1] = get(i + 1);
			pairs.add(pair);
		}

		if (rnd != null) {
			Collections.shuffle(pairs, rnd);
		}

		for (int i = 0; i < pairs.size(); ++i) {
			Dataset set = (Dataset) test.get(i % intervals);
			Object[] pair = (Object[]) pairs.get(i);
			set.add(pair[0]);
			set.add(pair[1]);
		}
	}

	public Instance createInstance(String[] fields) {
		Integer status = null;

		try {
			status = fields[fields.length - 1].compareTo(".") == 0 ? new Integer(1) : Integer.valueOf(fields[fields.length - 1]);
			// if status is missing, put "1" first and impute it later.

			if (status.intValue() != 0 && status.intValue() != 1) {
				throw new IllegalArgumentException("Status value must be 0, 1, or a defined missing value.");
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Non-integral status value '" + fields[fields.length - 1] + "'.");
		}

		List statusless = new ArrayList(fields.length - 1);
		statusless.addAll(Arrays.asList(fields).subList(0, fields.length - 1));

		return new Instance(statusless, status);
	}

	/*
	 * public double getRatio() { if (ratio == null) { int affected = 0; int
	 * unaffected = 0;
	 * 
	 * for (Iterator i = iterator(); i.hasNext(); ) { if (((Integer) ((Instance)
	 * i.next()).getStatus()).intValue() == 1) { ++affected; } else {
	 * ++unaffected; } }
	 * 
	 * ratio = new Double((double) affected / unaffected); }
	 * 
	 * return ratio.doubleValue(); }
	 */

	public double getRatio() {
		//boolean isLoadedScore = isloadscore;
		if (!isloadedScore) {
			int affected = 0;
			int unaffected = 0;

			for (Iterator i = iterator(); i.hasNext();) {
				if (((Integer) ((Instance) i.next()).getStatus()).intValue() == 1) {
					++affected;
				} else {
					++unaffected;
				}
			}
			ratio = new Double((double) affected / unaffected);
		} else {
			double affectedscore = 0.0;
			double unaffectedscore = 0.0;

			for (Iterator i = iterator(); i.hasNext();) {
				AbstractDataset.Instance instance = (AbstractDataset.Instance) i.next();
				double instscore = instance.getInstScore();
				if (instscore > 0) {
					affectedscore += instscore;
				} else {
					unaffectedscore += instscore * (-1.0);
				}
			}
			ratio = new Double((double) affectedscore / unaffectedscore);
		}

		return ratio.doubleValue();
	}

}
