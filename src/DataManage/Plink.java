package DataManage;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.rmi.CORBA.Stub;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.xml.crypto.Data;

import org.broadinstitute.variant.variantcontext.Genotype;
import org.molgenis.genotype.Alleles;
import org.molgenis.genotype.GenotypeData;
import org.molgenis.genotype.GenotypeDataException;
import org.molgenis.genotype.GenotypeInfo;
import org.molgenis.genotype.Sample;
import org.molgenis.genotype.plink.BedBimFamGenotypeData;
import org.molgenis.genotype.plink.PedMapGenotypeData;
import org.molgenis.genotype.variant.GeneticVariant;

import GUI.GUIMDR;
import GUI.NewFilter;
import addon.imputation.Imputation;
import gmdr.Main;
public class Plink 
{
	private String pedfile;
	private String mapfile;
	private String bedfile;
	private String famfile;
	private String bimfile;
	private String phefile;
	private String[][] Markers;
	private int nsnp;
	private int nind;
	private int nChr;
	private String[] SNPnames;
	private double[] phe;
	private int[] status;
	public Plink(String[] files)
	{
		 int i=files[0].lastIndexOf(".");
	     String a=files[0].substring(i+1);
        if("bed".equals(a)|"bim".equals(a)|"fam".equals(a))
        {
        	readbfile(files);
        }
        if ("ped".equals(a)|"map".equals(a)) 
        {
			readfile(files);
		}
        String Chr[] = null;
		try {
			Chr = NewFilter.getchromid(files[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        nChr=Chr.length;
	}
	
	public String[] getSNPnames() 
	{
		return SNPnames;
	}
	public String getSNPnameasString() 
	{
		String SNPnameString=String.join("\t", SNPnames);
		return SNPnameString;
	}
	
	public String getSNPnameasString(String seq) 
	{
		String SNPnameString=String.join(seq, SNPnames);
		return SNPnameString;
	}
	public double[] getPhenotype() 
	{
		return phe;
	}
	public int[] getstatus() 
	{
		return status;
	}
	public String[][] getMarkers() 
	{
		return Markers;
	}
	public int getIndividualnum()
	{
		return nind;
	}
	public int getChrNumber()
	{
		return nChr;
	}
	public int getSNPnum()
	{
		return nsnp;
	}
	public String getMarkersPheasString(int i) 
	{
		StringBuffer stringline=new StringBuffer();
		for(int j=0;j<nsnp;j++)
		{
			stringline.append(Markers[i][j]+"\t");
		}
		stringline.append(phe[i]);
		return stringline.toString();
	}
	public String getMarkersStatusasString(int i) 
	{
		StringBuffer stringline=new StringBuffer();
		for(int j=0;j<nsnp;j++)
		{
			stringline.append(Markers[i][j]+"\t");
		}
		stringline.append(status[i]);
		return stringline.toString();
	}
	
	
	public static void main(String[] args) {
		String[] files=new String[2];
		files[0]="C:\\Users\\Hou59\\eclipse-workspace\\gmdr\\example\\example.ped";
		files[1]="C:\\Users\\Hou59\\eclipse-workspace\\gmdr\\example\\example.map";
		//files[2]="C:\\Users\\Hou59\\eclipse-workspace\\gmdr\\example\\example.fam";
		Plink plink=new Plink(files);
	}
	
	
	
	private void readfile(String[] files) 
	{	
		for (int i = 0; i < files.length; i++) 
		{
			int dot=files[i].lastIndexOf(".");
			String extension=files[i].substring(dot+1);
			switch (extension) {
			case "map":
				mapfile=files[i];
				break;
			case "ped":
				pedfile=files[i];
				break;
			}
		}
		GenotypeData genotypeData=null;
		try {
			 genotypeData=new PedMapGenotypeData(new File(pedfile), new File(mapfile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (GenotypeDataException ex) {
			JOptionPane.showMessageDialog(new JFrame(), ex.getMessage()+"\nPlease check the genotype files.","Error",JOptionPane.ERROR_MESSAGE);
			try {
				GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\t"+ ex.getMessage()+"\n", GUIMDR.myUI.keyWordfailed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		Vector<String> tmpSNPname=new Vector<>();
		Vector<String[]> tmpmarkers=new Vector<>();
		
		for(GeneticVariant variant : genotypeData)
		{
			//Iterate over all variants
		//	System.out.println(variant.getPrimaryVariantId());
			tmpSNPname.add(variant.getPrimaryVariantId());
			Vector<String> marker_alleles=new Vector<>();
			for(Alleles sampleAlleles : variant.getSampleVariants())
			{
				//Iterate over the alleles form all samples.
				marker_alleles.add(String.join(" ",sampleAlleles.getAllelesAsString()));
			}
			String[] snpalleles=new String[marker_alleles.size()];
			marker_alleles.toArray(snpalleles);
			tmpmarkers.add(snpalleles);
		}
		nsnp=tmpSNPname.size();
		nind=tmpmarkers.get(0).length;
		Markers=new String[nind][nsnp];
		SNPnames=new String[nsnp];
		tmpSNPname.toArray(SNPnames);
		for(int i=0;i<nsnp;i++)
		{
			for(int j=0;j<nind;j++)
			{
				if (tmpmarkers.get(i)[j].equals("0 0")) 
				{
					Markers[j][i]=". .";
				}else 
				{
					Markers[j][i]=tmpmarkers.get(i)[j];
				}
			}
		}
		phe=new double[nind];
		status=new int[nind];
		int id=0;
		for(Sample sample : genotypeData.getSamples()){
			//Note sex is an enum, to string outputs a human readable form of the gender
			phe[id]= (double) sample.getAnnotationValues().get("phenotype");
			status[id]= (int)phe[id];
			id++;
		}
	  try {
		genotypeData.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

	
	private void readbfile(String[] files) 
	{
		for (int i = 0; i < files.length; i++) 
		{
			int dot=files[i].lastIndexOf(".");
			String extension=files[i].substring(dot+1);
			switch (extension) {
			case "bed":
				bedfile=files[i];
				break;
			case "bim":
				bimfile=files[i];
				break;
			case "fam":
				famfile=files[i];
				break;
			}
		}
		GenotypeData genotypeData=null;
		try {
			 genotypeData=new BedBimFamGenotypeData(new File(bedfile), new File(bimfile), new File(famfile), 1000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\t"+e.getMessage(), GUIMDR.myUI.keyWordfailed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}catch (GenotypeDataException ex) {
			JOptionPane.showMessageDialog(new JFrame(), ex.getMessage()+"\nPlease check the genotype files.","Error",JOptionPane.ERROR_MESSAGE);
			try {
				GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\t"+ ex.getMessage()+"\n", GUIMDR.myUI.keyWordfailed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		Vector<String> tmpSNPname=new Vector<>();
		Vector<String[]> tmpmarkers=new Vector<>();
		for(GeneticVariant variant : genotypeData)
		{
			//Iterate over all variants
		//	System.out.println(variant.getPrimaryVariantId());
			tmpSNPname.add(variant.getPrimaryVariantId());
		
			Vector<String> marker_alleles=new Vector<>();
			for(Alleles sampleAlleles : variant.getSampleVariants())
			{
				//Iterate over the alleles form all samples.
				marker_alleles.add(String.join(" ",sampleAlleles.getAllelesAsString()));
			}
			String[] snpalleles=new String[marker_alleles.size()];
			marker_alleles.toArray(snpalleles);
			tmpmarkers.add(snpalleles);
		}
		nsnp=tmpSNPname.size();
		nind=tmpmarkers.get(0).length;
		Markers=new String[nind][nsnp];
		SNPnames=new String[nsnp];
		tmpSNPname.toArray(SNPnames);
		for(int i=0;i<nsnp;i++)
		{
			for(int j=0;j<nind;j++)
			{
				if (tmpmarkers.get(i)[j].equals("0 0")) 
				{
					Markers[j][i]=". .";
				}else 
				{
					Markers[j][i]=tmpmarkers.get(i)[j];
				}
			}
				
		}
		phe=new double[nind];
		status=new int[nind];
		int id=0;
		for(Sample sample : genotypeData.getSamples()){
			//Note sex is an enum, to string outputs a human readable form of the gender
			phe[id]= (double) sample.getAnnotationValues().get("phenotype");
			status[id]= (int)phe[id];
			id++;
		}
	  
	try {
		genotypeData.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
	}
	
} 