package org.epistasis.combinatoric.mdr;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.epistasis.StringDoublePair;
import org.epistasis.combinatoric.AttributeCombination;
import org.epistasis.combinatoric.AttributeRankerThread;

import DataManage.Dataset;
import gmdr.AnalysisThread;
import gmdr.Main;

public class AnalysisFileManager {
	public static final String cfgMin = "ATTRIBUTECOUNTMIN";
	public static final String cfgMax = "ATTRIBUTECOUNTMAX";
	public static final String cfgIntervals = "CVINTERVALS";
	public static final String cfgPaired = "PAIRED";
	public static final String cfgSeed = "RANDOMSEED";
	public static final String cfgThreshold = "THRESHOLD";
	public static final String cfgTie = "TIEVALUE";
	public static final String cfgWrapper = "WRAPPER";
	public static final String cfgForced = "FORCED";
	public static final String cfgEvaluations = "EVALUATIONS";
	public static final String cfgRuntime = "RUNTIME";
	public static final String cfgRuntimeUnits = "RUNTIMEUNITS";

	public static final String cfgValAffected = "AFFECTED";
	public static final String cfgValUnaffected = "UNAFFECTED";
	public static final String cfgValUnknown = "UNKNOWN";

	public static final String cfgValExhaustive = "EXHAUSTIVE";
	public static final String cfgValForced = "FORCED";
	public static final String cfgValRandom = "RANDOM";

	private Dataset data;
	private Dataset filtered;
	private ModelFilter modelFilter;
	private String[] datafile;
	private Map cfg = new TreeMap();
	private Map filtercfg = new TreeMap();
	private List filterScores = new ArrayList();
	private AttributeRankerThread ranker;

	public void setAnalysis(Dataset data, String[] datafile, AnalysisThread analysis, AttributeRankerThread ranker) {
		this.setAnalysis(data, datafile, analysis);
		this.ranker = ranker;
		filtercfg = ranker.getConfig();
	}

	public void setAnalysis(Dataset data, String[] datafile, AnalysisThread analysis) {
		setAnalysis(data, datafile, (ModelFilter) analysis.getModelFilter(), analysis.getForced(), analysis.getSeed(), analysis.getTieValue());
		ranker = null;
	}

	public void setAnalysis(Dataset data, String[] datafile, ModelFilter modelFilter, AttributeCombination forced, long seed, int tieValue) {
		this.modelFilter = modelFilter;
		this.data = data;
		this.datafile = datafile;

		cfg.clear();

		cfg.put(cfgMin, Integer.toString(modelFilter.getMin()));
		cfg.put(cfgMax, Integer.toString(modelFilter.getMax()));
		cfg.put(cfgIntervals, Integer.toString(modelFilter.getIntervals()));

		if (forced != null) {
			cfg.put(cfgWrapper, cfgValForced);
			cfg.put(cfgForced, forced.getComboString());
		}

		cfg.put(cfgPaired, Boolean.toString(data.isPaired()));
		cfg.put(cfgSeed, Long.toString(seed));

		switch (tieValue) {
		case 1:
			cfg.put(cfgTie, cfgValAffected);
			break;
		case 0:
			cfg.put(cfgTie, cfgValUnaffected);
			break;
		case -1:
			cfg.put(cfgTie, cfgValUnknown);
			break;
		}
	}

	public void putCfg(String key, String value) {
		cfg.put(key, value);
	}

	public String getCfg(String key) {
		Object value = cfg.get(key);
		return value == null ? null : value.toString();
	}

	public int getMin() {
		int min = Main.defaultAttributeCountMin;

		if (cfg.containsKey(cfgMin)) {
			min = Integer.parseInt((String) cfg.get(cfgMin));
		}

		return min;
	}

	public int getMax() {
		int max = Main.defaultAttributeCountMax;

		if (cfg.containsKey(cfgMax)) {
			max = Integer.parseInt((String) cfg.get(cfgMax));
		}

		return max;
	}

	public int getIntervals() {
		int intervals = Main.defaultCrossValidationCount;

		if (cfg.containsKey(cfgIntervals)) {
			intervals = Integer.parseInt((String) cfg.get(cfgIntervals));
		}

		return intervals;
	}

	public boolean isPaired() {
		boolean paired = Main.defaultPairedAnalysis;

		if (cfg.containsKey(cfgPaired)) {
			paired = Boolean.valueOf((String) cfg.get(cfgPaired)).booleanValue();
		}

		return paired;
	}

	public long getRandomSeed() {
		long seed = Main.defaultCrossValidationCount;

		if (cfg.containsKey(cfgIntervals)) {
			seed = Long.parseLong((String) cfg.get(cfgIntervals));
		}

		return seed;
	}

	public boolean isAutoThreshold() {
		boolean autoThresh = Main.defaultAutoRatioThreshold;

		if (cfg.containsKey(cfgThreshold)) {
			autoThresh = ((String) cfg.get(cfgThreshold)).equalsIgnoreCase("AUTO");
		}

		return autoThresh;
	}

	public double getThreshold() {
		double threshold = Main.defaultRatioThreshold;

		if (cfg.containsKey(cfgThreshold) && !isAutoThreshold()) {
			threshold = Double.parseDouble((String) cfg.get(cfgThreshold));
		}

		return threshold;
	}

	public AttributeCombination getForced() {
		AttributeCombination forced = Main.defaultForcedAttributeCombination;

		if (cfg.containsKey(cfgForced)) {
			if (((String) cfg.get(cfgForced)).equalsIgnoreCase("NONE")) {
				forced = null;
			} else {
				forced = new AttributeCombination((String) cfg.get(cfgForced), data.getLabels());
			}
		}

		return forced;
	}

	public int getTieValue() {
		int tie = Main.defaultTieCells;

		if (cfg.containsKey(cfgForced)) {
			String value = (String) cfg.get(cfgForced);

			if (value.equalsIgnoreCase("AFFECTED")) {
				tie = 1;
			} else if (value.equalsIgnoreCase("UNAFFECTED")) {
				tie = 0;
			} else if (value.equalsIgnoreCase("UNKNOWN")) {
				tie = -1;
			}
		}

		return tie;
	}

	public Dataset getDataset() {
		return data;
	}

	public Dataset getFiltered() {
		return filtered;
	}

	public Map getFilterConfig() {
		return filtercfg;
	}

	public List getFilterScores() {
		return filterScores;
	}

	public ModelFilter getModelFilter() {
		return modelFilter;
	}

	public String[] getDatafile() {
		return datafile;
	}

	public void write(PrintWriter out) {
		out.println("@DatafileInformation");
		out.println("Datafile = " + String.join(",", datafile));
		out.println("Instances = " + data.getNumInstances());
		out.println("Attributes = " + data.getNumAttributes());
		out.println("Ratio = " + Main.nf.format(data.getRatio()));
		out.println("@Configuration");
		write_map(out, cfg);

		out.println("@Datafile");
		data.write(out);

		if (ranker != null) {
			out.println("@FilterConfig");
			write_map(out, ranker.getConfig());
			out.println("@FilterScores");

			for (Iterator i = ranker.getRanker().getSortedScores().iterator(); i.hasNext();) {
				StringDoublePair p = (StringDoublePair) i.next();

				out.print(p.toString());
				out.print(' ');
				out.println(p.getDouble());
			}

			out.println("@FilterSelection");
			out.println(ranker.getCombo().getComboString());
		}

		out.println("@Results");
		modelFilter.write(out);
		if (modelFilter.getLandscape() != null) {
			out.println("@Landscape");
			modelFilter.writeLandscape(out);
		}
		out.println("@End");
		out.flush();
	}

	private void write_map(PrintWriter out, Map map) {
		for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();

			out.print(entry.getKey());
			out.print(" = ");
			out.println(entry.getValue());
		}
	}

	public void read(BufferedReader r) throws IllegalArgumentException {
		try {
			Pattern p = Pattern.compile("^@(.+)$");
			Pattern kv = Pattern.compile("^(\\w+)\\s*=\\s*(.+)$");
			StringBuffer b = new StringBuffer();
			String s = null;
			String section = "";

			data = null;
			modelFilter = null;
			datafile = null;

			while ((s = r.readLine()) != null) {
				Matcher m = p.matcher(s);

				if (m.matches()) {
					Reader sr = new StringReader(b.toString());

					if (section.equals("DATAFILE")) {
						data = new Dataset();
						data.read(sr);
						data.setPaired(isPaired());
					} else if (section.equals("RESULTS")) {
						double threshold = (isAutoThreshold() && data != null) ? data.getRatio() : getThreshold();
						if (modelFilter == null) {
							modelFilter = new ModelFilter(getMin(), getMax(), getIntervals(), threshold, data, false);
						}

						modelFilter.read(sr);
					} else if (section.equals("LANDSCAPE")) {
						double threshold = (isAutoThreshold() && data != null) ? data.getRatio() : getThreshold();
						if (modelFilter == null) {
							modelFilter = new ModelFilter(getMin(), getMax(), getIntervals(), threshold, data, true);
						}

						modelFilter.readLandscape(sr);
					} else if (section.equals("END")) {
						break;
					}

					section = m.group(1).toUpperCase();
					b = new StringBuffer();
				} else if (section.equals("DATAFILEINFORMATION")) {
					Matcher m2 = kv.matcher(s);

					if (!m2.matches()) {
						throw new IllegalStateException();
					}

					String key = m2.group(1).toUpperCase();
					String value = m2.group(2);

					if (key.equals("DATAFILE")) {
						datafile = value.split(",");
					}
				} else if (section.equals("CONFIGURATION")) {
					Matcher m2 = kv.matcher(s);

					if (!m2.matches()) {
						throw new IllegalStateException();
					}

					String key = m2.group(1).toUpperCase();
					String value = m2.group(2);

					cfg.put(key, value);
				} else if (section.equals("FILTERCONFIG")) {
					Matcher m2 = kv.matcher(s);

					if (!m2.matches()) {
						throw new IllegalStateException();
					}

					String key = m2.group(1).toUpperCase();
					String value = m2.group(2);

					filtercfg.put(key, value);
				} else if (section.equals("FILTERSCORES")) {
					String[] values = s.split("\\s+");

					filterScores.add(new StringDoublePair(values[0], Double.parseDouble(values[1])));
				} else if (section.equals("FILTERSELECTION")) {
					filtered = (Dataset) data.filter(new AttributeCombination(s, data.getLabels()));
				} else if (section.length() == 0) {
					throw new IllegalStateException();
				} else {
					b.append(s + '\n');
				}
			}

			if (data == null || modelFilter == null) {
				throw new IllegalArgumentException();
			}
		} catch (Exception ex) {
			throw new IllegalArgumentException("Invalid or corrupt analysis file.");
		}
	}
}
