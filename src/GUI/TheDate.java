package GUI;

import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.io.*;

public class TheDate extends JFrame
{
	private JTabbedPane tabbed=new  JTabbedPane(JTabbedPane.NORTH);
	
	private JPanel Data_ped=new JPanel();
	
	private JTable table_ped=new JTable();
	private JScrollPane scrollPane_ped;
	public File file_ped;
	
	private String name_ped[]={"first","second","third","forth","fifth","sixth","seventh"};
	private Object data_ped[][]=new Object[100][7];
	
	private JPanel Data_map=new JPanel();
	
	private JTable table_map;
	private JScrollPane scrollPane_map;
	public File file_map;
	
	private JPanel last=new JPanel();
	private JButton button_yes=new JButton("   OK   ");
	private JButton button_no=new JButton("Cancel");

	private String name_map[]={"Chromosome","SNP","Genetic distance","Base-pair position"};
	private Object data_map[][]=new Object[50][9];
	
	
	public boolean is_change_line=false;
	public boolean is_end=false;
	
	
	
	public TheDate(String name1,String name2)
	{
		file_ped=new File(name1);
		file_map=new File(name2);
		initTabbedPane();
		
		last.add(button_yes);
		last.add(button_no);
		
		
		this.add(last,"South");
	}
	
	public void initTabbedPane()
	{
		initTabMap();
		tabbed.add("Data_ped",Data_ped);
		tabbed.add("Data_map",Data_map);
		
		this.add(tabbed,"North");
		this.setSize(970,727);
		this.setVisible(true);
		this.setLocation(270,0);
		this.getContentPane().setLayout(new BorderLayout(2,2));
	}
	
	public void initTabMap()
	{
		Data_map.setLayout(new BorderLayout(2,2));	
		initDataMap();
		Data_map.add(scrollPane_map,"Center");
		
		Data_ped.setLayout(new BorderLayout(2,2));
		initDataPed();
		Data_ped.add(scrollPane_ped,"Center");
	}
	
	public void initDataMap()
	{	
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file_map.getAbsolutePath()));
			
			String temp_string=new String();
			for(int i=0;i<30;i++)
			{
				int j=0;
				while(true)
				{
					temp_string=return_fix(br);
					data_map[i][j]=temp_string;
					if(is_change_line || is_end)
					{
						is_change_line=false;
						break;
					}
					j++;
				}
				if(is_end)
				{
					is_end=false;
					break;
				}
			}
			br.close();
		}
		catch(FileNotFoundException e)
		{
			
		}
		catch(IOException e)
		{
			
		}
		
		table_map=new JTable(data_map,name_map);
		scrollPane_map=new JScrollPane(table_map);			
	}
	
	public void initDataPed()
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file_ped.getAbsolutePath()));
			
			String temp_string=new String();
			for(int i=0;i<100;i++)
			{
				int j=0;
				while(j<7)
				{
					if((j+1)%7!=0)
					{
						temp_string=return_fix(br);
						data_ped[i][j]=temp_string;
						if(is_change_line || is_end)
						{
							is_change_line=false;
							break;
						}
					}
					else
					{
						String true_string=new String();
						while(true)
						{
							temp_string=return_fix(br);
							if(is_change_line || is_end)
							{
								is_change_line=false;
								data_ped[i][j]=true_string;
								break;
							}
							true_string=true_string+temp_string;
						}	
					}
					j++;
				}
				if(is_end)
				{
					is_end=false;
					break;
				}
			}
		}
		catch(FileNotFoundException e)
		{
			
		}
		catch(IOException e)
		{
			
		}
		table_ped=new JTable(data_ped,name_ped);
		scrollPane_ped=new JScrollPane(table_ped);
		
		int length[]={5,10,5,5,5,5,20};
		for(int i=0;i<7;i++)
		{
			switch(i)
			{
			case 0:
				table_ped.getColumnModel().getColumn(i).setPreferredWidth(100);
				break;
			case 1:
				table_ped.getColumnModel().getColumn(i).setPreferredWidth(100);
				break;
			case 2:
				table_ped.getColumnModel().getColumn(i).setPreferredWidth(5);
				break;
			case 3:
				table_ped.getColumnModel().getColumn(i).setPreferredWidth(5);
				break;
			case 4:
				table_ped.getColumnModel().getColumn(i).setPreferredWidth(5);
				break;
			case 5:
				table_ped.getColumnModel().getColumn(i).setPreferredWidth(5);
				break;
			case 6:
				table_ped.getColumnModel().getColumn(i).setPreferredWidth(300);
				break;
			default:
				break;
			}
		}
		
		
	}
	
	public String return_fix(BufferedReader br)
	{
		String temp_string=new String();
		try
		{
			int temp=br.read();
			while(temp==32 || temp==9)
			{
				temp=br.read();
			}
			while(temp!=32 && temp!=9)
			{
				if(temp==-1)
				{
					is_end=true;
					break;
				}
				
				temp_string=temp_string+(char)temp;
				temp=br.read();
				if(temp==10)
				{
					is_change_line=true;
					break;
				}
			}	
		}
		catch(FileNotFoundException e)
		{
			
		}
		catch(IOException e)
		{
			
		}
		return temp_string;
	}
}
