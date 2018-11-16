package DataManage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;
import java.util.regex.Pattern;

import javax.print.attribute.standard.RequestingUserName;

public class BedBimFamReader {
	private static final Pattern SEPARATOR_PATTERN = Pattern.compile("[ \\t]+");
	private static final byte MAGIC_NUMBER_1 = 108;
	private static final byte MAGIC_NUMBER_2 = 27;
	private static final byte MODE = 1; //We only write snp major mode
	private static final int READER_MASK = 3;
	private static final int HOMOZYGOTE_FIRST = 0;
	private static final int HOMOZYGOTE_SECOND = 3;
	private static final int HETEROZYGOTE = 2;
	private static final int MISSING = 1;
	private ArrayList<String[]> GeneData=null;/* row is SNPs, column is individuals*/
	private ArrayList<String[]> FamData=null;
	private ArrayList<String> SNPsName=null;
	private int Inds=0;
	private int SNPs=0;
	private ArrayList<String[]> BimData=null; 
	private static final Charset FILE_ENCODING = Charset.forName("UTF-8");
	
	public BedBimFamReader(File bedFile, File bimFile, File famFile) throws IOException
	{
		// TODO Auto-generated constructor stub
		FamFileReader(famFile);
		BedFileReader(bedFile);
		if (bimFile!=null)
		{
			if (!bimFile.isFile()) {
				return;
			}
			BimFileReader(bimFile);
			
		}else {
			SNPs=GeneData.size();
			SNPsName=new ArrayList<>();
			for(int i=0;i<SNPs;i++)
			{
				SNPsName.add("SNP"+i);
			}
				
		}
		
	}
	
	public ArrayList<String[]> GetMapInfo()
	{
		return BimData;
	}
	public BedBimFamReader(String bedFileads, String bimFileads, String famFileads) throws IOException
	{
		// TODO Auto-generated constructor stub
		File bedFile=new File(bedFileads);

		File famFile=new File(famFileads);
		FamFileReader(famFile);
		BedFileReader(bedFile);
		if (bimFileads!=null)
		{	
			File bimFile=new File(bimFileads);
			BimFileReader(bimFile);
		}else {
			SNPs=GeneData.size();
			SNPsName=new ArrayList<>();
			for(int i=0;i<SNPs;i++)
			{
				SNPsName.add("SNP"+i);
			}
		}
	}
	/*return Marker Matrix, row is Individuals, column is SNPs*/
	public String[][] GetMarkerbyInd() 
	{
		String[][] marker=new String[Inds][SNPs];
		for (int SNPID = 0; SNPID < SNPs; SNPID++) 
		{
			for (int IndID = 0; IndID < Inds; IndID++) 
			{
				marker[IndID][SNPID]=GeneData.get(SNPID)[IndID];
			}
		}
		return marker;
	}
	/*return Marker Matrix, row is SNPs, column is Individuals*/
	public String[][] GetMarkerbySNP() 
	{
		String[][] marker=new String[SNPs][Inds];
		for (int SNPID = 0; SNPID < SNPs; SNPID++) 
		{
			for (int IndID = 0; IndID < Inds; IndID++) 
			{
				marker[SNPID][IndID]=GeneData.get(SNPID)[IndID];
			}
		}
		return marker;
	}
	
	public String[] GetMarkerbyInd(int IndID)
	{
		String[] Markers=new String[SNPs];
		for(int i=0;i<SNPs;i++)
		{
			Markers[i]=GeneData.get(i)[IndID];
		}
		return Markers;
	}
	
	public String[] GetMarkerbySNP(int SNPID)
	{
		String[] Markers=new String[Inds];
		Markers=GeneData.get(SNPID);
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
	
	public void GeneratePedMapFile(File PedFile, File MapFile) throws FileNotFoundException 
	{
		PrintWriter PedFileWriter=new PrintWriter(PedFile);
		PrintWriter MapFileWriter=new PrintWriter(MapFile);
		for(int i=0;i<Inds;i++)
		{
			StringBuffer pedline=new StringBuffer();
			for (int j = 0; j < 6; j++) 
			{
				pedline.append(FamData.get(i)[j]+"\t");
			}
			for(int j=0;j<SNPs;j++)
			{
				if (GeneData.get(j)[i].equals(". .")) 
				{
					pedline.append("0 0"+"\t");
				}else 
				{
					pedline.append(GeneData.get(j)[i]+"\t");
				}
			}
			pedline.deleteCharAt(pedline.length()-1);
			PedFileWriter.println(pedline);
		}
		
		
		if (BimData!=null) 
		{
			for (int i = 0; i < BimData.size(); i++) 
			{
				StringBuffer mapline=new StringBuffer();
				for(int j=0;j<4;j++)
					mapline.append(BimData.get(i)[j]+'\t');
				mapline.deleteCharAt(mapline.length()-1);
				MapFileWriter.println(mapline);
			}
		}else 
		{
			for (int i = 0; i < SNPsName.size(); i++) 
			{
				MapFileWriter.println("1\t"+SNPsName.get(i)+"\t0\t23333333");
			}	
		}
		MapFileWriter.close();
		PedFileWriter.close();
		
	}
	
	public void GeneratePedMapFile(String PedFile, String MapFile) throws FileNotFoundException 
	{
		PrintWriter PedFileWriter=new PrintWriter(PedFile);
		PrintWriter MapFileWriter=new PrintWriter(MapFile);
		for(int i=0;i<Inds;i++)
		{
			StringBuffer pedline=new StringBuffer();
			for (int j = 0; j < 6; j++) 
			{
				pedline.append(FamData.get(i)[j]+"\t");
			}
			for(int j=0;j<SNPs;j++)
			{
				if (GeneData.get(j)[i].equals(". .")) 
				{
					pedline.append("0 0"+"\t");
				}else 
				{
					pedline.append(GeneData.get(j)[i]+"\t");
				}
			}
			pedline.deleteCharAt(pedline.length()-1);
			PedFileWriter.println(pedline);
		}
		
		
		if (BimData!=null) 
		{
			for (int i = 0; i < BimData.size(); i++) 
			{
				StringBuffer mapline=new StringBuffer();
				for(int j=0;j<4;j++)
					mapline.append(BimData.get(i)[j]+'\t');
				mapline.deleteCharAt(mapline.length()-1);
				MapFileWriter.println(mapline);
			}
		}else 
		{
			for (int i = 0; i < SNPsName.size(); i++) 
			{
				MapFileWriter.println("1\t"+SNPsName.get(i)+"\t0\t23333333");
			}	
		}
		MapFileWriter.close();
		PedFileWriter.close();
		
	}
	private boolean BedFileReader(File bedFile) throws IOException 
	{
		RandomAccessFile bedFileReader= new RandomAccessFile(bedFile, "r");
		if (bedFileReader.read() != MAGIC_NUMBER_1 || bedFileReader.read() != MAGIC_NUMBER_2) {
			return false;
		}	
		int bedFileMode = bedFileReader.read();
		if (bedFileMode != MODE) 
		{
			return false;
		}
		int BytesPoint = 3;
		long filelen=bedFileReader.length();
		int bytesPerSNPs = Inds% 4 == 0 ? Inds / 4 : (Inds / 4 + 1);
		bedFileReader.seek(BytesPoint);
		int sampleCounter = 0;
		Byte indGene=0;
		GeneData=new ArrayList<>();
		ArrayList<String> GenePerSNPs=new ArrayList<>();
		while(BytesPoint<filelen)
		{	
			indGene=bedFileReader.readByte();	
		    int oneByte=indGene.byteValue();
			for (int i = 0; i < 4; ++i) 
			{

				if (sampleCounter < Inds)
				{
					switch (oneByte & READER_MASK)
					{
						case HOMOZYGOTE_FIRST:
							GenePerSNPs.add("1 1");
							break;
						case HOMOZYGOTE_SECOND:
							GenePerSNPs.add("2 2");
							break;
						case HETEROZYGOTE:
							GenePerSNPs.add("1 2");
							break;
						case MISSING:
							GenePerSNPs.add(". .");
							break;
						default:
							return false;
					}
				}
				oneByte = oneByte >>> 2;
				++sampleCounter;
			}
			if (sampleCounter==Inds) 
			{
				String[] OneSNPs=new String[Inds];
				GeneData.add(GenePerSNPs.toArray(OneSNPs));
				GenePerSNPs=new ArrayList<>();
				sampleCounter=0;
			}
			BytesPoint++;
		}
		bedFileReader.close();
		return true;
	}
	
	private void BimFileReader(File bimFile) throws IOException 
	{
		BimData=new ArrayList<>();
		SNPsName=new ArrayList<>();
		BufferedReader bimFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(bimFile), FILE_ENCODING));
		String line=null;
		while((line=bimFileReader.readLine()) != null)
		{
			String[] elements = SEPARATOR_PATTERN.split(line);
			BimData.add(elements);
			SNPsName.add(elements[1]);
		}
		SNPs=BimData.size();
		bimFileReader.close();
	}
	
	private void FamFileReader(File famFile) throws IOException
	{
		FamData=new ArrayList<>();
		BufferedReader famFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(famFile), FILE_ENCODING));
		String line=null;
		while((line=famFileReader.readLine()) != null)
		{
			String[] elements = SEPARATOR_PATTERN.split(line);
			FamData.add(elements);
		}
		Inds=FamData.size();
		famFileReader.close();
	}

	
	public static void main(String[] args) throws IOException {
		String[] files=new String[3];
		files[0]="D:\\Java Respo\\GMDR\\example\\example.bed";
		files[1]="D:\\Java Respo\\GMDR\\example\\example.fam";
		files[2]="D:\\Java Respo\\GMDR\\example\\example.bim";
		BedBimFamReader bedBimFamReader=new BedBimFamReader(files[0], files[2], files[1]);
	}
}
