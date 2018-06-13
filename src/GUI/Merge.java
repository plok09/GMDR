/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.io.*;

/**
 *
 * @author Administrator
 */
public class Merge 
{
          static public void Merge(String[] OriAddress,String[] MerAddress,String OutName) throws IOException
         {
             TheMergeData domerge=new TheMergeData(OriAddress, MerAddress, OutName);
             int i=OriAddress[0].lastIndexOf(".");
             String a=OriAddress[0].substring(i+1);
             if("bed".equals(a)|"bim".equals(a)|"fam".equals(a))
             {
                 domerge.bMerge();
             }
             if("ped".equals(a)|"map".equals(a))
             {
                 domerge.Merge();
             }
         }
              
}

