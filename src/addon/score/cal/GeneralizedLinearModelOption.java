package addon.score.cal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import addon.imputation.PhenotypeImputation;

public class GeneralizedLinearModelOption {
	private String delim = "\\s+";
	private int[] responseIdx = null;
	private String responseStr = null;
	private int[] predictorIdx = null;
	private ArrayList<String> predictorStr = null;
	private int model = 0;
	private String pheFile;
	private String scorefile;
	private ArrayList<String> title = new ArrayList<String>();

	public GeneralizedLinearModelOption() {
	}

	public void setResponse(String resStr) {
		responseStr = resStr;
	}

	public void setPredictor(String[] idx) {
		predictorStr = new ArrayList<String>();
		Collections.addAll(predictorStr, idx);
	}

	public void setFamily(String m) { // c or 0 for linear regression, b or 1, 
										// for logistic regression, o or 2 for ordinal logit, m or 3 for multiclass logit
		if (m.toLowerCase().compareTo("c") == 0) {
			model = 0;
		} else if (m.toLowerCase().compareTo("b") == 0) {
			model = 1;
		} else if (m.toLowerCase().compareTo("o") == 0) {
			model =2;
		} else if (m.toLowerCase().compareTo("m") == 0)
		{
			model=3;
		}
		else {
			System.err.println(m + " is an unknow parameter for family.\n"
					+ "For option family, c is for the continuous distribution and b for the binary distribution.");
			System.exit(0);
		}
	}

	public String setPhenoFile(String f) {
		pheFile = f;
		scorefile = "_" + f;
		readTitle();
		return scorefile;
	}

	private int findString(String str) {
		int idx = -1;
		for (int i = 0; i < title.size(); i++) {
			if (str.toLowerCase().compareTo(title.get(i).toLowerCase()) == 0) {
				idx = i;
				break;
			}
		}
		return idx;
	}

	private void parseIndex() {
		responseIdx = new int[1];
		responseIdx[0] = -1;
		if (responseStr.startsWith("\"")) {
			String resStr = responseStr.substring(1, responseStr.length() - 1);
			responseIdx[0] = findString(resStr);
			if (responseIdx[0] == -1) {
				System.err.println("Could not find the phenotype: " + responseStr + ".");
				System.exit(0);
			}
		} else {
			responseIdx[0] = Integer.parseInt(responseStr) - 1;
		}

		if (predictorStr != null) {
			predictorIdx = new int[predictorStr.size()];
			for (int i = 0; i < predictorIdx.length; i++) {
				String pi = (String) predictorStr.get(i);
				if (pi.startsWith("\"")) {
					String p = pi.substring(1, pi.length() - 1);
					predictorIdx[i] = findString(p);
				} else {
					predictorIdx[i] = Integer.parseInt(pi) - 1;
				}
				if (predictorIdx[i] < 0) {
					System.out.println("Could not find the covariates " + pi + " in " + pheFile + ".");
					System.exit(0);
				}
			}
		}
	}

	private void readTitle() {
		String delim = "\\s+";
		FileReader r = null;
		try {
			r = new FileReader(pheFile);
		} catch (IOException E) {
			System.err.println("Could not find " + pheFile);
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
			title.add(t[i]);
		}
	}

	public void buildScore(String f) {
		parseIndex();

		String file = f == null ? pheFile : f;
		FileReader r = null;
		try {
			r = new FileReader(file);
		} catch (IOException E) {
			System.err.println("Could not find " + pheFile);
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

		PhenotypeImputation imputation = new PhenotypeImputation(t.length);
		int c = 1;
		try {
			while ((line = b.readLine()) != null) {
				c++;
				String[] fields = line.split(delim);
				if (fields.length != t.length) {
					throw new Exception("line " + c + " in the file " + file + " does not match " + fields.length + ".");
				}
				imputation.addValues(fields);
			}
		} catch (Exception E) {
			E.printStackTrace(System.err);
		}
		double[][] cov = null;
		if (predictorIdx != null) {
			imputation.ImplementImputation(predictorIdx);
			cov = imputation.getVariable(predictorIdx);
		}
		imputation.ImplementImputation(responseIdx[0], model == 0 ? true : false);
		double[] phe = imputation.getVariable(responseIdx[0]);

		double[] res = null;
		if (model == 0) {
			LinearRegression lr = new LinearRegression(phe, cov, true);
			lr.MLE();
			res = lr.getResiduals1();
		} else if (model == 1) {
			LogisticRegression lg = new LogisticRegression(phe, cov, true);
			lg.MLE();
			res = lg.getResiduals1();
		}else if (model==2) 
		{
			double[] tmpy=phe;
        	Arrays.sort(tmpy);
        	List<Double> listy = new ArrayList<>();  
        	listy.add(tmpy[0]);
            for(int i=1;i<tmpy.length;i++)
            {  
               if(tmpy[i]!=listy.get(listy.size()-1)){  
            	   listy.add(tmpy[i]);  
               }  
           }  
			OrdinalClassLogisticRegression olRegression=new OrdinalClassLogisticRegression(phe, cov, listy.size(), false);
			olRegression.ordgeeEst();
			res=olRegression.Residual();
		}else if (model==3) 
		{
			double[] tmpy=phe;
        	Arrays.sort(tmpy);
        	List<Double> listy = new ArrayList<>();  
        	listy.add(tmpy[0]);
            for(int i=1;i<tmpy.length;i++)
            {  
               if(tmpy[i]!=listy.get(listy.size()-1))
               {  
            	   listy.add(tmpy[i]);  
               }  
            }  
			MultiClassLogisticRegression mlr=new MultiClassLogisticRegression(phe, cov, listy.size());
			mlr.MLE();
			res=mlr.avgResidual1D();
		}
		String out = scorefile;
		PrintWriter output = null;
		try {
			output = new PrintWriter(new File(out));
		} catch (IOException E) {
			E.printStackTrace(System.err);
			System.exit(0);
		}
		output.println(getScoreName());

		for (int i = 0; i < res.length; i++) {
			output.println(res[i]);
		}
		output.close();
	}

	private String getScoreName() {
		String sn;
		if (model == 0) {
			sn = new String(title.get(responseIdx[0]) + "=");
		} else {
			sn = new String("logit(" + title.get(responseIdx[0]) + ")=");
		}
		if (predictorIdx != null) {
			for (int i = 0; i < predictorIdx.length; i++) {
				if (i == predictorIdx.length - 1) {
					sn += title.get(predictorIdx[i]);
				} else {
					sn += title.get(predictorIdx[i]) + "+";
				}
			}
		} else {
			sn += "intercept";
		}
		return sn;
	}
}
