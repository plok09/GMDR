package org.epistasis.combinatoric.mdr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.epistasis.Utility;
import org.epistasis.combinatoric.AttributeCombination;
import org.epistasis.combinatoric.CombinatoricModelFilter;

import DataManage.Dataset;

public class ModelFilter extends CombinatoricModelFilter {
	private double threshold;

	private static final Pattern patModel = Pattern.compile("^\\s*Attr\\s+(.+?)\\s+Train\\s+(.+?)(\\s+Test\\s+(.+?))?\\s*$");

	public ModelFilter(int min, int max, int intervals, double threshold, Dataset data, boolean computeLandscape) {
		super(min, max, intervals, data, computeLandscape);
		this.threshold = threshold;
	}

	public ModelFilter(int min, int max, int intervals, Dataset data, boolean computeLandscape) {
		super(min, max, intervals, data, computeLandscape);
	}

	protected synchronized org.epistasis.combinatoric.CVSummaryModel computeBest(int index) {
		org.epistasis.combinatoric.CVSummaryModel summary = super.computeBest(index);

		if (getIntervals() >= 10 && getIntervals() % 2 == 0) {
			int signTestCount = 0;

			for (int i = 0; i < getIntervals(); ++i) {
				if (((Result) getModel(index + getMin(), i).getTestResult()).getAccuracy() > 0.5) {
					++signTestCount;
				}
			}

			return new CVSummaryModel((Dataset) getData(), summary.getModel(), summary.getCVC(), signTestCount, Utility.computeOneTailedSignTest(
					getIntervals(), signTestCount), summary.getAverageTrainResult(), summary.getAverageTestResult());
		} else {
			return new CVSummaryModel((Dataset) getData(), summary.getModel(), summary.getCVC(), -1, Double.NaN, summary.getAverageTrainResult(),
					summary.getAverageTestResult());
		}
	}

	public void read(Reader r) throws IOException {
		BufferedReader in = new BufferedReader(r);

		for (int i = getMin(); i <= getMax(); ++i) {
			for (int j = 0; j < getIntervals(); ++j) {
				String line = in.readLine();

				if (line == null) {
					break;
				}

				putModel(parseModelLine(line), i, j);
			}

			putCVSummaryModel(i, new CVSummaryModel((Dataset) getData(), threshold, in));
		}
	}

	private Model parseModelLine(String line) {
		Matcher m = patModel.matcher(line);

		if (!m.matches()) {
			throw new IllegalStateException();
		}

		AttributeCombination attr = new AttributeCombination(m.group(1), getData().getLabels());

		Result train = new Result();
		train.setFromSummaryString(m.group(2));

		Result test = null;
		if (m.group(4) != null) {
			test = new Result();
			test.setFromSummaryString(m.group(4));
		}

		return new Model(attr, train, test);
	}
}
