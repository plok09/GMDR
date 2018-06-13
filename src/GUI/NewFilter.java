/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;
import java.io.*;
import  java.util.*;

/**
 *
 * @author FutaoZhang
 */
public class NewFilter
{
 
   /* public static void main(String[] args) throws IOException {
      String[] s={"E:\\NetBeans\\JavaApp\\example\\example.bed","E:\\NetBeans\\JavaApp\\example\\example.bim","E:\\NetBeans\\JavaApp\\example\\example.fam"};
      String[][] parameter=new String[8][2];
      parameter[0]=new String[2];
      parameter[0][0]="1";
      parameter[0][1]="10";
      parameter[1][0]="0";
      parameter[2][0]="0";
      parameter[3][0]="0";
      parameter[4][0]="0";
      parameter[5][0]="0";
      parameter[6][0]="0";
      parameter[7][0]="0";
     filt a=new filt(s);
     String[][] c=(String[][])a.func(parameter);
     String[] d=(String[])a.Gethead();
     PrintWriter pw=new PrintWriter("E:\\NetBeans\\JavaApp\\example\\out.txt");
     for(int i=0;i<d.length;i++)
     {
         pw.print(d[i]+" ");
     }
     pw.print("\n");
     for(int i=0;i<c.length;i++)
     {
         for(int j=0;j<c[i].length;j++)
         {
             pw.print(c[i][j]+" ");
         }
         pw.print("\n");
     }
    }*/
    File bedfile;
    File bimfile;
    File famfile;
    Filter filter;
    
        public static String[] getchromid(String file) throws IOException
        {
            int id=file.lastIndexOf(".");
            String tail=file.substring(id+1);
            File chrominfo = null;
            if(tail.equals("bim")||tail.equals("bed")||tail.equals("fam"))
            {
               chrominfo=new File(file.substring(0, id+1)+"bim");
            }
            if(tail.equals("ped")||tail.equals("map"))
            {
                chrominfo=new File(file.substring(0, id+1)+"map");
            }
            FileReader fis=new FileReader(chrominfo);
            BufferedReader bs=new BufferedReader(fis);
            String tmp;
            List<String[]> info=new ArrayList<>();
            while((tmp=bs.readLine())!=null)
            {
                String s=tmp.replaceAll(" ", "\t");
                String[] temp=s.split("\t");
                int k=0;
                for (int i = 0; i < temp.length; i++) 
                {
        			if (!temp[i].equals("")) 
        			{
        				k++;
        			}
        			
        		}
                String[] insert=new String[k];
                int m=0;
               for (int i = 0; i < temp.length; i++) 
               {
        			if (!temp[i].equals("")) 
        			{
        					insert[m++]=temp[i];
        			}
        			
                }
                info.add(insert);
            }
            List<String> chrom=new ArrayList<>();
            String temp="0";
            for(int i=0;i<info.size();i++)
            {
                if( !temp.equals(info.get(i)[0]))
                {
                    temp=info.get(i)[0];
                    chrom.add(temp);
                }
                
            }

            String[] num=(String[]) chrom.toArray(new String[chrom.size()]);
            
            return num;
            
        }
    
    public NewFilter(String[] fileaddrsss) throws IOException
    {
         bedfile=new File(fileaddrsss[0]);
         bimfile=new File(fileaddrsss[1]);
         famfile=new File(fileaddrsss[2]);
         filter=new Filter(bedfile,bimfile,famfile);
    }
     public Object[][] func(String[][] parameter) throws IOException 
    {
           
      
         
         
           
           if((Integer.parseInt(parameter[0][0])|Integer.parseInt(parameter[1][0])|Integer.parseInt(parameter[2][0])|Integer.parseInt(parameter[3][0])|Integer.parseInt(parameter[4][0])|Integer.parseInt(parameter[5][0])|Integer.parseInt(parameter[6][0])|Integer.parseInt(parameter[7][0]))==0)
                filter.FindData(1, filter.nSNP, 1, filter.nInd);
           if((Integer.parseInt(parameter[0][0])|Integer.parseInt(parameter[1][0])|Integer.parseInt(parameter[2][0]))==1)
           {
               if(Integer.parseInt(parameter[0][0])==1)
               {
                   if(Integer.parseInt(parameter[1][0])==0)
                   {
                       String lowtmp="1";
                       String hightmp=""+filter.biminfo[Integer.parseInt(parameter[0][1])-1];
                       filter.FilterbyMap(Integer.parseInt(parameter[0][1]), lowtmp,hightmp);

                   }
                   else
                   {
                       int low=Integer.parseInt(parameter[1][2]);
                       int high=Integer.parseInt(parameter[1][4]);
                       if("KB".equals(parameter[1][1])) low  *= 1000 ;
                       if("KB".equals(parameter[1][3])) high *= 1000 ;
                       if("MB".equals(parameter[1][1])) low  *= 1000*1000 ;
                       if("MB".equals(parameter[1][1])) high *= 1000*1000 ;
                       String lowtmp=""+low;
                       String hightmp=""+high;
                       filter.FilterbyMap(Integer.parseInt(parameter[0][1]), lowtmp, hightmp);
                   }
               }
               else 
               {
                   if(Integer.parseInt(parameter[1][0])==1)
                   {
                       filter.FilterbyMap(0, parameter[1][2],parameter[1][4]);
                    }
                    else
                   {
                        filter.FilterbyMap(parameter[2][1], Integer.parseInt(parameter[2][2]));
                    }
               }
           }
           //bymap
            if((Integer.parseInt(parameter[3][0])|Integer.parseInt(parameter[4][0]))==1)
            {
                if((Integer.parseInt(parameter[3][0])&Integer.parseInt(parameter[4][0]))==1)
                {
                    int SNPstate = 0,Indstate = 0;
                    if("exclude".equals(parameter[3][1])) SNPstate=1;
                    if("extract".equals(parameter[3][1])) SNPstate=0;
                    if("remove".equals(parameter[4][1])) Indstate=1;
                    if("keep".equals(parameter[4][1])) Indstate=0;
                    File snpadress=new File(parameter[3][2]);
                    File indaddress=new File(parameter[4][2]);
                    filter.FilterbyList(SNPstate, snpadress, Indstate, indaddress);
                }
                else if(1==Integer.parseInt(parameter[3][0]))
                     {
                          int SNPstate = 0;
                          if("exclude".equals(parameter[3][1])) SNPstate=1;
                          if("extract".equals(parameter[3][1])) SNPstate=0;
                          File snpadress=new File(parameter[3][2]);
                          filter.FilterbyList(SNPstate, snpadress, 0);
                     }
                     else
                    {
                         int Indstate = 0;
                         if("remove".equals(parameter[4][1])) Indstate=1;
                         if("keep".equals(parameter[4][1])) Indstate=0;
                         File indaddress=new File(parameter[4][2]);
                         filter.FilterbyList(Indstate, indaddress, 1);
                    }
                    
            }
           if((Integer.parseInt(parameter[5][0])|Integer.parseInt(parameter[6][0])|Integer.parseInt(parameter[7][0]))==1)
           {
               File address = null;
               int Filtervariable=0;
               String value = null,mfilter = null;
               int sexfilter=0,sex = 0;
               int individual=0,caseorcontrol = 0;
              if(Integer.parseInt(parameter[5][0])==1)
              {
                  address=new File(parameter[5][1]);
                  value=parameter[5][2];
                  mfilter=parameter[5][3];
                  Filtervariable=1;
              }
              if(Integer.parseInt(parameter[6][0])==1)
              {
                  sexfilter=1;
                  if("Males".equals(parameter[6][1]))
                  {
                      sex=1;
                  }
                  if("Females".equals(parameter[6][1]))
                  {
                      sex=2;
                  }
              }
               if(Integer.parseInt(parameter[7][0])==1)
               {
                   individual=1;
                   if("Cases".equals(parameter[7][1]))
                   {
                       caseorcontrol=2;
                   }
                   if("Controls".equals(parameter[7][1]))
                   {
                       caseorcontrol=1;
                   }
               }
               filter.FilterByIndSNP(Filtervariable, address, value, mfilter, sexfilter, sex, individual, caseorcontrol);
           }
           
           String[][] Garray=filter.Output();
           String[][] returnarray=new String[filter.outPedHead.length][filter.outPedHead[0].length+Garray[0].length];
           for(int id=0;id<filter.outPedHead.length;id++)
           {
               System.arraycopy(filter.outPedHead[id],0,returnarray[id], 0, filter.outPedHead[id].length);
               System.arraycopy(Garray[id], 0,returnarray[id],filter.outPedHead[id].length, Garray[id].length);
           }
           return returnarray; 
    }
    
    public Object[] Gethead()
    {
          List<String> Headlist=new ArrayList<String>();
           Headlist.add("Family ID");
           Headlist.add("Individual ID");
           Headlist.add("Paternal ID");
           Headlist.add("Maternal ID");
           Headlist.add("Sex");
           Headlist.add("Phenotype");
          for(int i=0;i<filter.outsnpnum.length;i++)
          {
                Headlist.add(filter.chrom.get(filter.outsnpnum[i]-1)[1]);
          }
          String[]  Head=new String[Headlist.size()];
          Headlist.toArray(Head);
           return Head;
    }
   static public void BuildPed(String[] files) throws IOException
    {
        File bedfile=new File(files[0]);
        File bimfile=new File(files[1]);
        File famfile=new File(files[2]);
        Filter ped=new Filter(bedfile,bimfile,famfile);
        ped.FindData(1, ped.nSNP, 1, ped.nInd);
        String[][] pedgen=ped.Output();
     //   String name=bedfile.getName().substring(0,bedfile.getPath().lastIndexOf("."));
        String path=bedfile.getPath().substring(0, bedfile.getPath().lastIndexOf("."));
      
        File Ped=new File((path+".ped"));
        int[] indnum=ped.GetIndNUM(ped.faminfo);
        ped.GetpedHead(indnum);
        PrintWriter pw=new PrintWriter(Ped);
        int l=pedgen.length;
        int i=0;
        for(;i<pedgen.length;i++)
        {
            for (int j=0;j<ped.outPedHead[i].length;j++) {
                pw.print(ped.outPedHead[i][j] + " ");
            }
            for (int j=0;j<pedgen[i].length;j++) {
                pw.print(pedgen[i][j] + " ");
            }
            pw.print("\n");
        }
        pw.close();
    }
    
   
}

 class BEDtoTXT 
{
   protected final byte data_temp[];
   protected int data[];
   public int bedArray[][];
   protected int OutbedArray[][];
   protected final int nSNP;
   protected final int nInd;
   protected int OutnSNP;
   protected int OutnInd;
   
    public BEDtoTXT(File bed,File bim,File fam) throws IOException
    {
       FileInputStream bedf=new FileInputStream(bed);
       BufferedInputStream beds=new BufferedInputStream(bedf);
       data_temp=new byte[beds.available()];
       beds.read(data_temp);
       nSNP=func_row(bim);
       nInd=func_row(fam);
       TransData();
    }
    
    protected int  func_row(File file) throws IOException
    {
        int count=0;
       FileReader f=new FileReader(file);
       BufferedReader fbs=new BufferedReader(f);
        while (fbs.readLine() != null) 
        {
            count++;
        }
        return count;
    }
 //this fuction is to transfer the data from linear array(a[]) into dyadic array (a[][])
//this fuction also get the real SNP data from each byte
    protected void TransData()
    {
       bedArray=new int[nSNP][nInd];
        int t = 3;
	int nB = 0, rB = 0;
	data=new int[data_temp.length];
	rB = nInd % 4;
	nB = (nInd - rB) / 4;
        int k = 0;
        for (; k < data_temp.length; k++) 
        {
            data[k]=data_temp[k]&0xff;
        }
        int i = 0;
	for ( ;i < nSNP; i++)
	{
		long offset=0;
		int j = 0;
		for (; (j < nInd)&(offset<nB);)
		{
			
			bedArray[i][j++] =data[t] & 3;
			
			bedArray[i][j++] =data[t] & 12;
			bedArray[i][j++] = data[t] & 48;
			bedArray[i][j++] = data[t++] & 192;
			offset++;
		}
	
		switch (rB)
		{
		case 0:
			break;
		case 1:
			bedArray[i][j++] = data[t++] & 3;
			break;
		case 2:
			bedArray[i][j++] = data[t] & 3;
			bedArray[i][j++] = data[t++] & 12;
			break;
		case 3:
			bedArray[i][j++] = data[t] & 3;
			bedArray[i][j++] = data[t] & 12;
			bedArray[i][j++] = data[t++] & 48;
			break;
		}
	}
	
    }
    //find the data of the number Indl to number Indh and the number SNPl to number SNPh 
//put the target data in the buffer OutbedArray
    protected void FindData(int SNPl, int SNPh, int Indl, int Indh)
    {
        OutnSNP = SNPh - SNPl + 1;
	    OutnInd = Indh - Indl + 1;
        OutbedArray=new int[OutnSNP][OutnInd];
        for (int i = 0; i < OutnSNP;i++)
	{
		for (int j = 0; j < OutnInd;j++)
		{
			OutbedArray[i][j] = bedArray[SNPl - 1 + i][Indl - 1 + j];
		}
	}
    }
  //the func is to find the given SNPs or individuals
//num a buffer which store the number of SNP or individual which we want to get 
//count the size of num 
//SNPorInd have two parameters SNP or IND ,if  the parameter is 0,it means that the num is  store the number of SNP and we want to get targer SNP,vice versa

//put the target data in the buffer OutbedArray
  protected void FindData(int[] num, int SNPorInd)
    {
      
	if (SNPorInd==1)
	{
            OutnSNP=nSNP;
            OutnInd=num.length;
            OutbedArray=new int[nSNP][num.length];
	    for (int i = 0; i < num.length;i++)
            {
                for (int j = 0; j <nSNP;j++)
                {
                    OutbedArray[j][i] = bedArray[j][num[i] - 1];
                }
            }
	}
	if (SNPorInd==0)
	{
            OutnSNP=num.length;
            OutnInd=nInd;
            OutbedArray=new int[num.length][nInd];
            for (int i = 0; i < num.length; i++)
            {
               
              System.arraycopy(bedArray[num[i] - 1], 0, OutbedArray[i], 0, nInd);
            }
	}
	
    }
    //
     protected void FindData(int[] SNPsNum,int[] IndNum)
     {
         OutnInd=IndNum.length;
         OutnSNP=SNPsNum.length;
         OutbedArray=new int[OutnSNP][OutnInd];
         int[][] temparray=new int[OutnSNP][nInd];
         for(int i=0;i<OutnSNP;i++)
         {
             System.arraycopy(bedArray[SNPsNum[i] - 1], 0, temparray[i], 0, nInd);
         }
         for(int i=0;i<OutnSNP;i++)
         {
             for(int j=0;j<OutnInd;j++)
             {
                 OutbedArray[i][j]=temparray[i][IndNum[j]-1];
             }
         }
         
     }
    //output the data we found to a file 
    public String[][] Output() throws IOException
    {
        
        List<String[]>  Genotypes=new ArrayList<>();
        for (int i = 0; i < OutnInd;i++)
	{
            String[] tmp=new String[OutnSNP];
            int id=0;
		for (int j = 0; j < OutnSNP;j++)
		{
			if (OutbedArray[j][i]==0)
			{
                            //outfile.print("1 1");//00=11
                            tmp[id++]="1 1";
			}
			else
			{
				if (OutbedArray[j][i] == 2 | OutbedArray[j][i] == 8 | OutbedArray[j][i] == 32 | OutbedArray[j][i] == 128)
				{
                                   //  outfile.print("1 2");//10==1 2
                                     tmp[id++]="1 2";
				} 
				else
				{
					if (OutbedArray[j][i] == 3 | OutbedArray[j][i] == 12 | OutbedArray[j][i] == 48 | OutbedArray[j][i] == 192)
					{
                                           //  outfile.print("2 2");//11==2 2
                                             tmp[id++]="2 2";
					} 
					else
					{
						if (OutbedArray[j][i] == 1 | OutbedArray[j][i] == 4 | OutbedArray[j][i] == 16 | OutbedArray[j][i] == 64)
						{
                                                    // outfile.print("0 0");
                                                     tmp[id++]="0 0";
						}
					}
				}
			}
			//outfile.print(' ');
		}
                
              // outfile.print('\n');
                Genotypes.add(tmp);
	}
       // outfile.close();
        Object[] s = (Object[])Genotypes.toArray();  
        String[][] Garray = new String[s.length][];
        for(int i=0;i<s.length;i++)
        {  
             Garray[i] = (String[])s[i];  
        }  
        return Garray;
    }
 
}




 class Filter extends BEDtoTXT{

    List<String[]> chrom = new ArrayList();
    List<String[]> faminfo=new ArrayList<>();
    int[] outsnpnum;
    int biminfo[];
    int lastnum=0;
    String[][] outPedHead;
    public Filter(File bed, File bim, File fam) throws IOException 
    {
        super(bed, bim, fam);
      int numcount=0;
      FileReader frb=new FileReader(bim);
      BufferedReader brb=new BufferedReader(frb);
      String tmp;
      while ((tmp=brb.readLine())!=null) {
        
        String s=tmp.replaceAll(" ", "\t");
        String[] temp=s.split("\t");
        int k=0;
        for (int i = 0; i < temp.length; i++) 
        {
			if (!temp[i].equals("")) 
			{
				k++;
			}
			
		}
        String[] insert=new String[k];
        int m=0;
       for (int i = 0; i < temp.length; i++) 
       {
			if (!temp[i].equals("")) 
			{
					insert[m++]=temp[i];
			}
			
        }
        chrom.add(insert);
        numcount++;
      }
     lastnum=Integer.parseInt(((String[])chrom.get(numcount-1))[0]);
     biminfo=new int[lastnum];
     SNPNUMeveryChrom();
    frb=new FileReader(fam);
    brb=new BufferedReader(frb);
    tmp=null;
    while ((tmp=brb.readLine())!=null)
    {    
      String s=tmp.replaceAll(" ", "\t");
    
      String[] temp=s.split("\t");
      int k=0;
      for (int i = 0; i < temp.length; i++) 
      {
			if (!temp[i].equals("")) 
			{
				k++;
			}
			
		}
      String[] insert=new String[k];
      int m=0;
     for (int i = 0; i < temp.length; i++) 
     {
			if (!temp[i].equals("")) 
			{
					insert[m++]=temp[i];
			}
			
      }
       faminfo.add(insert);
    }
    Object[] temp=faminfo.toArray();
    outPedHead=new String[temp.length][];
    for(int i=0;i<temp.length;i++)
    {
        outPedHead[i]=(String[]) temp[i];
    }
    outsnpnum=new int[nSNP];
    for(int i=0;i<nSNP;i++)
    {
        outsnpnum[i]=i+1;
    }
 }
    
    
    public void FilterbyMap(int Chromosome,String snpl,String snph)
    {
         int Snppos1;
         int Snppos2;
        if(Chromosome==0)
        {
            int i=0;
            while(((String[])chrom.get(i++))[1].equals(snpl)==false)
                ;
             Snppos1=i;
            i=0;
             while(((String[])chrom.get(i++))[1].equals(snph)==false)
                ;
            Snppos2=i;    
        }
        else
        {
          
            int snp1=Integer.parseInt(snpl);
            int snp2=Integer.parseInt(snph);
            Chromosome--;
            int prenum=0;
            for(int i=0;i<Chromosome;i++)
            {
                prenum+=biminfo[i];
            }
            Snppos1=prenum+snp1;
            Snppos2=prenum+snp2;
        }
         if(Snppos1>Snppos2)
            {
                FindData(Snppos2, Snppos1, 1, nInd);
                outsnpnum=new int[Snppos1-Snppos2+1];
                for(int i=0;i<outsnpnum.length;i++)
                {
                    outsnpnum[i]=Snppos2++;
                }
            }
            else
            {
                FindData(Snppos1, Snppos2, 1, nInd);
                outsnpnum=new int[Snppos2-Snppos1+1];
                for(int i=0;i<outsnpnum.length;i++)
                {
                    outsnpnum[i]=Snppos1++;
                }
            }
    }
    
        public void FilterbyMap(String SNPname,int windows)
    {
            int i=0;
            int Snppos1,Snppos2;
            while(((String[])chrom.get(i++))[1].equals(SNPname)==false)
                ;
             Snppos1=i-windows;
             Snppos2=i+windows;
            
            FindData(Snppos1, Snppos2, 1, nInd);
            outsnpnum=new int[Snppos2-Snppos1+1];
            for(int j=0;j<outsnpnum.length;j++)
            {
                outsnpnum[j]=Snppos2++;
            }
    }
        
     protected void FilterbyList(int state,File file,int SNPorInd) throws IOException
    {
        List<String[]> name=new ArrayList<>();
        FileReader fr=new FileReader(file);
        BufferedReader list=new BufferedReader(fr);
        String tmp;
        while((tmp=list.readLine())!=null)
           name.add(tmp.split(" "));
        if(SNPorInd==0)
        {
            
            String snp[]=new String[name.size()];
            for(int i=0;i<name.size();i++)
            {
                snp[i]=name.get(i)[0];
            }
            int num[]=new int[snp.length];
            for(int j=0;j<snp.length;j++)
            {
                int i=0;
                while(((String[])chrom.get(i++))[1].equals(snp[j])==false)
                ;
                num[j]=i;
            }
            if(state==1)
            {
                
               outsnpnum=remove(num,SNPorInd);

            }
            else
                outsnpnum=num;
              FindData(outsnpnum, 0);

        }
        else if(SNPorInd==1)
        {
            int[] Indlist;
            int[] indexect;
         /*   for(int i=0;i<name.size();i++)
            {
                Indlist[i]=Integer.parseInt(name.get(i));
            }
            */
            Indlist=GetIndNUM(name);
            if(state==1)
            {
               indexect=remove(Indlist,SNPorInd);

            }
            else
              indexect=Indlist;
            FindData(indexect,1);
            GetpedHead(indexect);
        }
    }
     
     
     protected void FilterbyList(int SNPstate,File snp,int Indstate,File Ind) throws IOException
     {
         List<String> SNPlist=new ArrayList();
         List<String[]> Indlist=new ArrayList<>();
         FileReader fr=new FileReader(snp);
         BufferedReader list=new BufferedReader(fr);
         String tmp;
         while((tmp=list.readLine())!=null)
           SNPlist.add(tmp);
        fr=new FileReader(Ind);
        list=new BufferedReader(fr);
        String indtmp;
        while((indtmp=list.readLine())!=null)
            Indlist.add(indtmp.split(" "));
     //   int SNPnum[];
        int Indnum[];
        int SNPnumtmp[]=new int[SNPlist.size()];
        int Indnumtmp[];
        String SNPname[] = SNPlist.toArray(new String[0]);
       /* for(int i=0;i<Indlist.size();i++)
        {
            Indnumtmp[i]=Integer.parseInt( Indlist.get(i));
        }
               */
        Indnumtmp=GetIndNUM(Indlist);
        for(int j=0;j<SNPlist.size();j++)
            {
                int i=0;
                while(((String[])chrom.get(i++))[1].equals(SNPname[j])==false)
                ;
                SNPnumtmp[j]=i;
            }
        if(SNPstate==1)
            outsnpnum=remove(SNPnumtmp,0);
        else 
            outsnpnum=SNPnumtmp;
        if(Indstate==1)
            Indnum=remove(Indnumtmp,1);
        else
            Indnum=Indnumtmp;
         FindData(outsnpnum, Indnum);
         GetpedHead(Indnum);
     }
     
     
   protected void FilterByIndSNP(int Filtervariable,File address,String value,String mfilter,int sexfilter,int sex,int individual,int caseorcontral) throws IOException
    {
         List<String[]> indtmpinfo = new ArrayList<>();
        if(Filtervariable!=0)
        {
            List<String[]> var=new ArrayList<>();
            FileReader fr=new FileReader(address);
            BufferedReader br=new BufferedReader(fr);
            String tmp;
            while((tmp=br.readLine())!=null)
                var.add(tmp.split(" "));
             int l = 0;
            if(mfilter!=null)
                l=Integer.parseInt(mfilter) ;
           
            int j=0;
            if(l!=0)
            {
               for(int i=0;i<var.size();)
               { 
                   if(var.get(i++)[l-1].equals(value))
                     indtmpinfo.add(var.get(i-1));
               }
            }
            else
            {
                for(int i=0;i<var.size();)
               { 
                   if(var.get(i++)[2].equals(value))
                     indtmpinfo.add(var.get(i-1));
               }
            }
        }
        else
            indtmpinfo=faminfo;
       if(sexfilter!=0)
       {
           List<String[]> tmp=new ArrayList<>();
             for (String[] indtmpinfo1 : indtmpinfo) {
                 if (Integer.parseInt(indtmpinfo1[4]) == sex) {
                     tmp.add(indtmpinfo1);
                 }
             }    
            indtmpinfo=tmp;  
       }
       if(individual!=0)
       {
           List<String[]> tmp=new ArrayList<>();
             for (String[] indtmpinfo1 : indtmpinfo) {
                 if (Integer.parseInt(indtmpinfo1[5]) == caseorcontral) {
                     tmp.add(indtmpinfo1);
                 }
             }    
            indtmpinfo=tmp;  
       }
       int[] indnum=GetIndNUM(indtmpinfo);
        FindData(indnum, 1);
        GetpedHead(indnum);
      
    }
  
   protected int[] GetIndNUM(List<String[]> indname)
    {
        int indnum[]=new int[indname.size()];
        for(int i=0;i<indname.size();i++)
        {
            
            for(int j=0;j<faminfo.size();j++)
            {
                if((faminfo.get(j)[0].equals(indname.get(i)[0]))&(faminfo.get(j)[1].equals(indname.get(i)[1])))
                {   
                    indnum[i]=j+1;
                    break;
                }
            }
          
        }
        return indnum;
    }
   
    
    
    private void SNPNUMeveryChrom()
    {
        for(int i=0;i<lastnum;i++)
        {
            biminfo[i]=SNPeveryChrome(i+1);
        }
    }
    
    protected int SNPeveryChrome(int chromnum)
    {
        
        int i=0;
        int count=0;
        for(;i<chrom.size();)
        {
            if (((String[])chrom.get(i++))[0].equals(String.valueOf(chromnum)))
               count++ ;
        }
        return count;
    }
    
   
     
     
     
     
    protected int[] remove(int num[],int SNPorInd)
    {
    
         int m=0;
         int count;
         if(SNPorInd==0)
             count=nSNP;
         else
             count=nInd;
         int numextract[]=new int[count-num.length];
         for(int i=1;i<=count;i++)
         {
             int doornot=0;    
            for(int j=0;j < num.length;j++)
            {
                if(i==num[j])
                {  
                    doornot=1;
                    break;
                }
            }    
            if(doornot!=1)
            {
                numextract[m++]=i;
            }
        }
         return numextract;
    }
    
    
    protected void GetpedHead(int [] indnum)
    {
       outPedHead=new String[indnum.length][];
        for(int i=0;i<indnum.length;i++)
        {
            outPedHead[i]=faminfo.get(indnum[i]-1);
        }
        
    }
}
