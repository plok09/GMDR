/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package addon.score.cal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.stat.StatUtils;


/**
 *
 * @author "sunxiwei"
 */
public class OrdinalClassLogisticRegression {

    int category;
    int nSize;
    boolean rev;
    int nBeta;
    public static double epsilon = 0.00001;

    double[][] Y;
    double[][] P;
    double[][] X;
    double[] B;
    double threshold = 0.00001;
    double LogLikelihood_old;
    double LogLikelihood_new;
 // y 起始值为1，依次2，3，4，，，，
 // rev=T, 则1 编码为1，1；2 编码为 0，1；3 编码为0，0；rev=F,则1编码为0，0；2编码为 1，0；3 编码为1，1；
   
//x no intercept 1;        
    public OrdinalClassLogisticRegression(double[] y, double[][] x, int k, boolean rev) {
        category = k;
        nSize = y.length;
        this.rev = rev;
        Y = new double[y.length][k - 1];
        P = new double[y.length][k - 1];
        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j < k - 1; j++) {
                if (rev) {
                    if (y[i] <= j + 1) {
                        Y[i][j] = 1;
                    }
                } else {
                    if (y[i] > j + 1) {
                        Y[i][j] = 1;
                    } 
                }
            }
        }

        X = new double[x.length * (k - 1)][x[0].length + k - 1];
        for (int i = 0; i < k - 1; i++) {
            for (int j = 0; j < x.length; j++) {
                X[i * x.length + j][i] = 1;
                System.arraycopy(x[j], 0, X[i * x.length + j], k - 1, x[j].length);
            }
        }

        B = new double[x[0].length + k - 1];
        this.nBeta = B.length;
    }
    
    // no covariate effect
    public OrdinalClassLogisticRegression(double [] y, int k, boolean rev){
        category = k;
        nSize = y.length;
        this.rev = rev;
        Y = new double[y.length][k - 1];
        P = new double[y.length][k - 1];
        
        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j < k - 1; j++) {
                if (rev) {
                    if (y[i] <= j + 1) {
                        Y[i][j] = 1;
                    }
                } else {
                    if (y[i] > j + 1) {
                        Y[i][j] = 1;
                    }
                    
                }
            }
        }
        
        X = new double[y.length * (k - 1)][ k - 1];
        for (int i = 0; i < k - 1; i++) {
            for (int j = 0; j < y.length; j++) {
                X[i * y.length + j][i] = 1;
            }
        }
        
        B = new double[k - 1];
        this.nBeta = B.length;
    }

    public void ordgeeEst() {
        
        int maxIteration = 25;
        double[] B_old = new double[nBeta];
        double[] B_new = new double[nBeta];
        
        double [] Ylog = new double[Y.length * (category-1)];
        for(int i =0; i < category - 1;i++ )
        for (int j = 0; j < Y.length; j++) {
             Ylog[i * Y.length + j] = Y[j][i];
        }
        
        LogisticRegression LogReg = new LogisticRegression(Ylog, X, false);
        LogReg.MLE();
        B_old = LogReg.getBeta();
        //System.out.println( "--->" + B_old[0]+"\t" +B_old[1]);
        
        for (int iteration = 0; iteration < maxIteration; iteration++) {
            
            B_new = updateBeta(B_old);
            //System.out.println(iteration + "--->" + B_new[0]+"\t" +B_new[1]);
            if(stop(B_old, B_new)){
            break;
            }else {
            System.arraycopy(B_new, 0, B_old, 0, B_old.length);
            }
        }
        System.arraycopy(B_new, 0, B, 0, B_new.length);          
    }

    public double[] updateBeta(double[] B_old) {
        double[] B_new = new double[nBeta];
        RealMatrix Xm = new Array2DRowRealMatrix(X);
        RealMatrix Ym = new Array2DRowRealMatrix(Y);
        RealMatrix B_oldm = new Array2DRowRealMatrix(B_old);

        RealMatrix H = new Array2DRowRealMatrix(nBeta, nBeta);
        RealMatrix G = new Array2DRowRealMatrix(nBeta, 1);

        int[] colinx = new int[nBeta];
        for (int ix = 0; ix < colinx.length; ix++) {
            colinx[ix] = ix;
        }
        int[] rowinx = new int[category - 1];

        for (int i = 0; i < nSize; i++) {
            for (int ixr = 0; ixr < rowinx.length; ixr++) {
                rowinx[ixr] = ixr * nSize + i;

            }

            RealMatrix Yi_T = Ym.getRowMatrix(i);
            RealMatrix Yi = Yi_T.transpose();
            RealMatrix Xi = Xm.getSubMatrix(rowinx, colinx);

            RealMatrix Etai = Xi.multiply(B_oldm);
            double[] EtaiArray = Etai.getColumn(0);
            // computer u               
            double[] mui = mu_logit(EtaiArray);
            RealMatrix Mui = new Array2DRowRealMatrix(mui);

// computer  diag(u(1-u)
            double[][] mu_etai = mu_eta_logit2D(EtaiArray);
            RealMatrix Mu_etai = new Array2DRowRealMatrix(mu_etai);

            RealMatrix PRi = Yi.subtract(Mui); //computer Y-u
            RealMatrix Di = Mu_etai.multiply(Xi); //computer D
            RealMatrix Vi = new Array2DRowRealMatrix(computerV(mui)); //computer V

            //RealMatrix inv_Vi = new LUDecomposition(Vi).getSolver().getInverse();

            RealMatrix inv_Vi = new SingularValueDecomposition(Vi).getSolver().getInverse();
            
            RealMatrix Matrix_DT = Di.transpose();
            RealMatrix Inv_DT_V = Matrix_DT.multiply(inv_Vi);
            RealMatrix Inv_DT_V_D = Inv_DT_V.multiply(Di);
            RealMatrix Inv_DT_V_S = Inv_DT_V.multiply(PRi);

            H = H.add(Inv_DT_V_D);
            G = G.add(Inv_DT_V_S);
        }

         //RealMatrix inv_H = new LUDecomposition(H).getSolver().getInverse();
        RealMatrix inv_H = new SingularValueDecomposition(H).getSolver().getInverse();
        
        RealMatrix inv_H_G = inv_H.multiply(G);
        RealMatrix B_newm = B_oldm.add(inv_H_G);

        B_new = B_newm.getColumn(0);

        return B_new;
    }

    public double mu_logit(double eta) {
        double thres = -Math.log(epsilon);
        eta = (eta > thres) ? thres : eta;
        eta = (eta < -thres) ? -thres : eta;
        return Math.exp(eta) / (1 + Math.exp(eta));
    }

    public double[] mu_logit(double[] eta) {
        double[] mu = new double[eta.length];
        for (int i = 0; i < eta.length; i++) {

            mu[i] = mu_logit(eta[i]);

        }
        return mu;
    }

    public double mu_eta_logit(double eta) {
        double thres = -Math.log(epsilon);
        if (Math.abs(eta) >= thres) {
            return epsilon;
        } else {
            return Math.exp(eta) / Math.pow(1 + Math.exp(eta), 2);
        }

    }

    public double[] mu_eta_logit(double[] eta) {
        double[] mu_eta = new double[eta.length];
        for (int i = 0; i < mu_eta.length; i++) {

            mu_eta[i] = mu_eta_logit(eta[i]);

        }
        return mu_eta;
    }

    public double[][] mu_eta_logit2D(double[] eta) {
        double[][] mu_eta = new double[eta.length][eta.length];
        for (int i = 0; i < mu_eta.length; i++) {
            for (int j = 0; j < mu_eta[i].length; j++) {
                if (i == j) {
                    mu_eta[i][j] = mu_eta_logit(eta[i]);
                } else {
                    mu_eta[i][j] = 0;
                }

            }

        }
        return mu_eta;
    }

    public double[][] computerV(double[] mu) {
        int c = mu.length, ij;
        double[][] ans = new double[c][c];
        for (int i = 0; i < c; i++) {
            for (int j = 0; j < c; j++) {
                if (rev) {
                    ij = Math.max(i, j);
                } else {
                    ij = Math.min(i, j);
                }

                ans[i][j] = mu[ij] - mu[i] * mu[j];

            }
        }
        return ans;
    }
    
    public boolean stop(RealMatrix b_old, RealMatrix b_new) {
		boolean stopit = true;
		for (int i = 0; i < b_old.getRowDimension(); i++) {
			if (Math.abs(b_old.getEntry(i, 0) - b_new.getEntry(i, 0)) > threshold) {
				stopit = false;
				break;
			}
		}
		return stopit;
	}
    
    public boolean stop(double [] b_old, double [] b_new) {
		boolean stopit = true;
		for (int i = 0; i < b_old.length; i++) {
			if (Math.abs(b_old[i] - b_new[i]) > threshold) {
				stopit = false;
				break;
			}
		}
		return stopit;
	}
    
    public double[] Residual (){
        
        double[] res = new double[nSize];
        RealMatrix Xm = new Array2DRowRealMatrix(X);
        RealMatrix Ym = new Array2DRowRealMatrix(Y);
        RealMatrix Bm = new Array2DRowRealMatrix(B);
        
        int[] colinx = new int[nBeta];
        for (int ix = 0; ix < colinx.length; ix++) {
            colinx[ix] = ix;
        }
        int[] rowinx = new int[category - 1];
        
        for (int i = 0; i < nSize; i++) {
            for (int ixr = 0; ixr < rowinx.length; ixr++) {
                rowinx[ixr] = ixr * nSize + i;

            }

            RealMatrix Yi_T = Ym.getRowMatrix(i);
            RealMatrix Yi = Yi_T.transpose();
            
            RealMatrix Xi = Xm.getSubMatrix(rowinx, colinx);

            RealMatrix Etai = Xi.multiply(Bm);
            double[] EtaiArray = Etai.getColumn(0);
            // computer u               
            double[] mui = mu_logit(EtaiArray);
            RealMatrix Mui = new Array2DRowRealMatrix(mui);

// computer  diag(u(1-u)
            double[][] mu_etai = mu_eta_logit2D(EtaiArray);
            RealMatrix Mu_etai = new Array2DRowRealMatrix(mu_etai);

            RealMatrix PRi = Yi.subtract(Mui); //computer Y-u
           // RealMatrix Di = Mu_etai.multiply(Xi); //computer D
            RealMatrix Vi = new Array2DRowRealMatrix(computerV(mui)); //computer V

            //RealMatrix inv_Vi = new LUDecomposition(Vi).getSolver().getInverse();
            
            RealMatrix inv_Vi = new SingularValueDecomposition(Vi).getSolver().getInverse();

            RealMatrix inv_deta_V = Mu_etai.multiply(inv_Vi);
            RealMatrix inv_deta_V_S = inv_deta_V.multiply(PRi);
            
            double[] r = inv_deta_V_S.getColumn(0);
            res[i] = StatUtils.mean(r);
        }
    return res;
    }
    
    public double [][] Residual2(){
        double [] res = Residual();
        double[][] res2 = new double[res.length][1];
	for(int i = 0; i < res2.length; i++) {
	    res2[i][0] = res[i];
	}
	return res2;
    }
  
    
    public static void main(String[] args) throws IOException {
    	
    	FileReader fileReader=new FileReader("C:\\Users\\Hou59\\eclipse-workspace\\gmdr1.1\\example\\example.txt");
    	BufferedReader inBufferedReader=new BufferedReader(fileReader);
    	String line=inBufferedReader.readLine();
    	line=inBufferedReader.readLine();
    	Vector<Double> phev=new Vector<>();
    	Vector<Vector<Double>> xm=new Vector<>();
    	while (line!=null)
		{
			String[] tmp=line.split("\t");
			phev.add(Double.valueOf(tmp[0]));
			Vector<Double> tmpx=new Vector<>();
			for(int i=1;i<tmp.length;i++)
			{
				tmpx.add(Double.valueOf(tmp[i]));
			}
			xm.add(tmpx);
			line=inBufferedReader.readLine();
		}
    	double[] y=new double[phev.size()];
    	double[][] x=new double[phev.size()][xm.get(0).size()];
    	for(int i=0;i<y.length;i++)
    	{
    		y[i]=phev.get(i);
    		for(int j=0;j<x[i].length;j++)
    		{
    			x[i][j]=xm.get(i).get(j);
    		}
    	}
        OrdinalClassLogisticRegression ordReg = new OrdinalClassLogisticRegression(y,x,5,false);
        ordReg.ordgeeEst();
        double[][] resid = ordReg.Residual2();
       
	}
    
}

