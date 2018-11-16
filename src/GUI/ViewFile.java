package GUI;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;

import gmdr.Main;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class ViewFile extends JFrame implements ActionListener {

	private JPanel contentPane;
	private File ReadBed;
	private File ReadBim;
	private File ReadFam;
	private File ReadPed;
	private File ReadMap;
	private JTable tab_bim;
	private JTable tab_bed;
	private JTable tab_fam;
	private JTable tab_ped;
	private JTable tab_map;
	private JTabbedPane tabbedPane;

	private JButton btnOk = new JButton("OK");
	/**
	 * Launch the application.


	/** 
	 * Create the frame.
	 */
	public ViewFile() {
		setBounds(100, 100, 739, 579);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
	    tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		if (GUIMDR.gmdrini.containsKey("ped")||GUIMDR.gmdrini.containsKey("map")) 
		{
			
			if (!GUIMDR.name_map.getName().equals("NULL")) 
			{
				
			
				ReadMap=GUIMDR.name_map;
				Object[] column={"chromosome",
					             "snp identifier",
					     "Genetic distance (morgans)",
					     "Base-pair position (bp units)"};
				Object[][] map=initFile(ReadMap,column.length);
				JPanel Map_File = new JPanel();
				tabbedPane.addTab("Map File",Map_File);
				Map_File.setLayout(new BorderLayout(0, 0));
				
				JPanel panel = new JPanel();
				Map_File.add(panel, BorderLayout.SOUTH);
				
				tab_map = new JTable(map,column);
				tab_map.setFillsViewportHeight(true);
				tab_map.setFont(new Font("Segoe WP Semibold", Font.PLAIN, 17));
				panel.add(tab_map);
				//FitTableColumns(tab_map);
				DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
			    tcr.setHorizontalAlignment(JLabel.CENTER);
			    tab_map.setDefaultRenderer(Object.class, tcr);
				JScrollPane scrollPane = new JScrollPane(tab_map);
				
				Map_File.add(scrollPane, BorderLayout.CENTER);
				
			}
			if (!GUIMDR.name_ped.getName().equals("NULL")) 
			{	
			
				ReadPed=GUIMDR.name_ped;

				Object[] tmp=getsnp();
				Object[] column=new Object[6+tmp.length];
				column[0]="Family ID ";
			    column[1]="Individual ID ";
				column[2]="Paternal ID ";
				column[3]="Maternal ID ";
				column[4]="Sex ";
			    column[5]="Phenotype ";
			    for (int i = 0; i < tmp.length; i++) 
			    {
				    column[i+6]=tmp[i];	
				}
				Object[][] Ped=initPed(ReadPed,column.length);
				JPanel Ped_File = new JPanel();
				tabbedPane.addTab("Ped File",Ped_File);
				Ped_File.setLayout(new BorderLayout(0, 0));
				
				JPanel panel = new JPanel();
				Ped_File.add(panel, BorderLayout.SOUTH);
				
				tab_ped = new JTable(Ped,column);
				tab_ped.setFillsViewportHeight(true);
				tab_ped.setFont(new Font("Segoe WP Semibold", Font.PLAIN, 17));
				 tab_ped.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				panel.add(tab_ped);
				FitTableColumns(tab_ped);
				
				JScrollPane scrollPane = new JScrollPane(tab_ped);
				scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		       
			    DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
			    tcr.setHorizontalAlignment(JLabel.CENTER);
			    tab_ped.setDefaultRenderer(Object.class, tcr);
				Ped_File.add(scrollPane, BorderLayout.CENTER);
			
				
			}
		}
		if (GUIMDR.gmdrini.containsKey("bed")||GUIMDR.gmdrini.containsKey("bim")||GUIMDR.gmdrini.containsKey("fam")) 
		{
			if (!GUIMDR.name_bed.getName().equals("NULL")) 
			{
			//	File a=new File("C:\\Users\\Administrator\\Desktop\\example\\example.bed");
				
				ReadBed=GUIMDR.name_bed;
				initBed(ReadBed);
				
			}
			
			if (!GUIMDR.name_bim.getName().equals("NULL")) 
			{
				
				ReadBim=GUIMDR.name_bim;
				Object[] column={"chromosome", "SNP", "cM", "base-position", "allele 1", "allele 2"};
				Object[][] Bim=initFile(ReadBim,column.length);
				JPanel Bim_File = new JPanel();
				tabbedPane.addTab("Bim File",Bim_File);
				Bim_File.setLayout(new BorderLayout(0, 0));
				
				JPanel panel = new JPanel();
				Bim_File.add(panel, BorderLayout.SOUTH);
				
				tab_bim = new JTable(Bim,column);
				tab_bim.setFillsViewportHeight(true);
				tab_bim.setFont(new Font("Segoe WP Semibold", Font.PLAIN, 17));
				panel.add(tab_bim);
				//FitTableColumns(tab_bim);
			    DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
			    tcr.setHorizontalAlignment(JLabel.CENTER);
			    tab_bim.setDefaultRenderer(Object.class, tcr);
				JScrollPane scrollPane = new JScrollPane(tab_bim);
				Bim_File.add(scrollPane, BorderLayout.CENTER);
				
			}
			if (!GUIMDR.name_fam.getName().equals("NULL")) 
			{
				
				ReadFam=GUIMDR.name_fam;
				Object[] column={"Family ID",
					             "Individual ID",
					             "Paternal ID",
					             "Maternal ID",
					             "Sex (1=male; 2=female; other=unknown)",
					             "Phenotype"};
				Object[][] fam=initFile(ReadFam,column.length);
				JPanel Fam_File = new JPanel();
				tabbedPane.addTab("Fam File",Fam_File);
				Fam_File.setLayout(new BorderLayout(0, 0));
				
				JPanel panel = new JPanel();
				Fam_File.add(panel, BorderLayout.SOUTH);
				
				tab_fam = new JTable(fam,column);
				tab_fam.setFillsViewportHeight(true);
				tab_fam.setFont(new Font("Segoe WP Semibold", Font.PLAIN, 17));
				panel.add(tab_fam);
				DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
			    tcr.setHorizontalAlignment(JLabel.CENTER);
			    tab_fam.setDefaultRenderer(Object.class, tcr);
				//FitTableColumns(tab_fam);
				JScrollPane scrollPane = new JScrollPane(tab_fam);
				Fam_File.add(scrollPane, BorderLayout.CENTER);
				
			}
		}
	
		if (!GUIMDR.name_phe.getName().equals("NULL")) 
		{
			File PheReader=GUIMDR.name_phe;
			
		}

		
		
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.SOUTH);
		
		
		btnOk.addActionListener(this);
		panel_2.add(btnOk);
		
		this.setVisible(true);
	}

   private void initBed(File name) 
	{
		
		
		Object[][] Bed=new Object[100][16];
		Object[] column=new Object[16];
		
		for (int i = 0; i < 10; i++) 
		{
			column[i]=(Object)(+i);
			
		}
		for (int i = 0; i < 6; i++) {
			column[i+10]=Character.toString((char) ('a'+i));
			
		}
		try {
			
			
			FileInputStream fi=new FileInputStream(name);
	        BufferedInputStream bi=new BufferedInputStream(fi);
	        byte[] inbed=new byte[bi.available()];
	        bi.read(inbed);
			int k=0;
			for (int i = 0; i < Bed.length; i++) 
			{
				
				for (int j = 0; j < Bed[i].length; j++) 
				{
					
					if (k<inbed.length) 
					{
						String abyte=Integer.toBinaryString(inbed[k++]&0xff);
						if (abyte.length() <8) {  
							   while (abyte.length()< 8) {  
							    StringBuffer sb = new StringBuffer();  
							    sb.append("0").append(abyte);//左补0  
//							    sb.append(str).append("0");//右补0  
							    abyte= sb.toString();  
							   }  
							  }  
						Bed[i][j]=abyte;
					}
					else {
						break;
					}
				}
				if (k>=inbed.length) 
				{
					break;
				}
			}
			
		} catch (IOException e) {
			// TODO ×Ô¶¯Éú³ÉµÄ catch ¿é
			e.printStackTrace();
		}
		
		JPanel Bed_File = new JPanel();
		tabbedPane.addTab("Bed File",Bed_File);
		Bed_File.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		Bed_File.add(panel, BorderLayout.SOUTH);
		
		tab_bed = new JTable(Bed,column);
		tab_bed.setFillsViewportHeight(true);
		tab_bed.setFont(new Font("Segoe WP Semibold", Font.PLAIN, 17));
		panel.add(tab_bed);
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
	    tcr.setHorizontalAlignment(JLabel.CENTER);
	    tab_bed.setDefaultRenderer(Object.class, tcr);
		tab_bed.setFillsViewportHeight(true);
	//	FitTableColumns(tab_bed);
		JScrollPane scrollPane = new JScrollPane(tab_bed);
		Bed_File.add(scrollPane, BorderLayout.CENTER);
	}
   
   private Object[] getsnp() 
   {
	Object[] snpnum;
	 FileReader fr;
	 int lines=0;
	try {
		fr = new FileReader(ReadMap);
		BufferedReader bstmp=new BufferedReader(fr);
		
		while (bstmp.readLine()!=null) 
		{
				
				lines++;
		}
	} catch ( IOException e) {
		// TODO ×Ô¶¯Éú³ÉµÄ catch ¿é
		e.printStackTrace();
	}

	if (lines<100) 
	{
		snpnum=new Object[lines];
	}
	else {
		snpnum=new Object[100];
	}
	
	try {
		   fr = new FileReader(ReadMap);

		   BufferedReader bs=new BufferedReader(fr);
		   String tmp;
		   int l=0;
		   while(((tmp=bs.readLine())!=null)&(l<100))
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
	          snpnum[l++]=insert[1];
	       }
		   
	} catch ( IOException e) {
		// TODO ×Ô¶¯Éú³ÉµÄ catch ¿é
		e.printStackTrace();
	}
	return snpnum;
   }
   
   
   public void FitTableColumns(JTable myTable){  
	    JTableHeader header = myTable.getTableHeader();  
	     int rowCount = myTable.getRowCount();  
	  
	     Enumeration columns = myTable.getColumnModel().getColumns();  
	     while(columns.hasMoreElements()){  
	         TableColumn column = (TableColumn)columns.nextElement();  
	         int col = header.getColumnModel().getColumnIndex(column.getIdentifier());  
	         int width = (int)myTable.getTableHeader().getDefaultRenderer()  
	                 .getTableCellRendererComponent(myTable, column.getIdentifier()  
	                         , false, false, -1, col).getPreferredSize().getWidth();  
	         for(int row = 0; row<rowCount; row++){  
	             int preferedWidth = (int)myTable.getCellRenderer(row, col).getTableCellRendererComponent(myTable,  
	               myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();  
	             width = Math.max(width, preferedWidth);  
	         }  
	         header.setResizingColumn(column); // ´ËÐÐºÜÖØÒª  
	         column.setWidth(width+myTable.getIntercellSpacing().width);  
	     }  
	} 
   
   private void setColumnWeidth(JTable table)
   {
	   int cols=table.getColumnCount();
	   for (int i = 0; i <=cols; i++) {
		table.getColumnModel().getColumn(i).setPreferredWidth(100);;

	}
   }
   
   private Object[][] initFile(File name,int cl) 
   {
	    FileReader fr;
	    Object[][] Array;
	    
	    int lines=0;
	    try {
			fr = new FileReader(name);
			BufferedReader bstmp=new BufferedReader(fr);
			while (bstmp.readLine()!=null) 
			{
					
					lines++;
			}
		} 
	    catch ( IOException e1) 
	    {
			// TODO ×Ô¶¯Éú³ÉµÄ catch ¿é
			e1.printStackTrace();
		}
	   
	    if (lines<100) 
	    {
	    	Array=new Object[lines][cl];
		}
	    else 
	    {
	    	Array=new Object[100][cl];
		}
	try {
		   fr = new FileReader(name);

		   BufferedReader bs=new BufferedReader(fr);
		   String tmp;
		   int l=0;
		   while(((tmp=bs.readLine())!=null)&(l<100))
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
	           for (int i = 0; i <cl; i++) 
	           {
	        	 Array[l][i]=insert[i];  
	           }
	           l++;
	       }
		   
	} catch ( IOException e) {
		// TODO ×Ô¶¯Éú³ÉµÄ catch ¿é
		e.printStackTrace();
	}

	
	return Array;
   }
   
   
   
   private Object[][] initPed(File name,int cl) 
   {
	    FileReader fr;
	    Object[][] Array;
	    
	    int lines=0;
	    try {
			fr = new FileReader(name);
			BufferedReader bstmp=new BufferedReader(fr);
			while (bstmp.readLine()!=null) 
			{
					
					lines++;
			}
		} 
	    catch ( IOException e1) 
	    {
			// TODO ×Ô¶¯Éú³ÉµÄ catch ¿é
			e1.printStackTrace();
		}
	   
	    if (lines<100) 
	    {
	    	Array=new Object[lines][6+cl];
		}
	    else 
	    {
	    	Array=new Object[100][6+cl];
		}
	try {
		   fr = new FileReader(name);

		   BufferedReader bs=new BufferedReader(fr);
		   String tmp;
		   int l=0;
		   while(((tmp=bs.readLine())!=null)&(l<100))
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
	           for (int i = 0; i <6; i++) 
	           {
	        	 Array[l][i]=insert[i];
	           }
	           int a=6;
	           for (int i = 6; i < insert.length; ) {
	        	   Array[l][a++] = insert[i++]+" "+insert[i++];
				
			}
	           l++;
	       }
		   
	} catch ( IOException e) {
		// TODO ×Ô¶¯Éú³ÉµÄ catch ¿é
		e.printStackTrace();
	}

	
	return Array;
   }
   
   public void actionPerformed(ActionEvent e) 
	{
	   if (e.getSource()==btnOk) 
	   {
		   this.dispose();
	   }
	   
	}
   
}
