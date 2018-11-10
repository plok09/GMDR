package GUI;

import java.awt.List;
import java.io.File;
import java.lang.*;
import java.util.*;

import javax.swing.*;

import DataManage.Plink;

public class GUIMDR
{
	public static boolean standard=false;
	public static boolean Binary=true;
	
	public static File name_bed=new File("NULL");
	public static File name_bim=new File("NULL");	
	public static File name_fam=new File("NULL");
	public static Plink dataset=null;
	public static File name_ped=new File("NULL");
	public static File name_map=new File("NULL");
	public static File name_phe=new File("NULL");
	public static boolean is_creat_object=false;
	public static String name_file[];
	public static String project_path=".";
	public static String gmdrini_path="";
	public static int open=1;
	public static  HashMap<String, String> gmdrini;
	public static String output[][]={
			{"0","0"},
			{"0","0","0","0","0"},
			{"0","0","0"},
			
			{"0","0","0"},
			{"0","0","0"},
			
			{"0","0","0","0"},
			{"0","0"},
			{"0","0"}	
	};
	
	public static UI myUI;
	public GUIMDR() {
		// TODO Auto-generated constructor stub
	}
	public void frame() 
	{
		gmdrini=new HashMap<>();
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              myUI=new UI();
              myUI.setVisible(true);
            }
        });
		
		
	}
	public static void main(String args[])
	{
		gmdrini=new HashMap<>();
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              myUI=new UI();
              myUI.setVisible(true);
            }
        });
	}
}
