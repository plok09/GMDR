package GUI;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;


public class NewProject{
	
	private JPanel file_name=new JPanel();
	public String selected_directory="";
	public JLabel file_names[];
	private File chooseFile;
	int resultval;
	public NewProject()
	{
	
		 JFileChooser choose=new JFileChooser();
		 choose.setCurrentDirectory(new File( GUIMDR.project_path));
		 FileNameExtensionFilter filter=new FileNameExtensionFilter("GMDR Projest File", "gmdr");
		 choose.setFileFilter(filter);
		 resultval =choose.showSaveDialog(new JPanel());
		
		 if (resultval==JFileChooser.APPROVE_OPTION) 
		 {
			 selected_directory=choose.getCurrentDirectory().toString();
			 GUIMDR.project_path=new String(selected_directory);
			 GUIMDR.gmdrini_path=choose.getSelectedFile().getAbsolutePath().toString()+".gmdr";
			 readFile();
		}
  
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
			//if (chooseFile.listFiles()[i].getPath().endsWith(".ped")|chooseFile.listFiles()[i].getPath().endsWith(".map")|chooseFile.listFiles()[i].getPath().endsWith(".bed")|chooseFile.listFiles()[i].getPath().endsWith(".fam")|chooseFile.listFiles()[i].getPath().endsWith(".bim")) {
				file_names[i]=new JLabel(chooseFile.listFiles()[i].getName(),JLabel.LEFT);
				GUIMDR.name_file[i]=new String(chooseFile.listFiles()[i].getName());
				file_name.add(file_names[i]);
			//}
			
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
