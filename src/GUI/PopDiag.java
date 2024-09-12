package GUI;

import javax.swing.JPanel;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.SystemColor;
import java.awt.BorderLayout;
import java.awt.Color;

public class PopDiag extends JPanel {

	/**
	 * Create the panel.
	 */
	public PopDiag(String message,int x, int y) {
		setLayout(new BorderLayout(0, 0));
		
		JTextPane PopInfo = new JTextPane();
		PopInfo.setBackground(SystemColor.info);
		PopInfo.setForeground(Color.red);
		PopInfo.setText(message);
		add(PopInfo);
		setLocation(x, y);
		this.setVisible(true);
	}
	public void close()
	{
		this.close();
	}

}
