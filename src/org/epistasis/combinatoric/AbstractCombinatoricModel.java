package org.epistasis.combinatoric;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.epistasis.AbstractDataset;

public abstract class AbstractCombinatoricModel extends AbstractMap implements Cloneable {
	private AttributeCombination attributes;
	private HashMap model = new HashMap();
	protected CombinatoricResult trainResult;
	protected CombinatoricResult testResult;

	protected static interface MergeSearchTarget {
		public void run(Map model, AttributeValueList values, List instances);
	}

	public AbstractCombinatoricModel(AttributeCombination attributes) {
		this.attributes = attributes;
	}

	public AttributeCombination getAttributes() {
		return attributes;
	}

	public Set entrySet() {
		return Collections.unmodifiableSet(model.entrySet());
	}

	public CombinatoricResult getTrainResult() {
		return trainResult;
	}

	public CombinatoricResult getTestResult() {
		return testResult;
	}

	public void clearCells() {
		model.clear();
	}

	public abstract CombinatoricResult train(List instances);

	public abstract CombinatoricResult test(List instances);

	protected Map getModel() {
		return model;
	}

	protected void mergeSearch(List instances, MergeSearchTarget target) {
		mergeSearch(instances, 0, new AttributeValueList(), target);
	}

	/**
	 * Recursive function that is at the heart of a combinatoric analysis. For
	 * each attribute under consideration, the data is split based on its value
	 * for that attribute. Finally, the data is split into groups, one for each
	 * cell in the model. At this point, the cells can be calculated and added
	 * to the model. This algorithm was initially devised by Will Bush and Bill
	 * White from Vanderbilt University's Center for Human Genetics Research
	 * (CHGR).
	 * 
	 * @param instances
	 *            List of <code>Dataset.Instance</code>s to be analyzed
	 * @param idxAttribute
	 *            Which attribute to use to split the data
	 * @param values
	 *            Structure representing where we are in the recursion tree
	 * @param target
	 *            MergeSearchTarget to execute when we hit a leaf node in the
	 *            recursion tree.
	 */
	protected void mergeSearch(List instances, int idxAttribute, AttributeValueList values, MergeSearchTarget target) {
		// if we haven't yet processed all attributes
		if (idxAttribute < attributes.size()) {
			// map to hold subsets of the data based on each row's value
			// at the current attribute
			Map subsets = new TreeMap();

			// go through all data rows
			for (Iterator i = instances.iterator(); i.hasNext();) {
				// get current data row
				AbstractDataset.Instance r = (AbstractDataset.Instance) i.next();

				// get value of current attribute from current row
				Object value = r.get(((Integer) attributes.get(idxAttribute)).intValue());

				// get subset matching value of current attribute
				ArrayList subset = (ArrayList) subsets.get(value);

				// if there is no matching subset, create one
				if (subset == null) {
					subset = new ArrayList();
					subsets.put(value, subset);
				}

				// add this row to the matching subset
				subset.add(r);
			}

			// recursively process each subset based on the next attribute

			for (Iterator i = subsets.entrySet().iterator(); i.hasNext();) {
				// get current subset
				Map.Entry entry = (Map.Entry) i.next();

				// mark where we are in the recursion tree
				values.push((Comparable) entry.getKey());

				// process current subset for next attribute
				mergeSearch((ArrayList) entry.getValue(), idxAttribute + 1, values, target);

				// we're done processing this subset, so remove its attribute
				// value from the genotype
				values.pop();
			}
		}

		// if we've processed all attributes
		else {
			target.run(model, (AttributeValueList) values.clone(), instances);
		}
	}

	public Object clone() {
		try {
			AbstractCombinatoricModel m = (AbstractCombinatoricModel) super.clone();

			m.attributes = attributes;
			m.model = (HashMap) model.clone();
			m.trainResult = trainResult;
			m.testResult = testResult;

			return m;
		} catch (CloneNotSupportedException ex) {
			return null;
		}
	}
}
