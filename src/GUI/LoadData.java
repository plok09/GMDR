package GUI;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;

import org.epistasis.combinatoric.mdr.IfThenRulesTextGenerator;

import DataManage.Plink;
import gmdr.Main;

public class LoadData extends JFrame implements ItemListener,ActionListener,ChangeListener
{
	private boolean is_fast_selected=false;
	private boolean UsePhe=false;
	private JTabbedPane tabpanel=new JTabbedPane(JTabbedPane.NORTH);
	private String name_ped;
	private String name_map;
	
	private JPanel pnlbtnokandcancel=new JPanel();
	private JButton buttonYes=new JButton("   OK   ");
	private JButton buttonNo=new JButton ("Cancel");
			
	private JPanel BinaryInput =new JPanel();
	
	private JPanel pnlbinQuickFiles=new JPanel();
	private JCheckBox ckuseqickboxbin=new JCheckBox("");
	private JComboBox<String> cobbinfilelist=new JComboBox<String>();
	private String fast_name1[];
	
	private JPanel set1=new JPanel();
	private JPanel pnlbedfilechoose=new JPanel();
	private JLabel labelbed=new JLabel(".bed file");
	private JTextArea txabedfilepath=new JTextArea(1,26);
	private JButton btnbedbrowse=new JButton("Browse");
	
	private JPanel pnlbimfilechoose=new JPanel();
	private JLabel labelbim=new JLabel(".bim file");
	private JTextArea txabimfilepath=new JTextArea(1,26);
	private JButton btnbimbrowse=new JButton("Browse");
	
	private JPanel pnlfamfilechoose=new JPanel();
	private JLabel labelfam=new JLabel(".fam file");
	private JTextArea txafamfilepath=new JTextArea(1,26);
	private JButton btnfambrowse=new JButton("Browse");
	
	private int indexofBinary=0;
	private int indexofStandard=0;
	private JPanel StandardInput=new JPanel();
	
	private JPanel pnlstandardQuickFiles=new JPanel();
	private JCheckBox ckusequickboxstandard=new JCheckBox("");
	private JComboBox<String> cbostandardfilelist=new JComboBox<String>();
	private String fast_name2[];
	
	private JPanel set2=new JPanel();
	private JPanel pnlstandardpedfilechoose=new JPanel();
	private JLabel labelped=new JLabel(".ped file");
	private JTextArea txapedfilepath=new JTextArea(1,26);
	private JButton btnpedbrowse=new JButton("Browse");
	
	private JPanel pnlstandardmapfilechoose=new JPanel();
	private JLabel labelmap=new JLabel(".map file");
	private JTextArea txamapfilepath=new JTextArea(1,26);
	private JButton btnmapbrowse=new JButton("Browse");
	
	
	
	private JPanel AlternatePhenotype=new JPanel();
	
	private JPanel pnlpheno=new JPanel();
	private JCheckBox ckusepheno=new JCheckBox("");
	private JLabel labelpheno=new JLabel("use alternative phenotype");
	private JTextArea txaphenofilepath=new JTextArea(1,15);
	private JButton btnphenobrowse=new JButton("Browse");
	private final JPanel pnlbinfilechoose = new JPanel();
	private final JPanel pnlstandardfilechoose = new JPanel();
	private int binary=-1;
	public static void main(String[] args) {
		LoadData loadData=new LoadData();
		loadData.setVisible(true);
	}
	
	
	public LoadData()
	{
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(true);
		setTitle("Input Data");
		if(GUIMDR.name_file!=null)
		{
			fast_name1=new String[GUIMDR.name_file.length];
			for(int i=0;i<fast_name1.length;i++)
			{
				fast_name1[i]=new String(GUIMDR.name_file[i]);
			}
			fast_name2=new String[GUIMDR.name_file.length];
			for(int i=0;i<fast_name2.length;i++)
			{
				fast_name2[i]=new String(GUIMDR.name_file[i]);
			}
		}
		else
		{
			fast_name1=new String[0];
			fast_name2=new String[0];
		}
		
		tabpanel.add("Binary Input",BinaryInput);
		tabpanel.add("Standard Input",StandardInput);
		tabpanel.add("Alternate Phenotype",AlternatePhenotype);
		tabpanel.addChangeListener(this);
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(tabpanel);
		getContentPane().add(pnlbtnokandcancel, BorderLayout.SOUTH);
/*		Iterator<Entry<String, String>> it = GUIMDR.gmdrini.entrySet().iterator();
        while (it.hasNext()) 
        {  
        	
            Map.Entry<String, String> entry =  it.next();  
            String key = entry.getKey();  
            String value = entry.getValue();  
			if (key.equals("bed"))
            {
				txabedfilepath.setText(value);
				
			}
			if (key.equals("bim"))
            {
				txabimfilepath.setText(value);
				
			}
			if (key.equals("fam"))
            {
				txafamfilepath.setText(value);
				
			}
			if (key.equals("ped"))
            {
				txapedfilepath.setText(value);
				
			}
			if (key.equals("map"))
            {
				txamapfilepath.setText(value);
				
			}
			if (key.equals("phe"))
            {
				txaphenofilepath.setText(value);
				
			}
        }
*/
		initBinary();
		initStandardInput();
		initAleternatePhenotype();
		initLast();
		
		this.setPreferredSize(new Dimension(660,300));
		this.setVisible(true);
		this.setLocation(470,210);
		this.pack();
	//	GUIMDR.myUI.DataMenuItem[1].setEnabled(true);
	}
	
	public void initBinary()
	{
		for(int i=0;i<fast_name1.length;i++)
		{
			cobbinfilelist.addItem(fast_name1[i]);
		}
		pnlbinQuickFiles.add(ckuseqickboxbin);
		ckuseqickboxbin.addItemListener(this);
		BinaryInput.setLayout(new BorderLayout(0, 0));
		pnlbinQuickFiles.setBorder(BorderFactory.createTitledBorder("QuickFileset"));
		pnlbinQuickFiles.add(cobbinfilelist);
		cobbinfilelist.setEnabled(false);
		BinaryInput.add(pnlbinQuickFiles, BorderLayout.NORTH);
		
		BinaryInput.add(pnlbinfilechoose, BorderLayout.CENTER);
		pnlbinfilechoose.setLayout(new GridLayout(0, 1, 0, 0));
		pnlbinfilechoose.add(pnlbedfilechoose);
		pnlbedfilechoose.setLayout(null);
		labelbed.setBounds(10, 10, 70, 15);
		pnlbedfilechoose.add(labelbed);
		txabedfilepath.setBounds(74, 6, 461, 23);
		pnlbedfilechoose.add(txabedfilepath);
		btnbedbrowse.setBounds(545, 6, 83, 23);
		pnlbedfilechoose.add(btnbedbrowse);
		pnlbinfilechoose.add(pnlbimfilechoose);
		pnlbimfilechoose.setLayout(null);
		labelbim.setBounds(10, 10, 54, 18);
		
		
		pnlbimfilechoose.add(labelbim);
		txabimfilepath.setBounds(74, 6, 461, 23);
		pnlbimfilechoose.add(txabimfilepath);
		btnbimbrowse.setBounds(545, 6, 83, 23);
		pnlbimfilechoose.add(btnbimbrowse);
		pnlbinfilechoose.add(pnlfamfilechoose);
		pnlfamfilechoose.setLayout(null);
		labelfam.setBounds(10, 10, 58, 14);
		
		
		pnlfamfilechoose.add(labelfam);
		txafamfilepath.setBounds(74, 6, 461, 23);
		pnlfamfilechoose.add(txafamfilepath);
		btnfambrowse.setBounds(549, 6, 79, 23);
		pnlfamfilechoose.add(btnfambrowse);
		btnfambrowse.addActionListener(this);
		btnbimbrowse.addActionListener(this);
		btnbedbrowse.addActionListener(this);
	//	BinaryInput.setLayout(new BoxLayout(BinaryInput,BoxLayout.Y_AXIS));
	}
	
	void initStandardInput()
	{
		pnlstandardQuickFiles.add(ckusequickboxstandard);
		ckusequickboxstandard.setSelected(false);
		ckusequickboxstandard.addItemListener(this);
		pnlstandardQuickFiles.setBorder(BorderFactory.createTitledBorder("QuickFileset"));
		pnlstandardQuickFiles.add(cbostandardfilelist);
		cbostandardfilelist.setEnabled(false);
		for(int i=0;i<fast_name2.length;i++)
		{
			cbostandardfilelist.addItem(fast_name2[i]);
		}
		StandardInput.setLayout(new BorderLayout(0, 0));
		
		
	//	set2.add(set21);
	//	set2.add(set22);
	//	set2.setLayout(new GridLayout(1,1,0,0));
		
		StandardInput.add(pnlstandardQuickFiles, BorderLayout.NORTH);
		
		StandardInput.add(pnlstandardfilechoose, BorderLayout.CENTER);
		pnlstandardfilechoose.setLayout(new GridLayout(0, 1, 0, 0));
		pnlstandardfilechoose.add(pnlstandardpedfilechoose);
		pnlstandardpedfilechoose.setLayout(null);
		labelped.setBounds(10, 10, 54, 14);
		
		pnlstandardpedfilechoose.add(labelped);
		txapedfilepath.setBounds(74, 6, 461, 24);
		pnlstandardpedfilechoose.add(txapedfilepath);
		btnpedbrowse.setBounds(545, 6, 83, 23);
		pnlstandardpedfilechoose.add(btnpedbrowse);
		pnlstandardfilechoose.add(pnlstandardmapfilechoose);
		pnlstandardmapfilechoose.setLayout(null);
		labelmap.setBounds(10, 10, 54, 14);
		
		pnlstandardmapfilechoose.add(labelmap);
		txamapfilepath.setBounds(74, 6, 461, 24);
		pnlstandardmapfilechoose.add(txamapfilepath);
		btnmapbrowse.setBounds(545, 6, 83, 23);
		pnlstandardmapfilechoose.add(btnmapbrowse);	
		btnmapbrowse.addActionListener(this);
		btnpedbrowse.addActionListener(this);
	}

	public void initAleternatePhenotype()
	{
		ckusepheno.setBounds(6, 16, 21, 21);
		ckusepheno.addItemListener(this);
		pnlpheno.setLayout(null);
		pnlpheno.add(ckusepheno);
		labelpheno.setBounds(33, 16, 194, 14);
		pnlpheno.add(labelpheno);
		txaphenofilepath.setBounds(11, 45, 381, 22);
		pnlpheno.add(txaphenofilepath);
		btnphenobrowse.setBounds(402, 45, 84, 23);
		btnphenobrowse.addActionListener(this);
		pnlpheno.add(btnphenobrowse);
		
		AlternatePhenotype.add(pnlpheno);
		btnphenobrowse.setEnabled(false);
		txaphenofilepath.setEnabled(false);
		AlternatePhenotype.setLayout(new GridLayout(1, 1, 0, 0));
		//AlternatePhenotype.setLayout(new BoxLayout(AlternatePhenotype, BoxLayout.X_AXIS));
	}
	
	public void initLast()
	{
		pnlbtnokandcancel.add(buttonYes);
		buttonYes.addActionListener(this);
		pnlbtnokandcancel.add(buttonNo);
		buttonNo.addActionListener(this);
		
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		JButton temp_button;
		temp_button=(JButton)e.getSource();
		if (temp_button==btnbedbrowse)
		{
			JFileChooser chooser = new JFileChooser(GUIMDR.project_path);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("BED","bed");
			chooser.setFileFilter(filter);
			chooser.setAcceptAllFileFilterUsed(true);
			int returnVal = chooser.showOpenDialog(new JPanel()); 
			if (returnVal!=JFileChooser.APPROVE_OPTION) {
				return;
			}
			txabedfilepath.setText(chooser.getSelectedFile().getAbsolutePath());
			if (indexofStandard!=0) 
			{
				indexofStandard=0;
				indexofBinary=0;
			}
			indexofBinary++;
		}
		if (temp_button==btnbimbrowse)
		{
			JFileChooser chooser = new JFileChooser(GUIMDR.project_path);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("BIM","bim");
			chooser.setFileFilter(filter);
			chooser.setAcceptAllFileFilterUsed(true);
			int returnVal = chooser.showOpenDialog(new JPanel()); 
			if (returnVal!=JFileChooser.APPROVE_OPTION) {
				return;
			}
			txabimfilepath.setText(chooser.getSelectedFile().getAbsolutePath());
		}
		if (temp_button==btnfambrowse)
		{
			JFileChooser chooser = new JFileChooser(GUIMDR.project_path);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("FAM","fam");
			chooser.setFileFilter(filter);
			chooser.setAcceptAllFileFilterUsed(true);
			int returnVal = chooser.showOpenDialog(new JPanel()); 
			if (returnVal!=JFileChooser.APPROVE_OPTION) {
				return;
			}
			txafamfilepath.setText(chooser.getSelectedFile().getAbsolutePath());
			if (indexofStandard!=0) 
			{
				indexofStandard=0;
				indexofBinary=0;
			}
			indexofBinary++;
		}
		if(temp_button==btnpedbrowse)
		{
			JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
			
			FileNameExtensionFilter filter=new FileNameExtensionFilter("PED","ped");
			chooser.setFileFilter(filter);
			chooser.setAcceptAllFileFilterUsed(true);
			int returnVal =chooser.showOpenDialog(new JPanel());
			if (returnVal!=JFileChooser.APPROVE_OPTION) {
				return;
			}
			chooser.getSelectedFile().getName();
			txapedfilepath.setText(chooser.getSelectedFile().getAbsolutePath());
			if(!txapedfilepath.getText().isEmpty() && returnVal==chooser.APPROVE_OPTION)
			{
				labelmap.setEnabled(true);
				txamapfilepath.setEnabled(true);
				btnmapbrowse.setEnabled(true);
			}
			if (indexofBinary!=0) 
			{
				indexofStandard=0;
				indexofBinary=0;
			}
			indexofStandard++;
		}
		if(temp_button==btnmapbrowse)
		{
			JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
			FileNameExtensionFilter filter=new FileNameExtensionFilter("MAP","map");
			chooser.setFileFilter(filter);
			int returnVal =chooser.showOpenDialog(new JPanel());
			if (returnVal!=JFileChooser.APPROVE_OPTION) {
				return;
			}
			txamapfilepath.setText(chooser.getSelectedFile().getAbsolutePath());
			name_map=new String(chooser.getSelectedFile().getAbsolutePath());
		}
		
       if (temp_button==btnphenobrowse) 
       {
			
			JFileChooser pheChooser=new JFileChooser(GUIMDR.project_path);
			FileNameExtensionFilter filter=new FileNameExtensionFilter("PHE", "phe");
			pheChooser.setFileFilter(filter);
			int returnVal=pheChooser.showOpenDialog(new JPanel());
			if (returnVal!=JFileChooser.APPROVE_OPTION) {
				return;
			}
			txaphenofilepath.setText(pheChooser.getSelectedFile().getAbsolutePath());
			
			
		}
		
		if(temp_button==buttonYes)
		{
			String temp_name;
			File temp_file;
		
			
			
			
			GUIMDR.open=0;
			if (indexofBinary==2&indexofStandard==0) 
			{
				binary=0;
				GUIMDR.name_ped=new File("");
				GUIMDR.name_map=new File("");
			}
			if (indexofBinary==0&indexofStandard==1) 
			{	
				GUIMDR.name_bed=new File("");
				GUIMDR.name_bim=new File("");	
				GUIMDR.name_fam=new File("");
				binary=1;
			}
			if (binary!=-1) {
				if(binary==0)
				{
					if (GUIMDR.gmdrini.containsKey("ped")||GUIMDR.gmdrini.containsKey("map")) 
					{
						try
						{
						
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old standard data files from project \n", GUIMDR.myUI.keyWordwarning);
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tRemoving "+GUIMDR.gmdrini.get("ped")+".\t",GUIMDR.myUI.keyWordwarning);
							GUIMDR.gmdrini.remove("ped");
							txapedfilepath.setText("");
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"Successed\n",GUIMDR.myUI.keyWordsuccessed);
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tRemoving "+GUIMDR.gmdrini.get("map")+".\t",GUIMDR.myUI.keyWordwarning);
							GUIMDR.gmdrini.remove("map");
							txamapfilepath.setText("");
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"Successed\n",GUIMDR.myUI.keyWordsuccessed);						
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(is_fast_selected)
					{
						temp_name=new String(cobbinfilelist.getSelectedItem().toString());
						temp_name=temp_name.substring(0,temp_name.lastIndexOf("."));
						
						temp_file=new File(GUIMDR.project_path+"//"+temp_name+".bed");
						
						
						if(!temp_file.exists())
						{
							JOptionPane.showMessageDialog(null,"Can't find a BED file");
							return;
						}
						GUIMDR.name_bed=new File(temp_file.getAbsolutePath());
						if (GUIMDR.gmdrini.containsKey("bed")&&!GUIMDR.gmdrini.get("bed").equals(temp_file.getAbsolutePath())) 
						{
							try
							{
								GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old bed file "+GUIMDR.gmdrini.get("bed")+" from project successed\n", GUIMDR.myUI.keyWordwarning);
							} catch (BadLocationException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						GUIMDR.gmdrini.put("bed", temp_file.getAbsolutePath());
						try 
						{
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tLoading bed file "+GUIMDR.gmdrini.get("bed")+"\tSuccessed\n", GUIMDR.myUI.keyWordsuccessed);
						} catch (BadLocationException e1) 
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
							
						//Loading Fam File, it must be existed. 
						temp_file=new File(GUIMDR.project_path+"//"+temp_name+".fam");
						if(!temp_file.exists())
						{
							JOptionPane.showMessageDialog(null,"Can't find a fam file");
							return;
						}
						GUIMDR.name_fam=new File(temp_file.getAbsolutePath());
						if (GUIMDR.gmdrini.containsKey("fam")&&!GUIMDR.gmdrini.get("fam").equals(temp_file.getAbsolutePath())) {
								try {
									GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old fam file "+GUIMDR.gmdrini.get("fam")+" from project successed\n", GUIMDR.myUI.keyWordfailed);
								} catch (BadLocationException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						GUIMDR.gmdrini.put("fam", temp_file.getAbsolutePath());
						try {
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tLoading fam file "+GUIMDR.gmdrini.get("fam")+"\tSuccessed\n", GUIMDR.myUI.keyWordsuccessed);
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						
						temp_file=new File(GUIMDR.project_path+"//"+temp_name+".bim");
						if(!temp_file.exists())
						{
							
							try {
								GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tWarning: Can't find a BIM file at"+GUIMDR.gmdrini.get("bim")+"\n", GUIMDR.myUI.keyWordwarning);
							} catch (BadLocationException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							GUIMDR.gmdrini.put("bim", "NULL");
						}
						else {
							GUIMDR.name_bim=new File(temp_file.getAbsolutePath());
							if (GUIMDR.gmdrini.containsKey("bim")&&!GUIMDR.gmdrini.get("bim").equals(temp_file.getAbsolutePath())) 
							{
								try {
									
									GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old bim file "+GUIMDR.gmdrini.get("bim")+" from project successed\n", GUIMDR.myUI.keyWordwarning);
								} catch (BadLocationException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							GUIMDR.gmdrini.put("bim", temp_file.getAbsolutePath());
							try 
							{
								GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tLoading bim file "+GUIMDR.gmdrini.get("bim")+"\tSuccessed\n", GUIMDR.myUI.keyWordsuccessed);
							} catch (BadLocationException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
					else
					{
						GUIMDR.name_bed=new File(txabedfilepath.getText());
						if(!GUIMDR.name_bed.exists())
						{
							JOptionPane.showMessageDialog(null,"Can't find a BED file");
							return;
						}						
						if (GUIMDR.gmdrini.containsKey("bed")&&!GUIMDR.gmdrini.get("bed").equals(GUIMDR.name_bed.getAbsolutePath())) {
							try {
								GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old bed file "+GUIMDR.gmdrini.get("bed")+" from project successed\n", GUIMDR.myUI.keyWordwarning);
							} catch (BadLocationException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						GUIMDR.gmdrini.put("bed", GUIMDR.name_bed.getAbsolutePath());
						try {
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tLoading bed file "+GUIMDR.gmdrini.get("bed")+"\tSuccessed\n", GUIMDR.myUI.keyWordsuccessed);
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						
						GUIMDR.name_fam=new File(txafamfilepath.getText());
						if(!GUIMDR.name_fam.exists())
						{
							JOptionPane.showMessageDialog(null,"Can't find a FAM file");
							return;
						}		
						GUIMDR.name_fam=new File(txafamfilepath.getText());
						if (GUIMDR.gmdrini.containsKey("fam")&&!GUIMDR.gmdrini.get("fam").equals(txafamfilepath.getText())) {
								try {
									GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old fam file "+GUIMDR.gmdrini.get("fam")+" from project successed\n", GUIMDR.myUI.keyWordwarning);
								} catch (BadLocationException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						GUIMDR.gmdrini.put("fam", txafamfilepath.getText());
						try {
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tLoading fam file "+GUIMDR.gmdrini.get("fam")+"\tSuccessed\n", GUIMDR.myUI.keyWordsuccessed);
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
			
						
						GUIMDR.name_bim=new File(txabimfilepath.getText());
						if(!GUIMDR.name_bim.exists())
						{
							
							try {
								GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tWarning: Can't find a BIM file "+"\n", GUIMDR.myUI.keyWordwarning);
							} catch (BadLocationException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							GUIMDR.gmdrini.put("bim", "NULL");
							GUIMDR.name_bim=new File("NULL");
						}
						else {
							GUIMDR.name_bim=new File(txabimfilepath.getText());
							if (GUIMDR.gmdrini.containsKey("bim")&&!GUIMDR.gmdrini.get("bim").equals(txabimfilepath.getText())) 
							{
								try {
									
									GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old bim file "+GUIMDR.gmdrini.get("bim")+" from project successed\n", GUIMDR.myUI.keyWordwarning);
								} catch (BadLocationException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							GUIMDR.gmdrini.put("bim", txabimfilepath.getText());
							try 
							{
								GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tLoading bim file "+GUIMDR.gmdrini.get("bim")+"\tSuccessed\n", GUIMDR.myUI.keyWordsuccessed);
							} catch (BadLocationException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}

						
					}
					String[] files=new String[3];
					files[0]=GUIMDR.name_bed.getAbsolutePath();
					files[1]=GUIMDR.name_bim.getAbsolutePath();
					files[2]=GUIMDR.name_fam.getAbsolutePath();
					try {
						GUIMDR.dataset=new Plink(files);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else 
				{
					if (GUIMDR.gmdrini.containsKey("bed")||GUIMDR.gmdrini.containsKey("bim")||GUIMDR.gmdrini.containsKey("fam")) 
					{
						try
						{
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old binary data files from project \n", GUIMDR.myUI.keyWordwarning);
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tRemoving "+GUIMDR.gmdrini.get("bed")+".\t",GUIMDR.myUI.keyWordwarning);
							GUIMDR.gmdrini.remove("bed");
							txabedfilepath.setText("");
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"Successed\n",GUIMDR.myUI.keyWordsuccessed);
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tRemoving "+GUIMDR.gmdrini.get("fam")+".\t",GUIMDR.myUI.keyWordwarning);
							GUIMDR.gmdrini.remove("fam");
							txafamfilepath.setText("");
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"Successed\n",GUIMDR.myUI.keyWordsuccessed);
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tRemoving "+GUIMDR.gmdrini.get("bim")+".\t",GUIMDR.myUI.keyWordwarning);
							GUIMDR.gmdrini.remove("bim");
							txabimfilepath.setText("");
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"Successed\n",GUIMDR.myUI.keyWordsuccessed);
							
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(is_fast_selected)
					{
						temp_name=new String(cbostandardfilelist.getSelectedItem().toString());
						temp_name=temp_name.substring(0,temp_name.lastIndexOf("."));
					
						temp_file=new File(GUIMDR.project_path+"//"+temp_name+".ped");
					//	System.out.print(temp_file);
						if(!temp_file.exists())
						{
							JOptionPane.showMessageDialog(null,"Can't find a PED file");
							return;
						}
						GUIMDR.name_ped=new File(temp_file.getAbsolutePath());
						if (GUIMDR.gmdrini.containsKey("ped")&&!GUIMDR.gmdrini.get("ped").equals(GUIMDR.name_ped.getAbsolutePath())) 
						{	
							
							try {
									GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old ped file "+GUIMDR.gmdrini.get("ped")+" from project successed\n", GUIMDR.myUI.keyWordwarning);
								} catch (BadLocationException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
						}
						GUIMDR.gmdrini.put("ped", temp_file.getAbsolutePath());
						try {
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tLoading ped file "+temp_file.getAbsolutePath()+"\tSuccessed\n", GUIMDR.myUI.keyWordsuccessed);
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}		
						
					
						temp_file=new File(GUIMDR.project_path+"//"+temp_name+".map");
						
						if(!temp_file.exists())
						{
							
							try {
								GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tWarning: Can't find a Map file "+"\n", GUIMDR.myUI.keyWordwarning);
							} catch (BadLocationException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							GUIMDR.gmdrini.put("map", "NULL");
						}
						else {
							GUIMDR.name_map=new File(temp_file.getAbsolutePath());
							if (GUIMDR.gmdrini.containsKey("map")&&!GUIMDR.gmdrini.get("map").equals(GUIMDR.name_map.getAbsolutePath())) 
							{	
								
								try {
										GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old map file "+GUIMDR.gmdrini.get("map")+" from project successed\n", GUIMDR.myUI.keyWordwarning);
									} catch (BadLocationException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
							}	
							GUIMDR.gmdrini.put("map", temp_file.getAbsolutePath());
							try {
								GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tLoading map file "+temp_file.getAbsolutePath()+"\tSuccessed\n", GUIMDR.myUI.keyWordsuccessed);
							} catch (BadLocationException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}	
						}

					}
					else
					{
						temp_file=new File(txapedfilepath.getText());
						if(!temp_file.exists())
						{
							JOptionPane.showMessageDialog(null,"Can't find a PED file");
							return;
						}
						GUIMDR.name_ped=temp_file;
						
						if (GUIMDR.gmdrini.containsKey("ped")&&!GUIMDR.gmdrini.get("ped").equals(GUIMDR.name_ped.getAbsolutePath())) 
						{	
							
							try {
									GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old ped file "+GUIMDR.gmdrini.get("ped")+" from project successed\n", GUIMDR.myUI.keyWordwarning);
								} catch (BadLocationException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
						}	
						GUIMDR.gmdrini.put("ped", temp_file.getAbsolutePath());
						try {
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tLoading ped file "+temp_file.getAbsolutePath()+"\tSuccessed\n", GUIMDR.myUI.keyWordsuccessed);
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}		
						temp_file=new File(txamapfilepath.getText());
						if(!temp_file.exists())
						{
							
							try {
								GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tWarning: Can't find a Map file "+"\n", GUIMDR.myUI.keyWordwarning);
							} catch (BadLocationException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							GUIMDR.gmdrini.put("map", "NULL");
						}
						else {
							GUIMDR.name_map=new File(temp_file.getAbsolutePath());
							if (GUIMDR.gmdrini.containsKey("map")&&!GUIMDR.gmdrini.get("map").equals(GUIMDR.name_map.getAbsolutePath())) 
							{	
								
								try {
										GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old map file "+GUIMDR.gmdrini.get("map")+" from project successed\n", GUIMDR.myUI.keyWordwarning);
									} catch (BadLocationException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
							}	
							GUIMDR.gmdrini.put("map", temp_file.getAbsolutePath());
							try {
								GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tLoading map file "+temp_file.getAbsolutePath()+"\tSuccessed\n", GUIMDR.myUI.keyWordsuccessed);
							} catch (BadLocationException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}	
						}
						
						
					}
					String[] files=new String[2];
					files[0]=GUIMDR.name_ped.getAbsolutePath();
					files[1]=GUIMDR.name_map.getAbsolutePath();
					try {
						GUIMDR.dataset=new Plink(files);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//SimpleTheData thedata=new SimpleTheData(GMDR.name_ped.getAbsolutePath(),GMDR.name_map.getAbsolutePath());
					
				}
				
			}
			
			if (UsePhe) {
				
				Analysis.phenofile=new File(txaphenofilepath.getText());
				if(!Analysis.phenofile.exists())
				{
					JOptionPane.showMessageDialog(null,"Can't find a phenotype file");
					return;
				}
				GUIMDR.name_phe=new File("");
				if (GUIMDR.gmdrini.containsKey("phe")&&!GUIMDR.gmdrini.get("phe").equals(Analysis.phenofile.getAbsolutePath())) 
				{	
					
					try {
							GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old phenotype file "+GUIMDR.gmdrini.get("phe")+" from project successed\n", GUIMDR.myUI.keyWordwarning);
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}	
				GUIMDR.gmdrini.put("phe", Analysis.phenofile.getAbsolutePath());
				try {
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tLoading phenotype file "+Analysis.phenofile.getAbsolutePath()+"\tSuccessed\n", GUIMDR.myUI.keyWordsuccessed);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}					
				Analysis.txtphenopath.setText(txaphenofilepath.getText());
				Analysis.refresh();
				
			}
			
			GUIMDR.myUI.DataMenuItem[1].setEnabled(true);
			GUIMDR.myUI.ToolsMenuItem[0].setEnabled(true);
			this.dispose();
		}
		if(temp_button==buttonNo)
		{
			this.dispose();
		}
		
		
	}

	public void itemStateChanged(ItemEvent e) 
	{
		JCheckBox temp_check=(JCheckBox)e.getSource();
	
		if(temp_check==ckuseqickboxbin)
		{
			if(ckuseqickboxbin.isSelected())
			{
				cobbinfilelist.setEnabled(true);
				
				labelbed.setEnabled(false);
				txabedfilepath.setEnabled(false);
				btnbedbrowse.setEnabled(false);
				
				labelbim.setEnabled(false);
				txabimfilepath.setEnabled(false);
				btnbimbrowse.setEnabled(false);
				
				labelfam.setEnabled(false);
				txafamfilepath.setEnabled(false);
				btnfambrowse.setEnabled(false);
				
				is_fast_selected=true;
				binary=0;
			}
			else
			{
				cobbinfilelist.setEnabled(false);
				
				labelbed.setEnabled(true);
				txabedfilepath.setEnabled(true);
				btnbedbrowse.setEnabled(true);
				binary=-1;
				labelbim.setEnabled(true);
				txabimfilepath.setEnabled(true);
				btnbimbrowse.setEnabled(true);
				
				labelfam.setEnabled(true);
				txafamfilepath.setEnabled(true);
				btnfambrowse.setEnabled(true);
				is_fast_selected=false;
			}
			return;
		}
		if(temp_check==ckusequickboxstandard)
		{
			if(ckusequickboxstandard.isSelected())
			{
				cbostandardfilelist.setEnabled(true);
				
				labelped.setEnabled(false);
				txapedfilepath.setEnabled(false);
				btnpedbrowse.setEnabled(false);
			
				labelmap.setEnabled(false);
				txamapfilepath.setEnabled(false);
				btnmapbrowse.setEnabled(false);
				binary=1;
				is_fast_selected=true;
			}
			else
			{
				cbostandardfilelist.setEnabled(false);
				binary=-1;
		
				labelped.setEnabled(true);
				txapedfilepath.setEnabled(true);
				btnpedbrowse.setEnabled(true);
			
				labelmap.setEnabled(true);
				txamapfilepath.setEnabled(true);
				btnmapbrowse.setEnabled(true);
				
				is_fast_selected=false;
			}
		}
		if (temp_check==ckusepheno) 
		{
			if(ckusepheno.isSelected())
			{
			UsePhe=true;
			btnphenobrowse.setEnabled(true);
			txaphenofilepath.setEnabled(true);
			}else {
				UsePhe=false;
				btnphenobrowse.setEnabled(false);
				txaphenofilepath.setEnabled(false);
			}
		}
	}

	public void stateChanged(ChangeEvent e) 
	{
		int index=tabpanel.getSelectedIndex();
/*		switch(index)
		{
		case 0:
			binary=true;
		//	GUIMDR.standard=false;
			break;
		case 1:
			//GUIMDR.standard=true;
			binary=false;
			break;
		case 2:
			break;
		default:
			break;
		}*/
	}
}


