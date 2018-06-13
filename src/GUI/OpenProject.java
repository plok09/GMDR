package GUI;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.omg.CORBA.StringValueHelper;

import java.awt.*;
import java.io.*;

public class OpenProject extends JPanel
{
	private JPanel file_name=new JPanel();
	public String selected_directory="";
	public JLabel file_names[];
	private File chooseFile;
	public  String gmdrproject;
	public int returnVal;
	public OpenProject()
	{
	
		JFileChooser chooser=new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		FileNameExtensionFilter filter=new FileNameExtensionFilter("GMDR Project File", "gmdr");
		chooser.setFileFilter(filter);
		returnVal=chooser.showOpenDialog(new JPanel());
		if (returnVal==JFileChooser.APPROVE_OPTION) 
		{
			selected_directory=chooser.getSelectedFile().getParentFile().getAbsolutePath();
			GUIMDR.project_path=new String(selected_directory);
			GUIMDR.gmdrini_path=chooser.getSelectedFile().getAbsolutePath().toString();
			readFile();
		}
		
	}
	public OpenProject(File projectfile)
	{
	
		selected_directory=projectfile.getParentFile().getAbsolutePath();
		GUIMDR.project_path=new String(selected_directory);
		GUIMDR.gmdrini_path=projectfile.getAbsolutePath().toString();
		readFile();
		
	}
	public void readFile()
	{
		chooseFile=new File(selected_directory);
		file_name.setLayout(new BoxLayout(file_name,BoxLayout.Y_AXIS));
	
		if(!chooseFile.isDirectory())
			System.out.print("Wrong");
		
		File[] t=chooseFile.listFiles();
		
		GUIMDR.name_file=new String[t.length];
		int length=t.length;
		file_names=new JLabel[length];
		for(int i=0;i<t.length;i++)
		{
			file_names[i]=new JLabel(chooseFile.listFiles()[i].getName(),JLabel.LEFT);
			GUIMDR.name_file[i]=new String(chooseFile.listFiles()[i].getName());
			file_name.add(file_names[i]);
		}
	}
	public JPanel returnNames()
	{
		GUIMDR.myUI.menu[1].setEnabled(true);
		return file_name;
	}
	
	public String getProjectpath() 
	{
		File rootdir=new File(selected_directory);
		return rootdir.getAbsolutePath();
		
	}
}
