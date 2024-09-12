package gmdr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.epistasis.AbstractDataset;
import org.epistasis.StringDoublePair;
import org.epistasis.combinatoric.AbstractAnalysisThread;
import org.epistasis.combinatoric.AttributeCombination;
import org.epistasis.combinatoric.AttributeCombinationGenerator;
import org.epistasis.combinatoric.FixedRandomACG;
import org.epistasis.combinatoric.TimedRandomACG;
import org.epistasis.combinatoric.mdr.AnalysisFileManager;
import org.epistasis.combinatoric.mdr.BestModelTextGenerator;
import org.epistasis.combinatoric.mdr.CVResultsTextGenerator;
import org.epistasis.combinatoric.mdr.CVSummaryModel;
import org.epistasis.combinatoric.mdr.IfThenRulesTextGenerator;


import addon.score.cal.GeneralizedLinearModelOption;
import ml.options.OptionSet;
import ml.options.Options;
import ml.options.Options.Multiplicity;
import ml.options.Options.Separator;
import DataManage.Dataset;
import DataManage.*;

public class Console {

	static String scorefile;
	static int scoreindex;
	static int filenum;
	static String delim = "\\s+|\\s*,\\s*";
	public static Set<String> selectedMarker = null;
	public static Set<Integer> selectedMarkerIdx = null;
	public static void main(String[] args) throws IOException {
		if (args.length == 1) {
			if (args[0].toLowerCase().compareTo("-help") == 0) {
				Writer w = new OutputStreamWriter(System.out);
				printUsage(w);
			} else if (args[0].toLowerCase().compareTo("-version") == 0) {
				Writer w = new OutputStreamWriter(System.out);
				printVersionHeader(w);
			} else {
				System.out.println("Try java -jar gmdr.jar -help");
			}
			System.exit(0);
		}
		String[] args_option=setFiles(args);
		String[] args0 = calculateScore(args_option);
		if (args0.length < 2) {
			System.exit(0);
		}
		//		String[] args1 = selectMarker(args0);
		String[] args_ = setScoreParameter(args0);
		if (args_.length < 2) {
			System.exit(0);
		}

		Vector<String> v = new Vector<String>();
		for (int i = 0; i < args_.length; i++) {
			if (args_[i].toLowerCase().startsWith("-threshold=")) {
				try {
					AnalysisThread.setR(Double.parseDouble(args_[i].substring("-threshold=".length())));
				} catch (NumberFormatException ex) {
					System.out.println(ex.toString());
					System.out.println("Try: java -jar gmdr.jar -help");
					System.exit(0);
				}
			} else {
				v.addElement(args_[i]);
			}
		}
		args_ = (String[]) v.toArray(new String[v.size()]);

		Options opt = new Options(args_, 1);

		OptionSet optHelp = opt.addSet("help", 0);
		OptionSet optVersion = opt.addSet("version", 0);
		OptionSet optAnalysis = opt.addSet("analysis", filenum);
		OptionSet optFilter = opt.addSet("filter", 2);

		optHelp.addOption("help", Multiplicity.ONCE);
		optVersion.addOption("version", Multiplicity.ONCE);

		optAnalysis.addOption("cv", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
		optAnalysis.addOption("max", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
		optAnalysis.addOption("min", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
		optAnalysis.addOption("nolandscape", Multiplicity.ZERO_OR_ONE);
		optAnalysis.addOption("paired", Multiplicity.ZERO_OR_ONE);
		optAnalysis.addOption("parallel", Multiplicity.ZERO_OR_ONE);
		optAnalysis.addOption("relieff_neighbors", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
		optAnalysis.addOption("relieff_samplesize", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
		optAnalysis.addOption("saveanalysis", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
		optAnalysis.addOption("seed", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
		optAnalysis.addOption("tie", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
		optAnalysis.addOption("forced_search", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
		optAnalysis.addOption("random_search_eval", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
		optAnalysis.addOption("random_search_runtime", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);

		optFilter.addOption("chisq_pvalue", Multiplicity.ZERO_OR_ONE);
		optFilter.addOption("filter", Separator.EQUALS, Multiplicity.ONCE);
		optFilter.addOption("parallel", Multiplicity.ZERO_OR_ONE);
		optFilter.addOption("relieff_neighbors", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
		optFilter.addOption("relieff_samplesize", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
		optFilter.addOption("seed", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
		optFilter.addOption("select_", true, Separator.EQUALS, Multiplicity.ZERO_OR_ONE);

		OptionSet set = opt.getMatchingSet();
		Writer w = new OutputStreamWriter(System.out);

		if (set == optHelp) {
			printUsage(w);
		} else if (set == optVersion) {
			printVersionHeader(w);
		} else if (set == optAnalysis) {
			runAnalysis(set, w);
		} else if (set == optFilter) {
			//runFilter(set, w);
		} else {
			System.out.println("Try: java -jar gmdr.jar -help");
		}
	}
/*
	private static String[] selectMarker(String[] args) {
		String[] args0 = null;
		String[] option = { "-mk=", "-mf=" };
		int[] mask = { -1, -1 };
		int flag = -1;
		String[] mk = null;
		String[] mks = null;
		for (int i = 0; i < args.length; i++) {
			for (int j = 0; j < option.length; j++) {
				if (args[i].toLowerCase().startsWith(option[j])) {
					flag = j;
					mask[j] = i;
					String trimString = args[i].substring(option[j].length());
					if (flag == 0) {
						mk = trimString.split(",");
						break;
					} else {
						mk = new String[1];
						mk[0] = trimString;
					}
				}
			}
		}
		if (mask[0] != -1 && mask[1] != -1) {
			System.err.println("Can not use -mk and -mf options together!\t Try: java -jar gmdr.jar -help.\n");
			System.exit(0);
		}
		if (flag == 0) {
			mks = mk;
		} else {
			FileReader r = null;
			try {
				r = new FileReader(mk[0]);
			} catch (IOException E) {
				E.printStackTrace(System.err);
				System.exit(0);
			}
			LineNumberReader l = new LineNumberReader(r);
			BufferedReader b = new BufferedReader(l);
			String line;
			Set<String> mkset = new TreeSet<String>();
			try {
				while ((line = b.readLine()) != null) {
					String[] markers = line.split(delim);
					Collections.addAll(mkset, markers);
				}
			} catch (IOException E) {
				E.printStackTrace(System.err);
				System.exit(0);
			}
			mks = (String[]) mkset.toArray(new String[mkset.size()]);
		}
		if (flag != -1) {
			ArrayList<Integer> markerIdx = new ArrayList<Integer>();
			ArrayList<String> markerName = new ArrayList<String>();
			for (int i = 0; i < mks.length; i++) {
				if (flag == 1) {
					markerName.add(mks[i].substring(1, mks[i].length() - 1));
				} else {
					if (mks[i].startsWith("\"")) {
						markerName.add(mks[i].substring(1, mks[i].length() - 1));
					} else {
						markerIdx.add(Integer.parseInt(mks[i]) - 1);
					}
				}
			}
			if (markerIdx.size() > 0) {
				selectedMarkerIdx = new TreeSet<Integer>();
				selectedMarkerIdx.addAll(markerIdx);
			}
			if (markerName.size() > 0) {
				selectedMarker = new TreeSet<String>();
				selectedMarker.addAll(markerName);
			}
		}
		if (flag == -1) {
			return args;
		} else {
			args0 = new String[args.length - 1];
			for (int i = 0, j = 0; i < args.length; i++) {
				if (i == mask[flag]) {
					continue;
				}
				args0[j++] = args[i];
			}
			return args0;
		}
	}

	*/
	private static String[] setFiles(String[] args0) {
		String[] args_ = null;
		String[] fileOption = { "-ped=", "-map=","-file=","-bed=","-bim=","-fam=","-bfile=" };
		int[] mask = { -1, -1 ,-1,-1,-1,-1,-1};
		String filename = null;
		Vector<String> file_v=new Vector<>();
		int flag = 0;
		for (int i = 0; i < args0.length; i++) {
			for (int j = 0; j < fileOption.length; j++) {
				if (args0[i].toLowerCase().startsWith(fileOption[j])) {
					mask[j] = i;
					filename = args0[i].substring(fileOption[j].length());
					if (j == 2) {
						
						file_v.add(filename+".map");
						file_v.add(filename+".ped");
						flag++;
						
					} else if (j==6) 
					{
						file_v.add(filename+".bed");
						file_v.add(filename+".fam");
						file_v.add(filename+".bim");
						flag++;
					}else {
						file_v.add(filename);
						flag++;
					}
				}
			}
		}
		filenum=file_v.size();
		Vector<String> args_v=new Vector<>();
		for (int i = 0, j = 0; i < args0.length; i++) {
			if (i == mask[0] || i == mask[1]||i == mask[2]||i == mask[3] || i == mask[4]||i == mask[5]||i == mask[6])
			{
				continue;
			}
			args_v.add(args0[i]);
		}
		args_v.addAll(file_v);
		args_=new String[args_v.size()];
		args_v.toArray(args_);
		return args_;
	}
	
	
	private static String[] calculateScore(String[] args) {
		String[] _args = null;
		GeneralizedLinearModelOption glmo = new GeneralizedLinearModelOption();
		String[] modelOption = { "-response=", "-predictor=", "-family=", "-phefile=" };
		int[] mask = { -1, -1, -1, -1 };
		int flag = 0;
		String scorefile = null;
		for (int i = 0; i < args.length; i++) 
		{
			for (int j = 0; j < modelOption.length; j++) {
				if (args[i].toLowerCase().startsWith(modelOption[j])) {
					flag++;
					mask[j] = i;
					String trimString = args[i].substring(modelOption[j].length());
					if (j == 0) {
						glmo.setResponse(trimString);// index starts from 1;
					} else if (j == 1) {
						String[] pIdx = trimString.split(",");
						// index starts from 1 or the name of the covariates
						glmo.setPredictor(pIdx);
					} else if (j == 2) {
						glmo.setFamily(trimString);
					} else {
						scorefile = glmo.setPhenoFile(trimString);
					}
				}
			}
		}
		if (flag >= 3) {
			glmo.buildScore(null);
			_args = new String[args.length - flag + 2];
			for (int i = 0, j = 0; i < args.length; i++) {
				if (i == mask[0] || i == mask[1] || i == mask[2] || i == mask[3]) {
					continue;
				}
				_args[j++] = args[i];
			}
			_args[_args.length - 2] = "-score=1";
			_args[_args.length - 1] = "-scorefile=" + scorefile;
		} else {
			_args = args;
		}
		return _args;
	}

	private static String[] setScoreParameter(String[] args0) {
		String[] args_ = null;
		String[] scoreOption = { "-score=", "-scorefile=" };
		int[] mask = { -1, -1 };
		String scIdx = null;
		boolean flag = false;
		for (int i = 0; i < args0.length; i++) {
			for (int j = 0; j < scoreOption.length; j++) {
				if (args0[i].toLowerCase().startsWith(scoreOption[j])) {
					mask[j] = i;
					if (j == 0) {
						scIdx = args0[i].substring(scoreOption[j].length());
						if (!scIdx.startsWith("\"")) {
							try {
								scoreindex = Integer.parseInt(scIdx);
							} catch (NumberFormatException ex) {
								System.err.println(ex.toString());
								System.out.println("Try: java -jar gmdr.jar -help");
								System.exit(0);
							}
						} else {
							flag = true;
							scIdx = scIdx.substring(1, scIdx.length() - 1);
						}
					} else {
						scorefile = args0[i].substring(scoreOption[j].length());
					}
				}
			}
		}
		if (flag) {
			scoreindex = getScoreIndex(scIdx, scorefile);
		}
		if (mask[0] != -1 && mask[1] != -1) {
			args_ = new String[args0.length - 2];
			for (int i = 0, j = 0; i < args0.length; i++) {
				if (i == mask[0] || i == mask[1]) {
					continue;
				}
				args_[j++] = args0[i];
			}
		} else {
			args_ = args0;
		}

		return args_;
	}

	private static int getScoreIndex(String scorename, String sFile) {
		String delim = "\\s+";
		FileReader r = null;
		int index = -1;
		try {
			r = new FileReader(sFile);
		} catch (IOException E) {
			System.err.println("Could not find " + sFile);
			E.printStackTrace(System.err);
			System.exit(0);
		}

		LineNumberReader l = new LineNumberReader(r);
		BufferedReader b = new BufferedReader(l);
		String line;
		String[] t = null;

		try {
			if ((line = b.readLine()) != null) {
				t = line.split(delim);
			}
		} catch (IOException E) {
			E.printStackTrace(System.err);
		}
		for (int i = 0; i < t.length; i++) {
			if (t[i].toLowerCase().compareTo(scorename.toLowerCase()) == 0) {
				index = i;
				break;
			}
		}
		if (index == -1) {
			System.err.println("Could not fint " + scorename);
			System.exit(0);
		}
		return index;
	}
/*
	private static void runFilter(OptionSet set, Writer w) {
		PrintWriter out = new PrintWriter(w, true);

		printVersionHeader(w);

		StringBuffer config = new StringBuffer();
		String filter = set.getOption("filter").getResultValue(0);
		ColumnFormat columns = new ColumnFormat(Arrays.asList(new Integer[] { new Integer(20), new Integer(59) }));
		Dataset data = openDataSet((String) set.getData().get(0), false);
		AbstractAttributeScorer scorer = null;
		boolean parallel = false;
		boolean ascending = false;
		File output = new File((String) set.getData().get(1));
		int criterion = 0;
		long seed = Main.defaultRandomSeed;
		Number critValue = new Integer(Main.defaultCriterionFilterTopN);

		config.append(columns.format(Arrays.asList(new String[] { "Input File:", (String) set.getData().get(0) })) + '\n');

		if (!output.getAbsoluteFile().getParentFile().canWrite() && !output.canWrite()) {
			out.println("Error: Unable to open '" + set.getData().get(1) + "' for writing.");
			return;
		}

		config.append("Output file:        " + set.getData().get(1) + '\n');

		if (set.isSet("seed")) {
			try {
				seed = Long.parseLong(set.getOption("seed").getResultValue(0));
			} catch (NumberFormatException ex) {
				out.println("Error: seed accepts integer values only.");
				return;
			}
		}

		config.append(columns.format(Arrays.asList(new String[] { "Random Seed:", Long.toString(seed) })) + '\n');

		if (set.isSet("select_")) {
			String sCriterion = set.getOption("select_").getResultDetail(0);
			String sCritValue = set.getOption("select_").getResultValue(0);

			if (sCriterion.equalsIgnoreCase("N")) {
				try {
					critValue = Integer.valueOf(sCritValue);
				} catch (NumberFormatException ex) {
					out.println("Error: select_N accepts integer values " + "only.");
					return;
				}

				if (critValue.intValue() <= 0 || critValue.intValue() > data.getNumAttributes()) {
					out.println("Error: N must be > 0 and <= number of " + "markers in the data set.");
					return;
				}

				criterion = 0;

				config.append(columns.format(Arrays.asList(new String[] { "Selection:", "Top N (" + critValue.toString() + ')' })) + '\n');
			} else if (sCriterion.equalsIgnoreCase("PCT")) {
				try {
					critValue = Double.valueOf(sCritValue);
				} catch (NumberFormatException ex) {
					out.println("Error: select_PCT accepts numeric " + "values only.");
					return;
				}

				if (critValue.doubleValue() <= 0 || critValue.doubleValue() > 100) {
					out.println("Error: PCT must be > 0 and <= 100");
					return;
				}

				criterion = 1;

				config.append(columns.format(Arrays.asList(new String[] { "Selection:", "Top Percent (" + critValue.toString() + "%)" })) + '\n');
			} else if (sCriterion.equalsIgnoreCase("THRESH")) {
				try {
					critValue = Double.valueOf(sCritValue);
				} catch (NumberFormatException ex) {
					out.println("Error: select_THRESH accepts numeric " + "values only.");
					return;
				}

				criterion = 2;

				config.append(columns.format(Arrays.asList(new String[] { "Selection:", "Threshold (" + critValue.toString() + ")" })) + '\n');
			} else {
				out.println("Error: Invalid selection criterion: " + sCriterion + "\n");
				return;
			}
		} else {
			config.append(columns.format(Arrays.asList(new String[] { "Selection:", "Top N (" + critValue.toString() + ')' })) + '\n');
		}

		if (set.isSet("parallel")) {
			parallel = true;
		}

		config.append("Parallel:           " + Boolean.toString(parallel) + '\n');

		if (filter.equalsIgnoreCase("ChiSquared")) {
			boolean pvalue = false;

			if (set.isSet("chisq_pvalue")) {
				pvalue = true;
				ascending = true;
			}

			if (set.isSet("relieff_neighbors") || set.isSet("relieff_samplesize")) {
				out.println("Warning: ReliefF parameters ignored for " + "ChiSquared filter.");
				out.println();
			}

			scorer = new ChiSquaredScorer(data, pvalue, parallel);

			config.append(columns.format(Arrays.asList(new String[] { "Filter:", "\u03a7\u00b2" })) + '\n');
			config.append(columns.format(Arrays.asList(new String[] { "Use P-Value:", Boolean.toString(pvalue) })) + '\n');
		} else if (filter.equalsIgnoreCase("OddsRatio")) {
			ascending = false;

			if (set.isSet("relieff_neighbors") || set.isSet("relieff_samplesize")) {
				out.println("Warning: ReliefF parameters ignored for " + "OddsRatio filter.");
				out.println();
			}

			if (set.isSet("chisq_pvalue")) {
				out.println("Warning: ChiSquared parameters ignored for " + "OddsRatio filter.");
				out.println();
			}

			scorer = new OddsRatioScorer(data, parallel);

			config.append(columns.format(Arrays.asList(new String[] { "Filter:", "OddsRatio" })) + '\n');
		} else if (filter.equalsIgnoreCase("ReliefF")) {
			int neighbors = Math.min(Main.defaultReliefFNeighbors, data.getNumInstances());

			if (set.isSet("relieff_neighbors")) {
				try {
					neighbors = Integer.parseInt(set.getOption("relieff_neighbors").getResultValue(0));
				} catch (NumberFormatException ex) {
					out.println("Error: relieff_neighbors accepts " + "integer values only.");
					return;
				}

				if (neighbors < 1 || neighbors > data.getNumInstances()) {
					out.println("Error: relieff_neighbors must be > 0 " + "and <= number of instances in the data set ");
					return;
				}
			}

			int samplesize = data.getNumInstances();

			if (set.isSet("relieff_samplesize")) {
				try {
					samplesize = Integer.parseInt(set.getOption("relieff_samplesize").getResultValue(0));
				} catch (NumberFormatException ex) {
					out.println("Error: relieff_samplesize accepts " + "integer values only.");
					return;
				}

				if (samplesize < 1 || samplesize > data.getNumInstances()) {
					out.println("Error: relieff_samplesize must be > 0 " + "and <= number of instances in the data set ");
					return;
				}
			}

			if (set.isSet("chisq_pvalue")) {
				out.println("Warning: ChiSquared parameters ignored for " + "ReliefF filter.");
				out.println();
			}

			scorer = new ReliefFAttributeScorer(data, samplesize, neighbors, samplesize <= 0 || samplesize >= data.getNumInstances(), new Random(seed),
					parallel);

			config.append(columns.format(Arrays.asList(new String[] { "Filter:", "ReliefF" })) + '\n');
			config.append(columns.format(Arrays.asList(new String[] { "Nearest Neighbors:", Integer.toString(neighbors) })) + '\n');
			config.append(columns.format(Arrays.asList(new String[] { "Sample Size:", Integer.toString(samplesize) })) + '\n');
		} else {
			out.println("Unknown filter type: " + filter + "\n");
			return;
		}

		out.println("=== Configuration ===\n");
		out.println(config.toString());

		AttributeRanker ranker = new AttributeRanker();
		AttributeCombination combo = null;
		ranker.setScorer(scorer);

		switch (criterion) {
		// top N
		case 0:
			combo = ranker.selectN(critValue.intValue(), ascending);
			break;
		// top %
		case 1:
			combo = ranker.selectPct(critValue.doubleValue() / 100.0, ascending);
			break;
		// THRESH
		case 2:
			combo = ranker.selectThreshold(critValue.doubleValue(), !ascending, true);
			break;
		}

		out.println("=== Scores ===\n");

		for (Iterator i = ranker.getSortedScores().iterator(); i.hasNext();) {
			StringDoublePair p = (StringDoublePair) i.next();
			out.print(p.toString());
			out.print('\t');
			out.println(p.getDouble());
		}

		out.println("\n=== Selected markers ===\n");
		if (combo.isEmpty()) {
			out.println("No markers selected.  " + "Output file not written.\n");
		} else {
			out.println(combo);
			out.println();
			try {
				data.filter(combo).write(new FileWriter(output));
			} catch (IOException ex) {
				out.println("Error: cannot write to output file.\n");
				return;
			}

			out.print('\'');
			out.print(output);
			out.println("' successfully written.\n");
		}
	}
*/
	private static void runAnalysis(OptionSet set, Writer w) throws IOException {
		PrintWriter out = new PrintWriter(w, true);
		StringBuffer config = new StringBuffer();
		ColumnFormat columns = new ColumnFormat(Arrays.asList(new Integer[] { new Integer(20), new Integer(59) }));

		printVersionHeader(w);

		boolean paired = false;

		if (set.isSet("paired")) {
			paired = true;
		}
		String[] filenames=new String[set.getData().size()];
		set.getData().toArray(filenames);
		Dataset data = openDataSet(filenames, paired);

		config.append(columns.format(Arrays.asList(new String[] { "Datafile:", String.join(",", filenames)})) + '\n');

		int cv = Math.min(Main.defaultCrossValidationCount, data.getNumInstances());

		if (set.isSet("cv")) {
			try {
				cv = Integer.parseInt(set.getOption("cv").getResultValue(0));
			} catch (NumberFormatException ex) {
				out.println("Error: cv accepts integer values only.");
				return;
			}

			if (cv < 1 || cv > data.getNumInstances()) {
				out.println("Error: cv must be > 0 and <= number " + "of instances in the data set.");
				return;
			}
		}

		config.append(columns.format(Arrays.asList(new String[] { "CV Intervals:", Integer.toString(cv) })) + '\n');

		AttributeCombination forced = null;

		int evaluations = 0;
		double runtime = 0;
		String units = "";
		int unitmult = 1;

		int min = Math.min(Main.defaultAttributeCountMin, data.getNumAttributes());

		int max = Math.min(Main.defaultAttributeCountMax, data.getNumAttributes());

		if (set.isSet("forced_search")) {
			String value = set.getOption("forced_search").getResultValue(0);

			if (set.isSet("min") || set.isSet("max")) {
				out.println("Warning: -min and -max ignored for forced " + "analysis.");
			}

			if (set.isSet("random_search_eval") || set.isSet("random_search_runtime")) {
				out.println("Error: Multiple search algorithms specified!");
				return;
			}

			try {
				forced = new AttributeCombination(value, data.getLabels());
			} catch (Exception ex) {
				out.println("Error: " + ex.getMessage());
				return;
			}

			config.append(columns.format(Arrays.asList(new String[] { "Wrapper:", "Forced" })) + '\n');
			config.append(columns.format(Arrays.asList(new String[] { "Forced:", forced.getComboString() })) + '\n');

			min = max = forced.size();
		} else if (set.isSet("random_search_eval")) {
			evaluations = Integer.parseInt(set.getOption("random_search_eval").getResultValue(0));

			if (set.isSet("random_search_runtime")) {
				out.println("Error: Multiple search algorithms specified!");
				return;
			}

			config.append(columns.format(Arrays.asList(new String[] { "Wrapper:", "Random (Evaluations)" })) + '\n');

			config.append(columns.format(Arrays.asList(new String[] { "Evaluations:", Integer.toString(evaluations) })) + '\n');
		} else if (set.isSet("random_search_runtime")) {
			String s = set.getOption("random_search_runtime").getResultValue(0);
			Pattern p = Pattern.compile("^(.+?)([smhd]?)$", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(s);

			if (m.matches()) {
				runtime = Double.parseDouble(m.group(1));

				if (m.group(2).equalsIgnoreCase("S")) {
					units = "Seconds";
				} else if (m.group(2).length() == 0 || m.group(2).equalsIgnoreCase("M")) {
					units = "Minutes";
					unitmult = 60;
				} else if (m.group(2).equalsIgnoreCase("H")) {
					units = "Hours";
					unitmult = 60 * 60;
				} else if (m.group(2).equalsIgnoreCase("D")) {
					units = "Days";
					unitmult = 60 * 60 * 24;
				}
			}
			config.append(columns.format(Arrays.asList(new String[] { "Wrapper:", "Random (Runtime)" })) + '\n');
			config.append(columns.format(Arrays.asList(new String[] { "Runtime:", Double.toString(runtime) })) + ' ' + units + '\n');
		}

		if (forced == null && set.isSet("min")) {
			try {
				min = Integer.parseInt(set.getOption("min").getResultValue(0));
			} catch (NumberFormatException ex) {
				out.println("Error: min accepts integer values only.");
				return;
			}

			if (min < 1 || min > data.getNumAttributes()) {
				out.println("Error: min must be > 0 and <= number " + "of markers in the data set.");
				return;
			}
		}

		if (forced == null && set.isSet("max")) {
			try {
				max = Integer.parseInt(set.getOption("max").getResultValue(0));
			} catch (NumberFormatException ex) {
				out.println("Error: max accepts integer values only.");
				return;
			}

			if (max < 1 || max > data.getNumAttributes()) {
				out.println("Error: max must be > 0 and <= number " + "of markers in the data set.");
				return;
			}
		}

		if (max < min) {
			out.println("Error: max must be >= min.");
			return;
		}

		config.append(columns.format(Arrays.asList(new String[] { "Min Attr:", Integer.toString(min) })) + '\n');

		config.append(columns.format(Arrays.asList(new String[] { "Max Attr:", Integer.toString(max) })) + '\n');

		config.append(columns.format(Arrays.asList(new String[] { "Paired:", Boolean.toString(paired) })) + '\n');

		boolean computeLandscape = true;

		if (set.isSet("nolandscape")) {
			computeLandscape = false;
		}

		boolean parallel = false;

		if (set.isSet("parallel")) {
			parallel = true;
		}

		config.append(columns.format(Arrays.asList(new String[] { "Parallel:", Boolean.toString(parallel) })) + '\n');

		long seed = Main.defaultRandomSeed;

		if (set.isSet("seed")) {
			try {
				seed = Long.parseLong(set.getOption("seed").getResultValue(0));
			} catch (NumberFormatException ex) {
				out.println("Error: seed accepts integer values only.");
				return;
			}
		}

		config.append(columns.format(Arrays.asList(new String[] { "Random Seed:", Long.toString(seed) })) + '\n');

		int tieValue = 1;

		if (set.isSet("tie")) {
			String value = set.getOption("tie").getResultValue(0);

			if (value.equalsIgnoreCase("AFFECTED")) {
				tieValue = Main.defaultTieCells;

				config.append(columns.format(Arrays.asList(new String[] { "Tie Cells:", "AFFECTED" })) + '\n');
			} else if (value.equalsIgnoreCase("UNAFFECTED")) {
				tieValue = 0;

				config.append(columns.format(Arrays.asList(new String[] { "Tie Cells:", "UNAFFECTED" })) + '\n');
			} else if (value.equalsIgnoreCase("UNKNOWN")) {
				tieValue = -1;

				config.append(columns.format(Arrays.asList(new String[] { "Tie Cells:", "UNKNOWN" })) + '\n');
			} else {
				out.println("Error: unknown tie cell value '" + value + "'.");
				return;
			}
		}

		out.println("=== Configuration ===\n");
		out.println(config.toString());

		// dispose score
		if (scorefile != null) {
			Object[][] data2;
			File file = new File(scorefile);
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				String line = br.readLine();
				String[] s = line.split("\t");
				ArrayList data_2 = new ArrayList(0);
				data_2.add(s);
				while ((line = br.readLine()) != null) {
					String[] s_ = line.split("\t");
					if (s.length != s_.length) {
						br.close();
						throw new Exception("Unrecognized file '" + file.toString() + "' format.");
					}
					Double[] data_double = new Double[s_.length];
					for (int i = 0; i < s_.length; i++) {
						data_double[i] = new Double(Double.parseDouble(s_[i]));
					}
					data_2.add(data_double);
				}
				data2 = (Object[][]) data_2.toArray(new Object[data_2.size()][s.length]);
//				org.epistasis.combinatoric.mdr.gui.Frame.data2 = data2;
				ArrayList instances = data.instances;
				try {
					for (int i = 0; i < instances.size(); i++) {
						((AbstractDataset.Instance) instances.get(i)).iniMU(((Double) data2[i + 1][scoreindex - 1]).doubleValue());
					}
					Main.isloadedscore=true;
				} catch (ArrayIndexOutOfBoundsException ex2) {
					System.out.println("Too big colunm index " + scoreindex + " defined for score file!");
					throw ex2;
				}
			} catch (Exception ex) {
				data2 = null;
				System.out.println("Unrecognized score file '" + file.toString() + "' format.");
				if (br != null) {
					try {
						br.close();
					} catch (IOException ex1) {
					}
				}
				System.exit(0);
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException ex) {
					}
				}
			}
		}
		//

		analysis = new AnalysisThread(min, max, data, cv, seed, tieValue, null, new OnEndAttribute(data, min, cv, w), new OnEnd(w), computeLandscape, parallel);

		if (forced != null) {
			analysis.addProducer(new AbstractAnalysisThread.ForcedCombinationProducer(forced, cv));
		} else if (set.isSet("random_search_eval") || set.isSet("random_search_runtime")) {
			Random rand = new Random(seed);

			if (set.isSet("random_search_eval")) {
				for (int i = min; i <= max; ++i) {
					analysis.addProducer(new AbstractAnalysisThread.CombinatoricProducer(new FixedRandomACG(data.getLabels(), i, rand, evaluations), cv));
				}
			} else if (set.isSet("random_search_runtime")) {
				long count = (long) Math.ceil(runtime * unitmult);

				for (int i = min; i <= max; ++i) {
					analysis.addProducer(new AbstractAnalysisThread.CombinatoricProducer(new TimedRandomACG(data.getLabels(), i, rand, count), cv));
				}
			}
		} else {
			for (int i = min; i <= max; ++i) {
				analysis.addProducer(new AbstractAnalysisThread.CombinatoricProducer(new AttributeCombinationGenerator(data.getLabels(), i), cv));
			}
		}

		analysis.run();

		if (set.isSet("saveanalysis")) {
			try {
				AnalysisFileManager afm = new AnalysisFileManager();
				String[] files=new String[set.getData().size()];
				set.getData().toArray(files);
				afm.setAnalysis(data,files, analysis);

				if (set.isSet("forced_search")) {
					afm.putCfg(AnalysisFileManager.cfgWrapper, AnalysisFileManager.cfgValForced);
				} else if (set.isSet("random_search_eval")) {
					afm.putCfg(AnalysisFileManager.cfgWrapper, AnalysisFileManager.cfgValRandom);
					afm.putCfg(AnalysisFileManager.cfgEvaluations, set.getOption("random_search_eval").getResultValue(0));
				} else if (set.isSet("random_search_runtime")) {
					afm.putCfg(AnalysisFileManager.cfgWrapper, AnalysisFileManager.cfgValRandom);

					afm.putCfg(AnalysisFileManager.cfgRuntime, Double.toString(runtime));
					afm.putCfg(AnalysisFileManager.cfgRuntimeUnits, units);
				}
				
				PrintWriter p = new PrintWriter(new FileWriter(set.getOption("saveanalysis").getResultValue(0)));
				afm.write(p);
				p.flush();
			} catch (IOException ex) {
				out.println("Error: Unable to write to analysis file.");
				return;
			}
		}
	}

	private static Dataset openDataSet(String filename[], boolean paired) throws IOException {
		Dataset data = new Dataset();
		data.setPaired(paired);
		data.read(filename);
		return data;
	}

	private static void printUsage(Writer w) {
		PrintWriter out = new PrintWriter(w, true);

		printVersionHeader(w);
		out.println("Usage:");
		out.println();
		out.println("java -jar gmdr.jar");
		out.println("  (GUI mode)");
		out.println();
		out.println("java -jar gmdr.jar -help");
		out.println("  (Display this message)");
		out.println();
		out.println("java -jar gmdr.jar -version");
		out.println("  (Display version information)");
		out.println();
		out.println("java -jar gmdr.jar [analysis options] <datafile>");
		out.println("  (Batch mode analysis)");
		out.println();
		out.println("java -jar gmdr.jar [filter options] <datafile> " + "<outputfile>");
		out.println("  (Batch mode filter)");
		out.println();
		out.println("Analysis Options:");
		out.println();
		out.println("-cv=<int>");
		out.println("-forced_search=<comma-separated marker list>");
		out.println("-max=<int>");
		out.println("-min=<int>");
		out.println("-nolandscape");
		out.println("-paired");
		out.println("-parallel");
		out.println("-random_search_eval=<int>");
		out.println("-random_search_runtime=<double><s|m|h|d>");
		out.println("-saveanalysis=<filename>");
		out.println("-seed=<long>");
		out.println("-tie=<AFFECTED | UNAFFECTED | UNKNOWN>");
		out.println();
		out.println("Score Selection Options:");
		out.println();
		out.println("-score and -scorefile options should be specified togather.");
		out.println("-score=<the index or the name of the column served as the score in the score file>"
				+ " If the name (string) of a score is specified, its name should be braced with \"\"");
		out.println("-scorefile=<the file name of the score file>.  This option works only once -score option is specified");
		out.println();
		out.println("Score Building Options:");
		out.println("The following four options below should be specified together.");
		out.println("-phefile=<specify the file for the phenotypes>");
		out.println("-response=<the name or the index of the response served as the index in the phenotype file>"
				+ " If the name is specified, it should be braced with \"\".");
		out.println("-predictor=<comma-separated the index(es) or the name(s) of the predictor(s)>"
				+ " If the names of a predictor are specified, each of them should be braced with \"\".");
		out.println("-family=<specify the distribution of the response| C | B | O | M>" + " C for linear regression; B for logistic regression, O for proportinal odds model; M for multinomial logit model.");

		out.println();
		//		out.println("Marker Selecction Options:");
		//		out.println();
		//		out.println("-mk=<the names (string) of the markers to be used in GMDR>"
		//				+ " If the names (string) of markers are specified, its name should be braced with \"\""
		//				+ " When more than one markers are specified, please delimit them with ','");
		//		out.println("-mf=<the name of the file in which the selected markers are specified>"
		//				+ " For more than one markers, please delimit them with ',' or whitespace"
		//				+ " The names of the selected markers Do Not Need to be braced by \"\"");
		//		out.println("Filter Options:");
		//		out.println();
		//		out.println("-chisq_pvalue");
		//		out.println("-filter=<CHISQUARED | ODDSRATIO | RELIEFF>");
		//		out.println("-parallel");
		//		out.println("-relieff_neighbors=<int>");
		//		out.println("-relieff_samplesize=<int>");
		out.println("-seed=<long>");
		out.println("-select_<N | PCT | THRESH>=<value>");
		out.println();
	}

	private static void printVersionHeader(Writer w) {
		PrintWriter out = new PrintWriter(w, true);

		out.println("GMDR Version " + Main.version);
		out.println();
	}

	private static AnalysisThread analysis;
	private static final NumberFormat nf = new DecimalFormat("0.0000");

	private static class OnEndAttribute implements Runnable {
		private Dataset data;
		private PrintWriter out;
		private int attrCount;
		private int intervals;

		public OnEndAttribute(Dataset data, int initAttr, int intervals, Writer w) {
			this.data = data;
			this.intervals = intervals;
			attrCount = initAttr;
			out = new PrintWriter(w);
		}

		public void run() {
			out.println("### " + attrCount + "-marker Results ###");
			out.println("\n=== Best Model ===\n");
			out.println(new BestModelTextGenerator((CVSummaryModel) analysis.getModelFilter().getCVSummaryModel(attrCount), intervals, Main.nf, Main.pValueTol));
			out.println("=== If-Then Rules ===\n");
			out.println(new IfThenRulesTextGenerator((CVSummaryModel) analysis.getModelFilter().getCVSummaryModel(attrCount), data.getLabels()));

			if (intervals > 1) {
				out.println("\n=== Cross-Validation Results ===\n");
				out.println(new CVResultsTextGenerator(analysis.getModelFilter(), attrCount, intervals, Main.nf, Main.pValueTol));
			}

			out.flush();

			attrCount++;
		}
	}

	private static class OnEnd implements Runnable {
		private PrintWriter out;

		public OnEnd(Writer w) {
			out = new PrintWriter(w, true);
		}

		public void run() {
			if (analysis.getModelFilter().getLandscape() != null) {
				out.println("### Fitness Landscape ###\n");

				for (Iterator i = analysis.getModelFilter().getLandscape().iterator(); i.hasNext();) {
					StringDoublePair p = (StringDoublePair) i.next();
					out.print(p.toString());
					out.print('\t');
					out.println(p.getDouble());
				}
			}

			out.flush();
		}
	}
}
