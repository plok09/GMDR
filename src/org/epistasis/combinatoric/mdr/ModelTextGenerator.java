package org.epistasis.combinatoric.mdr;

import java.text.NumberFormat;

import org.epistasis.ColumnFormat;

public class ModelTextGenerator {
	protected static String getResultText(Result result, String name, NumberFormat nf, int labelWidth, double pValueTol) {
		StringBuffer b = new StringBuffer();

		b.append(ColumnFormat.fitStringWidth(name + " Balanced Accuracy:", labelWidth));
		b.append(nf.format(result.getBalancedAccuracy()));
		b.append('\n');

		b.append(ColumnFormat.fitStringWidth(name + " Accuracy:", labelWidth));
		b.append(nf.format(result.getAccuracy()));
		b.append('\n');

		b.append(ColumnFormat.fitStringWidth(name + " Sensitivity:", labelWidth));
		b.append(nf.format(result.getSensitivity()));
		b.append('\n');

		b.append(ColumnFormat.fitStringWidth(name + " Specificity:", labelWidth));
		b.append(nf.format(result.getSpecificity()));
		b.append('\n');

		b.append(ColumnFormat.fitStringWidth(name + " Odds Ratio:", labelWidth));
		b.append(nf.format(result.getOddsRatio()));
		if (!Double.isNaN(result.getOddsRatio()) && !Double.isInfinite(result.getOddsRatio())) {
			b.append(" (");
			b.append(nf.format(result.getORConfIntLower()));
			b.append(',');
			b.append(nf.format(result.getORConfIntUpper()));
			b.append(')');
		}
		b.append('\n');

		b.append(ColumnFormat.fitStringWidth(name + " Chi-square:", labelWidth - 1));
		b.append(nf.format(result.getChiSquared()));
		if (!Double.isNaN(result.getChiSquared())) {
			double p = result.getChiSquaredPValue();

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
		b.append('\n');

		b.append(ColumnFormat.fitStringWidth(name + " Precision:", labelWidth));
		b.append(nf.format(result.getPrecision()));
		b.append('\n');

		b.append(ColumnFormat.fitStringWidth(name + " Kappa:", labelWidth));
		b.append(nf.format(result.getKappa()));
		b.append('\n');

		b.append(ColumnFormat.fitStringWidth(name + " F-Measure:", labelWidth));
		b.append(nf.format(result.getFMeasure()));
		b.append('\n');

		return b.toString();
	}

	public String toString() {
		return "";
	}
}
