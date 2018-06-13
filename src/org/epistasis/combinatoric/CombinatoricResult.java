package org.epistasis.combinatoric;

import java.text.NumberFormat;
import java.util.List;

public interface CombinatoricResult extends Comparable, Cloneable {
	public static final int VALID = 0;

	public void setNumberFormat(NumberFormat nf);

	public void assignAverage(List results);

	public double getFitness();

	public int getStatus();

	public String getStatusString();

	public String getSummaryString();

	public void setFromSummaryString(String s);

	public Object clone();
}
