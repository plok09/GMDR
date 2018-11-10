package gmdr;

import java.awt.Font;
import java.awt.Insets;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Set;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

import org.epistasis.combinatoric.AttributeCombination;

import GUI.GUIMDR;
import gmdr.Console;

public class Main {
	public static final String version = "alpha v0.1";
	public static boolean isloadedscore=false;
	public static Set<String> selectedMarker = null;
	public static Set<Integer> selectedMarkerIdx = null;
	public static final int defaultRandomSeed = 0;
	public static final int defaultAttributeCountMin = 1;
	public static final int defaultAttributeCountMax = 3;
	public static final AttributeCombination defaultForcedAttributeCombination = null;
	public static final double defaultRatioThreshold = 1.0;
	public static final boolean defaultAutoRatioThreshold = false;
	public static final int defaultCrossValidationCount = 10;
	public static final boolean defaultComputeLandscape = false;
	public static final boolean defaultPairedAnalysis = false;
	public static final int defaultTieCellsIndex = 0;
	public static final int defaultTieCells = 1;
 
	public static final int defaultSearchTypeIndex = 0;
	public static final boolean defaultIsRandomSearchEvaluations = true;
	public static final boolean defaultIsRandomSearchRuntime = false;
	public static final int defaultRandomSearchEvaluations = 1000;
	public static final double defaultRandomSearchRuntime = 10.0;
	public static final int defaultRandomSearchRuntimeUnitsIndex = 1;

	public static final int defaultFilterIndex = 0;
	public static final int defaultCriterionIndex = 0;
	public static final int defaultCriterionFilterTopN = 5;
	public static final double defaultCriterionFilterTopPct = 5.0;
	public static final double defaultCriterionFilterThreshold = 0.0;
	public static final boolean defaultChiSquaredPValue = false;
	public static final boolean defaultReliefFWholeDataset = true;
	public static final int defaultReliefFNeighbors = 10;
	public static final int defaultReliefFSampleSize = 100;

	public static final NumberFormat nf;
	public static final double pValueTol = 0.0001;
	public static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static Date date = new Date();
	static {
		DecimalFormat df = new DecimalFormat("0.0000");
		DecimalFormatSymbols sym = df.getDecimalFormatSymbols();
		sym.setNaN("NaN");
		df.setDecimalFormatSymbols(sym);
		nf = df;
	}

	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			Console.main(args);
		} else {
			//

		    try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				UIManager.put("MenuItem.margin", new Insets(2, -22, 2, 1));

			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//
		 GUIMDR gmdrgui=new GUIMDR();
		 gmdrgui.frame();
		}
	}
}