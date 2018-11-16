package GUI;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.table.TableStringConverter;

import org.epistasis.gui.SelectionEvent;
import org.epistasis.gui.SelectionListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MouseEvent;
import org.w3c.dom.views.AbstractView;

import DataManage.Plink;

import java.awt.Font;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JSplitPane;
import javax.swing.JComboBox;
import java.awt.FlowLayout;
import java.awt.Component;

import javax.swing.AbstractButton;
import javax.swing.Box;

public class SummaryStatisticsFrame extends JFrame
{
	private JTextField txtIndi;
	private JTextField txtSNPs;
	private JTextField txtChr;
	private JTextField txamissingrate;
	private JTextField txamaf;
	private JTextField txaposition;
	private Plink Data=null;
	ArrayList<String> SNPnames=new ArrayList<>();
	private JComboBox<String> comboBox = new JComboBox<>();
	private ArrayList<Integer> indexOfAll(String obj, ArrayList<String> list){
	    ArrayList<Integer> indexList = new ArrayList<Integer>();
	    for (int i = 0; i < list.size(); i++)
	        if(list.get(i).contains(obj))
	            indexList.add(i);
	    return indexList;
	}
	public SummaryStatisticsFrame(Plink Dataset) 
	{
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Summary");
		setResizable(false);
		getContentPane().setLayout(new BorderLayout(0, 0));
		this.setSize(560,250);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.9);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		panel.add(button);
		JPanel panelInfo = new JPanel();
		//getContentPane().add(panelInfo, BorderLayout.CENTER);
		splitPane.setLeftComponent(panelInfo);
		panelInfo.setLayout(null);
		
		JLabel lblIndividuals = new JLabel("Individuals:");
		lblIndividuals.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblIndividuals.setBounds(58, 47, 77, 25);
		lblIndividuals.setVerticalAlignment(SwingConstants.TOP);
		panelInfo.add(lblIndividuals);
		
		JLabel lblSnps = new JLabel("SNPs:");
		lblSnps.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblSnps.setBounds(90, 83, 46, 14);
		panelInfo.add(lblSnps);
		
		txtIndi = new JTextField();
		txtIndi.setHorizontalAlignment(SwingConstants.TRAILING);
		txtIndi.setBackground(Color.WHITE);
		txtIndi.setEditable(false);
		txtIndi.setBounds(160, 48, 86, 20);
		panelInfo.add(txtIndi);
		txtIndi.setColumns(10);
		
		txtSNPs = new JTextField();
		txtSNPs.setHorizontalAlignment(SwingConstants.TRAILING);
		txtSNPs.setEditable(false);
		txtSNPs.setColumns(10);
		txtSNPs.setBackground(Color.WHITE);
		txtSNPs.setBounds(160, 82, 86, 20);
		panelInfo.add(txtSNPs);
		txtIndi.setText(""+GUIMDR.dataset.getIndividualnum());
		txtSNPs.setText(""+GUIMDR.dataset.getSNPnum());
		
		JLabel lblChromosomes = new JLabel("Chromosomes:");
		lblChromosomes.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblChromosomes.setBounds(26, 108, 109, 25);
		panelInfo.add(lblChromosomes);
		
		txtChr = new JTextField();
		txtChr.setText("0");
		txtChr.setHorizontalAlignment(SwingConstants.TRAILING);
		txtChr.setEditable(false);
		txtChr.setColumns(10);
		txtChr.setBackground(Color.WHITE);
		txtChr.setBounds(160, 112, 86, 20); 
		panelInfo.add(txtChr);
		txtChr.setText(""+GUIMDR.dataset.getChrNumber());
		
		JPanel panel_1 = new JPanel();
		splitPane.setRightComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.NORTH);
		
		JLabel lblSnp = new JLabel("SNP:");
		panel_2.add(lblSnp);
		
	
		comboBox.setEditable(true);
		panel_2.add(comboBox);
		
		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.Y_AXIS));
		
		JPanel panel_6 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_6.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		panel_3.add(panel_6);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(80);
		panel_6.add(horizontalStrut_1);
		
		JLabel lblLocation = new JLabel("Location:");
		lblLocation.setHorizontalAlignment(SwingConstants.CENTER);
		panel_6.add(lblLocation);
		
		txaposition = new JTextField();
		txaposition.setEditable(false);
		txaposition.setBackground(Color.WHITE);
		txaposition.setHorizontalAlignment(SwingConstants.TRAILING);
		panel_6.add(txaposition);
		txaposition.setColumns(15);
		
		JPanel panel_4 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_4.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_3.add(panel_4);
		
		Component horizontalStrut = Box.createHorizontalStrut(56);
		panel_4.add(horizontalStrut);
		
		JLabel lblNewLabel = new JLabel("Missing Rate:");
		panel_4.add(lblNewLabel);
		
		txamissingrate = new JTextField();
		txamissingrate.setBackground(Color.WHITE);
		txamissingrate.setEditable(false);
		txamissingrate.setHorizontalAlignment(SwingConstants.TRAILING);
		panel_4.add(txamissingrate);
		txamissingrate.setColumns(15);
		
		JPanel panel_5 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_5.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel_3.add(panel_5);
		
		JLabel lblNewLabel_1 = new JLabel("Minor Allele Frequency:");
		panel_5.add(lblNewLabel_1);
		Data=Dataset;
		InitCombobox();
		
		txamaf = new JTextField();
		txamaf.setEditable(false);
		txamaf.setBackground(Color.WHITE);
		txamaf.setHorizontalAlignment(SwingConstants.TRAILING);
		panel_5.add(txamaf);
		txamaf.setColumns(15);
	
	//	comboBox.setSelectedIndex(0);
	}

	private void InitCombobox() 
	{
		int numSNPs=Data.getSNPnum();
		String snpname[]=new String[numSNPs];
		snpname=Data.getSNPnames();
		
		for(int i=0; i<numSNPs; i++)
		{
			comboBox.addItem(snpname[i]);
			SNPnames.add(snpname[i]);
		}
		comboBox.setSelectedIndex(-1);
		comboBox.addItemListener(new JComboBox_ItemAdapter(this));
		comboBox.getEditor().getEditorComponent().addKeyListener(new JComboBoxEditor_actionAdapter(comboBox));
		comboBox.getComponent(0).addMouseListener(new JComboBox_ButtonAdapter(comboBox));
		
	}
	
	class JComboBox_ButtonAdapter implements MouseListener
	{
		JComboBox<String> jComboBox;

		public JComboBox_ButtonAdapter(JComboBox<String> jComboBox) 
		{
			// TODO Auto-generated constructor stub
			this.jComboBox=jComboBox;
		}
		@Override
		public void mouseClicked(java.awt.event.MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(java.awt.event.MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(java.awt.event.MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(java.awt.event.MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(java.awt.event.MouseEvent e)
		{
			// TODO Auto-generated method stub
		
			jComboBox.removeAllItems();
			for(int i=0; i<SNPnames.size(); i++)
			{
				jComboBox.addItem(SNPnames.get(i));
			}
			
			
		}

	
		
	}
	class JComboBox_ItemAdapter implements ItemListener
	{

		SummaryStatisticsFrame adaptee;
		public JComboBox_ItemAdapter(SummaryStatisticsFrame adaptee)
		{
			// TODO Auto-generated constructor stub
			this.adaptee=adaptee;
		}
		@Override
		public void itemStateChanged(ItemEvent e)
		{
			// TODO Auto-generated method stub
			if (e.getStateChange() == e.SELECTED) 
			{
				
				String SNPname=e.getItem().toString();
				int SNPid=Data.GetSNPIDbyName(SNPname);
				if (SNPid!=-1)
				{
					adaptee.txaposition.setText(Data.GetSNPPosition(SNPid));
					adaptee.txamissingrate.setText(String.format("%.4f", Data.GetmissingRate(SNPid)));
					adaptee.txamaf.setText(String.format("%.4f", Data.GetMAF(SNPid)));
				}else 
				{
					adaptee.txaposition.setText("NA");
					adaptee.txamissingrate.setText("NA");
					adaptee.txamaf.setText("NA");
				}
				
			}
			
		}
		
	}
	

	class JComboBoxEditor_actionAdapter implements KeyListener
	{

		JComboBox<String> JcomboBox;
		JTextField editor=null;
		
		public JComboBoxEditor_actionAdapter(JComboBox<String> JcomboBox)
		{
			// TODO Auto-generated constructor stub
			this.JcomboBox=JcomboBox;
			editor=(JTextField) this.JcomboBox.getEditor().getEditorComponent();
		}
		@Override
		
		public void keyPressed(KeyEvent e) 
		{
			// TODO Auto-generated method stub
			char ch = e.getKeyChar();
			if (ch == KeyEvent.VK_ENTER) {
				return;
			}
			JcomboBox.setPopupVisible(true);
		}
	
		@Override
		public void keyReleased(KeyEvent e) 
		{
			// TODO Auto-generated method stub
			char ch = e.getKeyChar();
			if (ch == KeyEvent.CHAR_UNDEFINED || Character.isISOControl(ch)
					|| ch == KeyEvent.VK_DELETE)
				return;
	 
			int caretPosition = editor.getCaretPosition();
			String str = editor.getText();
			str=str.trim();
			ArrayList<Integer> names=new ArrayList<>();
			if (str.equals("")) 
			{
				for(int i=0;i<SNPnames.size();i++)
					names.add(i);
			}else 
			{
				names=indexOfAll(str,SNPnames);
			}
			
			JcomboBox.removeAllItems();
			for(int i=0;i<names.size();i++)
			{
				JcomboBox.addItem(SNPnames.get(names.get(i)));
			}
			if (names.size()>0) 
			{
				editor.setText(SNPnames.get(names.get(0)));
			}
			
			JcomboBox.repaint();
			if (str.length() == 0)
				return;
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
