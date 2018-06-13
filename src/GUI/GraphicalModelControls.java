package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;

import org.epistasis.FileSaver;
import org.epistasis.combinatoric.mdr.Model;

import DataManage.Dataset;
import gmdr.Main;

public class GraphicalModelControls extends JPanel {
	public GraphicalModelControls() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		this.setLayout(bolThis);
		scpGraphicalModel.setBorder(BorderFactory.createLoweredBevelBorder());
		gmpGraphicalModel.setCellFont(new java.awt.Font("Dialog", Font.BOLD, 14));
		gmpGraphicalModel.setValueFont(new java.awt.Font("Dialog", Font.PLAIN, 14));
		gmpGraphicalModel.setAxisFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		pnlControls.setLayout(gblControls);
		lblPage.setText("");
		chkLimitDimension.setSelected(true);
		chkLimitDimension.setText("Limit Dimension");
		chkLimitDimension.addActionListener(new GraphicalModelControls_chkLimitDimension_actionAdapter(this));
		cmdPrevious.setText("< Previous");
		cmdPrevious.addActionListener(new GraphicalModelControls_cmdPrevious_actionAdapter(this));
		cmdNext.setText("Next >");
		cmdNext.addActionListener(new GraphicalModelControls_cmdNext_actionAdapter(this));
		cmdSave.setText("Save");
		cmdSave.addActionListener(new GraphicalModelControls_cmdSave_actionAdapter(this));
		cmdView.setText("View");
		cmdView.addActionListener(new GraphicalModelControls_cmdView_actionAdapter(this));
		spnLimitDimension.setMinimumSize(new Dimension(60, 20));
		spnLimitDimension.setPreferredSize(new Dimension(60, 20));
		spnLimitDimension.addChangeListener(new GraphicalModelControls_spnLimitDimension_changeAdapter(this));
		spnLimitDimension.setModel(new SpinnerNumberModel(new Integer(3), new Integer(1), null, new Integer(1)));
		this.add(scpGraphicalModel, java.awt.BorderLayout.CENTER);
		scpGraphicalModel.setViewportView(gmpGraphicalModel);
		this.add(pnlControls, java.awt.BorderLayout.SOUTH);
		pnlControls
				.add(lblPage, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
		pnlControls.add(chkLimitDimension, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 0, 5), 0, 0));
		pnlControls.add(spnLimitDimension, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
		pnlControls.add(cmdPrevious, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 5),
				0, 0));
		pnlControls
				.add(cmdNext, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
		pnlControls
				.add(cmdSave, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));
		pnlControls
		.add(cmdView, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0));

	}

	public void setFont(Font font) {
		super.setFont(font);

		if (lblPage == null) {
			return;
		}

		lblPage.setFont(font);
		chkLimitDimension.setFont(font);
		cmdPrevious.setFont(font);
		cmdNext.setFont(font);
		cmdSave.setFont(font);
		cmdView.setFont(font);
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		if (lblPage != null) {
			checkEnabled();
		}
	}

	private void checkEnabled() {
		lblPage.setEnabled(isEnabled());
		chkLimitDimension.setEnabled(isEnabled());
		gmpGraphicalModel.setEnabled(isEnabled());

		if (isEnabled()) {
			spnLimitDimension.setEnabled(chkLimitDimension.isSelected());
			cmdPrevious.setEnabled(gmpGraphicalModel.getPage() > 0);
			cmdNext.setEnabled((gmpGraphicalModel.getPage() + 1) < gmpGraphicalModel.getNumPages());
			cmdSave.setEnabled(gmpGraphicalModel.getNumPages() > 0);
			cmdView.setEnabled(gmpGraphicalModel.getNumPages() > 0);
			if (vfFrame!=null) 
			{
				GraphicalModelPanel gModelPanel=new GraphicalModelPanel();
				gModelPanel.setLabels(gmpGraphicalModel.getLabels());
				gModelPanel.setLevels(gmpGraphicalModel.getLevels());
				gModelPanel.setModel(gmpGraphicalModel.getModel());
				vfFrame.setGraph(gModelPanel);
			}
		} else {
			spnLimitDimension.setEnabled(false);
			cmdPrevious.setEnabled(false);
			cmdNext.setEnabled(false);
			cmdSave.setEnabled(false);
			cmdView.setEnabled(false);
		}
	}

	public void setData(Dataset data) {
		gmpGraphicalModel.setLabels(data.getLabels());
		gmpGraphicalModel.setLevels(data.getLevels());
	}

	public void setModel(Model model) {
		gmpGraphicalModel.setModel(model);
		updateLabel();
		checkEnabled();
	}

	private void updateLabel() {
		if (gmpGraphicalModel.getModel() == null) {
			lblPage.setText("");
		} else {
			lblPage.setText("Page " + (gmpGraphicalModel.getPage() + 1) + " of " + gmpGraphicalModel.getNumPages());
		}
	}

	private BorderLayout bolThis = new BorderLayout();
	private JScrollPane scpGraphicalModel = new JScrollPane();
	private JPanel pnlControls = new JPanel();
	private GridBagLayout gblControls = new GridBagLayout();
	private JLabel lblPage = new JLabel();
	private JCheckBox chkLimitDimension = new JCheckBox();
	private JSpinner spnLimitDimension = new JSpinner();
	private JButton cmdPrevious = new JButton();
	private JButton cmdNext = new JButton();
	private JButton cmdSave = new JButton();
	private GraphicalModelPanel gmpGraphicalModel = new GraphicalModelPanel();
	private JButton cmdView = new JButton();
	private ViewFrame vfFrame=null;
	public void cmdPrevious_actionPerformed(ActionEvent e) {
		gmpGraphicalModel.setPage(gmpGraphicalModel.getPage() - 1);
		updateLabel();
		checkEnabled();
	}

	public void cmdNext_actionPerformed(ActionEvent e) {
		gmpGraphicalModel.setPage(gmpGraphicalModel.getPage() + 1);
		updateLabel();
		checkEnabled();
	}

	public void chkLimitDimension_actionPerformed(ActionEvent e) {
		if (chkLimitDimension.isSelected()) {
			gmpGraphicalModel.setMaxDim(((Number) spnLimitDimension.getValue()).intValue());
		} else {
			gmpGraphicalModel.setMaxDim(0);
		}

		updateLabel();
		checkEnabled();
	}

	public void spnLimitDimension_stateChanged(ChangeEvent e) {
		gmpGraphicalModel.setMaxDim(((Number) spnLimitDimension.getValue()).intValue());
		updateLabel();
		checkEnabled();
	}

	public void cmdSave_actionPerformed_old(JFileChooser fc) {
		NumberFormat nf = new DecimalFormat("0000");
		int pages = gmpGraphicalModel.getNumPages();
		try {
			File f = fc.getSelectedFile();
			String name = f.getName();
			int pos = name.lastIndexOf('.');
			if (pos < 0) {
				name += ".eps";
				pos = name.lastIndexOf('.');
			}
			for (int i = 0; i < pages; ++i) {
				String filename;
				if (pages > 1) {
					filename = f.getParent() + File.separator + name.substring(0, pos) + nf.format(i + 1) + name.substring(pos);
				} else {
					filename = f.getParent() + File.separator + name;
				}
				BufferedWriter w = new BufferedWriter(new FileWriter(filename));
				w.write(gmpGraphicalModel.getPageEPS(i));
				w.flush();
				w.close();
			}
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "I/O Error", JOptionPane.ERROR_MESSAGE);
		}
	}
 
	// add by leiyan 20070114 begin
	public void cmdSave_actionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser(GUIMDR.gmdrini_path);
		fc.setDialogTitle("Save");
		fc.setMultiSelectionEnabled(false);
		fc.setCurrentDirectory(FileSaver.getSaveFolder());
		fc.setAcceptAllFileFilterUsed(false);
		String[] fts = getFTs();
		for (int i = 0; i < fts.length; i++) {
			fc.addChoosableFileFilter(new MyFileFilter(fts[i]));
		}
		fc.addChoosableFileFilter(new MyFileFilter("eps"));
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			String filetype = fc.getFileFilter().getDescription();
			if (filetype.equalsIgnoreCase("all type") || filetype.equalsIgnoreCase("eps")) {
				cmdSave_actionPerformed_old(fc);
			} else {
				saveFile(filetype, fc.getSelectedFile());
			}
			FileSaver.setSaveFolder(fc.getSelectedFile().getParentFile());
		}
		 try {
				GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tSave GMDR figure to  "+fc.getSelectedFile().getAbsolutePath()+"  successed.\n",GUIMDR.myUI.keyWordsuccessed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	   
	}

	public void cmdView_actionPerformed(ActionEvent e) {
		GraphicalModelPanel gModelPanel=new GraphicalModelPanel();
		gModelPanel.setLabels(gmpGraphicalModel.getLabels());
		gModelPanel.setLevels(gmpGraphicalModel.getLevels());
		gModelPanel.setModel(gmpGraphicalModel.getModel());
		JScrollPane jPane=new JScrollPane();
		jPane.setViewportView(gModelPanel);
		if(vfFrame==null)
			vfFrame=new ViewFrame();
		vfFrame.setGraph(gModelPanel);
		vfFrame.setVisible(true);
	}
	


	private void saveFile(final String filetype, File file) {
		boolean tosave = false;
		while (!tosave) {
			file = new File(file.getParent(), file.getName() + "." + filetype);
			if (file.exists()) {
				switch (yesNoCancelOptionDialog(this, "File [" + file.getAbsolutePath() + "] exists, overwrite it?")) {
				case JOptionPane.YES_OPTION:
					tosave = true;
					break;
				case JOptionPane.NO_OPTION:
					tosave = false;
					break;
				default:
					return;
				}
			} else {
				tosave = true;
			}
		}
		// save
		try {
			file.delete();
			BufferedImage bufImage = new BufferedImage(gmpGraphicalModel.getWidth(), gmpGraphicalModel.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = bufImage.createGraphics();
			g2.setBackground(Color.white);
			gmpGraphicalModel.paint(g2);
			ImageIO.write(bufImage, filetype, file);
		} catch (IOException ex) {
			errorDialog(this, "Save file [" + file.getAbsolutePath() + "] failure!\n" + ex.toString());
		}
	}

	private static String[] getFTs() {
		ImageIO.scanForPlugins();
		String[] fts = ImageIO.getWriterFormatNames();
		HashSet set = new HashSet();
		for (int i = 0; i < fts.length; i++) {
			set.add(fts[i].toLowerCase());
		}
		set.remove("wbmp");
		return (String[]) set.toArray(new String[set.size()]);
	}

	public static int yesNoCancelOptionDialog(Component parentComponent, String message) {
		return JOptionPane.showConfirmDialog(parentComponent, message, "Please Select an Option", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);
	}

	public static void errorDialog(Component parentComponent, String message) {
		JOptionPane.showMessageDialog(parentComponent, message, "Error!", JOptionPane.ERROR_MESSAGE);
	}

	// add by leiyan 20070114 end

}

class GraphicalModelControls_spnLimitDimension_changeAdapter implements ChangeListener {
	private GraphicalModelControls adaptee;

	GraphicalModelControls_spnLimitDimension_changeAdapter(GraphicalModelControls adaptee) {
		this.adaptee = adaptee;
	}

	public void stateChanged(ChangeEvent e) {
		adaptee.spnLimitDimension_stateChanged(e);
	}
}
class GraphicalModelControls_cmdView_actionAdapter implements ActionListener {
	private GraphicalModelControls adaptee;

	GraphicalModelControls_cmdView_actionAdapter(GraphicalModelControls adaptee) {
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
			adaptee.cmdView_actionPerformed(e);
	}

	
}
class GraphicalModelControls_cmdSave_actionAdapter implements ActionListener {
	private GraphicalModelControls adaptee;

	GraphicalModelControls_cmdSave_actionAdapter(GraphicalModelControls adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.cmdSave_actionPerformed(e);
	}
}

class GraphicalModelControls_chkLimitDimension_actionAdapter implements ActionListener {
	private GraphicalModelControls adaptee;

	GraphicalModelControls_chkLimitDimension_actionAdapter(GraphicalModelControls adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.chkLimitDimension_actionPerformed(e);
	}
}

class GraphicalModelControls_cmdNext_actionAdapter implements ActionListener {
	private GraphicalModelControls adaptee;

	GraphicalModelControls_cmdNext_actionAdapter(GraphicalModelControls adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.cmdNext_actionPerformed(e);
	}
}

class GraphicalModelControls_cmdPrevious_actionAdapter implements ActionListener {
	private GraphicalModelControls adaptee;

	GraphicalModelControls_cmdPrevious_actionAdapter(GraphicalModelControls adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.cmdPrevious_actionPerformed(e);
	}
}

class MyFileFilter extends FileFilter {

	private String filetype;

	MyFileFilter(String filetype) {
		this.filetype = filetype;
	}

	public boolean accept(File f) {
		return true;
	}

	public String getDescription() {
		return filetype;
	}
}
