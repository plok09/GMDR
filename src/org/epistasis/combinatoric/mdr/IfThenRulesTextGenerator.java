package org.epistasis.combinatoric.mdr;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.TreeSet;

import org.epistasis.AttributeLabels;
import org.epistasis.combinatoric.AttributeCombination;
import org.epistasis.combinatoric.AttributeValueList;

public class IfThenRulesTextGenerator {
	private CVSummaryModel summary;
	private AttributeLabels labels;

	public IfThenRulesTextGenerator(CVSummaryModel summary, AttributeLabels labels) {
		this.summary = summary;
		this.labels = labels;
	}

	public String toString() {
		StringWriter s = new StringWriter();
		PrintWriter w = new PrintWriter(s);

		Model m = (Model) summary.getModel();
		AttributeCombination attributes = m.getAttributes();

		Iterator i = new TreeSet(m.keySet()).iterator();
		while (i.hasNext()) {
			AttributeValueList g = (AttributeValueList) i.next();
			Model.Cell c = (Model.Cell) m.get(g);

			w.print("IF ");

			for (int j = 0; j < g.size(); ++j) {
				if (j != 0) {
					w.print(" AND ");
				}

				w.print(labels.get(((Integer) attributes.get(j)).intValue()));
				w.print(" = ");
				w.print(g.get(j));
			}

			w.print(" THEN CLASSIFY AS ");
			w.print(c.getStatus());
			w.print('.');

			if (i.hasNext()) {
				w.println();
			}
		}

		return s.toString();
	}
}
