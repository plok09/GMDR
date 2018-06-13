package org.epistasis.combinatoric.mdr;

import java.text.NumberFormat;

import org.epistasis.combinatoric.CombinatoricModelFilter;

public class CVResultsTextGenerator extends ModelTextGenerator {
	private CombinatoricModelFilter filter;
	private int attrCount;
	private int intervals;
	private NumberFormat nf;
	private double pValueTol;

	public CVResultsTextGenerator(CombinatoricModelFilter filter, int attrCount, int intervals, NumberFormat nf, double pValueTol) {
		this.filter = filter;
		this.attrCount = attrCount;
		this.intervals = intervals;
		this.nf = nf;
		this.pValueTol = pValueTol;
	}

	public String toString() {
		StringBuffer b = new StringBuffer();

		for (int i = 0; i < intervals; ++i) {
			if (i != 0) {
				b.append('\n');
			}

			addModel(b, i);
		}

		return b.toString();
	}

	private void addModel(StringBuffer b, int interval) {
		Model model = (Model) filter.getModel(attrCount, interval);

		b.append("Cross Validation Interval #");
		b.append(interval + 1);
		b.append(" of ");
		b.append(intervals);
		b.append(":\n\n");

		b.append(model.getAttributes());
		b.append("\n\n");

		Result trainResult = (Result) model.getTrainResult();

		b.append(getResultText((Result) model.getTrainResult(), "Training", nf, 28, pValueTol));

		if (model.getTestResult() != null) {
			b.append(getResultText((Result) model.getTestResult(), "Testing", nf, 28, pValueTol));
		}
	}
}
