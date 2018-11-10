package GUI;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Map.Entry;
import java.util.*;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument.BranchElement;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


import DataManage.Plink;

import java.text.SimpleDateFormat;

import gmdr.Main;

import javax.swing.GroupLayout.Alignment;

public class UI extends JFrame implements ItemListener,ActionListener,MenuListener
{
	public JMenuBar menuBar=new JMenuBar();
	public JPopupMenu popupMenu=new JPopupMenu();
	String Jppmenuitems[]=
		{
		"load as bed file",
		"load as bim file",
		"load as fam file",
		"load as ped file",
		"load as map file",
		"load as phenotype file",
		"load as root directory",
		"load as gmdr project file"
		};
	public JMenuItem popupMenuItems[]=new JMenuItem[Jppmenuitems.length];
	String menuTitle[]={
			"Project",
			"Data",
			"Tools",
			"Analysis",
			"Advanced",
			"Help"};
	public JMenu menu[]=new JMenu[menuTitle.length];
	
	String FileMenuItemTitle[]={
			"New",
			"Open",
			"Save",
			"Save as",
			"Recent",
			"Exit"};
	public JMenuItem FileMenuItem[]=new JMenuItem[FileMenuItemTitle.length];
	
	String DataMenuItemTitle[]={
			"Load Data",
			"Output Data",
			"Merge Data"};
	public JMenuItem DataMenuItem[]=new JMenuItem[DataMenuItemTitle.length];
	
	String ToolsMenuItemTitle[]={
			"View File",
			"Define Data Structure",
			"Summary Statistics",
			"Filter Data"};
	public JMenuItem ToolsMenuItem[]=new JMenuItem[ToolsMenuItemTitle.length];
	
	
	String AdvancedMenuItemTitle[]={
			"Import GMDR Operation",
			"Create GMDR Operation",
			"Add non-GMDR Operation"
	};
	public JMenuItem AdvancedMenuItem[]=new JMenuItem[AdvancedMenuItemTitle.length];
	
	String HelpMenuItemTitle[]={
			"Help",
			"About"
	};
	public JMenuItem HelpMenuItem[]=new JMenuItem[HelpMenuItemTitle.length];
	
//	public String name[];
	
	JSplitPane sppmain = new JSplitPane();
	JScrollPane scpdirtree = new JScrollPane();
	JScrollPane scpHistoryCommand = new JScrollPane();
	protected FileTree fileTree=new FileTree();
	private JTextPane txtpnHistoryCommand = new JTextPane();
	public StyledDocument doc = txtpnHistoryCommand.getStyledDocument();
	public SimpleAttributeSet keyWordfailed = new SimpleAttributeSet();
	public SimpleAttributeSet keyWordsuccessed = new SimpleAttributeSet();
	public SimpleAttributeSet keyWordwarning = new SimpleAttributeSet();
	public FileTree getFileTree() 
	{
		return fileTree;
	}
	public Point getMenuLocation(int i) 
	{
		return menu[i].getLocationOnScreen();
	}
	public  Dimension getMenusize(int i) 
	{
		return menu[i].getSize();
	}
	public Point FocusMenuLocation(int i) 
	{
		int y=(int) (getMenuLocation(i).y+getMenusize(i).getHeight()/2);
		int x=(int) (getMenuLocation(i).x+getMenusize(i).getWidth()/2);
		Point point=new Point(x, y);
		return point;
	}
	public int FileMenuItemLocation(int itemid) 
	{
		int height=(int) FileMenuItem[itemid].getSize().getHeight();
		return height*(itemid+1);
	}
	public UI()
	{
		getContentPane().setBackground(UIManager.getColor("Button.focus"));
		setBackground(SystemColor.inactiveCaption);
		initTop();
		initpopmenu();
		this.setTitle("Generalized Multifactor Dimensionality Reduction V1.0");
		this.setSize(1358,791);
		this.setLocation(300, 30);//700,330
		this.setVisible(true);
		getContentPane().setLayout(new BorderLayout());
		menuBar.setBackground(UIManager.getColor("Button.focus"));
		
		getContentPane().add(menuBar, BorderLayout.NORTH);
		sppmain.setEnabled(false);
		sppmain.setResizeWeight(0.2);
		getContentPane().add(sppmain, BorderLayout.CENTER);
		sppmain.setLeftComponent(scpdirtree);

		sppmain.setRightComponent(scpHistoryCommand);
		txtpnHistoryCommand.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtpnHistoryCommand.setEditable(true);
	//	txtpnHistoryCommand.setDropMode(DropMode.ON);
		txtpnHistoryCommand.setForeground(new Color(0, 255, 0));
		txtpnHistoryCommand.setBackground(Color.BLACK);
		
		LocalDateTime currentTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		txtpnHistoryCommand.setText(currentTime.format(formatter)+"\nHistory Command :\n");
		
		StyleConstants.setForeground(keyWordfailed, Color.RED);
		StyleConstants.setForeground(keyWordsuccessed, Color.GREEN);
		StyleConstants.setForeground(keyWordwarning, Color.YELLOW);
		scpHistoryCommand.setViewportView(txtpnHistoryCommand);

		
		
		fileTree.setBackground(SystemColor.window);
		fileTree.addTreeWillExpandListener(new TreeWillExpand_actionadapter(this));
		scpdirtree.setViewportView(fileTree);
		FileTreeModel model=new FileTreeModel(new DefaultMutableTreeNode(new FileNode("root",null,null,true)));
	    fileTree.setModel(model);
	    fileTree.setCellRenderer(new FileTreeRenderer());
	    fileTree.addMouseListener(new mouseinfiletree_actionadapter(this));
	    
	    
	    
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.ToolsMenuItem[0].setEnabled(false);
		this.addWindowListener(new WindowAdapter() 
		{

			public void windowClosing(WindowEvent w) 
			{
				if (GUIMDR.gmdrini.size()!=0) {
					int dialogResult = JOptionPane.showConfirmDialog (null, "Would You Like to Save your Previous results First?","Warning",JOptionPane.YES_NO_OPTION);
					if(dialogResult == JOptionPane.YES_OPTION){
					  // Saving code here
						if (GUIMDR.gmdrini_path.equals("")) {
							NewProject myNewProject=new NewProject();
						}
						Saveingproject(GUIMDR.gmdrini_path);
					}
				} 
				
				
			}
		});
		
	}
	
	public void Saveingproject(String path)
	{
		File gmdr=new File(path);
		try {
			FileWriter gmdrfile=new FileWriter(gmdr);
			Iterator<Entry<String, String>> it = GUIMDR.gmdrini.entrySet().iterator();  
	        while (it.hasNext()) {  
	            Map.Entry<String, String> entry =  it.next();  
	            String key = entry.getKey();  
	            String value = entry.getValue();  
	            gmdrfile.write( key + ">>" + value+"\n");  
	        }  
			gmdrfile.close();
			} catch (IOException e) {
			e.printStackTrace();
			}
	}
	public void initpopmenu() 
	{
		for(int i=0;i<Jppmenuitems.length;i++)
		{
			popupMenuItems[i]=new JMenuItem(Jppmenuitems[i]);
			popupMenuItems[i].addActionListener(new popmenu_actionadapter(this));
			popupMenu.add(popupMenuItems[i]);
		}
	}
	
	private void getList(String address) throws IOException
	{
		  	File a=new File(address);
		  	List<String[]> FileList=new ArrayList<String[]>();
			FileReader fr=new FileReader(a);
			BufferedReader br=new BufferedReader(fr);
			String tmp=null;
			while ((tmp=br.readLine())!=null) 
			{
				String[] tmpslpit=tmp.split(">>");
				GUIMDR.gmdrini.put(tmpslpit[0],tmpslpit[1]);
			}
	
	}
	
	private void OpenGMDRProject(OpenProject myProject) 
	{
		try {
				doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tLoading project file "+GUIMDR.project_path+"\tSuccessed.\n",keyWordsuccessed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			FileTreeModel model=new FileTreeModel(new DefaultMutableTreeNode(new FileNode("root",null,null,true)),myProject.getProjectpath());
            fileTree.setModel(model);
            fileTree.setCellRenderer(new FileTreeRenderer());
			try {
				getList(GUIMDR.gmdrini_path);
			} catch (IOException e3) 
			{
				e3.printStackTrace();
			}
			Iterator<Entry<String, String>> it = GUIMDR.gmdrini.entrySet().iterator();    
			int flag=0;
	        while (it.hasNext()) 
	        {  
	            Map.Entry<String, String> entry =  it.next();  
	            String key = entry.getKey();  
	            String value = entry.getValue();  
	            File inputfile=new File(value);
	            if (!inputfile.exists()) 
	            {
	            	it.remove();
	            	if (key.equals("bim")|key.equals("map")) 
	            	{
	            		try {
							doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tWarning :Loading "+key+" file "+value+"\tFailed.\n",keyWordwarning);
							doc.insertString(doc.getLength(),"\t\t"+key +" is not found\n",keyWordwarning);
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}else
					{
						JOptionPane.showMessageDialog(new JFrame(), "Error :" +value+" is not found","Error",JOptionPane.ERROR_MESSAGE);
						try {
							doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tError :Loading "+key+" file "+value+"\tFailed.\n",keyWordfailed);
							doc.insertString(doc.getLength(),"\t\t"+value +" is not found\n",keyWordfailed);
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						continue;
					}
					
				}
	          
	            switch (key) 
	            {
				case "bed":
					GUIMDR.name_bed=new File(value);
					this.ToolsMenuItem[0].setEnabled(true);
					this.DataMenuItem[1].setEnabled(true);
					try {
						doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tLoading bed file "+GUIMDR.name_bed+"\tSuccessed.\n",keyWordsuccessed);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					flag-=1;
					break;
				case "bim":
					GUIMDR.name_bim=new File(value);
					if (GUIMDR.name_bim.exists()) 
		            {
						try {
						doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tLoading bim file "+GUIMDR.name_bim+"\tSuccessed.\n",keyWordsuccessed);
						} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						}
					this.ToolsMenuItem[0].setEnabled(true);
					this.DataMenuItem[1].setEnabled(true);
					
					}
					flag-=1;
					break;
				case "fam":
					GUIMDR.name_fam=new File(value);
					try {
						doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tLoading fam file "+GUIMDR.name_fam+"\tSuccessed.\n",keyWordsuccessed);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					this.ToolsMenuItem[0].setEnabled(true);
					this.DataMenuItem[1].setEnabled(true);
					flag-=1;
					break;
				case "ped":
					GUIMDR.name_ped=new File(value);
					try {
						doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tLoading ped file "+GUIMDR.name_ped+"\tSuccessed.\n",keyWordsuccessed);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					this.ToolsMenuItem[0].setEnabled(true);
					flag+=1;
					break;
					
				case "map":
					GUIMDR.name_map=new File(value);
					if (GUIMDR.name_map.exists()) 
			        {
						try 
						{
							doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tLoading map file "+GUIMDR.name_map+"\tSuccessed.\n",keyWordsuccessed);
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		            }
					this.ToolsMenuItem[0].setEnabled(true);
					this.DataMenuItem[1].setEnabled(true);
					
					flag+=1;
					break;	
					
				case "phe":
					GUIMDR.name_phe=new File(value);
					try {
						doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tLoading phenofile file "+value+"\tSuccessed.\n",keyWordsuccessed);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					this.DataMenuItem[1].setEnabled(true);
					Analysis.phenofile=GUIMDR.name_phe;
					Analysis.txtphenopath.setText(value);
					Analysis.refresh();
					break;
				}
	        } 
	        if (flag==-3) 
	        {
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
	        else if (flag==2) 
	        	{
		        	String[] files=new String[2];
					files[0]=GUIMDR.name_ped.getAbsolutePath();
					files[1]=GUIMDR.name_map.getAbsolutePath();
					try {
						GUIMDR.dataset=new Plink(files);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	        	}else 
	        	{
	        		try {
						doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tLoading Genotyoe date failed.\n",keyWordfailed);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
	        
			this.DataMenuItem[1].updateUI();
			this.ToolsMenuItem[0].updateUI();
			this.FileMenuItem[2].setEnabled(true);
		    this.FileMenuItem[3].setEnabled(true);
		
	}
	public void actionPerformed(ActionEvent e) 
	{	
		JMenuItem temp=(JMenuItem)e.getSource();
		switch(temp.getText())
		{
		case "New":
			{
				NewProject myNewProject=new NewProject();
				if (myNewProject.resultval==JFileChooser.APPROVE_OPTION) 
				{
				  FileTreeModel model=new FileTreeModel(new DefaultMutableTreeNode(new FileNode("root",null,null,true)),myNewProject.getProjectpath());
		          fileTree.setModel(model);
		          fileTree.setCellRenderer(new FileTreeRenderer());
				  GUIMDR.open=0;
				  this.ToolsMenuItem[0].setEnabled(false);
				  this.DataMenuItem[1].setEnabled(false);
				  this.FileMenuItem[2].setEnabled(false);
				  this.FileMenuItem[3].setEnabled(false);
				  repaint(); 
				try {
					doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tCreating project in "+GUIMDR.project_path+"\tSuccessed.\n",keyWordsuccessed);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				}     
				
			}
			break;
		case "Open":
		    {
		    	OpenProject myOpenProject=new OpenProject();
				if (myOpenProject.returnVal==JFileChooser.APPROVE_OPTION) 
				{
					OpenGMDRProject(myOpenProject);
				}
		    }
			break;
		case "Exit" :
			{
				if (GUIMDR.gmdrini.size()!=0) 
				{
			
					int dialogResult = JOptionPane.showConfirmDialog (null, "Would You Like to Save your Previous results First?","Warning",JOptionPane.YES_NO_OPTION);
					if(dialogResult == JOptionPane.YES_OPTION)
					{
					  // Saving code here
						if (GUIMDR.gmdrini_path.equals("")) 
						{
							NewProject myNewProject=new NewProject();
						}
						Saveingproject(GUIMDR.gmdrini_path);
					}
				} 
				System.exit(0);
			}
			break;
		case "Load Data":
			
            LoadData myLoadData=new LoadData();
			this.FileMenuItem[2].setEnabled(true);
			this.FileMenuItem[3].setEnabled(true);
			break;
		case "Output Data":
			OutputData myOutputData=new OutputData();
			break;
		case "Merge Data":
			MergeData myMergeData=new MergeData();
			break;
		case "Analysis":
			Analysis analysis=new Analysis();
			break;
		case "Filter Data":
			try {
				FilterData myFilterData=new FilterData();
			} catch (IOException e2) {
				// TODO ×Ô¶¯Éú³ÉµÄ catch ¿é
				e2.printStackTrace();
			}
			break;
		case "Summary Statistics":
			if ((!GUIMDR.name_bed.isFile()|!GUIMDR.name_bim.isFile()|!GUIMDR.name_fam.isFile())&&(!GUIMDR.name_ped.isFile()|!GUIMDR.name_map.isFile())) 
			{
					JOptionPane.showMessageDialog(null,"Please input  Genotype Files");
					try {
						GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tTry to summarize the genotype Failed\n", GUIMDR.myUI.keyWordfailed);
						GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), "\t\tGenotype Files is not existed\n", GUIMDR.myUI.keyWordfailed);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					return;
			} 
			if (GUIMDR.dataset==null)
			{
				String[] genofiles;
				if(GUIMDR.name_bed.getName()=="")
				{
					genofiles=new String[2];
					genofiles[0]=new String(GUIMDR.name_ped.getAbsolutePath());
					genofiles[1]=new String(GUIMDR.name_map.getAbsolutePath());
				}
				else
				{
					genofiles=new String[3];
					genofiles[0]=new String(GUIMDR.name_bed.getAbsolutePath());
					genofiles[1]=new String(GUIMDR.name_bim.getAbsolutePath());
					genofiles[2]=new String(GUIMDR.name_fam.getAbsolutePath());
				}
				try {
					GUIMDR.dataset=new Plink(genofiles);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			SummaryStatisticsFrame statisticsFrame=new SummaryStatisticsFrame();
			break;
		case "Save":	
			Saveingproject(GUIMDR.gmdrini_path);
			try {
				doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tSaving project "+GUIMDR.gmdrini_path+"\tSuccessed.\n",keyWordsuccessed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case "Save as":	
		{ 
			JFileChooser choose=new JFileChooser();
			 choose.setCurrentDirectory(new File(GUIMDR.project_path));
			 FileNameExtensionFilter filter=new FileNameExtensionFilter("GMDR Projest File", "gmdr");
			 choose.setFileFilter(filter);
			 int resultval =choose.showSaveDialog(new JPanel());
			 if (resultval==JFileChooser.APPROVE_OPTION) 
			 {
				 String path=choose.getSelectedFile().getAbsolutePath().toString()+".gmdr";
				 Saveingproject(path);
				 try {
						doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tSaving project "+path+"\tSuccessed.\n",keyWordsuccessed);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			 }
		}
			break;
		case "View File":
				ViewFile vf=new ViewFile();
			break;
		case "About":
			AboutFrame aboutFrame=new AboutFrame();
			break;
		case "Help":
			try {
				
				HelpAuto helpAuto=new HelpAuto(this);
				
			} catch (AWTException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
/*			if (Desktop.isDesktopSupported())
			{
				File myFile = new File("manual.pdf");
				if (myFile.isFile()) 
				{
					 try {
					      Desktop.getDesktop().open(myFile);
					    } catch (IOException ex) 
					 {
					        // no application registered for PDFs
					    	}
				}else 
				{
					JOptionPane.showMessageDialog(null, "Cannot find the manual at "+System.getProperty("user.dir")+"/example/manual.pdf", "Eorr",JOptionPane.YES_OPTION );
				    
				}
			}
			*/
			break;
		default:
			break;
		}
	}

	public void mouseclickedinfiletree_actionPerformed(MouseEvent e) 
	{
		
		int n=fileTree.getRowForLocation(e.getX(),e.getY());
		if (n<0) 
		{
			return;
		}
		if (e.getButton()==MouseEvent.BUTTON3&&fileTree.getSelectionPath()!=null) 
		{
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
		
	}
	
	public void mouseclickedinbar_actionPerformed(MouseEvent e) 
	{
		
		if(e.getSource()==menu[3])
		{
			    Analysis analysis=new Analysis();
				this.setVisible(false);
				//this.setExtendedState(JFrame.ICONIFIED);
		}
		
	}
	private void checkdatafileisbinary(boolean binary) 
	{
		if (binary) 
		{
			if (GUIMDR.gmdrini.containsKey("ped")||GUIMDR.gmdrini.containsKey("map")) 
			{
				try
				{
				
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old standard data files from project \n", GUIMDR.myUI.keyWordwarning);
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tRemoving "+GUIMDR.gmdrini.get("ped")+".\t",GUIMDR.myUI.keyWordwarning);
					GUIMDR.gmdrini.remove("ped");
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"Successed\n",GUIMDR.myUI.keyWordsuccessed);
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tRemoving "+GUIMDR.gmdrini.get("map")+".\t",GUIMDR.myUI.keyWordwarning);
					GUIMDR.gmdrini.remove("map");
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"Successed\n",GUIMDR.myUI.keyWordsuccessed);						
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}else 
		{
			if (GUIMDR.gmdrini.containsKey("bed")||GUIMDR.gmdrini.containsKey("bim")||GUIMDR.gmdrini.containsKey("fam")) 
			{
				try
				{
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old binary data files from project \n", GUIMDR.myUI.keyWordwarning);
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tRemoving "+GUIMDR.gmdrini.get("bed")+".\t",GUIMDR.myUI.keyWordwarning);
					GUIMDR.gmdrini.remove("bed");
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"Successed\n",GUIMDR.myUI.keyWordsuccessed);
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tRemoving "+GUIMDR.gmdrini.get("fam")+".\t",GUIMDR.myUI.keyWordwarning);
					GUIMDR.gmdrini.remove("fam");
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"Successed\n",GUIMDR.myUI.keyWordsuccessed);
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tRemoving "+GUIMDR.gmdrini.get("bim")+".\t",GUIMDR.myUI.keyWordwarning);
					GUIMDR.gmdrini.remove("bim");
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"Successed\n",GUIMDR.myUI.keyWordsuccessed);
					
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
	
	}
	public void popmenu_actionPerformed(ActionEvent e)
	{
		TreePath treePath=fileTree.getSelectionPath();
		DefaultMutableTreeNode MutableTreeNode=(DefaultMutableTreeNode) treePath.getLastPathComponent();
		FileNode fileNode=(FileNode) MutableTreeNode.getUserObject();
		File selectedNode=fileNode.file;
		JMenuItem menuevent=(JMenuItem) e.getSource();
		String selecteditem=menuevent.getText();

		if (selecteditem.equals("load as bed file")) 
		{
			if (!selectedNode.isFile()) 
			{

				JOptionPane.showMessageDialog(new Frame(), "Error ：  The selected item must be a bed file!","Error",JOptionPane.ERROR_MESSAGE);
				try {
					doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tOpen "+selectedNode+"  failed.\n",keyWordfailed);
					doc.insertString(doc.getLength(),"\t\tThe selected item must be a bed file\n",keyWordfailed);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return;
			}
			
			if (GUIMDR.gmdrini.containsKey("bed")&&!GUIMDR.gmdrini.get("bed").equals(GUIMDR.name_bed.getAbsolutePath())) 
			{
				try {
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old bed file "+GUIMDR.gmdrini.get("bed")+" from project successed\n", GUIMDR.myUI.keyWordwarning);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			GUIMDR.name_bed=selectedNode;	
			GUIMDR.gmdrini.put("bed",selectedNode.getAbsolutePath());
			this.ToolsMenuItem[0].setEnabled(true);
			this.DataMenuItem[1].setEnabled(true);
			try {
				
				doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tLoading bed file "+selectedNode+"\tSuccessed.\n",keyWordsuccessed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (selecteditem.equals("load as bim file")) 
		{
			if (!selectedNode.isFile()) 
			{

				JOptionPane.showMessageDialog(new Frame(), "Error ：  The selected item must be a bim file!","Error",JOptionPane.ERROR_MESSAGE);
				try {
					doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tOpen "+selectedNode+"  failed.\n",keyWordfailed);
					doc.insertString(doc.getLength(),"\t\tThe selected item must be a bim file\n",keyWordfailed);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return;
			}
			
			if (GUIMDR.gmdrini.containsKey("bim")&&!GUIMDR.gmdrini.get("bim").equals(GUIMDR.name_bim.getAbsolutePath())) {
				try {
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old bim file "+GUIMDR.gmdrini.get("bim")+" from project successed\n", GUIMDR.myUI.keyWordwarning);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			GUIMDR.name_bim=selectedNode;
			GUIMDR.gmdrini.put("bim",selectedNode.getAbsolutePath());
			this.ToolsMenuItem[0].setEnabled(true);
			this.DataMenuItem[1].setEnabled(true);
			try {
				doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tLoading bim file "+selectedNode+"\tSuccessed.\n",keyWordsuccessed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (selecteditem.equals("load as fam file")) 
		{
			if (!selectedNode.isFile()) 
			{

				JOptionPane.showMessageDialog(new Frame(), "Error ：  The selected item must be a fam file!","Error",JOptionPane.ERROR_MESSAGE);
				try {
					doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tOpen "+selectedNode+"  failed.\n",keyWordfailed);
					doc.insertString(doc.getLength(),"\t\tThe selected item must be a fam file\n",keyWordfailed);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return;
			}
			
			if (GUIMDR.gmdrini.containsKey("bim")&&!GUIMDR.gmdrini.get("fam").equals(GUIMDR.name_fam.getAbsolutePath()))
			{
				
				if (GUIMDR.gmdrini.containsKey("fam")) {
					try {
						GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old fam file "+GUIMDR.gmdrini.get("fam")+" from project successed\n", GUIMDR.myUI.keyWordwarning);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			GUIMDR.name_fam=selectedNode;
			GUIMDR.gmdrini.put("fam",selectedNode.getAbsolutePath());
			this.ToolsMenuItem[0].setEnabled(true);
			this.DataMenuItem[1].setEnabled(true);
			try {
				doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tLoading fam file "+selectedNode+"\tSuccessed.\n",keyWordsuccessed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (selecteditem.equals("load as ped file")) 
		{
			if (!selectedNode.isFile()) 
			{

				JOptionPane.showMessageDialog(new Frame(), "Error ：  The selected item must be a ped file!","Error",JOptionPane.ERROR_MESSAGE);
				try {
					doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tOpen "+selectedNode+"  failed.\n",keyWordfailed);
					doc.insertString(doc.getLength(),"\t\tThe selected item must be a ped file\n",keyWordfailed);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return;
			}
			if (GUIMDR.gmdrini.containsKey("ped")&&!GUIMDR.gmdrini.get("ped").equals(GUIMDR.name_ped.getAbsolutePath())) 
			{	
				
				try {
						GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old ped file "+GUIMDR.gmdrini.get("ped")+" from project successed\n", GUIMDR.myUI.keyWordwarning);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}	
			GUIMDR.name_ped=selectedNode;
			GUIMDR.gmdrini.put("ped",selectedNode.getAbsolutePath());
			this.ToolsMenuItem[0].setEnabled(true);
			this.DataMenuItem[1].setEnabled(true);
			try {
				doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tLoading ped file "+selectedNode+"\tSuccessed.\n",keyWordsuccessed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (selecteditem.equals("load as map file")) 
		{
			if (!selectedNode.isFile()) 
			{

				JOptionPane.showMessageDialog(new Frame(), "Error ：  The selected item must be a map file!","Error",JOptionPane.ERROR_MESSAGE);
				try {
					doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tOpen "+selectedNode+"  failed.\n",keyWordfailed);
					doc.insertString(doc.getLength(),"\t\tThe selected item must be a map file\n",keyWordfailed);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return;
			}
			if (GUIMDR.gmdrini.containsKey("map")&&!GUIMDR.gmdrini.get("map").equals(GUIMDR.name_map.getAbsolutePath())) 
			{	
				
				try {
						GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old map file "+GUIMDR.gmdrini.get("map")+" from project successed\n", GUIMDR.myUI.keyWordwarning);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}	
			GUIMDR.name_map=new File(selecteditem);
			GUIMDR.gmdrini.put("map",selectedNode.getAbsolutePath());
			this.ToolsMenuItem[0].setEnabled(true);
			this.DataMenuItem[1].setEnabled(true);
			try {
				doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tLoading map file "+selectedNode+"\tSuccessed.\n",keyWordsuccessed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (selecteditem.equals("load as phenotype file")) 
		{
			if (!selectedNode.isFile()) 
			{

				JOptionPane.showMessageDialog(new Frame(), "Error ：  The selected item must be a phenotype file!","Error",JOptionPane.ERROR_MESSAGE);
				try {
					doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tOpen "+selectedNode+"  failed.\n",keyWordfailed);
					doc.insertString(doc.getLength(),"\t\tThe selected item must be a phenotype file\n",keyWordfailed);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return;
			}
			if (GUIMDR.gmdrini.containsKey("phe")&&!GUIMDR.gmdrini.get("phe").equals(Analysis.phenofile.getAbsolutePath())) 
			{	
				
				try {
						GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old phenotype file "+GUIMDR.gmdrini.get("phe")+" from project successed\n", GUIMDR.myUI.keyWordwarning);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}	
			Analysis.phenofile=selectedNode;
			GUIMDR.gmdrini.put("phe",selectedNode.getAbsolutePath());
			this.ToolsMenuItem[0].setEnabled(true);
			this.DataMenuItem[1].setEnabled(true);
			try {
				doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tLoading phenotype file "+selectedNode+"\tSuccessed.\n",keyWordsuccessed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (selecteditem.equals("load as root directory"))
		{
			if (GUIMDR.gmdrini.size()!=0) 
			{
				try 
				{
					doc.insertString(doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tSaveing project "+GUIMDR.gmdrini_path+".\t", keyWordwarning);
					Saveingproject(GUIMDR.gmdrini_path);
					doc.insertString(doc.getLength(),"Successed. \n", keyWordsuccessed);
					doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tCleaning all data files\n", keyWordwarning);
			        for (Iterator<Entry<String, String>> it = GUIMDR.gmdrini.entrySet().iterator();it.hasNext();) {  
			            Map.Entry<String, String> entry =  it.next();  
			            String key = entry.getKey();  
			            String value = entry.getValue();  
			            doc.insertString(doc.getLength(),"\t\tCleaning"+value+"\t", keyWordwarning);
						it.remove();
						doc.insertString(doc.getLength(),"Successed.\n", keyWordsuccessed);
			        }  
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			if (!selectedNode.isDirectory()) 
			{
				try {
					doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tCreating project in "+selectedNode+"  failed.\n",keyWordfailed);
					doc.insertString(doc.getLength(),"\t\tThe selected item must be a directory\n",keyWordfailed);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(new Frame(), "Error ： The selected item must be a directory!","Error",JOptionPane.ERROR_MESSAGE);
				return;
			}
			GUIMDR.project_path=selectedNode.getAbsolutePath();
			NewProject myNewProject=new NewProject();
			if (myNewProject.resultval==JFileChooser.APPROVE_OPTION) 
			{
				FileTreeModel model=new FileTreeModel(new DefaultMutableTreeNode(new FileNode("root",null,null,true)),myNewProject.getProjectpath());
	            fileTree.setModel(model);
	            fileTree.setCellRenderer(new FileTreeRenderer());
	            fileTree.updateUI();
				GUIMDR.open=0;
			} 
			try {
				doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tCreating project in "+selectedNode+"\tSuccessed.\n",keyWordsuccessed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (selecteditem.equals("load as gmdr project file"))
		{
			if (GUIMDR.gmdrini.size()!=0) 
			{
				try 
				{
					doc.insertString(doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tSaveing project "+GUIMDR.gmdrini_path+".\t", keyWordwarning);
					Saveingproject(GUIMDR.gmdrini_path);
					doc.insertString(doc.getLength(),"Successed. \n", keyWordsuccessed);
					doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tCleaning all data files\n", keyWordwarning);
			        for (Iterator<Entry<String, String>> it = GUIMDR.gmdrini.entrySet().iterator();it.hasNext();) {  
			            Map.Entry<String, String> entry =  it.next();  
			            String key = entry.getKey();  
			            String value = entry.getValue();  
			            doc.insertString(doc.getLength(),"\t\tCleaning"+value+"\t", keyWordwarning);
						it.remove();
						doc.insertString(doc.getLength(),"Successed.\n", keyWordsuccessed);
			        }  
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (!selectedNode.isFile()) 
			{
				
				JOptionPane.showMessageDialog(new Frame(), "Error ：  The selected item must be a gmdr project file!","Error",JOptionPane.ERROR_MESSAGE);
				try {
					doc.insertString(doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tOpening project in "+selectedNode+"  failed.\n",keyWordfailed);
					doc.insertString(doc.getLength(),"\t\tThe selected item must be a gmdr project file\n",keyWordfailed);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return;
			}
			OpenProject openProject=new OpenProject(selectedNode);
			OpenGMDRProject(openProject);
		}
		
		txtpnHistoryCommand.updateUI();
	}
	class popmenu_actionadapter implements ActionListener
	{
		private UI adaptee;
		public popmenu_actionadapter(UI adaptee)
		{
			this.adaptee=adaptee;
			// TODO Auto-generated constructor stub
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			adaptee.popmenu_actionPerformed(e);
		}
		
	}
	
	class TreeWillExpand_actionadapter implements TreeWillExpandListener
	{
		UI adaptee;
		public TreeWillExpand_actionadapter(UI adaptee) 
		{
			// TODO Auto-generated constructor stub
			this.adaptee=adaptee;
		}
		@Override
		public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
		{
			// TODO Auto-generated method stub
			adaptee.getContentPane().setCursor(new Cursor(Cursor.WAIT_CURSOR));
			DefaultMutableTreeNode lastTreeNode=(DefaultMutableTreeNode) event.getPath().getLastPathComponent();
			FileNode fileNode = (FileNode) lastTreeNode.getUserObject();
			File[] files = null;
			if (!fileNode.isInit) 
			{
						
		 	    if (fileNode.isDummyRoot)
		 	             {
		 	                 files = adaptee.getFileTree().getFileSystemView().getRoots();
		 	             } else 
		 	             {
		 	                 files = adaptee.getFileTree().getFileSystemView().getFiles(
		 	                         ((FileNode) lastTreeNode.getUserObject()).file,
		 	                         false);
		 	             }
			}
					
			for (int i = 0; i < files.length; i++)
			{
                        FileNode childFileNode = new FileNode(
                        		adaptee.getFileTree().getFileSystemView().getSystemDisplayName(files[i]),
                        		adaptee.getFileTree().getFileSystemView().getSystemIcon(files[i]), files[i],
                                false);
                        DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(childFileNode);
                        lastTreeNode.add(childTreeNode);
             }
			fileNode.isInit=true;
			adaptee.getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
		}
		
	}
	
	class mouseinfiletree_actionadapter implements MouseListener
	{
		UI adaptee;
		
		public mouseinfiletree_actionadapter(UI adaptee) {
			// TODO Auto-generated constructor stub
			this.adaptee=adaptee;
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			adaptee.mouseclickedinfiletree_actionPerformed(e);
		}
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	class mouseinbar_actionadapter implements MouseListener
	{
		UI adaptee;
		public mouseinbar_actionadapter(UI adaptee) {
			// TODO Auto-generated constructor stub
			this.adaptee=adaptee;
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			adaptee.mouseclickedinbar_actionPerformed(arg0);
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	
	public void initTop()
	{
		
		for(int i=0;i<menuTitle.length;i++)
		{
			menu[i]=new JMenu(menuTitle[i]);
			menuBar.add(menu[i]);
		}
		
		
		for(int i=0;i<FileMenuItemTitle.length;i++)
		{
			FileMenuItem[i]=new JMenuItem(FileMenuItemTitle[i]);
			FileMenuItem[i].addActionListener(this);
			menu[0].add(FileMenuItem[i]);
		}
		for(int i=0;i<DataMenuItemTitle.length;i++)
		{
			DataMenuItem[i]=new JMenuItem(DataMenuItemTitle[i]);
			DataMenuItem[i].addActionListener(this);
			menu[1].add(DataMenuItem[i]);
		}
		for(int i=0;i<ToolsMenuItemTitle.length;i++)
		{
			ToolsMenuItem[i]=new JMenuItem(ToolsMenuItemTitle[i]);
			ToolsMenuItem[i].addActionListener(this);
			menu[2].add(ToolsMenuItem[i]);
		}
		
		for(int i=0;i<AdvancedMenuItemTitle.length;i++)
		{
			AdvancedMenuItem[i]=new JMenuItem(AdvancedMenuItemTitle[i]);
			AdvancedMenuItem[i].addActionListener(this);
			menu[4].add(AdvancedMenuItem[i]);
		}
		for(int i=0;i<HelpMenuItemTitle.length;i++)
		{
			HelpMenuItem[i]=new JMenuItem(HelpMenuItemTitle[i]);
			HelpMenuItem[i].addActionListener(this);
			menu[5].add(HelpMenuItem[i]);
		}
		menu[3].addMouseListener(new mouseinbar_actionadapter(this));
		FileMenuItem[2].setEnabled(false);
		FileMenuItem[3].setEnabled(false);
		DataMenuItem[1].setEnabled(false);
	}
	
	public void itemStateChanged(ItemEvent e) 
	{
	}

	public void menuSelected(MenuEvent e) 
	{
//		if(e.getSource()==menu[3])
//		{
//		    Analysis analysis=new Analysis();
//			this.setVisible(false);
			//this.setExtendedState(JFrame.ICONIFIED);
//		}
	}

	public void menuDeselected(MenuEvent e) {
		
	}

	public void menuCanceled(MenuEvent e) {
		
	}
	
	class NewProject{
		
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
				 GUIMDR.gmdrini=new HashMap<>();
				 GUIMDR.standard=false;
				 GUIMDR.Binary=true;
					
				 GUIMDR.name_bed=new File("NULL");
				 GUIMDR.name_bim=new File("NULL");	
				 GUIMDR.name_fam=new File("NULL");
				 GUIMDR.dataset=null;
				 GUIMDR.name_ped=new File("NULL");
				 GUIMDR.name_map=new File("NULL");
				 GUIMDR.name_phe=new File("NULL");
				 GUIMDR.is_creat_object=false;
				 GUIMDR.name_file=null;
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

}
