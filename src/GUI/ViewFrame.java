package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;

import org.epistasis.FileSaver;

import gmdr.Main;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.awt.GridBagConstraints;

public class ViewFrame extends JFrame {

	private JPanel contentPane;
	JScrollPane gnPanel=new JScrollPane();
	GraphicalModelPanel gModelPanel=null;
	JTextArea ViewText=new JTextArea();
	String btnresutl=null;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewFrame frame = new ViewFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/** 
	 * Create the frame.
	 */
	public ViewFrame() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	
		setBounds(100, 100, 753, 688);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		JButton btnsave = new JButton("Save");
		btnsave.addActionListener(new ViewFrame_cmdSave_actionAdapter(this));
		panel.add(btnsave, BorderLayout.EAST);
		contentPane.add(gnPanel, BorderLayout.CENTER);
	}
	public void cmdSave_actionPerformed(ActionEvent e)
	{
		if (gModelPanel!=null) 	{
			saveimage();
		}else {
			saveText();
		}
	
	}
	private void saveText()
	{
		 JFileChooser choose=new JFileChooser(GUIMDR.project_path);
		 choose.setCurrentDirectory(new File(GUIMDR.project_path));
		 FileNameExtensionFilter filteresp=new FileNameExtensionFilter("Rsult Save", "Result");
		 choose.addChoosableFileFilter(filteresp);
		 choose.setAcceptAllFileFilterUsed(false);
		 int resultval =choose.showSaveDialog(new JPanel());
		 if (resultval!=JFileChooser.APPROVE_OPTION)
		 {
			return;
		 }

		 File resultfile=new File(choose.getSelectedFile().getAbsolutePath()+".Result");
		 PrintWriter pWriter;
		try 
		{
			pWriter = new PrintWriter(resultfile);
			pWriter.println(ViewText.getText());
			pWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 try {
				GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tSave result to  "+resultfile.getAbsolutePath()+"  successed.\n",GUIMDR.myUI.keyWordsuccessed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
	
	
	private void saveimage()
	{
		 NumberFormat nf = new DecimalFormat("0000");
		 JFileChooser choose=new JFileChooser();
		 choose.setCurrentDirectory(new File(GUIMDR.project_path));
		 FileNameExtensionFilter filteresp=new FileNameExtensionFilter("esp", "eps");
		 FileNameExtensionFilter filterjpg=new FileNameExtensionFilter("jpg", "jpg");
		 FileNameExtensionFilter filtergif=new FileNameExtensionFilter("gif", "gif");
		 FileNameExtensionFilter filterpng=new FileNameExtensionFilter("png", "png");
		 FileNameExtensionFilter filterjpeg=new FileNameExtensionFilter("jpeg","jpeg");
		 choose.addChoosableFileFilter(filteresp);
		 choose.addChoosableFileFilter(filterjpg);
		 choose.addChoosableFileFilter(filtergif);
		 choose.addChoosableFileFilter(filterpng);
		 choose.addChoosableFileFilter(filterjpeg);
		 choose.setAcceptAllFileFilterUsed(false);
		 int resultval =choose.showSaveDialog(new JPanel());
		 if (resultval!=JFileChooser.APPROVE_OPTION)
		 {
			return;
		 }
		 String filetype=choose.getFileFilter().getDescription();
		 if (filetype.equals("esp")) 
		 {
		
			 int pages = gModelPanel.getNumPages();
			 for (int i = 0; i < pages; ++i) 
			 {
				 	String image=choose.getSelectedFile().getAbsolutePath();
					if (pages > 1) {
						image += nf.format(i + 1) +"."+ filetype;
					} else {
						image += "." + filetype;
					}
					BufferedWriter w;
					try {
						w = new BufferedWriter(new FileWriter(image));
						w.write(gModelPanel.getPageEPS(i));
						w.flush();
						w.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				
		 }else
		 {
			 	String image=choose.getSelectedFile().getAbsolutePath()+"."+filetype;
			 	File file=new File(image);
				BufferedImage bufImage = new BufferedImage(gModelPanel.getWidth(), gModelPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D g2 = bufImage.createGraphics();
				g2.setBackground(Color.white);
				gModelPanel.paint(g2);
				try {
					ImageIO.write(bufImage, filetype, file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 }
		 try {
				GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tSave figure to  "+choose.getSelectedFile().getAbsolutePath()+"."+filetype+"  successed.\n",GUIMDR.myUI.keyWordsuccessed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}

	public void setGraph(GraphicalModelPanel gModelPanel) 
	{
		this.gModelPanel=gModelPanel;
		gnPanel.setViewportView(this.gModelPanel);
		contentPane.add(gnPanel, BorderLayout.CENTER);
		contentPane.updateUI();
	}
	public void setText(String text,String cmdbtn) 
	{
		btnresutl=cmdbtn;
		ViewText.setText(text);
		gnPanel.setViewportView(ViewText);
		contentPane.add(gnPanel, BorderLayout.CENTER);
		contentPane.updateUI();
	}
	public void reset()
	{
		remove(gnPanel);
		contentPane.updateUI();
	}
	
	class ViewFrame_cmdSave_actionAdapter implements ActionListener
	{
		private ViewFrame adaptee;
		public ViewFrame_cmdSave_actionAdapter(ViewFrame adaptee) 
		{
			this.adaptee=adaptee;
			// TODO Auto-generated constructor stub
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			adaptee.cmdSave_actionPerformed(e);
		}
		
	}
}
