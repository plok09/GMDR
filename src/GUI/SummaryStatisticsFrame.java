package GUI;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SummaryStatisticsFrame extends JFrame
{
	private JTextField txtIndi;
	private JTextField txtSNPs;
	private JTextField txtChr;
	public SummaryStatisticsFrame() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Summary");
		setResizable(false);
		getContentPane().setLayout(new BorderLayout(0, 0));
		this.setSize(374,240);
		JPanel panelbtn = new JPanel();
		getContentPane().add(panelbtn, BorderLayout.SOUTH);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		panelbtn.add(btnOk);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		JPanel panelInfo = new JPanel();
		getContentPane().add(panelInfo, BorderLayout.CENTER);
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
		setVisible(true);
	}
}
