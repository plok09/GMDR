package DataManage;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class PedMapReader 
{
	private static final Pattern SEPARATOR_PATTERN = Pattern.compile("[ \\t]+");
	private ArrayList<String[]> GeneData=null;//row is individuals, column is SNPs
	private ArrayList<String[]> FamData=null;
	private ArrayList<String[]> MapData=null;
	private ArrayList<String> SNPsName=null;
	private int Inds=0;
	private int SNPs=0;
	private static final Charset FILE_ENCODING = Charset.forName("UTF-8");
	public PedMapReader(File PedFile, File Mapfile) throws IOException 
	{
		// TODO Auto-generated constructor stub
		PedFileReader(PedFile);
		if (Mapfile!=null) 
		{
			
		}else 
		{
			SNPsName=new ArrayList<>();
			for(int i=0;i<SNPs;i++)
			{
				SNPsName.add("SNP"+i);
			}
		}
	}
	public ArrayList<String[]> GetMapInfo()
	{
		return MapData;
	}
	
	public PedMapReader(String PedFileads, String Mapfileads) throws IOException 
	{
		// TODO Auto-generated constructor stub
		File PedFile=new File(PedFileads);
		PedFileReader(PedFile);
		if (Mapfileads!=null) 
		{
			File MapFile=new File(Mapfileads);
			MapFileReader(MapFile);
		}else 
		{
			SNPsName=new ArrayList<>();
			for(int i=0;i<SNPs;i++)
			{
				SNPsName.add("SNP"+i);
			}
		}
	}
	
	private void PedFileReader(File PedFile) throws IOException 
	{
		FamData=new ArrayList<>();
		GeneData=new ArrayList<>();
		BufferedReader pedFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(PedFile), FILE_ENCODING));
		String line=null;
		while ((line=pedFileReader.readLine())!=null) 
		{
			String[] elements = SEPARATOR_PATTERN.split(line);
			String[] famline=new String[6];
			for (int i = 0; i < 6; i++) 
			{
				famline[i]=elements[i];
			}
			FamData.add(famline);
			ArrayList<String> MarkerPerInd=new ArrayList<>();
			for (int i = 6; i < elements.length; ) 
			{
				String Alleles=elements[i++]+" "+elements[i++];
				if (Alleles.equals("0 0")) 
				{
					Alleles=". .";
				}
				MarkerPerInd.add(Alleles);
			}
			String[] addinSNPs=new String[MarkerPerInd.size()];
			MarkerPerInd.toArray(addinSNPs);
			GeneData.add(addinSNPs);
		}
		Inds=GeneData.size();
		SNPs=GeneData.get(0).length;
		pedFileReader.close();
	}
	
	private void MapFileReader(File MapFile) throws IOException 
	{
		MapData=new ArrayList<>();
		SNPsName=new ArrayList<>();
		BufferedReader mapFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(MapFile), FILE_ENCODING));
		String line=null;
		while ((line=mapFileReader.readLine())!=null) 
		{
			String[] elements = SEPARATOR_PATTERN.split(line);
			SNPsName.add(elements[1]);
			MapData.add(elements);
			
		}
		mapFileReader.close();
	}
	
	
	/*return Marker Matrix, row is SNPs, column is Individuals*/
	public String[][] GetMarkerbySNP() 
	{
		String[][] marker=new String[SNPs][Inds];
		for (int SNPID = 0; SNPID < SNPs; SNPID++) 
		{
			for (int IndID = 0; IndID < Inds; IndID++) 
			{
				marker[SNPID][IndID]=GeneData.get(IndID)[SNPID];
			}
		}
		return marker;
	}
	/*return Marker Matrix, row is Individuals, column is SNPs*/
	public String[][] GetMarkerbyInd() 
	{
		String[][] marker=new String[Inds][SNPs];
		int IndID=0;
		for(String[] oneSNPs : GeneData)
		{
			marker[IndID]=GeneData.get(IndID);
			IndID++;
		}
		return marker;
	}
	
	public String[] GetMarkerbyInd(int IndID)
	{
		String[] Markers=GeneData.get(IndID);
		return Markers;
	}
	public String[] GetMarkerbySNP(int SNPID)
	{
		String[] Markers=new String[Inds];
		for (int i = 0; i < GeneData.size(); i++) 
		{
			Markers[i]=GeneData.get(i)[SNPID];
		}
		return Markers;
	}
	public double GetPhe(int IndID)
	{
		return Double.valueOf(FamData.get(IndID)[5]);
	}
	public double[] GetPhes() 
	{
		double[] Phe=new double[Inds];
		int id=0;
		for(String[] IndInfo : FamData)
		{
			Phe[id++]=Double.valueOf(IndInfo[5])-1;
		}
		return Phe;
	}
	public int GetNumInds()
	{
		return Inds;
	}
	public int GetNumSNP() 
	{
		return SNPs;
	}
	public String[] GetSNPsName()
	{
		String[] SNPsN=new String[SNPs];
		SNPsName.toArray(SNPsN);
		return SNPsN;
	}
	public int GetSNPIndexByName(String name)
	{
		return SNPsName.indexOf(name);
	}
	
	public static void main(String[] args) throws IOException
	{
		String filesPed="D:\\Java Respo\\GMDR\\example\\example.ped";
		String filesmap="D:\\Java Respo\\GMDR\\example\\example.map";
		PedMapReader test=new PedMapReader(filesPed, filesmap);
	}
	
}
