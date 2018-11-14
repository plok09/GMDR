package GUI;

import javax.swing.JFrame;
import java.awt.Window.Type;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.DropMode;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;

import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.Box;
import javax.swing.JScrollPane;
import java.awt.GridLayout;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JEditorPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import javax.swing.JLabel;

public class AboutFrame  extends JFrame
{
	
	
	
	public AboutFrame() {
		setResizable(false);
		setAlwaysOnTop(false);
		setTitle("About");
		setType(Type.POPUP);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(465,290);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		JEditorPane jEdit_Contact = new JEditorPane();
		jEdit_Contact.setEditable(false);
		jEdit_Contact.setContentType("text/html");
		jEdit_Contact.setFont(new Font("STLiti", Font.PLAIN, 17));
	//	jEdit_Contact.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		
		jEdit_Contact.setText("<br /><div align=\"center\"><font face=\"STLiti\",size=5>Generalized Multifactor Dimensionality Reduction V1.0</font></div align=\"center\">"
								+ "<p style=\"line-height:50px;\"><div align=\"center\"><font face=\"STLiti\",size=5>Release Time : Nov 12th 2018, 17:35</font></div align=\"center\"><br />"
								+ "&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;<font face=\"STLiti\",size=5>Author: <a href=\"http://ibi.zju.edu.cn/bcl/\">Biocomputing Lab Team</a></font><br />"
								+ "&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;<font face=\"STLiti\",size=5>Contact: <a href=\"mailto:houtt@zju.edu.cn?\">houtt@zju.edu.cn£»</a></font><br />"
								+ "&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&ensp;&ensp;<font face=\"STLiti\",size=5><a href=\"mailto:hmxu@zju.edu.cn?\">hmxu@zju.edu.cn;</a></font><br />"
								+ "&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&ensp;&ensp;<font face=\"STLiti\",size=5><a href=\"mailto:XLou@uams.edu?\">XLou@uams.edu;</a></font><br /></p>");
		jEdit_Contact.addHyperlinkListener(new HyperlinkListener() {
			 public void hyperlinkUpdate(HyperlinkEvent e) {
				 if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				 if (Desktop.isDesktopSupported()) {
				 try {
				 Desktop.getDesktop().browse(e.getURL().toURI());
				 } catch (IOException e1) {
				//TODO Auto-generated catch block
				 e1.printStackTrace();
				 } catch (URISyntaxException e1) {
				//TODO Auto-generated catch block
				 e1.printStackTrace();
				 }
				 }
				 }
				 }
				});
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(jEdit_Contact);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnOk.setSize(60, 30);
		panel.add(btnOk);
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{getContentPane()}));
		setVisible(true);
	}
	
	public static void main(String args[])
	{
		
	         AboutFrame myUI=new AboutFrame();
	       
	}
	
	
}

