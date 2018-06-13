package org.epistasis.combinatoric;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.epistasis.AbstractDataset;
import org.epistasis.StringDoublePair;
import org.epistasis.StringListener;

public class CombinatoricModelFilter {
	private static Pattern patLandscape = Pattern.compile("^(.+)\t([-+]?(\\d*\\.)?\\d+([eE][-+]?\\d+)?)$");
	private int min;
	private int max;
	private int intervals;
	private AbstractCombinatoricModel models[][];
	private CVSummaryModel best[];
	private AbstractDataset data;
	private boolean computeLandscape;
	private Map landscapeMap;
	private Map labelMap;
	private List landscape;
	private List warningListeners = new LinkedList();

	public CombinatoricModelFilter(int min, int max, int intervals, AbstractDataset data, boolean computeLandscape) {
		this.min = min;
		this.max = max;
		this.intervals = intervals;
		this.data = data;
		this.computeLandscape = computeLandscape;

		if (computeLandscape) {
			landscapeMap = new TreeMap();
		}

		models = new AbstractCombinatoricModel[max - min + 1][];

		for (int i = 0; i < models.length; ++i) {
			models[i] = new AbstractCombinatoricModel[intervals];
		}

		best = new CVSummaryModel[max - min + 1];
	}

	public synchronized void add(AbstractCombinatoricModel model, int interval) {
		if (landscape != null) {
			return;
		}

		AttributeCombination attributes = model.getAttributes();

		if (computeLandscape) {
			List results = (List) landscapeMap.get(attributes);
			if (results == null) {
				results = new ArrayList(intervals);
				landscapeMap.put(attributes, results);
			}
			results.add(model.getTrainResult());
		}

		int index = attributes.size() - min;
		AbstractCombinatoricModel oldModel = models[index][interval];

		if (model.getTrainResult().getStatus() != CombinatoricResult.VALID) {
			StringBuffer b = new StringBuffer();

			b.append("Model ");
			b.append(model.getAttributes());

			if (intervals > 1) {
				b.append(" at CV interval ");
				b.append(interval + 1);
			}

			b.append(": ");
			b.append(model.getTrainResult().getStatusString());

			fireWarning(b.toString());
		}

		if (oldModel == null || model.getTrainResult().compareTo(oldModel.getTrainResult()) > 0) {
			models[index][interval] = model;
		}
	}

	public List getLandscape() {
		if (!computeLandscape) {
			return null;
		}

		if (landscape == null) {
			compactifyLandscape();
		}

		return landscape;
	}

	public Map getLabelMap() {
		if (!computeLandscape) {
			return null;
		}

		if (landscape == null) {
			compactifyLandscape();
		}

		return labelMap;
	}

	private synchronized void compactifyLandscape() {
		Map countMap = new TreeMap();
		landscape = new ArrayList(landscapeMap.size());

		for (Iterator i = landscapeMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			List results = (List) entry.getValue();
			CombinatoricResult result = (CombinatoricResult) ((CombinatoricResult) results.get(0)).clone();
			result.assignAverage(results);

			Integer size = new Integer(((List) entry.getKey()).size());
			Integer count = (Integer) countMap.get(size);

			countMap.put(size, count == null ? new Integer(1) : new Integer(count.intValue() + 1));

			landscape.add(new StringDoublePair(entry.getKey().toString(), result.getFitness()));
		}

		landscape = Collections.unmodifiableList(landscape);
		landscapeMap = null;
		labelMap = countMapToLabelMap(countMap);
	}

	public void read(Reader r) throws IOException {
		throw new UnsupportedOperationException();
	}

	private Map countMapToLabelMap(Map countMap) {
		Map labelMap = new HashMap();
		int runningTotal = 0;

		for (Iterator i = countMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			labelMap.put(entry.getKey().toString(), new Double((double) runningTotal / (landscape.size() - 1)));
			runningTotal += ((Integer) entry.getValue()).intValue();
		}

		return Collections.unmodifiableMap(labelMap);
	}

	public void readLandscape(Reader r) throws IOException {
		LineNumberReader lr = new LineNumberReader(r);
		BufferedReader in = new BufferedReader(lr);
		List landscape = new ArrayList();
		Map countMap = new TreeMap();
		String line;

		while ((line = in.readLine()) != null) {
			Matcher m = patLandscape.matcher(line);

			if (m.matches()) {
				String s = m.group(1);
				double d = Double.parseDouble(m.group(2));
				landscape.add(new StringDoublePair(s, d));

				Integer size = new Integer(AttributeCombination.countAttributesInString(s));
				Integer count = (Integer) countMap.get(size);

				countMap.put(size, count == null ? new Integer(1) : new Integer(count.intValue() + 1));
			} else {
				throw new IOException("Invalid landscape line at line" + lr.getLineNumber());
			}
		}

		this.computeLandscape = true;
		this.landscape = Collections.unmodifiableList(landscape);
		landscapeMap = null;
		labelMap = countMapToLabelMap(countMap);
	}

	public void write(Writer w) {
		PrintWriter out = new PrintWriter(w);

		for (int i = 0; i < best.length; ++i) {
			for (int j = 0; j < intervals; ++j) {
				AbstractCombinatoricModel model = models[i][j];

				out.print("Attr ");
				out.print(model.getAttributes().getComboString());
				out.print(" Train ");
				out.print(model.getTrainResult().getSummaryString());

				if (model.getTestResult() != null) {
					out.print(" Test ");
					out.print(model.getTestResult().getSummaryString());
				}

				out.println();
			}

			best[i].write(w);
		}

		out.flush();
	}

	public void writeLandscape(Writer w) {
		PrintWriter out = new PrintWriter(w);

		for (Iterator i = getLandscape().iterator(); i.hasNext();) {
			StringDoublePair p = (StringDoublePair) i.next();
			out.print(p.toString());
			out.print('\t');
			out.println(p.getDouble());
		}

		out.flush();
	}

	public synchronized AbstractCombinatoricModel getModel(int attrCount, int interval) {
		return models[attrCount - min][interval];
	}

	protected void putModel(AbstractCombinatoricModel model, int attrCount, int interval) {
		models[attrCount - min][interval] = model;
	}

	public synchronized CVSummaryModel getCVSummaryModel(int attrCount) {
		int index = attrCount - min;

		if (best[index] == null) {
			best[index] = computeBest(index);
		}

		return best[index];
	}

	protected void putCVSummaryModel(int attrCount, CVSummaryModel cvsm) {
		best[attrCount - min] = cvsm;
	}

	public synchronized void addWarningListener(StringListener l) {
		warningListeners.add(l);
	}

	public synchronized void removeWarningListener(StringListener l) {
		warningListeners.remove(l);
	}

	protected synchronized void fireWarning(String warning) {
		for (Iterator i = warningListeners.iterator(); i.hasNext();) {
			StringListener l = (StringListener) i.next();
			l.stringReceived(warning);
		}
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public int getIntervals() {
		return intervals;
	}

	public AbstractDataset getData() {
		return data;
	}

	protected synchronized CVSummaryModel computeBest(int index) {
		if (intervals <= 1) {
			return new CVSummaryModel(models[index][0], 1, models[index][0].getTrainResult(), null);
		}

		Map cvcMap = new TreeMap();
		ArrayList trainList = new ArrayList(intervals);
		ArrayList testList = new ArrayList(intervals);
		CombinatoricResult avgTrainResult = null;
		CombinatoricResult avgTestResult = null;

		for (int i = 0; i < intervals; ++i) {
			AbstractCombinatoricModel model = models[index][i];
			ArrayList cvc = (ArrayList) cvcMap.get(model.getAttributes());

			if (cvc == null) {
				cvc = new ArrayList();
				cvcMap.put(model.getAttributes(), cvc);
			}

			cvc.add(model);

			model.test((List) data.getTestSets().get(i));
			model.clearCells();

			if (avgTrainResult == null) {
				avgTrainResult = (CombinatoricResult) model.getTrainResult().clone();
			}

			if (avgTestResult == null) {
				avgTestResult = (CombinatoricResult) model.getTestResult().clone();
			}

			trainList.add(model.getTrainResult());
			testList.add(model.getTestResult());
		}

		ArrayList cvcBest = new ArrayList();

		for (Iterator i = cvcMap.values().iterator(); i.hasNext();) {
			if (cvcBest.isEmpty()) {
				cvcBest.add(i.next());
				continue;
			}

			ArrayList prevBest = (ArrayList) cvcBest.get(0);
			ArrayList current = (ArrayList) i.next();

			if (current.size() < prevBest.size()) {
				continue;
			}

			if (current.size() > prevBest.size()) {
				cvcBest.clear();
			}

			cvcBest.add(current);
		}

		ArrayList bestMatches = null;
		Comparable bestTestResult = null;

		for (Iterator i = cvcBest.iterator(); i.hasNext();) {
			ArrayList cvc = (ArrayList) i.next();
			ArrayList cvcTestList = new ArrayList(cvc.size());

			for (Iterator j = cvc.iterator(); j.hasNext();) {
				AbstractCombinatoricModel model = (AbstractCombinatoricModel) j.next();

				cvcTestList.add(model.getTestResult());
			}

			avgTestResult.assignAverage(cvcTestList);

			if (bestTestResult == null || avgTestResult.compareTo(bestTestResult) > 0) {
				bestTestResult = avgTestResult;
				bestMatches = cvc;
			}
		}

		AbstractCombinatoricModel model = (AbstractCombinatoricModel) ((AbstractCombinatoricModel) bestMatches.get(0)).clone();

		model.train(data);
		avgTrainResult.assignAverage(trainList);
		avgTestResult.assignAverage(testList);

		return new CVSummaryModel(model, bestMatches.size(), avgTrainResult, avgTestResult);
	}
}
