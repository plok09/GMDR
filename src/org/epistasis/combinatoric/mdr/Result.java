package org.epistasis.combinatoric.mdr;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

import org.epistasis.AbstractDataset;
import org.epistasis.Utility;
import org.epistasis.combinatoric.CombinatoricResult;

public class Result implements CombinatoricResult {
	public static final int NOCLASS = VALID + 1;

	public static final int ACCURACY_CRITERION = 1;
	public static final int SENSITIVITY_CRITERION = 2;
	public static final int SPECIFICITY_CRITERION = 3;
	public static final int CHISQUARED_CRITERION = 4;
	public static final int ODDSRATIO_CRITERION = 5;
	public static final int KAPPA_CRITERION = 6;
	public static final int PRECISION_CRITERION = 7;
	public static final int FMEASURE_CRITERION = 8;
	public static final int BALANCEDACCURACY_CRITERION = 9;

	private NumberFormat nf;
	private int criterion;
	private Double accuracy = null;
	private Double chiSq = null;
	private Double error = null;
	private Double sensitivity = null;
	private Double specificity = null;
	private Double oddsRatio = null;
	private Double logORStdErr = null;
	private Double kappa = null;
	private Double precision = null;
	private Double fMeasure = null;

	/** Number of instances which were incorrectly classified as 0. */
	private double falseNeg = 0;

	/** Number of instances which were correctly classified 0. */
	private double trueNeg = 0;

	/** Number of instances which were incorrectly classified as 1. */
	private double falsePos = 0;

	/** Number of instances which were correctly classified as 1. */
	private double truePos = 0;

	/** Number of instances which could not be classified. */
	private double unknown = 0;

	public Result() {
		criterion = BALANCEDACCURACY_CRITERION;
	}

	public Result(int criterion) {
		this.criterion = criterion;
	}

	public void setNumberFormat(NumberFormat nf) {
		this.nf = nf;
	}

	/**
	 * Get number of instances incorrectly classified as 0
	 * 
	 * @return false negatives
	 */
	public double getFalseNegatives() {
		return falseNeg;
	}

	/**
	 * Get number of instances incorrectly classified as 1
	 * 
	 * @return false positives
	 */
	public double getFalsePositives() {
		return falsePos;
	}

	/**
	 * Get number of instances correctly classified as 0
	 * 
	 * @return true negatives
	 */
	public double getTrueNegatives() {
		return trueNeg;
	}

	/**
	 * Get number of instances correctly classified as 1
	 * 
	 * @return true positives
	 */
	public double getTruePositives() {
		return truePos;
	}

	/**
	 * Get number of instances unable to be classified
	 * 
	 * @return unknown instances
	 */
	public double getUnknowns() {
		return unknown;
	}

	/**
	 * Get the number of tested instances which were able to be classified.
	 * 
	 * @return number of classified instances
	 */
	public double getNumClassified() {
		return falseNeg + falsePos + trueNeg + truePos;
	}

	/**
	 * Get the total nunmber of instances tested.
	 * 
	 * @return total
	 */
	public double getTotal() {
		return getNumClassified() + unknown;
	}

	/**
	 * Get the ratio of incorrectly classified instances to total number of
	 * instances.
	 * 
	 * @return error
	 */
	public double getError() {
		if (error == null) {
			error = new Double((falseNeg + falsePos) / getNumClassified());
		}

		return error.doubleValue();
	}

	/**
	 * Get the ratio of correctly classified instances to total number of
	 * instances.
	 * 
	 * @return accuracy
	 */
	public double getAccuracy() {
		if (accuracy == null) {
			accuracy = new Double((trueNeg + truePos) / getNumClassified());
		}

		return accuracy.doubleValue();
	}

	public double getBalancedAccuracy() {
		return (getSensitivity() + getSpecificity()) / 2.0;
	}

	/**
	 * Get the ratio of instances correctly classified as positive to total
	 * number of positive instances. Also known as recall.
	 * 
	 * @return sensitivity
	 */
	public double getSensitivity() {
		if (sensitivity == null) {
			sensitivity = new Double(truePos / (falseNeg + truePos));
		}

		return sensitivity.doubleValue();
	}

	/**
	 * Get the ratio of instances correctly classified as negative to total
	 * number of negative instances.
	 * 
	 * @return sensitivity
	 */
	public double getSpecificity() {
		if (specificity == null) {
			specificity = new Double(trueNeg / (falsePos + trueNeg));
		}

		return specificity.doubleValue();
	}

	public double getPrecision() {
		if (precision == null) {
			precision = new Double(truePos / (truePos + falsePos));
		}

		return precision.doubleValue();
	}

	public double getFMeasure() {
		if (fMeasure == null) {
			fMeasure = new Double(2 * getPrecision() * getSensitivity() / (getPrecision() + getSensitivity()));
		}

		return fMeasure.doubleValue();
	}

	/**
	 * Get the ratio of correctly classified instances to incorrectly classified
	 * instances.
	 * 
	 * @return odds ratio
	 */
	public double getOddsRatio() {
		if (oddsRatio == null) {
			oddsRatio = new Double((truePos * trueNeg) / (falsePos * falseNeg));
		}

		return oddsRatio.doubleValue();
	}

	/**
	 * Get the standard error of the log of the odds ratio.
	 * 
	 * @return standard error
	 */
	public double getLogORStdErr() {
		if (logORStdErr == null) {
			logORStdErr = new Double(Math.sqrt(1.0 / truePos + 1.0 / falsePos + 1.0 / trueNeg + 1.0 / falseNeg));
		}

		return logORStdErr.doubleValue();
	}

	/**
	 * Get the lower bound of the 95% confidence interval for the odds ratio.
	 * 
	 * @return confidence interval lower bound
	 */
	public double getORConfIntLower() {
		return Math.exp(Math.log(getOddsRatio()) - 1.96 * getLogORStdErr());
	}

	/**
	 * Get the upper bound of the 95% confidence interval for the odds ratio.
	 * 
	 * @return confidence interval upper bound
	 */
	public double getORConfIntUpper() {
		return Math.exp(Math.log(getOddsRatio()) + 1.96 * getLogORStdErr());
	}

	public double[][] getConfusionMatrix() {
		double[][] confusion = new double[2][];

		for (int i = 0; i < 2; ++i) {
			confusion[i] = new double[2];
		}

		confusion[0][0] = truePos;
		confusion[0][1] = falseNeg;
		confusion[1][0] = falsePos;
		confusion[1][1] = trueNeg;

		return confusion;
	}

	public double getChiSquared() {
		if (chiSq == null) {
			chiSq = new Double(Utility.computeChiSquared(getConfusionMatrix()));
		}

		return chiSq.doubleValue();
	}

	public double getChiSquaredPValue() {
		return Utility.pchisq(getChiSquared(), 1);
	}

	public double getKappa() {
		if (kappa == null) {
			kappa = new Double(Utility.computeKappaStatistic(getConfusionMatrix()));
		}

		return kappa.doubleValue();
	}

	public int getSelectionCriterion() {
		return criterion;
	}

	public double getFitness() {
		return getFitness(criterion);
	}

	public double getFitness(int criterion) {
		switch (criterion) {
		case ACCURACY_CRITERION:
			return getAccuracy();
		case SENSITIVITY_CRITERION:
			return getSensitivity();
		case SPECIFICITY_CRITERION:
			return getSpecificity();
		case CHISQUARED_CRITERION:
			return getChiSquared();
		case ODDSRATIO_CRITERION:
			return getOddsRatio();
		case KAPPA_CRITERION:
			return getKappa();
		case PRECISION_CRITERION:
			return getPrecision();
		case FMEASURE_CRITERION:
			return getFMeasure();
		case BALANCEDACCURACY_CRITERION:
			return getBalancedAccuracy();
		default:
			throw new IllegalStateException("Unknown selection criterion: " + criterion);
		}
	}

	public int getStatus() {
		return getNumClassified() > 0 ? VALID : NOCLASS;
	}

	public String getSummaryString() {
		StringBuffer b = new StringBuffer();
		double[][] confusion = getConfusionMatrix();

		for (int i = 0; i < confusion.length; ++i) {
			for (int j = 0; j < confusion[i].length; ++j) {
				b.append(confusion[i][j]);
				b.append(' ');
			}
		}

		b.append(unknown);

		return b.toString();
	}

	public void setFromSummaryString(String s) {
		String[] values = s.split("\\s+");
		double[][] confusion = getConfusionMatrix();
		int k = 0;

		for (int i = 0; i < confusion.length; ++i) {
			for (int j = 0; j < confusion.length; ++j) {
				confusion[i][j] = Double.parseDouble(values[k++]);
			}
		}

		setConfusionMatrix(confusion, Double.parseDouble(values[k]));
	}

	public void setConfusionMatrix(double[][] confusion) {
		setConfusionMatrix(confusion, 0);
	}

	public void setConfusionMatrix(double[][] confusion, double unknown) {
		truePos = confusion[0][0];
		falseNeg = confusion[0][1];
		falsePos = confusion[1][0];
		trueNeg = confusion[1][1];
		this.unknown = unknown;

		accuracy = chiSq = error = sensitivity = specificity = oddsRatio = logORStdErr = kappa = precision = fMeasure = null;
	}

	public int compareTo(Object o) {
		Result r = (Result) o;

		if (getStatus() == VALID && r.getStatus() == VALID) {
			return Double.compare(getFitness(), r.getFitness(criterion));
		}

		if (getStatus() == VALID) {
			return 1;
		}

		if (r.getStatus() == VALID) {
			return -1;
		}

		return Double.compare(getStatus(), r.getStatus());
	}

	// cgb start
	public void addInstances(List instances, int status) {
		if (status == -1) {
			unknown += instances.size();
			return;
		}

		for (Iterator i = instances.iterator(); i.hasNext();) {
			AbstractDataset.Instance instance = (AbstractDataset.Instance) i.next();

			double instscore = instance.getInstScore();

			if (status == 0) {
				if (instscore < 0) {
					trueNeg += instscore * (-1);
				} else {
					falseNeg += instscore;
				}

			} else if (status == 1) {
				if (instscore < 0) {
					falsePos += instscore * (-1);
				} else {
					truePos += instscore;
				}
			} else {
				unknown++;
			}
		}

		accuracy = chiSq = error = sensitivity = specificity = oddsRatio = logORStdErr = kappa = precision = fMeasure = null;
	}

	// cgb end 2006y/6m/6d
	/*
	 * public void addInstances(List instances, int status) { if (status == -1)
	 * { unknown += instances.size(); return; }
	 * 
	 * for (Iterator i = instances.iterator(); i.hasNext(); ) {
	 * AbstractDataset.Instance instance = (AbstractDataset.Instance) i.next();
	 * 
	 * int instStatus = ((Integer) instance.getStatus()).intValue();
	 * 
	 * if (status == 0) { if (instStatus == 0) { trueNeg++; } else { falseNeg++;
	 * } } else if (status == 1) { if (instStatus == 0) { falsePos++; } else {
	 * truePos++; } } else { unknown++; } }
	 * 
	 * accuracy = chiSq = error = sensitivity = specificity = oddsRatio =
	 * logORStdErr = kappa = precision = fMeasure = null; }
	 */
	public void assignAverage(List results) {
		double truePosSum = 0;
		double falsePosSum = 0;
		double trueNegSum = 0;
		double falseNegSum = 0;
		double unknownSum = 0;
		double accuracySum = 0;
		double chiSqSum = 0;
		double errorSum = 0;
		double sensitivitySum = 0;
		double specificitySum = 0;
		double oddsRatioSum = 0;
		double logORStdErrSum = 0;
		double kappaSum = 0;
		double precisionSum = 0;
		double fMeasureSum = 0;

		for (Iterator i = results.iterator(); i.hasNext();) {
			Result r = (Result) i.next();

			truePosSum += r.getTruePositives();
			falsePosSum += r.getFalsePositives();
			trueNegSum += r.getTrueNegatives();
			falseNegSum += r.getFalseNegatives();
			unknownSum += r.getUnknowns();
			accuracySum += r.getAccuracy();
			chiSqSum += r.getChiSquared();
			errorSum += r.getError();
			sensitivitySum += r.getSensitivity();
			specificitySum += r.getSpecificity();
			oddsRatioSum += r.getOddsRatio();
			logORStdErrSum += r.getLogORStdErr();
			kappaSum += r.getKappa();
			precisionSum += r.getPrecision();
			fMeasureSum += r.getFMeasure();
		}

		truePos = truePosSum / results.size();
		falsePos = falsePosSum / results.size();
		trueNeg = trueNegSum / results.size();
		falseNeg = falseNegSum / results.size();
		unknown = unknownSum / results.size();
		accuracy = new Double(accuracySum / results.size());
		chiSq = new Double(chiSqSum / results.size());
		error = new Double(errorSum / results.size());
		sensitivity = new Double(sensitivitySum / results.size());
		specificity = new Double(specificitySum / results.size());
		oddsRatio = new Double(oddsRatioSum / results.size());
		logORStdErr = new Double(logORStdErrSum / results.size());
		kappa = new Double(kappaSum / results.size());
		precision = new Double(precisionSum / results.size());
		fMeasure = new Double(fMeasureSum / results.size());
	}

	public String toString() {
		switch (getStatus()) {
		case VALID:
			if (nf == null) {
				return Double.toString(getFitness());
			} else {
				return nf.format(getFitness());
			}

		case NOCLASS:
			return "N/A";
		}

		return null;
	}

	public String getStatusString() {
		switch (getStatus()) {
		case VALID:
			return "";

		case NOCLASS:
			return "No instances were able to be clasified for this model.";
		}

		return null;
	}

	public Object clone() {
		try {
			Result r = (Result) super.clone();

			r.truePos = truePos;
			r.falsePos = falsePos;
			r.trueNeg = trueNeg;
			r.falseNeg = falseNeg;
			r.unknown = unknown;
			r.nf = nf;
			r.criterion = criterion;
			r.accuracy = accuracy;
			r.chiSq = chiSq;
			r.error = error;
			r.sensitivity = sensitivity;
			r.specificity = specificity;
			r.oddsRatio = oddsRatio;
			r.logORStdErr = logORStdErr;
			r.kappa = kappa;
			r.precision = precision;
			r.fMeasure = fMeasure;

			return r;
		} catch (CloneNotSupportedException ex) {
			return null;
		}
	}
}
