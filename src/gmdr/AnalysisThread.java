package gmdr;

import org.epistasis.combinatoric.AbstractAnalysisThread;
import org.epistasis.combinatoric.AbstractCombinatoricModel;
import org.epistasis.combinatoric.AttributeCombination;
import org.epistasis.combinatoric.mdr.Model;
import org.epistasis.combinatoric.mdr.ModelFilter;

import DataManage.Dataset;

public class AnalysisThread extends AbstractAnalysisThread {

	private int tieValue;
	private AttributeCombination forced;

	// magic 2007-12-28
	private static boolean setR = false;
	private static double r;

	public static void setR(double newR) {
		setR = true;
		r = newR;
	}

	// magic 2007-12-28

	protected AbstractCombinatoricModel createModel(AttributeCombination attributes) {
		if (setR) {
			return new Model(attributes, r, tieValue);
		} else {
			return new Model(attributes, ((Dataset) getModelFilter().getData()).getRatio(), tieValue);
		}
	}

	public AnalysisThread(int min, int max, Dataset data, int intervals, long seed, int tieValue, Runnable onEndModel, Runnable onEndAttribute, Runnable onEnd,
			boolean computeLandscape, boolean parallel) {
		super(new ModelFilter(min, max, intervals, data, computeLandscape), data, intervals, seed, onEndModel, onEndAttribute, onEnd, parallel);
		init(null, tieValue);
	}

	public AttributeCombination getForced() {
		return forced;
	}

	public int getTieValue() {
		return tieValue;
	}

	private void init(AttributeCombination forced, int tieValue) {
		this.forced = forced;
		this.tieValue = tieValue;
	}
}
