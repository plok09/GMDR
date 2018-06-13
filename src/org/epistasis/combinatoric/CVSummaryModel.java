package org.epistasis.combinatoric;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

public class CVSummaryModel {
	private AbstractCombinatoricModel model;
	private int cvc;
	private CombinatoricResult avgTrainResult;
	private CombinatoricResult avgTestResult;

	public CVSummaryModel(AbstractCombinatoricModel model, int cvc, CombinatoricResult avgTrainResult, CombinatoricResult avgTestResult) {
		set(model, cvc, avgTrainResult, avgTestResult);
	}

	protected CVSummaryModel() {
	}

	public AbstractCombinatoricModel getModel() {
		return model;
	}

	public int getCVC() {
		return cvc;
	}

	public CombinatoricResult getAverageTrainResult() {
		return avgTrainResult;
	}

	public CombinatoricResult getAverageTestResult() {
		return avgTestResult;
	}

	protected void set(AbstractCombinatoricModel model, int cvc, CombinatoricResult avgTrainResult, CombinatoricResult avgTestResult) {
		this.model = model;
		this.cvc = cvc;
		this.avgTrainResult = avgTrainResult;
		this.avgTestResult = avgTestResult;
	}

	public void read(BufferedReader r) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void write(Writer w) {
		throw new UnsupportedOperationException();
	}
}
