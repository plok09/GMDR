package org.epistasis.combinatoric.mdr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.epistasis.combinatoric.AbstractCombinatoricModel;
import org.epistasis.combinatoric.AttributeCombination;
import org.epistasis.combinatoric.AttributeValueList;
import org.epistasis.combinatoric.CombinatoricResult;

import DataManage.Dataset;

public class CVSummaryModel extends org.epistasis.combinatoric.CVSummaryModel {
	private static final Pattern patModel = Pattern.compile("^\\s*Attr\\s+(.+?)\\s+AvgTrain\\s+(.+?)" + "(\\s+AvgTest\\s+(.+?))?\\s+Train\\s+(.+?)"
			+ "\\s+Summary\\s+(.+?)\\s*$");
	// cgb begin
	private static final Pattern patModelDetail = Pattern.compile("^(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+\\.\\d+)\\s+(-{0,1}\\d+\\.\\d+)$");	// private static final Pattern patModelDetail = Pattern.compile(
	// "^(\\S+)\\s+(\\d+)\\s+(\\d+)$");
	// cgb end 2006y/6m/14d
	private Dataset data;
	private int signTestCount;
	private double signTestPValue;
	private double threshold;

	public CVSummaryModel(Dataset data, double threshold, BufferedReader r) {
		this.data = data;
		this.threshold = threshold;

		try {
			read(r);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public CVSummaryModel(Dataset data, AbstractCombinatoricModel model, int cvc, int signTestCount, double signTestPValue, CombinatoricResult avgTrainResult,
			CombinatoricResult avgTestResult) {
		super(model, cvc, avgTrainResult, avgTestResult);
		this.data = data;
		this.signTestCount = signTestCount;
		this.signTestPValue = signTestPValue;
	}

	public int getSignTestCount() {
		return signTestCount;
	}

	public double getSignTestPValue() {
		return signTestPValue;
	}

	public void read(BufferedReader in) throws IOException {
		String line;

		if ((line = in.readLine()) == null) {
			throw new IllegalStateException();
		}

		Matcher m = patModel.matcher(line);

		if (!m.matches()) {
			throw new IllegalStateException();
		}

		AttributeCombination attr = new AttributeCombination(m.group(1), data.getLabels());

		Result avgTrain = new Result();
		avgTrain.setFromSummaryString(m.group(2));

		Result avgTest = null;
		if (m.group(4) != null) {
			avgTest = new Result();
			avgTest.setFromSummaryString(m.group(4));
		}

		Result train = new Result();
		train.setFromSummaryString(m.group(5));

		Model model = new Model(attr, threshold, train, null);

		String[] fields = m.group(6).split("\\s+");

		if (fields.length != 1 && fields.length != 3) {
			throw new IllegalStateException();
		}

		if ((line = in.readLine()) == null) {
			throw new IllegalStateException();
		}

		if (!line.equalsIgnoreCase("BeginModelDetail")) {
			throw new IllegalStateException();
		}

		while ((line = in.readLine()) != null) {
			if (line.equalsIgnoreCase("EndModelDetail")) {
				break;
			}

			Matcher md = patModelDetail.matcher(line);

			if (!md.matches()) {
				throw new IllegalStateException();
			}

			AttributeValueList values = new AttributeValueList();
			values.set(Arrays.asList(md.group(1).split(",")));

			try {
				int affected = Integer.parseInt(md.group(2));
				int unaffected = Integer.parseInt(md.group(3));
				// cgb start
				double affectedscore = Double.parseDouble(md.group(4));
				double unaffectedscore = Double.parseDouble(md.group(5));
				model.addCell(values, affected, unaffected, affectedscore, unaffectedscore);
				// model.addCell(values, affected, unaffected);
				// cgb end 2006/6m/9d
			} catch (NumberFormatException ex) {
				throw new IllegalStateException();
			}
		}

		try {
			int cvc = Integer.parseInt(fields[0]);
			set(model, cvc, avgTrain, avgTest);

			if (fields.length > 1) {
				this.signTestCount = Integer.parseInt(fields[1]);
				this.signTestPValue = Double.parseDouble(fields[2]);
			} else {
				signTestCount = -1;
				signTestPValue = Double.NaN;
			}
		} catch (NumberFormatException ex) {
			throw new IllegalStateException();
		}
	}

	public void write(Writer w) {
		PrintWriter out = new PrintWriter(w);

		out.print("Attr ");
		out.print(getModel().getAttributes().getComboString());
		out.print(" AvgTrain ");
		out.print(getAverageTrainResult().getSummaryString());

		if (getAverageTestResult() != null) {
			out.print(" AvgTest ");
			out.print(getAverageTestResult().getSummaryString());
		}

		out.print(" Train ");
		out.print(getModel().getTrainResult().getSummaryString());

		out.print(" Summary ");
		out.print(getCVC());

		if (signTestCount >= 0) {
			out.print(' ');
			out.print(signTestCount);
			out.print(' ');
			out.print(signTestPValue);
		}

		out.println();
		out.println("BeginModelDetail");

		for (Iterator j = getModel().entrySet().iterator(); j.hasNext();) {
			Map.Entry entry = (Map.Entry) j.next();

			out.print(entry.getKey());
			out.print(' ');
			out.print(entry.getValue());
			out.println();
		}

		out.println("EndModelDetail");
		out.flush();
	}
}
