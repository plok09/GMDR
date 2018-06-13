package GUI;

import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class PedFilter 
{

	List<String[]> PedList=new ArrayList<>();
	List<String[]> MapList=new ArrayList<>();
	List<String> Chrom=new ArrayList<>();
	List<String[]> PedHead=new ArrayList<String[]>();
	List<String[]> PedData=new ArrayList<String[]>();
	String[][] Output;
	List<Integer> MapInfo=new ArrayList<>();
	 int[] outsnpnum;
	  String[][] outPedHead;
	int SNP;
	int IND;
	public  PedFilter(File PedFile,File MapFile) 
	{
	   init(PedFile, MapFile);
	   initChrom();
	   initPedHead();
	   initPedData();
	   SNPNUMeveryChrom();
	   IND=PedData.size();
	   SNP=MapList.size();
	   outPedHead=new String[PedHead.size()][];
	   outsnpnum=new int[MapList.size()];
	    for (int i = 0; i < outPedHead.length; i++) 
	    {
			outPedHead[i]=PedHead.get(i);
		}
	    for (int i = 0; i < MapList.size(); i++)
	    {
			outsnpnum[i]=i+1;
		}
	}
	
	private void initPedData() 
	{
	   for (int i = 0; i < PedList.size(); i++)
	   {
		   String tmp[]=new String[PedList.get(i).length/2];
		   int p=0;
	      for (int j = 6; j < PedList.get(i).length;) 
	      {
			tmp[p++]=PedList.get(i)[j++]+" "+PedList.get(i)[j++];
		  }	
	      PedData.add(tmp);
    	}	
	}
	
	private void initChrom() 
	{
		String tmp =new String();
	
		
		for (int i = 0; i < MapList.size(); i++) 
		{
	
			if (!tmp.equals(MapList.get(i)[0])) 
			{
				Chrom.add(MapList.get(i)[0]);
				tmp=MapList.get(i)[0];
			}
		}
	}
	
	   private void SNPNUMeveryChrom()
	    {
	        for(int i=0;i<Chrom.size();i++)
	        {
	            MapInfo.add(SNPeveryChrome(Chrom.get(i)));
	        }
	    }
	    
	    protected int SNPeveryChrome(String chromnum)
	    {
	        
	        int i=0;
	        int count=0;
	        for(;i<MapList.size();)
	        {
	            if (MapList.get(i++)[0].equals(chromnum))
	               count++ ;
	        }
	        return count;
	    }
	
	
	private void initPedHead() 
	{
	   	for (int i = 0; i < PedList.size(); i++) 
	   	{
	   		String[] a=new String[6];
		   	System.arraycopy(PedList.get(i), 0,a, 0, 6);
		   	PedHead.add(a);
		}
	}
	
	private void init(File PedFile,File MapFile)
	{
		FileReader pedfr = null;
		try {
		pedfr=new FileReader(PedFile);
			
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		BufferedReader pedbr=new BufferedReader(pedfr);
		String tmp;
	      try {
			while ((tmp=pedbr.readLine())!=null) {
			      
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
			      PedList.add(insert);
			    }
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	      
	      
	      FileReader mapfr = null;
			try {
			mapfr=new FileReader(MapFile);
				
			} catch (FileNotFoundException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			BufferedReader mapbr=new BufferedReader(mapfr);
			tmp=null;
		      try {
				while ((tmp=mapbr.readLine())!=null) {
				      
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
				      MapList.add(insert);
				    }
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
	}
	
	private void FindData(int IndL,int IndH,int SNPL,int SNPH) 
	{
		int OutInd=IndH-IndL+1;
		int OutSNP=SNPH-SNPL+1;
		Output=new String[OutInd][OutSNP];
		for (int i = 0; i <OutInd; i++) 
		{
			for (int j = 0; j < OutSNP; j++) 
			{
			   Output[i][j]=PedData.get(IndL-1+i)[SNPL-1+j];
			}
		}
	}
	  //the func is to find the given SNPs or individuals
	//num a buffer which store the number of SNP or individual which we want to get 
	//count the size of num 
	//SNPorInd have two parameters SNP or IND ,if  the parameter is 0,it means that the num is  store the number of SNP and we want to get targer SNP,vice versa

	private void FindData(int num[],int SNPorIND) 
	{
		if (SNPorIND==0) 
		{
			Output=new String[IND][num.length];
			for (int i = 0; i < IND; i++) 
			{
			  
			   for (int j = 0; j < num.length; j++) 
			   {
				  Output[i][j]=PedData.get(i)[num[j]-1];
			   }
			}
		}
		if (SNPorIND==1) 
		{
		  Output=new String[num.length][];
		  for (int i = 0; i < num.length; i++) 
		  {
			 Output[i]=PedData.get(num[i]-1);
		  }
		
		}
	}
	private void FindData(int IndsNum[],int SNPsNum[])
	{
		int outind=IndsNum.length;
		int outsnp=SNPsNum.length;
		Output=new String[outind][outsnp];
		String[][] temparray=new String[IndsNum.length][SNP];
		for (int i = 0; i < IndsNum.length; i++)
		{
			for (int j = 0; j < SNPsNum.length; j++) 
			{
				Output[i][j]=PedData.get(IndsNum[i]-1)[SNPsNum[j]-1];
			}
		}
		
	}
	
	private void FilterByMap(int Chromosome,String SNPL,String SNPH) 
	{
		int SNPPos1;
		int SNPPos2;
	   
		if (Chromosome==0) 
		{
			int i=0;
			while (MapList.get(i++)[1].equals(SNPL)==false) ;
			SNPPos1=i;
			i=0;
			while (MapList.get(i++)[1].equals(SNPH)==false) ;
			SNPPos2=i;
		}
		else
		{
			int snp1=Integer.parseInt(SNPL);
			int snp2=Integer.parseInt(SNPH);
			
		    int k= Chrom.indexOf(""+Chromosome);
		    int perm=0;
		    for (int i = 0; i <k; i++) 
		    {
				perm+=MapInfo.get(i);
			}
		    SNPPos1=snp1+perm;
		    SNPPos2=snp2+perm;
		}
		if(SNPPos1>SNPPos2)
        {
            FindData(1, IND,SNPPos2, SNPPos1);
          outsnpnum=new int[SNPPos1-SNPPos2+1];
           for(int i=0;i<outsnpnum.length;i++)
            {
                outsnpnum[i]=SNPPos2++;
            }
       
        }
        else
        {
            FindData( 1, IND,SNPPos1, SNPPos2);
            outsnpnum=new int[SNPPos2-SNPPos1+1];
            for(int i=0;i<outsnpnum.length;i++)
            {
                outsnpnum[i]=SNPPos1++;
            }
            
        }
	}
	
	
	private void FilterByMap(String SNPname,int windows) 
	{
		 int i=0;
         int Snppos1,Snppos2;
         while((MapList.get(i++))[1].equals(SNPname)==false)
             ;
          Snppos1=i-windows;
          Snppos2=i+windows;
         
         FindData( 1, IND,Snppos1, Snppos2);
         outsnpnum=new int[Snppos2-Snppos1+1];
         for(int j=0;j<outsnpnum.length;j++)
         {
             outsnpnum[j]=Snppos1++;
         }
         
	}
	
	protected void FilterbyList(int state,File file,int SNPorInd) throws IOException
    {
        List<String[]> name=new ArrayList<>();
        FileReader fr=new FileReader(file);
        BufferedReader list=new BufferedReader(fr);
        String  tmp;
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
                while(((String[])MapList.get(i++))[1].equals(snp[j])==false)
                ;
                num[j]=i;
            }
            if(state==1)
            {
                
               outsnpnum=remove(num,SNPorInd);

            }
            else
                outsnpnum=num;
              FindData(outsnpnum,0);

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
    
	   protected int[] GetIndNUM(List<String[]> indname)
	    {
	        int indnum[]=new int[indname.size()];
	        for(int i=0;i<indname.size();i++)
	        {
	            
	            for(int j=0;j<PedList.size();j++)
	            {
	                if((PedList.get(j)[0].equals(indname.get(i)[0]))&(PedList.get(j)[1].equals(indname.get(i)[1])))
	                {   
	                    indnum[i]=j+1;
	                    break;
	                }
	            }
	          
	        }
	        return indnum;
	    }
	   
	
	  protected int[] remove(int num[],int SNPorInd)
	    {
	    
	         int m=0;
	         int count;
	         if(SNPorInd==0)
	             count=SNP;
	         else
	             count=IND;
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
	            outPedHead[i]=PedHead.get(indnum[i]-1);
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
                while(((String[])MapList.get(i++))[1].equals(SNPname[j])==false)
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
         FindData(Indnum,outsnpnum);
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
             indtmpinfo=PedHead;
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
     
     public Object[] Gethead()
     {
           List<String> Headlist=new ArrayList<String>();
            Headlist.add("Family ID");
            Headlist.add("Individual ID");
            Headlist.add("Paternal ID");
            Headlist.add("Maternal ID");
            Headlist.add("Sex");
            Headlist.add("Phenotype");
           for(int i=0;i<outsnpnum.length;i++)
           {
                 Headlist.add(MapList.get(outsnpnum[i]-1)[1]);
           }
           String[]  Head=new String[Headlist.size()];
           Headlist.toArray(Head);
            return Head;
     }
	
	Object[][] func(String[][] parameter) throws IOException
	{
		
	    if((Integer.parseInt(parameter[0][0])|Integer.parseInt(parameter[1][0])|Integer.parseInt(parameter[2][0])|Integer.parseInt(parameter[3][0])|Integer.parseInt(parameter[4][0])|Integer.parseInt(parameter[5][0])|Integer.parseInt(parameter[6][0])|Integer.parseInt(parameter[7][0]))==0)
              FindData( 1,IND,1, SNP);
       if((Integer.parseInt(parameter[0][0])|Integer.parseInt(parameter[1][0])|Integer.parseInt(parameter[2][0]))==1)
       {
           if(Integer.parseInt(parameter[0][0])==1)
           {
               if(Integer.parseInt(parameter[1][0])==0)
               {
                   String lowtmp="1";
                   String hightmp=""+MapInfo.get(Chrom.indexOf(parameter[0][1]));;
                  FilterByMap(Integer.parseInt(parameter[0][1]), lowtmp,hightmp);

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
                   FilterByMap(Integer.parseInt(parameter[0][1]), lowtmp, hightmp);
               }
           }
           else 
           {
               if(Integer.parseInt(parameter[1][0])==1)
               {
                    FilterByMap(0, parameter[1][2],parameter[1][4]);
                }
                else
               {
                    FilterByMap(parameter[2][1], Integer.parseInt(parameter[2][2]));
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
               FilterbyList(SNPstate, snpadress, Indstate, indaddress);
            }
            else if(1==Integer.parseInt(parameter[3][0]))
                 {
                      int SNPstate = 0;
                      if("exclude".equals(parameter[3][1])) SNPstate=1;
                      if("extract".equals(parameter[3][1])) SNPstate=0;
                      File snpadress=new File(parameter[3][2]);
                       FilterbyList(SNPstate, snpadress, 0);
                 }
                 else
                {
                     int Indstate = 0;
                     if("remove".equals(parameter[4][1])) Indstate=1;
                     if("keep".equals(parameter[4][1])) Indstate=0;
                     File indaddress=new File(parameter[4][2]);
                     FilterbyList(Indstate, indaddress, 1);
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
           FilterByIndSNP(Filtervariable, address, value, mfilter, sexfilter, sex, individual, caseorcontrol);
       }
       

	       String[][] Garray=Output;
       String[][] returnarray=new String[outPedHead.length][outPedHead[0].length+Garray[0].length];
      for(int id=0;id<outPedHead.length;id++)
       {
           System.arraycopy(outPedHead[id],0,returnarray[id], 0, outPedHead[id].length);
           System.arraycopy(Garray[id], 0,returnarray[id],outPedHead[id].length, Garray[id].length);
        }
       return returnarray; 
	}
	/*public static void main(String[] args)
	{
		File PedFile=new File("C:\\Users\\Administrator\\Desktop\\example\\example.ped");
		File MapFile=new File("C:\\Users\\Administrator\\Desktop\\example\\example.map");
		PedFilter a=new PedFilter(PedFile, MapFile);
	
		String[][] parameter=new String[8][];
	      parameter[0]=new String[2];
	      parameter[0][0]="1";
	      parameter[0][1]="10";
	    
	      parameter[1]=new String[5];
	      parameter[1][0]="1";
	      parameter[1][1]="BASE";
	      parameter[1][2]="1";
	      parameter[1][3]="BASE";
	      parameter[1][4]="2";
	      parameter[2]=new String[2];
	      parameter[2][0]="0";
	      parameter[3]=new String[2];
	      parameter[3][0]="0";
	      parameter[4]=new String[2];
	      parameter[4][0]="0";
	      parameter[5]=new String[2];
	      parameter[5][0]="0";
	      parameter[6]=new String[2];
	      parameter[6][0]="0";
	      parameter[7]=new String[2];
	      parameter[7][0]="0";
		a.FilterByMap(18, "1", "2");
		
	}
	*/
}
