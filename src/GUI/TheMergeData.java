/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;
import java.io.*;
import java.util.*;

import javax.management.monitor.MonitorNotification;
/**
 *
 * @author æŒºæŒº
 */
public class TheMergeData 
{
    
   /* public static void main(String[] args) throws IOException 
    {
     //  String[] OriFileAddress={"hapmap10-test.ped","hapmap10-test.map"};
      // String[] MergeFileAddress={"hapmap10.ped","hapmap10.map"};
       String[] OriFileAddress={"hapmap10-test.bed","hapmap10-test.bim","hapmap10-test.fam"};
       String[] MergeFileAddress={"hapmap10.bed","hapmap10.bim","hapmap10.fam"};
       int MergeModel=1;
       String out="out";
       TheMergeData a=new TheMergeData(OriFileAddress, MergeFileAddress,out) ;
    // a.Merge();
       a.bMerge();
    }*/
     byte[] oribed;
     List<String[]> OriBim=new ArrayList<>();
     List<String[]> OriFam=new ArrayList<>();
     byte[] merbed;
     List<String[]> MerBim=new ArrayList<>();
     List<String[]> MerFam=new ArrayList<>();
     int OriSNP;
     int OriInd;
     int MerSNP;
     int MerInd;
     int MergeModel=1;
     String outfile;
     List<String[]> OriPed=new ArrayList<>();
     List<String[]> MerPed=new ArrayList<>();
     List<String[]> OriMap=new ArrayList<>();
     List<String[]> MerMap=new ArrayList<>();
     int[][] OriBed;
     int[][] MerBed;
    public TheMergeData(String[] OriFileAddress,String[] MergeFileAddress/*, int MergeModel*/,String out) throws FileNotFoundException, IOException
    {
        
        int i=OriFileAddress[0].lastIndexOf(".");
        String a=OriFileAddress[0].substring(i+1);
        if("bed".equals(a)|"bim".equals(a)|"fam".equals(a))
        {
        File orifbed=new File(OriFileAddress[0]);
        FileInputStream oriifbed=new FileInputStream(orifbed);
        BufferedInputStream oribfbed=new BufferedInputStream(oriifbed);
        oribed=new byte[oribfbed.available()];
        oribfbed.read(oribed);
       
        File oribim=new File(OriFileAddress[1]);
        FileReader oriinbim=new FileReader(oribim);
        BufferedReader oribbim=new BufferedReader(oriinbim);
        
        String tmp=null;
        while((tmp=oribbim.readLine())!=null)
        {
           
            String s=tmp.replaceAll(" ", "\t");
            String[] temp=s.split("\t");
            int k=0;
            for (int l = 0; l < temp.length; l++) 
            {
    			if (!temp[l].equals("")) 
    			{
    				k++;
    			}
    			
    		}
            String[] insert=new String[k];
            int m=0;
           for (int l = 0; l < temp.length; l++) 
           {
    			if (!temp[l].equals("")) 
    			{
    					insert[m++]=temp[l];
    			}
    			
            }
            OriBim.add(insert);
        }
        File orifam=new File(OriFileAddress[2]);
        FileReader oriinfam=new FileReader(orifam);
        BufferedReader oribfam=new BufferedReader(oriinfam);
       
        tmp=null;
        while((tmp=oribfam.readLine())!=null)
        {
           
            String s=tmp.replaceAll(" ", "\t");
            String[] temp=s.split("\t");
            int k=0;
            for (int i1 = 0; i1 < temp.length; i1++) 
            {
    			if (!temp[i1].equals("")) 
    			{
    				k++;
    			}
    			
    		}
            String[] insert=new String[k];
            int m=0;
           for (int i1 = 0; i1 < temp.length; i1++) 
           {
    			if (!temp[i1].equals("")) 
    			{
    					insert[m++]=temp[i1];
    			}
    			
            }
            OriFam.add(insert);
        }
        
        //åˆ�å§‹åŒ–mergeæ–‡ä»¶
        File merfbed=new File(MergeFileAddress[0]);
        FileInputStream merifbed=new FileInputStream(merfbed);
        BufferedInputStream merbfbed=new BufferedInputStream(merifbed);
        merbed=new byte[merbfbed.available()];
        merbfbed.read(merbed);
        File merbim=new File(MergeFileAddress[1]);
        FileReader merinbim=new FileReader(merbim);
        BufferedReader merbbim=new BufferedReader(merinbim);
        
        tmp=null;
        while((tmp=merbbim.readLine())!=null)
        {
            String s=tmp.replaceAll(" ", "\t");
            String[] temp=s.split("\t");
            int k=0;
            for (int i1 = 0; i1 < temp.length; i1++) 
            {
    			if (!temp[i1].equals("")) 
    			{
    				k++;
    			}
    			
    		}
            String[] insert=new String[k];
            int m=0;
           for (int i1 = 0; i1 < temp.length; i1++) 
           {
    			if (!temp[i1].equals("")) 
    			{
    					insert[m++]=temp[i1];
    			}
    			
            }
            MerBim.add(insert);
        }
        File merfam=new File(MergeFileAddress[2]);
        FileReader merinfam=new FileReader(merfam);
        BufferedReader merbfam=new BufferedReader(merinfam);
       
        tmp=null;
        while((tmp=merbfam.readLine())!=null)
        {
            String s=tmp.replaceAll(" ", "\t");
            String[] temp=s.split("\t");
            int k=0;
            for (int i1 = 0; i1 < temp.length; i1++) 
            {
    			if (!temp[i1].equals("")) 
    			{
    				k++;
    			}
    			
    		}
            String[] insert=new String[k];
            int m=0;
           for (int i1 = 0; i1 < temp.length; i1++) 
           {
    			if (!temp[i1].equals("")) 
    			{
    					insert[m++]=temp[i1];
    			}
    			
            }
            MerFam.add(insert);
        }
        
        
       OriSNP=func_row(oribim);
       OriInd=func_row(orifam);
       MerSNP=func_row(merbim);
       MerInd=func_row(merfam);
       
       OriBed=Getped(TransData(oribed, OriSNP, OriInd));
       MerBed=Getped(TransData(merbed, MerSNP, MerInd));
       
       
        }
         //åˆ�å§‹åŒ–æº�æ–‡ä»¶
       if("ped".equals(a)|"map".equals(a))
       {
           FileReader orifped=new FileReader(OriFileAddress[0]);
           BufferedReader oribped=new BufferedReader(orifped);
           String tmp=null;
           while((tmp=oribped.readLine())!=null)
           {
               String s=tmp.replaceAll(" ", "\t");
               String[] temp=s.split("\t");
               int k=0;
               for (int i1 = 0; i1 < temp.length; i1++) 
               {
       			if (!temp[i1].equals("")) 
       			{
       				k++;
       			}
       			
       		}
               String[] insert=new String[k];
               int m=0;
              for (int i1 = 0; i1 < temp.length; i1++) 
              {
       			if (!temp[i1].equals("")) 
       			{
       					insert[m++]=temp[i1];
       			}
       			
               }
               OriPed.add(insert);
           }
           FileReader merfped=new FileReader(MergeFileAddress[0]);
           BufferedReader merbped=new BufferedReader(merfped);
           tmp=null;
           while((tmp=merbped.readLine())!=null)
           {
               String s=tmp.replaceAll(" ", "\t");
               String[] temp=s.split("\t");
               int k=0;
               for (int i1 = 0; i1 < temp.length; i1++) 
               {
       			if (!temp[i1].equals("")) 
       			{
       				k++;
       			}
       			
       		}
               String[] insert=new String[k];
               int m=0;
              for (int i1 = 0; i1 < temp.length; i1++) 
              {
       			if (!temp[i1].equals("")) 
       			{
       					insert[m++]=temp[i1];
       			}
       			
               }
               MerPed.add(insert);
           }
           FileReader orifmap=new FileReader(OriFileAddress[1]);
           BufferedReader oribmap=new BufferedReader(orifmap);
           tmp=null;
           while((tmp=oribmap.readLine())!=null)
           {
               String s=tmp.replaceAll(" ", "\t");
               String[] temp=s.split("\t");
               int k=0;
               for (int i1 = 0; i1 < temp.length; i1++) 
               {
       			if (!temp[i1].equals("")) 
       			{
       				k++;
       			}
       			
       		}
               String[] insert=new String[k];
               int m=0;
              for (int i1 = 0; i1 < temp.length; i1++) 
              {
       			if (!temp[i1].equals("")) 
       			{
       					insert[m++]=temp[i1];
       			}
       			
               }
               OriMap.add(insert);
           }
           FileReader merfmap=new FileReader(MergeFileAddress[1]);
           BufferedReader merbmap=new BufferedReader(merfmap);
           tmp=null;
           while((tmp=merbmap.readLine())!=null)
           {
               String s=tmp.replaceAll(" ", "\t");
               String[] temp=s.split("\t");
               int k=0;
               for (int i1 = 0; i1 < temp.length; i1++) 
               {
       			if (!temp[i1].equals("")) 
       			{
       				k++;
       			}
       			
       		}
               String[] insert=new String[k];
               int m=0;
              for (int i1 = 0; i1 < temp.length; i1++) 
              {
       			if (!temp[i1].equals("")) 
       			{
       					insert[m++]=temp[i1];
       			}
       			
               }
               MerMap.add(insert);
           }
       }
       this.MergeModel=MergeModel;
       outfile=out;
    }
    
    public void Merge() throws FileNotFoundException
    {
        switch(MergeModel)
        {
            case 1:
            {
               int[] match=MatchBim(OriMap, MerMap, MergeModel);
                BuildPed(match);
                BuildMap(match);
            }
                
             break;
            case 2:
            {

            }
                break;
           case 3:
           {
               
           }
                break;
        }
    }
    
    private void BuildMap(int[] MatchMap) throws FileNotFoundException
    {
        List<String[]> OutMap=OriMap;
        for(int i=0;i<MatchMap.length;i++)
        {
            OutMap.add(MerMap.get(MatchMap[i]));
        }
        PrintWriter pw=new PrintWriter(outfile+".map");
        for(int i=0;i<OutMap.size();i++)
        {
            for(int j=0;j<OutMap.get(i).length;j++)
            {
                pw.print(OutMap.get(i)[j]+" ");
            }
            pw.print("\n");
        }
        pw.close();
        
    }
    
    private void BuildPed(int[] MatchMap) throws FileNotFoundException
    {
        List<String[]> OutPed=new ArrayList<>();
        List<Integer> Indid=new ArrayList<>();
        List<Integer> MapId=new ArrayList<>();
        for(int i=0;i<MerPed.size();i++)
        {
            int march=0;
            for(int j=0;j<OriPed.size();j++)
            {
                if(MerPed.get(i)[0].equals(OriPed.get(j)[0])&MerPed.get(i)[1].equals(OriPed.get(j)[1]))
                {
                    march=1;
                    break;
                    
                }
            }
            if(march==0)
            {
                Indid.add(i);
            }
        }
        
        for(int i=0;i<OriMap.size();i++)
        {
            int match=0;
            int j=0;
            for(;j<MerMap.size();j++)
            {
                if(OriMap.get(i)[1].equals(MerMap.get(j)[1]))
                {
                   match=1;
                    break;
                }
            }
            if(match==1)
            {
                MapId.add(j);
            }
            else
            {
                MapId.add(-1);
            }
        }
        
        for(int i=0;i<OriPed.size();i++)
        {
            int j=0;
            int yesorno=0;
            for(;j<MerPed.size();j++)
            {
                if(OriPed.get(i)[0].equals(MerPed.get(j)[0])&&OriPed.get(i)[1].equals(MerPed.get(j)[1]))
                    {
                        yesorno=1;
                          break;
                    }
            }
            if(yesorno==0)
            {
                String[] tmp=new String[MatchMap.length*2+OriPed.get(i).length];
                System.arraycopy(OriPed.get(i), 0, tmp, 0, OriPed.get(i).length);
                OutPed.add(tmp); 
            }
            else
            {
                 String[] tmp=new String[(MatchMap.length*2)+OriPed.get(i).length];
                 int gg=OriPed.get(i).length;
                 System.arraycopy(OriPed.get(i), 0, tmp, 0, OriPed.get(i).length);
                 String[] add=new String[MatchMap.length*2];
                 int id=0;
                 for(int k=0;k<MatchMap.length;k++)
                 {
                    add[id++]=MerPed.get(j)[MatchMap[k]*2+6];
                    add[id++]=MerPed.get(j)[MatchMap[k]*2+7];
                 }
                 System.arraycopy(add, 0, tmp, OriPed.get(i).length, add.length);
                 OutPed.add(tmp);
            }
        }
        
        for(int i=0;i<Indid.size();i++)
        {
            String[] tmp=new String[(MatchMap.length*2)+OriPed.get(i).length];
            System.arraycopy(MerPed.get(Indid.get(i)), 0, tmp, 0, 6);
           
            int id=6;
            for( int j=0;j<MapId.size();j++)
            {
                if(MapId.get(j)==-1)
                {
                    tmp[id++]="0";
                    tmp[id++]="0";
                }
                else
                {
                    tmp[id++]=MerPed.get(Indid.get(i))[6+MapId.get(j)*2];
                    tmp[id++]=MerPed.get(Indid.get(i))[7+MapId.get(j)*2];
                }
                
               
            }
         
                 for(int k=0;k<MatchMap.length;k++)
                 {
                   tmp[id++]=MerPed.get(Indid.get(i))[MatchMap[k]*2+6];
                   tmp[id++]=MerPed.get(Indid.get(i))[MatchMap[k]*2+7];
                 }
              
            OutPed.add(tmp);
        }
        
        
        PrintWriter pw=new PrintWriter(outfile+".ped");
        for(int i=0;i<OutPed.size();i++)
        {
            for(int j=0;j<OutPed.get(i).length;j++)
            {
                pw.print(OutPed.get(i)[j]+" ");
            }
            pw.print("\n");
        }
        pw.close();
    }
    
    public void bMerge() throws FileNotFoundException, IOException
    {
       
        //å¯¹æ¯�?fam
        
        switch(MergeModel)
        {
            case 1:
            {
                int[] BimMatch=MatchBim(OriBim, MerBim,MergeModel);
                int[] FamMatch=MatchFam(OriFam, MerFam, MergeModel);
                BuildBed(BimMatch, FamMatch);
                BuildBim(BimMatch);
                BuildFam(FamMatch);
            }
                
             break;
            case 2:
            {
          
            }
                break;
           case 3:
           {
             
           }
                break;
        }
    }
    
    private void BuildFam(int[] FamMatch) throws FileNotFoundException
    {
        List<String[]> outFam=OriFam;
        for(int i=0;i<FamMatch.length;i++)
        {
            outFam.add(MerFam.get(FamMatch[i]));
        }
        PrintWriter pw=new PrintWriter(outfile+".fam");
        for(int i=0;i<outFam.size();i++)
        {
            for(int j=0;j<outFam.get(i).length;j++)
            {
                pw.print(outFam.get(i)[j]+" ");
            }
            pw.print("\n");
        }
        pw.close();
    }
    
     private void BuildBim(int[] BimMatch) throws FileNotFoundException
    {
        List<String[]> outBim=OriBim;
        for(int i=0;i<BimMatch.length;i++)
        {
            outBim.add(MerBim.get(BimMatch[i]));
        }
        PrintWriter pw=new PrintWriter(outfile+".bim");
        for(int i=0;i<outBim.size();i++)
        {
            for(int j=0;j<outBim.get(i).length;j++)
            {
                pw.print(outBim.get(i)[j]+" ");
            }
            pw.print("\n");
        }
        pw.close();
    }
    
    private void BuildBed(int[] BimMatch,int[] FamMatch) throws FileNotFoundException, IOException
    {
        List<Integer> idFam=new ArrayList<>();
        List<Integer> idBim=new ArrayList<>();
        int[][] outbed=new int[OriSNP+BimMatch.length][OriInd+FamMatch.length];
        for(int i=0;i<OriFam.size();i++)
        {
            int haveornot=0;
            for(int j=0;j<MerFam.size();j++)
            {
                if(OriFam.get(i)[0].equals(MerFam.get(j)[0])&OriFam.get(i)[1].equals(MerFam.get(j)[1]))
                {
                    haveornot=1;
                    idFam.add(j);
                    break;
                }
            }
            if(haveornot==0)
            {
                idFam.add(-1);
            }
        }
        
        for(int i=0;i<OriBim.size();i++)
        {
            int haveornot=0;
            for(int j=0;j<MerBim.size();j++)
            {
                if(OriBim.get(i)[1].equals(MerBim.get(j)[1]))
                {
                    haveornot=1;
                    idBim.add(j);
                    break;
                }
            }
             if(haveornot==0)
            {
                idBim.add(-1);
            }
        }
        
        for(int i=0;i<OriBim.size();i++)
        {
            
          
          System.arraycopy(OriBed[i],0,outbed[i],0,OriBed[i].length);
                
           for(int j=0;j<FamMatch.length;j++)
           {
               if(idBim.get(i)!=-1)
               {
                   outbed[i][j+OriBed[i].length]=MerBed[idBim.get(i)][FamMatch[j]];
                
               }
               else
               {
                   outbed[i][j+OriBed[i].length]=1;
                  
               }

           }
        }
        
        for(int i=0;i<BimMatch.length;i++)
        {
            for(int j=0;j<idFam.size();j++)
            {
                if(idFam.get(j)!=-1)
                  outbed[i+OriBed.length][j]=MerBed[BimMatch[i]][idFam.get(j)];
                else
                  outbed[i+OriBed.length][j]=1;
            }
            for(int j=0;j<FamMatch.length;j++)
            {
                outbed[i+OriBed.length][j+idFam.size()]=MerBed[BimMatch[i]][FamMatch[j]];
           }
        }
      
        byte[] outBinary=TransToBinary(outbed);
        FileOutputStream fos=new FileOutputStream(outfile+".bed");
        DataOutputStream dos=new DataOutputStream(fos);
        fos.write(outBinary);
        dos.close();
        
        fos.close();
        
    }
  
    private byte[] TransToBinary(int[][] OutBed)
    {
        int Ind=OutBed[0].length;
        int bytenum=Ind/4+((Ind%4)==0?0:1);
        
        byte[] Binary=new byte[3+bytenum*OutBed.length];
        
        System.arraycopy(oribed,0,Binary,0,3);
        int id =3;
        for(int i=0;i<OutBed.length;i++)
        {
            for(int j=0;j<OutBed[i].length;)
            {
                byte k=(byte) OutBed[i][j++];
                if(j<OutBed[i].length)
                {
                    k+=OutBed[i][j++]<<2;
                }
                else
                {
                    k+=0;
                }
                if(j<OutBed[i].length)
                {
                    k+=OutBed[i][j++]<<4;
                }
                else
                {
                    k+=0;
                }
                if(j<OutBed[i].length)
                {
                    k+=OutBed[i][j++]<<6;
                }
                else
                {
                    k+=0;
                }
                Binary[id++]=k;
                
            }
        }
        return Binary;
    }
    
    private int[] MatchFam(List<String[]> OriFam,List<String[]> MerFam,int model)
    {
        List<String> id=new ArrayList<>();
        List<String> idmatch=new ArrayList<>();
        for(int i=0;i<MerFam.size();i++)
        {
            int haveornot=0;
            for(int j=0;j<OriFam.size();j++)
            {
               if(MerFam.get(i)[0].equals(OriFam.get(j)[0])&MerFam.get(i)[1].equals(OriFam.get(j)[1]))
               {
                   haveornot=1;
                   break;
               }
            }
            if(haveornot==1)
            {
                idmatch.add(""+i);
            }
            else
            {
                id.add(""+i);
            }
        }
       
             
        int[] result = null;
        if(model==1)
        {
            result=new int[id.size()];
            for(int i=0;i<result.length;i++)
            {
               result[i]=Integer.parseInt(id.get(i));
            }
        }
        else if(model==2)
             { 
                 result=new int[idmatch.size()];
                 for(int i=0;i<result.length;i++)
                 {
                   result[i]=Integer.parseInt(idmatch.get(i));
                 }
             }
        return result;
    }
    
    private int[] MatchBim(List<String[]> OriBim,List<String[]> MerBim,int model)
    {
        List<String> id=new ArrayList();
        List<String> idmatch=new ArrayList<>();
        for(int i=0;i<MerBim.size();i++)
        {
            int haveornot=0;
            for(int j=0; j<OriBim.size();j++)
            {
            
                if(MerBim.get(i)[1].equals(OriBim.get(j)[1]))
                {
                    haveornot=1;
                    break;
                }
            }
            if(haveornot==0)
            {
                id.add(""+i);
            }
            if(haveornot==1)
            {
                idmatch.add(""+i);
            }
        }
     
        int[] result = null;
        if(model==1)
        {
            result=new int[id.size()];
            for(int i=0;i<result.length;i++)
            {
               result[i]=Integer.parseInt(id.get(i));
            }
        }
        else if(model==2)
             { 
                 result=new int[idmatch.size()];
                 for(int i=0;i<result.length;i++)
                 {
                   result[i]=Integer.parseInt(idmatch.get(i));
                 }
             }
        return result;
    }
    //
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
    
    protected int[][] TransData(byte[] data_temp,int nSNP,int nInd)
    {
       int[][] bedArray=new int[nSNP][nInd];
        int t = 3;
	int nB = 0, rB = 0;
	int[] data=new int[data_temp.length];
	rB = nInd % 4;
	nB = (nInd - rB) / 4;
        for (int i = 0; i < data_temp.length; i++) 
        {
            data[i]=data_temp[i]&0xff;
        }

	for (int i = 0; i < nSNP; i++)
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
	return bedArray;
}
    
    
   public int[][] Getped(int[][] bedArray) throws IOException
    {
        
        int[][] bedarray=new int[bedArray.length][bedArray[0].length];
        
        for (int i = 0; i < bedArray.length;i++)
	{
		for (int j = 0; j < bedArray[i].length;j++)
		{
			if (bedArray[i][j]==0)
			{
                            bedarray[i][j]=0;
			}
			else
			{
				if (bedArray[i][j] == 2 | bedArray[i][j] == 8 | bedArray[i][j] == 32 | bedArray[i][j] == 128)
				{
                                     bedarray[i][j]=2;
				} 
				else
				{
					if (bedArray[i][j] == 3 | bedArray[i][j] == 12 | bedArray[i][j] == 48 | bedArray[i][j] == 192)
					{
                                              bedarray[i][j]=3;
					} 
					else
					{
						if (bedArray[i][j] == 1 | bedArray[i][j] == 4 | bedArray[i][j] == 16 |bedArray[i][j] == 64)
						{
                                                     bedarray[i][j]=1;
						}
					}
				}
			}
			
		}
               
	}
      return bedarray;
    }
   
   
}
