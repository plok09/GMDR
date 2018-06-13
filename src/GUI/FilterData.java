package GUI;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FilterData extends JFrame implements ItemListener,ActionListener
{
	
	private JPanel last=new JPanel();
	private JButton button_yes=new JButton("OK");
	private JButton button_no=new JButton("Cancel");
	
	private JPanel Map=new JPanel();
	
	private JPanel set11=new JPanel();
	private JCheckBox checkbox111=new JCheckBox("Chromosome(--chr)");
	private JComboBox<String> combobox111=new JComboBox<String>();
	
	private JPanel set12=new JPanel();
	private JCheckBox checkbox121=new JCheckBox("--from");
	private JComboBox<String> combobox121=new JComboBox<String>();
	private JTextArea textarea121=new JTextArea(1,7);
	private JLabel label121=new JLabel("--to");
	private JComboBox<String> combobox122=new JComboBox<String>();
	private JTextArea textarea122=new JTextArea(1,7);
	
	private JPanel set13=new JPanel();
	private JCheckBox checkbox131=new JCheckBox("Specific SNP");
	private JTextArea textarea131=new JTextArea(1,7);
	private JLabel label131=new JLabel("Optional kb window(--window)");
	private JTextArea textarea132=new JTextArea(1,7);
	
	
	private JPanel List=new JPanel();
	
	private JPanel set21=new JPanel();
	private JCheckBox checkbox211=new JCheckBox("SNPs--");
	private JComboBox<String> combobox211=new JComboBox<String>();
	private JTextArea textarea211=new JTextArea(1,7);
	private JButton button211=new JButton("Browse");
	
	private JPanel set22=new JPanel();
	private JCheckBox checkbox221=new JCheckBox("Individuals--");
	private JComboBox<String> combobox221=new JComboBox<String>();
	private JTextArea textarea221=new JTextArea(1,7);
	private JButton button221=new JButton("Browse");
	
	
	private JPanel IndivdualSNP=new JPanel();
	
	private JPanel set31=new JPanel();
	private JCheckBox checkbox311=new JCheckBox("Filter variables(--filter)");
	private JTextArea textarea311=new JTextArea(1,7);
	private JButton button311=new JButton("Browse");
	private JTextArea textarea312=new JTextArea(1,5);
	private JLabel label311=new JLabel("Optional column gene(mfilter)");
	private JTextArea textarea313=new JTextArea(1,5);
	
	
	private JPanel set32=new JPanel();
	private JCheckBox checkbox321=new JCheckBox("Sex filter --filter-");
	private JComboBox<String> combobox321=new JComboBox<String>();
	
	private JPanel set33=new JPanel();
	private JCheckBox checkbox331=new JCheckBox("Individuals--");
	private JComboBox<String> combobox331=new JComboBox<String>();
	
	public FilterData() throws FileNotFoundException, IOException
	{		
		this.setSize(770,640);
		this.setVisible(true);
		this.setLocation(300,20);
		
		initMap();
		initList();
		initIndivdualSNP();
		
		last.add(button_yes);
		button_yes.addActionListener(this);
		last.add(button_no);
		button_no.addActionListener(this);
		last.setPreferredSize(new Dimension(750,40));

		this.add(Map);
		this.add(List);
		this.add(IndivdualSNP);
		this.add(last);
		this.setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
	}
	
	private void initMap() throws FileNotFoundException, IOException 
	{
		Map.add(set11);
		set11.add(checkbox111);
		checkbox111.addItemListener(this);
		set11.add(combobox111);
		
		String temp_string[] = null;
		
        if (!GUIMDR.name_map.getName().equals("")) {
        	temp_string=new String[NewFilter.getchromid(GUIMDR.name_map.toString()).length];
    		temp_string=NewFilter.getchromid(GUIMDR.name_map.toString()).clone();
		}
        if (!GUIMDR.name_bim.getName().equals("")) {
        	temp_string=new String[NewFilter.getchromid(GUIMDR.name_bim.toString()).length];
    		temp_string=NewFilter.getchromid(GUIMDR.name_bim.toString()).clone();
		}
		for(int i=0;i<temp_string.length;i++)
		{
			combobox111.addItem(temp_string[i]);
		}
		combobox111.setEnabled(false);
		set11.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		Map.add(set12);
		set12.add(checkbox121);
		checkbox121.addItemListener(this);
		set12.add(combobox121);
		combobox121.setEnabled(false);
		set12.add(textarea121);
		textarea121.setEnabled(false);
		set12.add(label121);
		label121.setEnabled(false);
		set12.add(combobox122);
		combobox122.setEnabled(false);
		set12.add(textarea122);
		textarea122.setEnabled(false);
		set12.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		Map.add(set13);
	    set13.add(checkbox131);
	    checkbox131.addItemListener(this);
	    set13.add(textarea131);
	    textarea131.setEnabled(false);
	    set13.add(label131);
	    label131.setEnabled(false);
	    set13.add(textarea132);
	    textarea132.setEnabled(false);
	    set13.setLayout(new FlowLayout(FlowLayout.LEFT));
	    
	    Map.setPreferredSize(new Dimension(750,190));
	    Map.setBorder(BorderFactory.createTitledBorder("By Map"));
	    Map.setLayout(new BoxLayout(Map,BoxLayout.Y_AXIS));
	    //Map.setAlignmentX(JPanel.LEFT_ALIGNMENT);	
	}
	
	private void initList()
	{	
		List.add(set21);
		set21.add(checkbox211);
		checkbox211.addItemListener(this);
		set21.add(combobox211);
		combobox211.addItem("extract");
		combobox211.addItem("exclude");
		combobox211.setEnabled(false);
		set21.add(textarea211);
		textarea211.setEnabled(false);
		set21.add(button211);
		button211.addActionListener(this);
		button211.setEnabled(false);
		set21.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		List.add(set22);
		set22.add(checkbox221);
		checkbox221.addItemListener(this);
		set22.add(combobox221);
		combobox221.setEnabled(false);
		combobox221.addItem("keep");
		combobox221.addItem("remove");
		set22.add(textarea221);
		textarea221.setEnabled(false);
		set22.add(button221);
		button221.setEnabled(false);
		button221.addActionListener(this);
		set22.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		List.setPreferredSize(new Dimension(750,150));
		List.setBorder(BorderFactory.createTitledBorder("By List"));
	    List.setLayout(new BoxLayout(List,BoxLayout.Y_AXIS));
	    //List.setAlignmentX(JPanel.LEFT_ALIGNMENT);	
	}

	private void initIndivdualSNP() 
	{
		IndivdualSNP.add(set31);
		set31.add(checkbox311);
		checkbox311.addItemListener(this);
		set31.add(textarea311);
		textarea311.setEnabled(false);
		set31.add(button311);
		button311.setEnabled(false);
		button311.addActionListener(this);
		set31.add(textarea312);
		textarea312.setEnabled(false);
		set31.add(label311);
		label311.setEnabled(false);
		set31.add(textarea313);
		textarea313.setEnabled(false);
		set31.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		IndivdualSNP.add(set32);
		set32.add(checkbox321);
		checkbox321.addItemListener(this);
		set32.add(combobox321);
		combobox321.setEnabled(false);
		combobox321.addItem("Males");
		combobox321.addItem("Females");
		set32.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		IndivdualSNP.add(set33);
		set33.add(checkbox331);
		checkbox331.addItemListener(this);
		set33.add(combobox331);
		combobox331.setEnabled(false);
		combobox331.addItem("Cases");
		combobox331.addItem("Controls");
		set33.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		IndivdualSNP.setPreferredSize(new Dimension(750,190));
		IndivdualSNP.setBorder(BorderFactory.createTitledBorder("By IndivdualSNP"));
		IndivdualSNP.setLayout(new BoxLayout(IndivdualSNP,BoxLayout.Y_AXIS));
		//IndivdualSNP.setAlignmentX(JPanel.LEFT_ALIGNMENT);	
	}

	public void actionPerformed(ActionEvent e)
	{
		JButton temp_button=(JButton)e.getSource();
		{
			if(temp_button==button_yes)
			{
				outputSelect();
				JFrame temp_frame=new JFrame();
				JPanel temp_panel=new JPanel();
				JTable temp_table=new JTable();
				boolean a=GUIMDR.name_bed.getPath().equals("");
				if (!GUIMDR.name_bed.getPath().equals("")&&!GUIMDR.name_bim.getPath().equals("")&&!GUIMDR.name_fam.getPath().equals(""))
				{
					String temp_file[] = new String[3];
					temp_file[0]=new String(GUIMDR.name_bed.getAbsolutePath());
					temp_file[1]=new String(GUIMDR.name_bim.getAbsolutePath());
					temp_file[2]=new String(GUIMDR.name_fam.getAbsolutePath());
					
					
					NewFilter temp_filter;
					try {
						temp_filter=new NewFilter(temp_file);
					} catch (IOException e2) 
					{
						return;
					}
					
					try
					{
						temp_filter.func(GUIMDR.output);
						temp_table=new JTable(temp_filter.func(GUIMDR.output),temp_filter.Gethead());
					}
					catch (IOException e1)
					{
						
					}
				}
				if (!GUIMDR.name_ped.getPath().equals("")&&!GUIMDR.name_map.getPath().equals("")) 
				{
			
					
					
					PedFilter temp_filter;
					temp_filter=new PedFilter(GUIMDR.name_ped,GUIMDR.name_map);
					
					try
					{
					//	String[][] k=(String[][])temp_filter.func(GMDR.output);
						temp_table=new JTable(temp_filter.func(GUIMDR.output),temp_filter.Gethead());
					}
					catch (IOException e1)
					{
						
					}
				}
				
				JScrollPane temp_scroll=new JScrollPane(temp_table);
				temp_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				temp_table.setPreferredScrollableViewportSize(new Dimension(1100,500));
				temp_frame.add(temp_panel);
				temp_panel.add(temp_scroll);
				temp_frame.setSize(1200,660);
				temp_frame.setVisible(true);
				temp_frame.setLocation(100, 50);
				
				
				this.dispose();
				return;
			}
			if(temp_button==button_no)
			{
				this.dispose();
				return;
			}
			if(temp_button==button211)
			{
				JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
				FileNameExtensionFilter filter=new FileNameExtensionFilter("txt文件","txt");
				chooser.setFileFilter(filter);
				int returnVal =chooser.showOpenDialog(new JPanel());
				chooser.getSelectedFile().getName();
				textarea211.setText(chooser.getSelectedFile().getAbsolutePath());
				return;
			}
			if(temp_button==button221)
			{
				JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
				FileNameExtensionFilter filter=new FileNameExtensionFilter("txt文件","txt");
				chooser.setFileFilter(filter);
				int returnVal =chooser.showOpenDialog(new JPanel());
				chooser.getSelectedFile().getName();
				textarea221.setText(chooser.getSelectedFile().getAbsolutePath());
				return;
			}
			if(temp_button==button311)
			{
				JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
				FileNameExtensionFilter filter=new FileNameExtensionFilter("txt文件","txt");
				chooser.setFileFilter(filter);
				int returnVal =chooser.showOpenDialog(new JPanel());
				chooser.getSelectedFile().getName();
				textarea211.setText(chooser.getSelectedFile().getAbsolutePath());
				return;
			}
		}
	}

	public void itemStateChanged(ItemEvent e) 
	{
		JCheckBox temp_check=new JCheckBox();
		temp_check=(JCheckBox)e.getSource();
		boolean is_selected=false;
		if(temp_check==checkbox111)
		{
			if(checkbox111.isSelected())
			{
				is_selected=true;
				checkbox131.setSelected(false);
				if(checkbox121.isSelected())
				{
					combobox121.removeAllItems();
					combobox122.removeAllItems();
					
					combobox121.addItem("BASE");
					combobox121.addItem("KB");
					combobox121.addItem("MB");
					combobox122.addItem("BASE");
					combobox122.addItem("KB");
					combobox122.addItem("MB");
				}
			}
			else
				is_selected=false;
			combobox111.setEnabled(is_selected);
			return;
		}
		if(temp_check==checkbox121)
		{
			if(checkbox121.isSelected())
			{
				is_selected=true;
				checkbox131.setSelected(false);
				combobox121.removeAllItems();
				combobox122.removeAllItems();
				if(checkbox111.isSelected())
				{
					combobox121.addItem("BASE");
					combobox121.addItem("KB");
					combobox121.addItem("MB");
					combobox122.addItem("BASE");
					combobox122.addItem("KB");
					combobox122.addItem("MB");
				}
				else
				{
					combobox121.addItem("SNP");
					combobox122.addItem("SNP");
				}
			}
			else
			{
				combobox121.removeAllItems();
				combobox122.removeAllItems();
				is_selected=false;
			}
			combobox121.setEnabled(is_selected);
			textarea121.setEnabled(is_selected);
			label121.setEnabled(is_selected);
			combobox122.setEnabled(is_selected);
			textarea122.setEnabled(is_selected);
			return;
		}
		if(temp_check==checkbox131)
		{
			if(checkbox131.isSelected())
			{
				is_selected=true;
				checkbox111.setSelected(false);
				checkbox121.setSelected(false);
			}
			else
				is_selected=false;
			textarea131.setEnabled(is_selected);
			label131.setEnabled(is_selected);
			textarea132.setEnabled(is_selected);
			return;
		}
		if(temp_check==checkbox211)
		{
			if(checkbox211.isSelected())
			{
				is_selected=true;
			}
			else
				is_selected=false;
			combobox211.setEnabled(is_selected);
			textarea211.setEnabled(is_selected);
			button211.setEnabled(is_selected);
			return;
		}
		if(temp_check==checkbox221)
		{
			if(checkbox221.isSelected())
				is_selected=true;
			else
				is_selected=false;
			combobox221.setEnabled(is_selected);
			textarea221.setEnabled(is_selected);
			button221.setEnabled(is_selected);
			return;
		}
		if(temp_check==checkbox311)
		{
			if(checkbox311.isSelected())
				is_selected=true;
			else
				is_selected=false;
			textarea311.setEnabled(is_selected);
			button311.setEnabled(is_selected);
			textarea312.setEnabled(is_selected);
			return;
		}
		if(temp_check==checkbox321)
		{
			if(checkbox321.isSelected())
				is_selected=true;
			else
				is_selected=false;
			combobox321.setEnabled(is_selected);
			return;
		}
		if(temp_check==checkbox331)
		{
			if(checkbox331.isSelected())
				is_selected=true;
			else
				is_selected=false;
			combobox331.setEnabled(is_selected);
			return;
		}
	}

	public void outputSelect()
	{
		if(checkbox111.isSelected())
		{
			GUIMDR.output[0][0]="1";
			GUIMDR.output[0][1]=combobox111.getSelectedItem().toString();
		}
		else
		{
			GUIMDR.output[0][0]="0";
			GUIMDR.output[0][1]="0";
		}
		if(checkbox121.isSelected())
		{
			GUIMDR.output[1][0]="1";
			GUIMDR.output[1][1]=combobox121.getSelectedItem().toString();
			GUIMDR.output[1][2]=textarea121.getText();
			GUIMDR.output[1][3]=combobox122.getSelectedItem().toString();
			GUIMDR.output[1][4]=textarea122.getText();
		}
		else
		{
			GUIMDR.output[1][0]="0";
			GUIMDR.output[1][1]="0";
			GUIMDR.output[1][2]="0";
			GUIMDR.output[1][3]="0";
			GUIMDR.output[1][4]="0";
		}
		if(checkbox131.isSelected())
		{
			GUIMDR.output[2][0]="1";
			GUIMDR.output[2][1]=textarea131.getText();
			GUIMDR.output[2][2]=textarea132.getText();
		}
		else
		{
			GUIMDR.output[2][0]="0";
			GUIMDR.output[2][1]="0";
			GUIMDR.output[2][2]="0";
		}
		if(checkbox211.isSelected())
		{
			GUIMDR.output[3][0]="1";
			GUIMDR.output[3][1]=combobox211.getSelectedItem().toString();
			GUIMDR.output[3][2]=textarea211.getText();
		}
		else
		{
			GUIMDR.output[3][0]="0";
			GUIMDR.output[3][1]="0";
			GUIMDR.output[3][2]="0";
		}
		if(checkbox221.isSelected())
		{
			GUIMDR.output[4][0]="1";
			GUIMDR.output[4][1]=combobox221.getSelectedItem().toString();
			GUIMDR.output[4][2]=textarea221.getText();
		}
		else
		{
			GUIMDR.output[4][0]="0";
			GUIMDR.output[4][1]="0";
			GUIMDR.output[4][2]="0";
		}
		if(checkbox311.isSelected())
		{
			GUIMDR.output[5][0]="1";
			GUIMDR.output[5][1]=textarea311.getText();
			GUIMDR.output[5][2]=textarea312.getText();
			GUIMDR.output[5][3]=textarea313.getText();
		}
		else
		{
			GUIMDR.output[5][0]="0";
			GUIMDR.output[5][1]="0";
			GUIMDR.output[5][2]="0";
			GUIMDR.output[5][3]="0";
		}
		if(checkbox321.isSelected())
		{
			GUIMDR.output[6][0]="1";
			GUIMDR.output[6][1]=combobox321.getSelectedItem().toString();
		}
		else
		{
			GUIMDR.output[6][0]="0";
			GUIMDR.output[6][1]="0";
		}
		if(checkbox331.isSelected())
		{
			GUIMDR.output[7][0]="1";
			GUIMDR.output[7][1]=combobox331.getSelectedItem().toString();
		}
		else
		{
			GUIMDR.output[7][0]="0";
			GUIMDR.output[7][1]="0";
		}
	}
}
	
