package GUI;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class OutputData extends JFrame implements ItemListener,ActionListener,ChangeListener
{
	private boolean is_fast_selected=false;
	
	private JTabbedPane tabbed=new JTabbedPane(JTabbedPane.NORTH);
	private String name_ped;
	private String name_map;
	
	private JPanel last=new JPanel();
	private JButton buttonYes=new JButton("   OK   ");
	private JButton buttonNo=new JButton ("Cancel");
			
	private JPanel BinaryInput =new JPanel();
	
	private JPanel QuickFileset1=new JPanel();
	private JCheckBox checkbox1=new JCheckBox("");
	private JComboBox<String> combobox1=new JComboBox<String>();
	private String fast_name1[];
	
	private JPanel set1=new JPanel();
	private JPanel set11=new JPanel();
	private JLabel bed1=new JLabel(".bed file");
	private JTextArea text11=new JTextArea(1,26);
	private JButton button11=new JButton("Browse");
	
	private JPanel set12=new JPanel();
	private JLabel bim1=new JLabel(".bim file");
	private JTextArea text12=new JTextArea(1,26);
	private JButton button12=new JButton("Browse");
	
	private JPanel set13=new JPanel();
	private JLabel fam1=new JLabel(".fam file");
	private JTextArea text13=new JTextArea(1,26);
	private JButton button13=new JButton("Browse");
	
	
	
	private JPanel StandardInput=new JPanel();
	
	private JPanel QuickFileset2=new JPanel();
	private JCheckBox checkbox2=new JCheckBox("");
	private JComboBox<String> combobox2=new JComboBox<String>();
	private String fast_name2[];
	
	private JPanel set2=new JPanel();
	private JPanel set21=new JPanel();
	private JLabel ped2=new JLabel(".ped file");
	private JTextArea text21=new JTextArea(1,26);
	private JButton button21=new JButton("Browse");
	
	private JPanel set22=new JPanel();
	private JLabel map2=new JLabel(".map file");
	private JTextArea text22=new JTextArea(1,26);
	private JButton button22=new JButton("Browse");
	
	
	
	private JPanel AlternatePhenotype=new JPanel();
	
	private JPanel set3=new JPanel();
	private JCheckBox checkbox3=new JCheckBox("");
	private JLabel label3=new JLabel("use alternative phenotype");
	private JTextArea text3=new JTextArea(1,15);
	private JButton button3=new JButton("Browse");
	
	
	
	public OutputData()
	{
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
		
		tabbed.add("Binary Input",BinaryInput);
		tabbed.add("Standard Input",StandardInput);
		tabbed.add("Alternate Phenotype",AlternatePhenotype);
		tabbed.addChangeListener(this);
		this.add(tabbed,"North");
		this.add(last,"South");
	
		initBinary();
		initStandardInput();
		initAleternatePhenotype();
		initLast();
		
		this.setSize(470,300);
		this.setVisible(true);
		this.setLocation(470,210);
		this.getContentPane().setLayout(new BorderLayout(2,2));
	}
	
	public void initBinary()
	{
		QuickFileset1.add(checkbox1);
		checkbox1.addItemListener(this);
		QuickFileset1.setBorder(BorderFactory.createTitledBorder("QuickFileset"));
		QuickFileset1.add(combobox1);
		combobox1.setEnabled(false);
		for(int i=0;i<fast_name1.length;i++)
		{
			combobox1.addItem(fast_name1[i]);
		}
		
		set11.add(bed1);
		set11.add(text11);
		set11.add(button11);
		button11.addActionListener(this);
		
		set12.add(bim1);
		set12.add(text12);
		set12.add(button12);
		button12.addActionListener(this);
		
		set13.add(fam1);
		set13.add(text13);
		set13.add(button13);
		button13.addActionListener(this);
		
		set1.add(set11);
		set1.add(set12);
		set1.add(set13);
		set1.setLayout(new GridLayout(1,1,0,0));
		
		
		BinaryInput.add(QuickFileset1);
		BinaryInput.add(set11);
		BinaryInput.add(set12);
		BinaryInput.add(set13);
		BinaryInput.setLayout(new BoxLayout(BinaryInput,BoxLayout.Y_AXIS));
	}
	
	void initStandardInput()
	{
		QuickFileset2.add(checkbox2);
		checkbox2.setSelected(false);
		checkbox2.addItemListener(this);
		QuickFileset2.setBorder(BorderFactory.createTitledBorder("QuickFileset"));
		QuickFileset2.add(combobox2);
		combobox2.setEnabled(false);
		for(int i=0;i<fast_name2.length;i++)
		{
			combobox2.addItem(fast_name2[i]);
		}
		
		set21.add(ped2);
		set21.add(text21);
		set21.add(button21);
		button21.addActionListener(this);
		
		set22.add(map2);
		set22.add(text22);
		set22.add(button22);	
		button22.addActionListener(this);
		
		
		set2.add(set21);
		set2.add(set22);
		set2.setLayout(new GridLayout(1,1,0,0));
		
		StandardInput.add(QuickFileset2);
		StandardInput.add(set21);
		StandardInput.add(set22);
		StandardInput.setLayout(new BoxLayout(StandardInput,BoxLayout.Y_AXIS));
	}

	public void initAleternatePhenotype()
	{
		set3.add(checkbox3);
		set3.add(label3);
		set3.add(text3);
		set3.add(button3);
		
		AlternatePhenotype.add(set3);
	}
	
	public void initLast()
	{
		last.add(buttonYes);
		buttonYes.addActionListener(this);
		last.add(buttonNo);
		buttonNo.addActionListener(this);
		
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		JButton temp_button;
		temp_button=(JButton)e.getSource();
		if (temp_button==button11)
		{
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("BED","bed");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(new JPanel()); 
			chooser.getSelectedFile().getName();
			text11.setText(chooser.getSelectedFile().getName());
		
		}
		if (temp_button==button12)
		{
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("BIM","bim");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(new JPanel()); 
			chooser.getSelectedFile().getName();
			text12.setText(chooser.getSelectedFile().getName());
		}
		if (temp_button==button13)
		{
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("FAM","fam");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(new JPanel()); 
			chooser.getSelectedFile().getName();
			text13.setText(chooser.getSelectedFile().getName());
		}
		if(temp_button==button21)
		{
			JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
			chooser.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filter=new FileNameExtensionFilter("ped文件","ped");
			chooser.setFileFilter(filter);
			int returnVal =chooser.showOpenDialog(new JPanel());
			chooser.getSelectedFile().getName();
			text21.setText(chooser.getSelectedFile().getAbsolutePath());
			name_ped=new String(chooser.getSelectedFile().getAbsolutePath());
			
			
			
			if(!text21.getText().isEmpty() && returnVal==chooser.APPROVE_OPTION)
			{
				map2.setEnabled(true);
				text22.setEnabled(true);
				button22.setEnabled(true);
			}
		}
		if(temp_button==button22)
		{
			JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
			FileNameExtensionFilter filter=new FileNameExtensionFilter("map文件","map");
			chooser.setFileFilter(filter);
			int returnVal =chooser.showOpenDialog(new JPanel());
			chooser.getSelectedFile().getName();
			text22.setText(chooser.getSelectedFile().getAbsolutePath());
			name_map=new String(chooser.getSelectedFile().getAbsolutePath());
		}
		
		if(temp_button==buttonYes)
		{
			String temp_name;
			File temp_file;
			if(GUIMDR.Binary)
			{
				if(is_fast_selected)
				{
					temp_name=new String(combobox1.getSelectedItem().toString());
					temp_name=temp_name.substring(0,temp_name.lastIndexOf("."));
					
					temp_file=new File(GUIMDR.project_path+"//"+temp_name+".bed");
					if(!temp_file.exists())
					{
						JOptionPane.showMessageDialog(null,"没有找到指定的bed文件");
						return;
					}
					else
					{
						GUIMDR.name_bed=new File(temp_file.getAbsolutePath());
					}
					
					temp_file=new File(GUIMDR.project_path+"//"+temp_name+".bim");
					if(!temp_file.exists())
					{
						JOptionPane.showMessageDialog(null,"没有找到指定的bim文件");
						return;
					}
					else
					{
						GUIMDR.name_bim=new File(temp_file.getAbsolutePath());
					}
					
					temp_file=new File(GUIMDR.project_path+"//"+temp_name+".fam");
					if(!temp_file.exists())
					{
						JOptionPane.showMessageDialog(null,"没有找到指定的fam文件");
						return;
					}
					else
					{
						GUIMDR.name_fam=new File(temp_file.getAbsolutePath());
					}
					this.dispose();
				}
				else
				{
					GUIMDR.name_bed=new File(text11.getText());
					GUIMDR.name_bim=new File(text12.getText());
					GUIMDR.name_fam=new File(text13.getText());
				}
			}
			else
			{
				if(is_fast_selected)
				{
					temp_name=new String(combobox1.getSelectedItem().toString());
					temp_name=temp_name.substring(0,temp_name.lastIndexOf("."));
				
					temp_file=new File(GUIMDR.project_path+"//"+temp_name+".ped");
					if(!temp_file.exists())
					{
						JOptionPane.showMessageDialog(null,"没有找到指定的ped文件");
						return;
					}
					else
					{
						GUIMDR.name_ped=new File(temp_file.getAbsolutePath());
					}
				
					temp_file=new File(GUIMDR.project_path+"//"+temp_name+".map");
					if(!temp_file.exists())
					{
						JOptionPane.showMessageDialog(null,"没有找到指定的map文件");
						return;
					}
					else
					{
						GUIMDR.name_map=new File(temp_file.getAbsolutePath());
					}
				}
				else
				{
					GUIMDR.name_ped=new File(text21.getText());
					GUIMDR.name_map=new File(text22.getText());
				}
			}
		}
		if(temp_button==buttonNo)
		{
			this.dispose();
		}
	}

	public void itemStateChanged(ItemEvent e) 
	{
		JCheckBox temp_check=(JCheckBox)e.getSource();
		if(temp_check==checkbox1)
		{
			if(checkbox1.isSelected())
			{
				combobox1.setEnabled(true);
				
				bed1.setEnabled(false);
				text11.setEnabled(false);
				button11.setEnabled(false);
				
				bim1.setEnabled(false);
				text12.setEnabled(false);
				button12.setEnabled(false);
				
				fam1.setEnabled(false);
				text13.setEnabled(false);
				button13.setEnabled(false);
				
				is_fast_selected=true;
			}
			else
			{
				combobox1.setEnabled(false);
				
				bed1.setEnabled(true);
				text11.setEnabled(true);
				button11.setEnabled(true);
				
				bim1.setEnabled(true);
				text12.setEnabled(true);
				button12.setEnabled(true);
				
				fam1.setEnabled(true);
				text13.setEnabled(true);
				button13.setEnabled(true);
				
				is_fast_selected=false;
			}
			return;
		}
		if(temp_check==checkbox2)
		{
			if(checkbox2.isSelected())
			{
				combobox2.setEnabled(true);
				
				ped2.setEnabled(false);
				text21.setEnabled(false);
				button21.setEnabled(false);
			
				map2.setEnabled(false);
				text22.setEnabled(false);
				button22.setEnabled(false);
				
				is_fast_selected=true;
			}
			else
			{
				combobox2.setEnabled(false);
				
				ped2.setEnabled(true);
				text21.setEnabled(true);
				button21.setEnabled(true);
			
				map2.setEnabled(true);
				text22.setEnabled(true);
				button22.setEnabled(true);
				
				is_fast_selected=false;
			}
		}
	}

	public void stateChanged(ChangeEvent e) 
	{
		int index=tabbed.getSelectedIndex();
		switch(index)
		{
		case 0:
			GUIMDR.Binary=true;
			GUIMDR.standard=false;
			break;
		case 1:
			GUIMDR.standard=true;
			GUIMDR.Binary=false;
			break;
		case 2:
			break;
		default:
			break;
		}
	}
}


