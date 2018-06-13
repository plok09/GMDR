package GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class FactorLevel extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtDefault;
	private JButton okButton;
	private JButton cancelButton;
    private  String rank;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			FactorLevel dialog = new FactorLevel();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public FactorLevel() {
		setBounds(100, 100, 421, 138);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblNewLabel = new JLabel("Levels : ");
			lblNewLabel.setToolTipText("The levels of phenotypes.");
			lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
			lblNewLabel.setFont(new Font("Arial Narrow", Font.PLAIN, 13));
			lblNewLabel.setBounds(41, 11, 93, 26);
			contentPanel.add(lblNewLabel);
		}
		{
			txtDefault = new JTextField();
			txtDefault.setToolTipText("In default, the levels are determined by ASCII. And  use \",\" to separate. ");
			txtDefault.setBounds(126, 15, 211, 20);
			contentPanel.add(txtDefault);
			txtDefault.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("  OK  ");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					}
				});
				okButton.setActionCommand("OK");
				getRootPane().setDefaultButton(okButton);
				{
					cancelButton = new JButton("Cancel");
					cancelButton.setActionCommand("Cancel");
				}
			}
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			buttonPane.add(okButton);
			buttonPane.add(cancelButton);
		}

	}
	public void name() {
		
	}

}
