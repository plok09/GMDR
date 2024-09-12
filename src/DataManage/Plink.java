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




import GUI.GUIMDR;
import GUI.NewFilter;
import addon.imputation.Imputation;
import gmdr.Main;
public class Plink 
{
	private String pedfile=null;
	private String mapfile=null;
	private String bedfile=null;
	private String famfile=null;
	private String bimfile=null;
	private String phefile=null;
	private String[][] Markers;
	private int nsnp;
	private int nind;
	private int nChr;
	private String[] SNPnames;
	private double[] phe;
	private int[] status;
	public Plink(String[] files) throws IOException
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
	
	
	public static void main(String[] args) throws IOException {
		String[] files=new String[3];
		files[0]="D:\\Java Respo\\GMDR\\example\\example.bed";
		files[1]="";
		files[2]="";
		Plink plink=new Plink(files);
	}
	
	
	
	private void readfile(String[] files) throws IOException 
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
		PedMapReader genotypeData=new PedMapReader(pedfile, mapfile);
		nsnp=genotypeData.GetNumSNP();
		nind=genotypeData.GetNumInds();
		Markers=genotypeData.GetMarkerbyInd();
		SNPnames=genotypeData.GetSNPsName();
		phe=genotypeData.GetPhes();
		status=new int[nind];
		for(int i=0;i<phe.length;i++)
		{
			status[i]=(int)phe[i];
		}
		
	}

	
	private void readbfile(String[] files) throws IOException 
	{
		for (int i = 0; i < files.length; i++) 
		{
			if (files[i].equals(""))
			{
				continue;
			}
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
		
		BedBimFamReader genotypeData=new BedBimFamReader(bedfile, bimfile, famfile);
	
		nsnp=genotypeData.GetNumSNP();
		nind=genotypeData.GetNumInds();
		Markers=genotypeData.GetMarkerbyInd();
		SNPnames=genotypeData.GetSNPsName();
		phe=genotypeData.GetPhes();
		status=new int[nind];
		for(int i=0;i<phe.length;i++)
		{
			status[i]=(int)phe[i];
		}
	}
	
} 