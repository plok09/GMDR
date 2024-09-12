package org.epistasis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.epistasis.AbstractDataset.Instance;

import DataManage.Plink;
import gmdr.Console;
import gmdr.Main;

import addon.imputation.Imputation;

public abstract class AbstractDataset extends AbstractList implements Cloneable {

	private static String delim = "\\s+";
	private AttributeLabels labels = new AttributeLabels();
	public ArrayList instances = new ArrayList();
	private ArrayList levels = null;
	protected ArrayList train = new ArrayList();
	protected ArrayList test = new ArrayList();
	private Imputation imp;
	private int[] selectedMarkerIdx;
	protected static  boolean isloadedScore=false;	
	public void IsloadScore(boolean isloaded) 
		{
			isloadedScore=isloaded;
		}

	public static class Instance extends AbstractList {

		private List values;
		private Object status;
		// cgb begin
		private Object mu;
		// cgb end
		public Instance(List values, Object status) {
			this.status = status;
			this.values = values;
		}

		public void impute(int idx, Object code) {
			if (idx < values.size()) {
				values.set(idx, code);
			} else {
				status = code;
			}
		}

		// cgb begin
		public void iniMU(double mu) {
			this.mu = new Double(mu);
		}

		public void iniMU(Object mu) {
			this.mu = mu;
		}

		public Object getMU() {
			return mu;
		}

		public double getMU_double() {
			return getDouble(mu);
		}

		public double getInstScore() {
		//	boolean isLoadedScore = isloadscore;
			if (isloadedScore) {
				double s = ((Double) mu).doubleValue();
				return s;
			} else {
				double c = ((Integer) status).doubleValue();
				double s = ((Double) mu).doubleValue();
				return c - s;
			}
		}

		private static double getDouble(Object o) {
			return ((Double) o).doubleValue();
		}

		// cgb end
		public Object getStatus() {
			return status;
		}

		public int size() {
			return values == null ? 0 : values.size();
		}

		public Object get(int index) {
			return values == null ? null : values.get(index);
		}

		public void add(int index, Object obj) {
			values.add(index, obj);
		}

		public Object remove(int index) {
			return values.remove(index);
		}

		public String toString() {
			StringBuffer b = new StringBuffer();

			for (int i = 0; i < size(); ++i) {
				b.append(get(i));
				b.append("\t");
			}

			b.append(status);

			return b.toString();
		}

	}

	public abstract Instance createInstance(String[] fields);

	public List getTrainSets() {
		return Collections.unmodifiableList(train);
	}

	public List getTestSets() {
		return Collections.unmodifiableList(test);
	}

	public void setLabels(AttributeLabels labels) {
		this.labels = labels;
	}

	abstract public double getRatio();

	public AttributeLabels getLabels() {
		return labels;
	}

	public int getNumAttributes() {
		return labels.size();
	}

	public int getNumInstances() {
		return size();
	}

	public int size() {
		return instances.size();
	}

	public Object get(int index) {
		return instances.get(index);
	}

	public Object set(int index, Object element) {
		return instances.set(index, (Instance) element);
	}

	public void add(int index, Object element) {
		modCount++;
		instances.add(index, (Instance) element);
	}

	public Object remove(int index) {
		modCount++;
		return instances.remove(index);
	}

	public String setSelectedMarkerIndex(String line) {
		String[] title = line.split(delim);
		String selectedMarkers = null;

		if (Main.selectedMarkerIdx == null && Main.selectedMarker == null) {
			selectedMarkerIdx = new int[title.length - 1];
			selectedMarkers = new String();
			for (int i = 0; i < title.length - 1; i++) {
				selectedMarkerIdx[i] = i;
			}
			return line;
		} else {
			Set<Integer> MI = new TreeSet<Integer>();
			if (Main.selectedMarkerIdx != null) {
				MI.addAll(Main.selectedMarkerIdx);
			}
			if (Main.selectedMarker != null) {
				for (Iterator<String> e = Main.selectedMarker.iterator(); e.hasNext();) {
					String m = e.next();
					for (int i = 0; i < title.length - 1; i++) {
						if (title[i].compareTo(m) == 0) {
							MI.add(new Integer(i));
							break;
						}
					}
				}
			}
			if (MI.size() == 0) {
				System.err.println("No markers were selected!");
				System.exit(0);
			}
			selectedMarkerIdx = new int[MI.size()];
			int i = 0;
			selectedMarkers = new String();
			for (Iterator<Integer> e = MI.iterator(); e.hasNext();) {
				Integer I = e.next();
				selectedMarkers += title[I.intValue()] + "\t";
				selectedMarkerIdx[i++] = I.intValue();
			}
			selectedMarkers += title[title.length - 1];
			return selectedMarkers;
		}
	}

	private String[] getSubStringArray(String[] str, int len) {
		String[] subArray = new String[selectedMarkerIdx.length + 1];
		for (int i = 0; i < selectedMarkerIdx.length; i++) {
			if (str.length == len + 1) {//genotype
				subArray[i] = str[selectedMarkerIdx[i]];
			} else if (str.length == 2 * len + 1) {//allels
				if (str[2 * selectedMarkerIdx[i]].compareTo(".") == 0 || str[2 * selectedMarkerIdx[i] + 1].compareTo(".") == 2) {
					subArray[i] = ".";
				} else {
					StringBuilder sb = new StringBuilder();
					if (str[2 * selectedMarkerIdx[i]].compareTo(str[2 * selectedMarkerIdx[i] + 1]) > 0 ) {
						sb.append(str[2 * selectedMarkerIdx[i]]);
						sb.append(str[2 * selectedMarkerIdx[i]+1]);
					} else {
						sb.append(str[2 * selectedMarkerIdx[i]+1]);
						sb.append(str[2 * selectedMarkerIdx[i]]);						
					}
					subArray[i] = sb.toString();
//					subArray[i] = str[2 * selectedMarkerIdx[i]].compareTo(str[2 * selectedMarkerIdx[i] + 1]) > 0 ? str[2 * selectedMarkerIdx[i]]
//							+ str[2 * selectedMarkerIdx[i] + 1] : str[2 * selectedMarkerIdx[i] + 1] + str[2 * selectedMarkerIdx[i]];
				}
			} else {
				try {
					throw new Exception("Expected " + (len) + "/" + (2 * len) + " genotypes/alleles , found " + subArray.length + ".");
				} catch (Exception E) {
					E.printStackTrace(System.err);
				}
			}
		}
		subArray[subArray.length - 1] = str[str.length - 1];
		return subArray;
	}

	public void read(Reader r) throws IOException {
		ArrayList<ArrayList> missingGenotypeInformation = new ArrayList();
		clear();
		LineNumberReader l = new LineNumberReader(r);
		BufferedReader b = new BufferedReader(l);
		String line;

		if ((line = b.readLine()) == null) {
			labels = new AttributeLabels();
			return;
		}
		int len = line.split(delim).length - 1;
		String selectedMarkers = setSelectedMarkerIndex(line);
		labels = new AttributeLabels(selectedMarkers.split(delim), true);
		imp = new Imputation();
		imp.initial(labels.size() + 1);
		try {
			while ((line = b.readLine()) != null) {
				String[] fields = line.split(delim);
				String[] subFields = getSubStringArray(fields, len);
				ArrayList m = imp.MarkerCensus(subFields);
				if (m != null) {
					missingGenotypeInformation.add(m);
				}
				add(createInstance(subFields));
			}
		} catch (Exception e) {
			throw new IOException(Integer.toString(l.getLineNumber()) + ':' + e.getMessage());
		}
		imp.CalculateGenotypeFrequency();
		for (Iterator e = missingGenotypeInformation.iterator(); e.hasNext();) {
			ArrayList g = (ArrayList) e.next();
			int id = ((Integer) g.get(0)).intValue();
			Instance inst = (Instance) get(id);
			for (int j = 1; j < g.size(); j++) {
				int idx = ((Integer) g.get(j)).intValue();
				String imputed_code = imp.generateGenotype(((Integer) g.get(j)).intValue());
				inst.impute(((Integer) g.get(j)).intValue(), imputed_code);
			}
		}

		// cgb begin
		double ratio = getRatio();
		double mu = ratio / (1 + ratio);
		for (int i = 0; i < instances.size(); i++) {
			((Instance) instances.get(i)).iniMU(mu);
		}
		// cgb end
	}
	public void read(String[]  filename) throws IOException
	{
		ArrayList<ArrayList> missingGenotypeInformation = new ArrayList();
		clear();
		Plink plinkread=new Plink(filename);
		String line=plinkread.getSNPnameasString();
		line+="\tclass";
		String selectedMarkers = setSelectedMarkerIndex(line);
		labels = new AttributeLabels(selectedMarkers.split(delim),true);
		try {
			imp = new Imputation();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		imp.initial(labels.size() + 1);
		int len=plinkread.getSNPnum();
		try {
			for(int i=0;i<plinkread.getIndividualnum();i++)
			{
				String line_markers_phe=plinkread.getMarkersStatusasString(i);
				String[] fields = line_markers_phe.split(delim);
				String[] subFields = getSubStringArray(fields, len);
				ArrayList m = imp.MarkerCensus(subFields);
				if (m != null) {
					missingGenotypeInformation.add(m);
				}
				add(createInstance(subFields));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		imp.CalculateGenotypeFrequency();
		for (Iterator e = missingGenotypeInformation.iterator(); e.hasNext();) 
		{
			ArrayList g = (ArrayList) e.next();
			int id = ((Integer) g.get(0)).intValue();
			Instance inst = (Instance) get(id);
			for (int j = 1; j < g.size(); j++) {
				int idx = ((Integer) g.get(j)).intValue();
				String imputed_code = imp.generateGenotype(((Integer) g.get(j)).intValue());
				inst.impute(((Integer) g.get(j)).intValue(), imputed_code);
			}
		}
		// cgb begin
		double ratio = getRatio();
		double mu = ratio / (1 + ratio);
		for (int i = 0; i < instances.size(); i++) {
			((Instance) instances.get(i)).iniMU(mu);
		}
		// cgb end
		
	}

	public void read(Plink plinkread)
	{
		ArrayList<ArrayList> missingGenotypeInformation = new ArrayList();
		clear();
//		Plink plinkread=new Plink(filename);
		String line=plinkread.getSNPnameasString();
		line+="\tclass";
		String selectedMarkers = setSelectedMarkerIndex(line);
		labels = new AttributeLabels(selectedMarkers.split(delim),true);
		try {
			imp = new Imputation();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		imp.initial(labels.size() + 1);
		int len=plinkread.getSNPnum();
		try {
			for(int i=0;i<plinkread.getIndividualnum();i++)
			{
				String line_markers_phe=plinkread.getMarkersStatusasString(i);
				String[] fields = line_markers_phe.split(delim);
				String[] subFields = getSubStringArray(fields, len);
				ArrayList m = imp.MarkerCensus(subFields);
				if (m != null) {
					missingGenotypeInformation.add(m);
				}
				add(createInstance(subFields));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		imp.CalculateGenotypeFrequency();
		for (Iterator e = missingGenotypeInformation.iterator(); e.hasNext();) 
		{
			ArrayList g = (ArrayList) e.next();
			int id = ((Integer) g.get(0)).intValue();
			Instance inst = (Instance) get(id);
			for (int j = 1; j < g.size(); j++) {
				int idx = ((Integer) g.get(j)).intValue();
				String imputed_code = imp.generateGenotype(((Integer) g.get(j)).intValue());
				inst.impute(((Integer) g.get(j)).intValue(), imputed_code);
			}
		}
		// cgb begin
		double ratio = getRatio();
		double mu = ratio / (1 + ratio);
		for (int i = 0; i < instances.size(); i++) {
			((Instance) instances.get(i)).iniMU(mu);
		}
		// cgb end
		
	}

	public List getLevels() {
		if (levels == null) {
			levels = new ArrayList(getNumAttributes());
			for (int i = 0; i < getNumAttributes(); ++i) {
				levels.add(new TreeSet());
			}

			for (Iterator i = iterator(); i.hasNext();) {
				Instance inst = (Instance) i.next();
				for (int j = 0; j < getNumAttributes(); ++j) {
					Set s = (Set) levels.get(j);
					s.add(inst.get(j));
				}
			}
		}

		return levels;
	}

	public void write(Writer w) {
		if (labels.size() == 0) {
			return;
		}

		PrintWriter p = new PrintWriter(w);

		p.println(labels);

		for (Iterator i = iterator(); i.hasNext();) {
			p.println(i.next());
			p.flush();
		}

		p.flush();
	}

	public void partition(int intervals, Random rnd) {
		train.clear();
		test.clear();

		if (intervals < 1 || intervals > size()) {
			return;
		}

		if (intervals == 1) {
			train.add(this);
			return;
		}

		train.ensureCapacity(intervals);
		test.ensureCapacity(intervals);

		for (int i = 0; i < intervals; ++i) {
			AbstractDataset a = (AbstractDataset) clone();
			a.clear();
			train.add(a);
			a = (AbstractDataset) clone();
			a.clear();
			test.add(a);
		}

		ArrayList inst = instances;

		if (rnd != null) {
			inst = (ArrayList) instances.clone();
			Collections.shuffle(inst, rnd);
		}

		for (int i = 0; i < inst.size(); ++i) {
			((AbstractDataset) test.get(i % intervals)).add(inst.get(i));
		}

		for (int i = 0; i < intervals; ++i) {
			for (int j = 0; j < intervals; ++j) {
				if (i != j) {
					((AbstractDataset) train.get(i)).addAll(((AbstractDataset) test.get(j)));
				}
			}
		}
	}

	public List computeAttributeVector(int index) {
		List attribute = new ArrayList(size());

		for (Iterator i = iterator(); i.hasNext();) {
			attribute.add(((Instance) i.next()).get(index));
		}

		return Collections.unmodifiableList(attribute);
	}

	public List computeStatusVector() {
		List attribute = new ArrayList(size());

		for (Iterator i = iterator(); i.hasNext();) {
			attribute.add(((Instance) i.next()).getStatus());
		}

		return Collections.unmodifiableList(attribute);
	}

	public AbstractDataset filter(List attributes) {
		AbstractDataset filtData = (AbstractDataset) clone();
		filtData.clear();

		for (Iterator i = iterator(); i.hasNext();) {
			AbstractDataset.Instance instance = (AbstractDataset.Instance) i.next();

			ArrayList filtAttr = new ArrayList(attributes.size());
			for (Iterator j = attributes.iterator(); j.hasNext();) {
				filtAttr.add(instance.get(((Integer) j.next()).intValue()));
			}

			filtData.add(new AbstractDataset.Instance(filtAttr, instance.getStatus()));
		}

		if (getLabels() != null) {

			ArrayList filtLabelList = new ArrayList(attributes.size());
			for (Iterator j = attributes.iterator(); j.hasNext();) {
				filtLabelList.add(getLabels().get(((Integer) j.next()).intValue()));
			}

			filtData.setLabels(new AttributeLabels((String[]) filtLabelList.toArray(new String[0]), getLabels().getClassLabel()));
		}

		return filtData;
	}

	public void addAttribute(int idx, String label, List values) {
		if (idx < 0 || idx > getNumAttributes()) {
			throw new IndexOutOfBoundsException(Integer.toString(idx));
		}

		if (getLabels().contains(label)) {
			throw new IllegalArgumentException("Label '" + label + "' already exists.");
		}

		if (values.size() != getNumInstances()) {
			throw new IllegalArgumentException("Expected " + getNumInstances() + " values; got " + values.size());
		}

		getLabels().add(idx, label);

		for (Iterator i = iterator(), j = values.iterator(); i.hasNext();) {
			((Instance) i.next()).add(idx, j.next());
		}

		if (levels != null) {
			Set s = new TreeSet();
			s.addAll(values);
			levels.add(idx, s);
		}
	}

	public List getAttribute(int idx) {
		if (idx < 0 || idx > getNumAttributes()) {
			throw new IndexOutOfBoundsException(Integer.toString(idx));
		}

		List attr = new ArrayList(getNumInstances());

		for (Iterator i = iterator(); i.hasNext();) {
			attr.add(((Instance) i.next()).get(idx));
		}

		return attr;
	}

	public List getAttribute(String label) {
		Integer idx = (Integer) labels.get(label);

		if (idx == null) {
			throw new NoSuchElementException(label);
		}

		return getAttribute(idx.intValue());
	}

	public List removeAttribute(int idx) {
		List attr = getAttribute(idx);

		for (Iterator i = iterator(); i.hasNext();) {
			((Instance) i.next()).remove(idx);
		}

		labels.remove(idx);

		if (levels != null) {
			levels.remove(idx);
		}

		return attr;
	}

	public List removeAttribute(String label) {
		Integer idx = (Integer) labels.get(label);

		if (idx == null) {
			throw new NoSuchElementException(label);
		}

		return removeAttribute(idx.intValue());
	}

	public Object clone() {
		try {
			AbstractDataset d = (AbstractDataset) super.clone();

			d.labels = labels;
			d.levels = levels;
			d.instances = (ArrayList) instances.clone();
			d.train = (ArrayList) train.clone();
			d.test = (ArrayList) test.clone();

			return d;
		} catch (CloneNotSupportedException ex) {
			return null;
		}
	}
}
