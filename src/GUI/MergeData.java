package GUI;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MergeData extends JFrame implements ActionListener, ItemListener
{
	private String name1[];
	private String name2[];
	//private String name3[]=new String[3];
	//private String name4[]=new String[2];
	
	private JPanel set=new JPanel();
	
	private JTabbedPane tabblepane1=new JTabbedPane();
	private JPanel BinaryInput=new JPanel();
	private JPanel set11=new JPanel();
	private JCheckBox checkbox111=new JCheckBox();
	private JComboBox<String> combobox111=new JComboBox<String>();
	private JPanel set12=new JPanel();
	private JPanel set121=new JPanel();
	private JLabel label121=new JLabel(".bed file");
	private JTextArea textarea121=new JTextArea(1,41);
	private JButton button121=new JButton("Browse");
	private JPanel set122=new JPanel();
	private JLabel label122=new JLabel(".bim file");
	private JTextArea textarea122=new JTextArea(1,41);
	private JButton button122=new JButton("Browse");
	private JPanel set123=new JPanel();
	private JLabel label123=new JLabel(".fam file");
	private JTextArea textarea123=new JTextArea(1,41);
	private JButton button123=new JButton("Browse");
	
	private JPanel StandardInput=new JPanel();
	private JPanel set21=new JPanel();
	private JCheckBox checkbox211=new JCheckBox();
	private JComboBox<String> combobox211=new JComboBox<String>();
	private JPanel set22=new JPanel();
	private JPanel set221=new JPanel();
	private JLabel label221=new JLabel(".ped file");
	private JTextArea textarea221=new JTextArea(1,41);
	private JButton button221=new JButton("Browse");
	private JPanel set222=new JPanel();
	private JLabel label222=new JLabel(".map file");
	private JTextArea textarea222=new JTextArea(1,41);
	private JButton button222=new JButton("Browse");
	
	
	private JTabbedPane tabblepane2=new JTabbedPane();
	private JPanel MergeBinaryInput=new JPanel();
	private JPanel set31=new JPanel();
	private JCheckBox checkbox311=new JCheckBox();
	private JComboBox<String> combobox311=new JComboBox<String>();
	private JPanel set32=new JPanel();
	private JPanel set321=new JPanel();
	private JLabel label321=new JLabel(".bed file");
	private JTextArea textarea321=new JTextArea(1,41);
	private JButton button321=new JButton("Browse");
	private JPanel set322=new JPanel();
	private JLabel label322=new JLabel(".bim file");
	private JTextArea textarea322=new JTextArea(1,41);
	private JButton button322=new JButton("Browse");
	private JPanel set323=new JPanel();
	private JLabel label323=new JLabel(".fam file");
	private JTextArea textarea323=new JTextArea(1,41);
	private JButton button323=new JButton("Browse");
	
	private JPanel MergeStandardInput=new JPanel();
	private JPanel set41=new JPanel();
	private JCheckBox checkbox411=new JCheckBox();
	private JComboBox<String> combobox411=new JComboBox<String>();
	private JPanel set42=new JPanel();
	private JPanel set421=new JPanel();
	private JLabel label421=new JLabel(".ped file");
	private JTextArea textarea421=new JTextArea(1,41);
	private JButton button421=new JButton("Browse");
	private JPanel set422=new JPanel();
	private JLabel label422=new JLabel(".map file");
	private JTextArea textarea422=new JTextArea(1,41);
	private JButton button422=new JButton("Browse");
	
	private JPanel last=new JPanel();
	private JButton button1_last=new JButton("merge");
	private JButton button2_last=new JButton("cancel");
	
	public MergeData()
	{
		initTab1();
		initTab2();
		initLast();
		
		if(GUIMDR.name_file!=null)
		{
			for(int i=0;i<GUIMDR.name_file.length;i++)
			{
				combobox111.addItem(GUIMDR.name_file[i]);
				combobox211.addItem(GUIMDR.name_file[i]);
				combobox311.addItem(GUIMDR.name_file[i]);
				combobox411.addItem(GUIMDR.name_file[i]);
			}
		}
		
		this.setSize(630,530);
		this.setVisible(true);
		this.setLocation(370,110);
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
	}
	
	public void initTab1()
	{
		tabblepane1.add("Binary Input",BinaryInput);
		BinaryInput.add(set11);
		set11.add(checkbox111);
		checkbox111.setSelected(false);
		checkbox111.setSelected(false);
		checkbox111.addItemListener(this);
		set11.add(combobox111);
		
		combobox111.setEnabled(false);
		set11.setBorder(BorderFactory.createTitledBorder("Quick Fileset"));
		set11.setPreferredSize(new Dimension(610,65));
		set11.setLayout(new FlowLayout(FlowLayout.LEFT));
		BinaryInput.add(set12);
		set12.add(set121);
		set121.add(label121);
		set121.add(textarea121);
		set121.add(button121);
		button121.addActionListener(this);
		set121.setPreferredSize(new Dimension(610,65));
		set121.setLayout(new FlowLayout(FlowLayout.LEFT));
		set12.add(set122);
		set122.add(label122);
		set122.add(textarea122);
		set122.add(button122);
		button122.addActionListener(this);
		set122.setPreferredSize(new Dimension(610,65));
		set122.setLayout(new FlowLayout(FlowLayout.LEFT));
		set12.add(set123);
		set123.add(label123);
		set123.add(textarea123);
		set123.add(button123);
		button123.addActionListener(this);
		set123.setPreferredSize(new Dimension(610,65));
		set123.setLayout(new FlowLayout(FlowLayout.LEFT));
		set12.setLayout(new BoxLayout(set12,BoxLayout.Y_AXIS));
		set12.setPreferredSize(new Dimension(610,200));
		BinaryInput.setPreferredSize(new Dimension(610,265));
		BinaryInput.setLayout(new BoxLayout(BinaryInput,BoxLayout.Y_AXIS));
		
		tabblepane1.add("Standard Input",StandardInput);
		StandardInput.add(set21);
		set21.add(checkbox211);
		checkbox211.setSelected(false);
		checkbox211.addItemListener(this);
		set21.add(combobox211);
		combobox211.setEnabled(false);
		set21.setBorder(BorderFactory.createTitledBorder("Quick Fileset"));
		set21.setPreferredSize(new Dimension(610,65));
		set21.setLayout(new FlowLayout(FlowLayout.LEFT));
		StandardInput.add(set22);
		set22.add(set221);
		set221.add(label221);
		set221.add(textarea221);
		set221.add(button221);
		button221.addActionListener(this);
		set221.setPreferredSize(new Dimension(610,65));
		set221.setLayout(new FlowLayout(FlowLayout.LEFT));
		set22.add(set222);
		set222.add(label222);
		set222.add(textarea222);
		set222.add(button222);
		button222.addActionListener(this);
		set222.setPreferredSize(new Dimension(610,65));
		set222.setLayout(new FlowLayout(FlowLayout.LEFT));
		set22.setLayout(new BoxLayout(set22,BoxLayout.Y_AXIS));
		set22.setPreferredSize(new Dimension(610,200));
		StandardInput.setPreferredSize(new Dimension(610,265));
		StandardInput.setLayout(new BoxLayout(StandardInput,BoxLayout.Y_AXIS));
		
		
		
		tabblepane1.setPreferredSize(new Dimension(620,210));
		this.add(tabblepane1);
	}
	
	public void initTab2()
	{
		tabblepane2.add("Merge Binary Input",MergeBinaryInput);
		MergeBinaryInput.add(set31);
		set31.add(checkbox311);
		checkbox311.setSelected(false);
		checkbox311.addItemListener(this);
		set31.add(combobox311);
		combobox311.setEnabled(false);
		set31.setBorder(BorderFactory.createTitledBorder("Quick Fileset"));
		set31.setPreferredSize(new Dimension(610,65));
		set31.setLayout(new FlowLayout(FlowLayout.LEFT));
		MergeBinaryInput.add(set32);
		set32.add(set321);
		set321.add(label321);
		set321.add(textarea321);
		set321.add(button321);
		button321.addActionListener(this);
		set321.setPreferredSize(new Dimension(610,65));
		set321.setLayout(new FlowLayout(FlowLayout.LEFT));
		set32.add(set322);
		set322.add(label322);
		set322.add(textarea322);
		set322.add(button322);
		button322.addActionListener(this);
		set322.setPreferredSize(new Dimension(610,65));
		set322.setLayout(new FlowLayout(FlowLayout.LEFT));
		set32.add(set323);
		set323.add(label323);
		set323.add(textarea323);
		set323.add(button323);
		button323.addActionListener(this);
		set323.setPreferredSize(new Dimension(610,65));
		set323.setLayout(new FlowLayout(FlowLayout.LEFT));
		set32.setLayout(new BoxLayout(set32,BoxLayout.Y_AXIS));
		set32.setPreferredSize(new Dimension(610,200));
		MergeBinaryInput.setPreferredSize(new Dimension(610,265));
		MergeBinaryInput.setLayout(new BoxLayout(MergeBinaryInput,BoxLayout.Y_AXIS));
		
		tabblepane2.add("Merge Standard Input",MergeStandardInput);
		MergeStandardInput.add(set41);
		set41.add(checkbox411);
		checkbox411.setSelected(false);
		checkbox411.addItemListener(this);
		set41.add(combobox411);
		combobox411.setEnabled(false);
		set41.setBorder(BorderFactory.createTitledBorder("Quick Fileset"));
		set41.setPreferredSize(new Dimension(610,65));
		set41.setLayout(new FlowLayout(FlowLayout.LEFT));
		MergeStandardInput.add(set42);
		set42.add(set421);
		set421.add(label421);
		set421.add(textarea421);
		set421.add(button421);
		button421.addActionListener(this);
		set421.setPreferredSize(new Dimension(610,65));
		set421.setLayout(new FlowLayout(FlowLayout.LEFT));
		set42.add(set422);
		set422.add(label422);
		set422.add(textarea422);
		set422.add(button422);
		button422.addActionListener(this);
		set422.setPreferredSize(new Dimension(610,65));
		set422.setLayout(new FlowLayout(FlowLayout.LEFT));
		set42.setLayout(new BoxLayout(set42,BoxLayout.Y_AXIS));
		set42.setPreferredSize(new Dimension(610,200));
		MergeStandardInput.setPreferredSize(new Dimension(610,265));
		MergeStandardInput.setLayout(new BoxLayout(MergeStandardInput,BoxLayout.Y_AXIS));
		
		
		tabblepane2.setPreferredSize(new Dimension(620,210));
		this.add(tabblepane2);
	}
	
	public void initLast()
	{
		last.add(button1_last);
		button1_last.addActionListener(this);
		last.add(button2_last);
		button2_last.addActionListener(this);
		last.setPreferredSize(new Dimension(620,210));
		
		this.add(last);
	}

	public void itemStateChanged(ItemEvent e)
	{
		if(e.getSource()==checkbox111)
		{
			if(checkbox111.isSelected())
			{
				combobox111.setEnabled(true);
				
				label121.setEnabled(false);
				label122.setEnabled(false);
				label123.setEnabled(false);
				textarea121.setEnabled(false);
				textarea122.setEnabled(false);
				textarea123.setEnabled(false);
				button121.setEnabled(false);
				button122.setEnabled(false);
				button123.setEnabled(false);
				
			}
			else
			{
				combobox111.setEnabled(false);
				
				label121.setEnabled(true);
				label122.setEnabled(true);
				label123.setEnabled(true);
				textarea121.setEnabled(true);
				textarea122.setEnabled(true);
				textarea123.setEnabled(true);
				button121.setEnabled(true);
				button122.setEnabled(true);
				button123.setEnabled(true);
			}
			return;
		}
		if(e.getSource()==checkbox211)
		{
			if(checkbox211.isSelected())
			{
				combobox211.setEnabled(true);
				
				label121.setEnabled(false);
				label122.setEnabled(false);
				label123.setEnabled(false);
				textarea121.setEnabled(false);
				textarea122.setEnabled(false);
				textarea123.setEnabled(false);
				button121.setEnabled(false);
				button122.setEnabled(false);
				button123.setEnabled(false);
			}
			else
			{
				combobox211.setEnabled(false);
				
				label121.setEnabled(true);
				label122.setEnabled(true);
				label123.setEnabled(true);
				textarea121.setEnabled(true);
				textarea122.setEnabled(true);
				textarea123.setEnabled(true);
				button121.setEnabled(true);
				button122.setEnabled(true);
				button123.setEnabled(true);
			}
			return;
		}
		if(e.getSource()==checkbox311)
		{
			if(checkbox311.isSelected())
			{
				combobox311.setEnabled(true);
				
				label121.setEnabled(false);
				label122.setEnabled(false);
				label123.setEnabled(false);
				textarea121.setEnabled(false);
				textarea122.setEnabled(false);
				textarea123.setEnabled(false);
				button121.setEnabled(false);
				button122.setEnabled(false);
				button123.setEnabled(false);
			}
			else
			{
				combobox311.setEnabled(false);
				
				label121.setEnabled(true);
				label122.setEnabled(true);
				label123.setEnabled(true);
				textarea121.setEnabled(true);
				textarea122.setEnabled(true);
				textarea123.setEnabled(true);
				button121.setEnabled(true);
				button122.setEnabled(true);
				button123.setEnabled(true);
			}
			return;
		}
		if(e.getSource()==checkbox411)
		{
			if(checkbox411.isSelected())
			{
				combobox411.setEnabled(true);
				
				label121.setEnabled(false);
				label122.setEnabled(false);
				label123.setEnabled(false);
				textarea121.setEnabled(false);
				textarea122.setEnabled(false);
				textarea123.setEnabled(false);
				button121.setEnabled(false);
				button122.setEnabled(false);
				button123.setEnabled(false);
			}
			else
			{
				combobox411.setEnabled(false);
				
				label121.setEnabled(true);
				label122.setEnabled(true);
				label123.setEnabled(true);
				textarea121.setEnabled(true);
				textarea122.setEnabled(true);
				textarea123.setEnabled(true);
				button121.setEnabled(true);
				button122.setEnabled(true);
				button123.setEnabled(true);
			}
			return;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource()==button1_last)
		{
			if(tabblepane1.getSelectedIndex()==0)
			{
				name1=new String[3];
				if(checkbox111.isEnabled())
				{
					String temp_string=new String(combobox111.getSelectedItem().toString());
					int index=temp_string.lastIndexOf('.');
					String temp_string2=new String(temp_string.substring(0,index));
					name1[0]=temp_string2+".bed";
					name1[1]=temp_string2+".bim";
					name1[2]=temp_string2+".fam";
				}
				else
				{
					name1[0]=textarea121.getText();
					name1[1]=textarea122.getText();
					name1[2]=textarea123.getText();
				}
			}
			else
			{
				name1=new String[2];
				if(checkbox211.isEnabled())
				{
					String temp_string=new String(combobox211.getSelectedItem().toString());
					int index=temp_string.lastIndexOf('.');
					String temp_string2=new String(temp_string.substring(0,index));
					name1[0]=temp_string2+".ped";
					name1[1]=temp_string2+".map";
				}
				else
				{
					name1[0]=textarea221.getText();
					name1[1]=textarea222.getText();
				}
			}
			if(tabblepane2.getSelectedIndex()==0)
			{
				name2=new String[3];
				if(checkbox311.isEnabled())
				{
					String temp_string=new String(combobox311.getSelectedItem().toString());
					int index=temp_string.lastIndexOf('.');
					String temp_string2=new String(temp_string.substring(0,index));
					name2[0]=temp_string2+".bed";
					name2[1]=temp_string2+".bim";
					name2[2]=temp_string2+".fam";
				}
				else
				{	
					name2[0]=textarea321.getText();
					name2[1]=textarea322.getText();
					name2[2]=textarea323.getText();
				}
			}
			else
			{
				name2=new String[2];
				if(checkbox411.isEnabled())
				{
					String temp_string=new String(combobox211.getSelectedItem().toString());
					int index=temp_string.lastIndexOf('.');
					String temp_string2=new String(temp_string.substring(0,index));
					name2[0]=temp_string2+".ped";
					name2[1]=temp_string2+".map";
				}
				else
				{
					name2[0]=textarea421.getText();
					name2[1]=textarea422.getText();
				}
			}
			String name=new String("temp_name");
			Merge theMerge=new Merge();
			try 
			{
				theMerge.Merge(name1, name2, name);
			} 
			catch (IOException e1) 
			{
				e1.printStackTrace();
			}
			return;
		}
		if(e.getSource()==button2_last)
		{
			this.dispose();
			return;
		}
		if(e.getSource()==button121)
		{
			JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
			FileNameExtensionFilter filter=new FileNameExtensionFilter("bed文件","bed");
			chooser.setFileFilter(filter);
			int returnVal =chooser.showOpenDialog(new JPanel());
			chooser.getSelectedFile().getName();
			textarea121.setText(chooser.getSelectedFile().getAbsolutePath());
			return;
		}
		if(e.getSource()==button122)
		{
			JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
			FileNameExtensionFilter filter=new FileNameExtensionFilter("bim文件","bim");
			chooser.setFileFilter(filter);
			int returnVal =chooser.showOpenDialog(new JPanel());
			chooser.getSelectedFile().getName();
			textarea122.setText(chooser.getSelectedFile().getAbsolutePath());
			return;
		}
		if(e.getSource()==button123)
		{
			JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
			FileNameExtensionFilter filter=new FileNameExtensionFilter("fam文件","fam");
			chooser.setFileFilter(filter);
			int returnVal =chooser.showOpenDialog(new JPanel());
			chooser.getSelectedFile().getName();
			textarea123.setText(chooser.getSelectedFile().getAbsolutePath());
			return;
		}
		if(e.getSource()==button221)
		{
			JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
			FileNameExtensionFilter filter=new FileNameExtensionFilter("ped文件","ped");
			chooser.setFileFilter(filter);
			int returnVal =chooser.showOpenDialog(new JPanel());
			chooser.getSelectedFile().getName();
			textarea221.setText(chooser.getSelectedFile().getAbsolutePath());
			return;
		}
		if(e.getSource()==button222)
		{
			JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
			FileNameExtensionFilter filter=new FileNameExtensionFilter("map文件","map");
			chooser.setFileFilter(filter);
			int returnVal =chooser.showOpenDialog(new JPanel());
			chooser.getSelectedFile().getName();
			textarea222.setText(chooser.getSelectedFile().getAbsolutePath());
			return;
		}
		if(e.getSource()==button321)
		{
			JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
			FileNameExtensionFilter filter=new FileNameExtensionFilter("bed文件","bed");
			chooser.setFileFilter(filter);
			int returnVal =chooser.showOpenDialog(new JPanel());
			chooser.getSelectedFile().getName();
			textarea321.setText(chooser.getSelectedFile().getAbsolutePath());
			return;
		}
		if(e.getSource()==button322)
		{
			JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
			FileNameExtensionFilter filter=new FileNameExtensionFilter("bim文件","bim");
			chooser.setFileFilter(filter);
			int returnVal =chooser.showOpenDialog(new JPanel());
			chooser.getSelectedFile().getName();
			textarea322.setText(chooser.getSelectedFile().getAbsolutePath());
			return;
		}
		if(e.getSource()==button323)
		{
			JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
			FileNameExtensionFilter filter=new FileNameExtensionFilter("fam文件","fam");
			chooser.setFileFilter(filter);
			int returnVal =chooser.showOpenDialog(new JPanel());
			chooser.getSelectedFile().getName();
			textarea323.setText(chooser.getSelectedFile().getAbsolutePath());
			return;
		}
		if(e.getSource()==button421)
		{
			JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
			FileNameExtensionFilter filter=new FileNameExtensionFilter("ped文件","ped");
			chooser.setFileFilter(filter);
			int returnVal =chooser.showOpenDialog(new JPanel());
			chooser.getSelectedFile().getName();
			textarea421.setText(chooser.getSelectedFile().getAbsolutePath());
			return;
		}
		if(e.getSource()==button422)
		{
			JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
			FileNameExtensionFilter filter=new FileNameExtensionFilter("map文件","map");
			chooser.setFileFilter(filter);
			int returnVal =chooser.showOpenDialog(new JPanel());
			chooser.getSelectedFile().getName();
			textarea422.setText(chooser.getSelectedFile().getAbsolutePath());
			return;
		}
	}
}
