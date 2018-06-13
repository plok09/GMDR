package org.epistasis.combinatoric.mdr;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.epistasis.ColumnFormat;

public class BestModelTextGenerator extends ModelTextGenerator {
	private CVSummaryModel summary;
	private NumberFormat nf;
	private int intervals;
	private double pValueTol;

	public BestModelTextGenerator(CVSummaryModel summary, int intervals, NumberFormat nf, double pValueTol) {
		this.summary = summary;
		this.nf = nf;
		this.intervals = intervals;
		this.pValueTol = pValueTol;
	}

	public String toString() {
		Model model = (Model) summary.getModel();
		StringBuffer b = new StringBuffer();

		b.append(model.getAttributes());
		b.append("\n");

		if (summary.getAverageTestResult() != null) {
			b.append("\nCross-validation Statistics:\n\n");

			b.append(getResultText((Result) summary.getAverageTrainResult(), "Training", nf, 30, pValueTol));
			b.append(getResultText((Result) summary.getAverageTestResult(), "Testing", nf, 30, pValueTol));

			b.append(ColumnFormat.fitStringWidth("Sign Test:", 30));
			if (summary.getSignTestCount() >= 0) {
				b.append(summary.getSignTestCount());
				if (!Double.isNaN(summary.getSignTestCount())) {
					double p = summary.getSignTestPValue();

					b.append(" (p ");

					if (p < pValueTol) {
						b.append("< ");
						b.append(nf.format(pValueTol));
					} else {
						b.append("= ");
						b.append(nf.format(p));
					}

					b.append(')');
				}
			} else {
				b.append("N/A");
			}
			b.append("\n");

			b.append(ColumnFormat.fitStringWidth("Cross-validation Consistency:", 30));
			b.append(summary.getCVC());
			b.append('/');
			b.append(intervals);
			b.append('\n');
		}

		b.append("\nWhole Dataset Statistics:\n\n");

		b.append(getResultText((Result) model.getTrainResult(), "Training", nf, 30, pValueTol));

		b.append("\nModel Detail:\n\n");

		ArrayList list = new ArrayList(5);
		// cgb begin
		list.add(new Integer(25));
		list.add(new Integer(25));
		list.add(new Integer(25));
		list.add(new Integer(20));
		list.add(new Integer(15));
		// cgb end 2006y/6m/14d
		ColumnFormat cf = new ColumnFormat(list);

		list.clear();
		list.add("Combination");
		// cgb begin
		list.add("Class 1 Score");
		list.add("Class 0 Score");
		// list.add("Class 1");
		// list.add("Class 0");
		// list.add("Ratio");
		list.add("Sum Score");
		// cgb end 2006y/6m/14d
		list.add("New Class Level");
		b.append(cf.format(list));
		b.append('\n');

		list.clear();
		list.add("-----------");
		list.add("-------------");
		list.add("-------------");
		list.add("-------------");
		list.add("---------------");
		b.append(cf.format(list));
		b.append('\n');

		Set entries = new TreeSet(model.keySet());
		for (Iterator i = entries.iterator(); i.hasNext();) {
			Object key = i.next();
			Model.Cell cell = (Model.Cell) model.get(key);

			list.clear();
			list.add(key);
			// cgb begin
			// list.add(new Integer(cell.getAffected()));
			// list.add(new Integer(cell.getUnaffected()));
			list.add(new Double(cell.getAffectedScore()));
			list.add(new Double(cell.getUnaffectedScore()));
			// cgb end 2006y/6m/14d
			list.add(nf.format(cell.getRatio()));
			list.add(new Integer(cell.getStatus()));
			b.append(cf.format(list));
			b.append('\n');
		}

		return b.toString();
	}
}
