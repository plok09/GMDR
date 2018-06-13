/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;
import java.io.*;
import java.security.MessageDigest;
import java.util.*;

import javax.swing.JOptionPane;

import org.apache.tools.ant.taskdefs.email.Message;

import addon.score.cal.*;
/**
 *
 * @author Administrator
 */
public class Residual_cal 
{
   static String PheAddress;
   static int[] IndexOfResponse;
   static int[] IndexOfPredictor;
   static String LinkFunction;
   static String EstimatingMethod;
   public Residual_cal(String Address,int[] IndexOfRes,int[] IndexOfPre,String Function,String Method)
    {
        this.PheAddress=Address;
        this.IndexOfResponse=IndexOfRes;
        this.IndexOfPredictor=IndexOfPre;
        this.LinkFunction=Function;
        this.EstimatingMethod=Method;
    }
    public Object[][] CalofResidual() throws IOException
    { 
        File Phefile=new File(PheAddress);
        FileReader fr = null;
        fr = new FileReader(Phefile);
        BufferedReader br1=new BufferedReader(fr);
        List<String[]> phe=new ArrayList<>();
        String tmp1=null;
        tmp1=br1.readLine();
        String tmp2=tmp1.replace(" ", "\t");
        String[] a = tmp2.split("\t");
        int k=0;
        for (int i = 0; i < a.length; i++) 
        {
			if (!a[i].equals("")) 
			{
				k++;
			}
			
		}
        while( (tmp1=br1.readLine())!=null)
        {
        	if (tmp1.equals("\n")) 
        	{
				continue;
			}
        	String[] insert=new String[k];
        	tmp2=tmp1.replace(" ", "\t");
        	
        	String[] tmpall = tmp2.split("\t");
        	if (tmpall.length!=k) 
        	{
				System.out.println();
			}
        	String[] tmp=new String[k];
        	int id=0;
        	for(int i=0;i<tmpall.length;i++)
        	{
        		if (!tmpall[i].equals("")) 
        		{
					tmp[id++]=tmpall[i];
				}
        	}
        	
        	int m=0;
        	for (int i = 0; i < tmp.length; i++) 
        	{
				if (!tmp[i].equals("")) 
				{
					insert[m++]=tmp[i];
				}
				
			}
            phe.add(insert);
        }
        double[][] PheReal=new double[phe.size()][phe.get(0).length];
        for(int i=0;i<PheReal.length;i++)
        {
             for(int j=0;j<PheReal[i].length;j++)
            {
              PheReal[i][j]=Double.valueOf(phe.get(i)[j]);;
            }
        }
    
        double[][] Y=new double[PheReal.length][IndexOfResponse.length];
        double[][] X=new double[PheReal.length][IndexOfPredictor.length];
        for(int i=0;i<PheReal.length;i++)
        {
            for(int j=0;j<IndexOfResponse.length;j++)
            {
                Y[i][j]=PheReal[i][IndexOfResponse[j]];
            }
        }
          for(int i=0;i<PheReal.length;i++)
        {
            for(int j=0;j<IndexOfPredictor.length;j++)
            {
               X[i][j]=PheReal[i][IndexOfPredictor[j]];
            }
        }
       boolean intercept;
       if((IndexOfPredictor.length+IndexOfResponse.length)==phe.get(0).length)
           intercept=false;
       else
           intercept=true;
       double[][] r = null;
       switch(LinkFunction)
       {
           case "Linear Model":
           {
               switch(EstimatingMethod)
                {
                      case "Generalized estimating equations":
                     {
                       JOptionPane.showMessageDialog(null, "Sorry, this function is not available now. it will be work soon");
                      }
                      break;
                      case "Maximum likelihood method" :
                      { 
                    	  LinearRegression LinR = new LinearRegression(Y, X, intercept);
                          LinR.MLE();
                          r = LinR.getResiduals2();
                      }
                      break;
                       case "Quasi-maximum likelihood method":
                           JOptionPane.showMessageDialog(null, "Sorry, this function is not available now. it will be work soon");

                      break;
               }
              
           }
	    break;
           case "Logistic Model":
           {
               switch(EstimatingMethod)
                {
                      case "Generalized estimating equations":
                     {
                         JOptionPane.showMessageDialog(null, "Sorry, this function is not available now. it will be work soon");

                      }
                      break;
                      case "Maximum likelihood method" :
                      {  
                    	  LogisticRegression LogReg = new LogisticRegression(Y, X, intercept);
                    	  LogReg.MLE();
                    	  r = LogReg.getResiduals2();
                      }
                         break;
                       case "Quasi-maximum likelihood method":
                           JOptionPane.showMessageDialog(null, "Sorry, this function is not available now. it will be work soon");

                      break;
               }
              
           }
            break;
          case "Poisson Model":
              JOptionPane.showMessageDialog(null, "Sorry, this function is not available now. it will be work soon");

              break;
           case "Multinomial Logit Model":
           {
        		double[] orderedy=new double[Y.length];
            	for(int i=0;i<Y.length;i++)
            	{
            		orderedy[i]=Y[i][0];
            	}
            	double[] tmpy=orderedy;
            	Arrays.sort(tmpy);
            	List<Double> listy = new ArrayList<>();  
            	listy.add(tmpy[0]);
	            for(int i=1;i<tmpy.length;i++)
	            {  
	               if(tmpy[i]!=listy.get(listy.size()-1)){  
	            	   listy.add(tmpy[i]);  
	               }  
	           }  
                MultiClassLogisticRegression mutlilogitglm=new MultiClassLogisticRegression(orderedy,X, listy.size());
                mutlilogitglm.MLE();
                r=mutlilogitglm.avgResidual2D();
           }
              break;
            case "Proportional Odds Model":
            {
            	double[] orderedy=new double[Y.length];
            	for(int i=0;i<Y.length;i++)
            	{
            		orderedy[i]=Y[i][0];
            	}
            	double[] tmpy=orderedy;
            	Arrays.sort(tmpy);
            	List<Double> listy = new ArrayList<>();  
            	listy.add(tmpy[0]);
	            for(int i=1;i<tmpy.length;i++)
	            {  
	               if(tmpy[i]!=listy.get(listy.size()-1)){  
	            	   listy.add(tmpy[i]);  
	               }  
	           }  
                OrdinalClassLogisticRegression orderedglm=new OrdinalClassLogisticRegression(orderedy,X, listy.size(), false);
                orderedglm.ordgeeEst();
                r=orderedglm.Residual2();
            }
              break;
             case "Proportional Hazards Model":
                 JOptionPane.showMessageDialog(null, "Sorry, this function is not available now. it will be work soon");
              break;    
       }
      int x=r.length;
      int y=r[0].length;
       Object true_data[][]=new Object[x][y];
       
       for(int i=0;i<x;i++)
       {
    	   for(int j=0;j<y;j++)
    	   {
    		   true_data[i][j]=r[i][j];
    	   }
       }
       
       return true_data;
    }
    
    
    
    
    /*public static void main(String[] args) throws IOException {
        String adress="E:\\NetBeans\\analysis\\example\\example.phe";
        int[] a={2};
        int[] b={3,4};
        Residual_cal s=new Residual_cal(adress,a,b,"Logistic Model","Generalized Estimating Equations (GEE) Model");
        double[][] r= (double[][])s.CalofResidual();
        for (int i = 0; i < r.length; i++) {
			System.out.println(r[i][0]);
		}
		System.out.println();
    }*/
}
