package GUI;

import java.io.*;
import java.io.*;
import java.lang.*;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import javax.imageio.IIOException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;

import org.apache.commons.math3.geometry.spherical.oned.ArcsSet.Split;
import org.epistasis.AbstractDataset;
import org.epistasis.FileSaver;
import org.epistasis.ReverseComparator;
import org.epistasis.StringDoublePair;
import org.epistasis.StringIntPair;
import org.epistasis.TimerRunnableTask;
import org.epistasis.Utility;
import org.epistasis.combinatoric.AbstractAnalysisThread;
import org.epistasis.combinatoric.AttributeCombination;
import org.epistasis.combinatoric.AttributeCombinationGenerator;
import org.epistasis.combinatoric.AttributeRankerThread;
import org.epistasis.combinatoric.FixedRandomACG;
import org.epistasis.combinatoric.TimedRandomACG;
import org.epistasis.combinatoric.gui.LandscapePanel;
import org.epistasis.combinatoric.mdr.AnalysisFileManager;
import org.epistasis.combinatoric.mdr.BestModelTextGenerator;
import org.epistasis.combinatoric.mdr.CVResultsTextGenerator;
import org.epistasis.combinatoric.mdr.CVSummaryModel;
import org.epistasis.combinatoric.mdr.IfThenRulesTextGenerator;
import org.epistasis.combinatoric.mdr.Model;
import org.epistasis.combinatoric.mdr.ModelFilter;
import org.epistasis.gui.ProgressPanel;
import org.epistasis.gui.ProgressPanelUpdater;
import org.epistasis.gui.ReadOnlyTableModel;
import org.epistasis.gui.SwingInvoker;
import org.epistasis.gui.TextComponentUpdaterThread;

import DataManage.Dataset;
import DataManage.Plink;
import gmdr.AnalysisThread;
import gmdr.Main;
import GUI.ColumnSelectableJTable;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.BevelBorder;
import javax.swing.GroupLayout.Alignment;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class Analysis extends JFrame implements ActionListener, ItemListener,ChangeListener
{
	public static File phenofile=null;
	public static int num_of_data;
	Object score_data[][];
	Object scoredata_head[];
	String[] datafiles;
	private Timer tmrProgress;
	private final Runnable onEnd = new SwingInvoker(new OnEnd(), false);
	private final Runnable onEndAttribute = new SwingInvoker(new OnEndAttribute(), false);
	private ViewFrame viewBestModelResultwindows=null;
	private ViewFrame viewCVResultsResultwindows=null;
	private ViewFrame viewIfThenRuleswindows=null;
	private Dataset unfiltered;
	public Dataset data;
	public static Object[][] data2;
	private AttributeRankerThread rankerThread;
	private AttributeCombination forced;
	private AnalysisThread analysis;
	private ModelFilter loadedModelFilter;
	
	private JTabbedPane tabbed=new JTabbedPane(JTabbedPane.NORTH);
	
	private JPanel jpanalysis=new JPanel();
	
	private JPanel pnlanalysisconfig=new JPanel();
	private JPanel pnlline1=new JPanel();
	private JLabel label1111=new JLabel("Random Seed:                ");
	private JTextArea seed=new JTextArea(1,5);
	private JLabel label1112=new JLabel("                     Paired Analysis:        ");
	private JCheckBox ispaired=new JCheckBox();
	private JLabel label1113=new JLabel("                     permutation:       ");
	private JTextArea permutation=new JTextArea(1,5);
	private JPanel pnlline2=new JPanel();
	private JLabel label1121=new JLabel("Marker Count Range:     ");
	private JTextArea mincount=new JTextArea(1,5);
	private JTextArea maxcount=new JTextArea(1,5);
	private JLabel label1122=new JLabel("     Tie Cells:                               ");
	private JComboBox<String> tie=new JComboBox();
	private JPanel pnlline3=new JPanel();
	private JLabel label1131=new JLabel("Cross_Validation Count:");
	private JTextArea cv=new JTextArea(1,5);
	private JLabel label1132=new JLabel("                    Compute Fitness Landscape:");
	private JCheckBox landscape=new JCheckBox();
	private JPanel pnlsearchconfig=new JPanel();
	private JPanel pnlsearchtype=new JPanel();
	private JLabel label1211=new JLabel("Search Type");
	private JComboBox<String> searchtype=new JComboBox<String>();
	private JPanel pnlconfig1=new JPanel();
	private JLabel markercombination=new JLabel("Forced Marker Combination:");
	private JTextArea searchtypetext=new JTextArea(1,30);
	private ButtonGroup btg=new ButtonGroup();
	private JRadioButton jrbevaluations=new JRadioButton("Evaluations:");
	private JTextArea textarea1222=new JTextArea(1,5);
	private JPanel pnlconfig2=new JPanel();
	private JRadioButton jrbruntime=new JRadioButton("Runtime:      ");
	private JTextArea runtimetext=new JTextArea(1,5);
	private JLabel label1231=new JLabel("Units:");
	private JComboBox<String> unit=new JComboBox();
	private JPanel set124=new JPanel();
	private JButton defaultsearchtype=new JButton("Defaults");
	
	
	private JPanel pnlanalysiscontrol=new JPanel();
	private JButton runanalysis=new JButton("Run Analysis");
	private JButton loadanalysis=new JButton("Load Analysis");
	private JButton saveanalysis=new JButton("Save Analysis");
	private JButton button134=new JButton("Revert Filter");
//	private JPanel pnlprogress=new JPanel();
	ProgressPanel prgProgress = new ProgressPanel();

	private JPanel jpsummary=new JPanel();
	private JTable jtsummary=new JTable();
	DefaultTableModel dtmSummaryTable = new ReadOnlyTableModel();
	
	private String jtsummaryname[]={"Model","Training Bal.Acc.","Testing Bal.Acc","Sign Test(p)","CV Consistency"};
	private Object[][] data_table151=new Object[10][5];
	private JScrollPane scrollpane_table151;
	private GraphicalModelControls pnlGraphicalModel = new GraphicalModelControls();
	private JPanel pnlbestmodel=new JPanel();
	private JPanel pnlBestModelButtons=new JPanel();
	private JButton cmdBestModelSave=new JButton("Save");
	LandscapePanel pnlLandscape = new LandscapePanel();
	
	
	private JPanel score_calculation=new JPanel();
	
	private JPanel pnlScorePhenopath=new JPanel();
	private JLabel labelphenofile=new JLabel("Phenotype File");
	public static JTextArea txtphenopath=new JTextArea(1,50);
	private JButton loadphenotype=new JButton("Load Phenotype");
	private static JPanel pnlpheofile=new JPanel();
	private static ColumnSelectableJTable table221;
	private static JScrollPane scrollpane_table221;
	private static Object name221[];
	private static Object data221[][];
	private JPanel pnlloadvariable=new JPanel();
	private JPanel pnlloadvar=new JPanel();
	private static JButton removeresponse=new JButton("\u25B2");
	private static JButton addresponse=new JButton("\u25BC");
	private JPanel pnlblank=new JPanel();
	private static JButton removepredictor=new JButton("\u25B2");
	private static JButton addpredictor=new JButton("\u25BC");
	private JPanel pnlselectedvar=new JPanel();
	private JPanel pnlresponse=new JPanel();
	private ColumnSelectableJTable table241;
	private JScrollPane scrollpane_table241;
	private Object name241[];
	private Vector index_table241=new Vector(10,5);
	private Object data241[][];
	private JPanel pnlpredictor=new JPanel();
	private ColumnSelectableJTable table242;
	private JScrollPane scrollpane_table242;
	private Object name242[];
	private Vector index_table242=new Vector(10,5);
	private Object data242[][];
	private JPanel pnlestimationfunc=new JPanel();
	private JPanel panel_select=new JPanel();
	private ButtonGroup btg241=new ButtonGroup();
	private JLabel label241=new JLabel("Link Function");
	private JRadioButton radiobutton241=new JRadioButton("Linear Regression");
	private JLabel label242=new JLabel("Estimating Method");
	private JRadioButton radiobutton242=new JRadioButton("Logistic Regression");
	private JComboBox<String> Comblinkfunc=new JComboBox();
	private JComboBox<String> combestimatemethod=new JComboBox();
	private JButton bntrunscorecalc=new JButton("Run");
	private JPanel pnlselectedresidual=new JPanel();
	private JPanel pnldealresidual=new JPanel();
	private static JCheckBox Isusescore=new JCheckBox("Use Residual");
	private JPanel pnlresidual_path=new JPanel();
	private JButton savescore=new JButton("Save");
	
	
	
	private JPanel pnlconfiguration=new JPanel();
	
	private JCheckBox checkbox301=new JCheckBox("Population Stratification");
	private JPanel set3010=new JPanel();
	private JLabel label3010=new JLabel("Principal Components File");
	private JTextArea textarea3010=new JTextArea(1,30);
	private JButton button3010=new JButton("Load");
	private JPanel set31=new JPanel();
	private ButtonGroup btg2=new ButtonGroup();
	private JPanel set301=new JPanel();
	private JPanel set311=new JPanel();
	private String returnString=new String("Unrelated subjects");
	private JRadioButton radiobutton311=new JRadioButton("Unrelated subjects");
	private JPanel set312=new JPanel();
	private JRadioButton radiobutton312=new JRadioButton("Non-founders");
	private JPanel set313=new JPanel();
	private JRadioButton radiobutton313=new JRadioButton("Pooled samples of unrelated subjects and non-founders");
	
	private String residual_path="";
	private  JScrollPane scpBestModel = new JScrollPane();
	private  JTextArea txaBestModel = new JTextArea();
	private  JPanel pnlIfThenRules = new JPanel();
	private  JPanel pnlIfThenRulesButtons = new JPanel();
	private  JButton cmdIfThenRulesSave = new JButton("Save");
	private  JScrollPane scpIfThenRules = new JScrollPane();
	private  JTextArea txaIfThenRules = new JTextArea();
	private  JTabbedPane tpnResults = new JTabbedPane(JTabbedPane.TOP);
	private  JPanel pnlCVResults = new JPanel();
	private  JPanel pnlCVResultsButtons = new JPanel();
	private  JButton cmdCVResultsSave = new JButton("Save");
	private  JScrollPane scpCVResults = new JScrollPane();
	private  JTextArea txaCVResults = new JTextArea();
	private  JPanel panel = new JPanel();
	private  JButton btnNewButton = new JButton("save");
	private  JPanel panel_1 = new JPanel();
	private JButton cmdBestModelView = new JButton("View");
	private JButton cmdCVResultsView = new JButton("View");
	private JButton cmdIfThenRulesView = new JButton("View");
	private JButton cmdloadscore = new JButton("Load");
	private final JTextArea txascorefile = new JTextArea();
	private class OnEndAttribute implements Runnable {
		public void run() {
			int attrCount = jtsummary.getRowCount();
			if (forced == null) {
				attrCount += Integer.valueOf(mincount.getText());
			} else {
				attrCount += forced.size();
			}
			addTableRow((CVSummaryModel) analysis.getModelFilter().getCVSummaryModel(attrCount), Integer.valueOf(cv.getText()));
		}
	}

	private class OnEnd implements Runnable {
		public void run() {
			if (tmrProgress != null) {
				tmrProgress.cancel();
				tmrProgress = null;
			}

			if (analysis != null && analysis.isComplete()) {
				prgProgress.setValue(1);
			}

			if (landscape.isSelected()) {
				tpnResults.add(pnlLandscape, "Landscape");
				pnlLandscape.setLandscape(analysis.getModelFilter().getLandscape(), analysis.getModelFilter().getLabelMap(), false);
				pnlLandscape.setEnabled(true);
			}

			//lockdown(false);
	//		cmdApply.setEnabled(true);
			runanalysis.setText("Run Analysis");
//			runanalysis.setForeground(cmdLoadDatafile.getForeground());
			setCursor(Cursor.getDefaultCursor());
		}
	}
	




	public Analysis()
	{
		tabbed.add("MDR Analysis",jpanalysis);
		tabbed.add("Residual Calculation",score_calculation);
		tabbed.add("Study Design",pnlconfiguration);
		
		initAnalysis();
		initScoreCalculation();
		initConfiguration();
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(tabbed);
		this.setVisible(true);
		this.setSize(838,772);
		this.setLocation(316,9);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent w) {
			
			//	if (GMDR.myUI.getExtendedState()==JFrame.ICONIFIED) {
				//	GMDR.myUI.setExtendedState(JFrame.NORMAL);
			//	}
				GUIMDR.myUI.setVisible(true);
				
			}
		});
	
	}
	
	public void initAnalysis()
	{
		
		btg.add(jrbevaluations);
		btg.add(jrbruntime);
		jrbevaluations.addItemListener(this);
		jrbevaluations.setSelected(true);
		jrbruntime.addItemListener(this);
		unit.addItem("Seconds");
		unit.addItem("Minutes");
		unit.addItem("Hours");
		unit.addItem("Days");
	
		dtmSummaryTable.addColumn("Model");
		dtmSummaryTable.addColumn("Training Bal. Acc.");
		dtmSummaryTable.addColumn("Testing Bal. Acc.");
		dtmSummaryTable.addColumn("Sign Test (p)");
		dtmSummaryTable.addColumn("CV Consistency");
		
		pnlanalysisconfig.add(pnlline1);
		pnlline1.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlline1.add(label1111);
		pnlline1.add(seed);
		seed.setText("0");
		pnlline1.add(label1112);
		pnlline1.add(ispaired);
		pnlline1.add(label1113);
		pnlline1.add(permutation);
		permutation.setText("100");
		pnlanalysisconfig.add(pnlline2);
		pnlline2.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlline2.add(label1121);
		pnlline2.add(mincount);
		mincount.setText("1");
		pnlline2.add(maxcount);
		maxcount.setText("3");
		pnlline2.add(label1122);
		pnlline2.add(tie);
		tie.addItem("Affected");
		tie.addItem("Unaffected");
		tie.addItem("Unknown");
		jpanalysis.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		pnlanalysisconfig.add(pnlline3);
		pnlline3.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlline3.add(label1131);
		pnlline3.add(cv);
		cv.setText("10");
		pnlline3.add(label1132);
		pnlline3.add(landscape);
		pnlanalysisconfig.setBorder(BorderFactory.createTitledBorder("Analysis Configuration"));
		pnlanalysisconfig.setPreferredSize(new Dimension(785,130));
		pnlanalysisconfig.setLayout(new BoxLayout(pnlanalysisconfig,BoxLayout.Y_AXIS));
		jpanalysis.add(pnlanalysisconfig);
		pnlsearchconfig.add(pnlsearchtype);
		pnlsearchtype.setPreferredSize(new Dimension(785,35));
		pnlsearchtype.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlsearchtype.add(label1211);
		pnlsearchtype.add(searchtype);
		searchtype.addItem("Exhaustive");
		searchtype.addItem("Forced");
		searchtype.addItem("Random");
		searchtype.addItemListener(this);
		pnlsearchconfig.add(pnlconfig1);
		pnlconfig1.setPreferredSize(new Dimension(785,35));
		pnlconfig1.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlsearchconfig.add(pnlconfig2);
		pnlconfig2.setPreferredSize(new Dimension(785,35));
		pnlconfig2.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlsearchtype.add(defaultsearchtype);
		defaultsearchtype.addActionListener(this);
		pnlsearchconfig.setLayout(new BoxLayout(pnlsearchconfig,BoxLayout.Y_AXIS));
		pnlsearchconfig.setPreferredSize(new Dimension(785,130));
		pnlsearchconfig.setBorder(BorderFactory.createTitledBorder("Search Method Configuration"));
		jpanalysis.add(pnlsearchconfig);
		pnlanalysiscontrol.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		pnlanalysiscontrol.add(runanalysis);
		runanalysis.addActionListener(new Frame_cmdRunAnalysis_actionAdapter(this));
		pnlanalysiscontrol.add(loadanalysis);
		loadanalysis.addActionListener(new Frame_cmdLoadAnalysis_actionAdapter(this));
		pnlanalysiscontrol.add(saveanalysis);
		saveanalysis.addActionListener(new Frame_cmdSaveAnalysis_actionAdapter(this));
		saveanalysis.setEnabled(false);
		pnlanalysiscontrol.add(button134);
		button134.addActionListener(this);
		button134.setEnabled(false);
		pnlanalysiscontrol.setPreferredSize(new Dimension(785,60));
		pnlanalysiscontrol.setBorder(BorderFactory.createTitledBorder("Analysis Controls"));
		jpanalysis.add(pnlanalysiscontrol);
		prgProgress.setPreferredSize(new Dimension(785,60));
		jpanalysis.add(prgProgress);
		jtsummary.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jtsummary.setModel(dtmSummaryTable);
		//	jtsummary=new ColumnSelectableJTable(data_table151,jtsummaryname);
		jtsummary.setPreferredScrollableViewportSize(new Dimension(750,60));
		scrollpane_table151=new JScrollPane(jtsummary);
		/////////////////////////
		jtsummary.getTableHeader().setReorderingAllowed(false);
		jtsummary.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jtsummary.getSelectionModel().addListSelectionListener(new Frame_tblSummaryTable_selectionAdapter(this));
			//	tblSummaryTable.getSelectionModel().addListSelectionListener(new Frame_tblSummaryTable_selectionAdapter(this));
				///////////////////
		jpsummary.add(scrollpane_table151);
		jpsummary.setLayout(new FlowLayout(FlowLayout.LEFT));
		jpsummary.setBorder(BorderFactory.createTitledBorder("Summary Completed"));
		jpsummary.setPreferredSize(new Dimension(785,120));
		jpanalysis.add(jpsummary);
		jpanalysis.add(panel_1);
		pnlGraphicalModel.setEnabled(false);
		tpnResults.addTab("Graphical Model", null, pnlGraphicalModel, null);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWeights = new double[]{0.0};
		gbl_panel.rowWeights = new double[]{0.0};
		panel.setLayout(gbl_panel);
		pnlLandscape.setXAxisLabel("Models");
		pnlLandscape.setYAxisLabel("Accuracy");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.weightx = 1.0;
		gbc_btnNewButton.insets = new Insets(5, 5, 5, 5);
		gbc_btnNewButton.anchor = GridBagConstraints.EAST;
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 0;
		panel.add(btnNewButton, gbc_btnNewButton);
		tpnResults.addTab("Best Model", null, pnlbestmodel, null);
		pnlbestmodel.setLayout(new BorderLayout(0, 0));
		pnlbestmodel.add(pnlBestModelButtons, BorderLayout.SOUTH);
		GridBagLayout gbl_pnlBestModelButtons = new GridBagLayout();
		gbl_pnlBestModelButtons.columnWeights = new double[]{0.0, 0.0};
		gbl_pnlBestModelButtons.rowWeights = new double[]{0.0};
		pnlBestModelButtons.setLayout(gbl_pnlBestModelButtons);
		GridBagConstraints gbc_cmdBestModelSave = new GridBagConstraints();
		gbc_cmdBestModelSave.weightx = 1.0;
		gbc_cmdBestModelSave.insets = new Insets(5, 5, 0, 5);
		gbc_cmdBestModelSave.gridy = 0;
		gbc_cmdBestModelSave.gridx = 0;
		gbc_cmdBestModelSave.anchor = GridBagConstraints.EAST;
		cmdBestModelSave.addActionListener(new Frame_cmdBestmodelSave_actionAdapter(this));
		cmdCVResultsSave.addActionListener(new Frame_cmdCVResults_actionAdapter(this));
		cmdIfThenRulesSave.addActionListener(new Frame_cmdIfThenRules_actionAdapter(this));
		pnlBestModelButtons.add(cmdBestModelSave, gbc_cmdBestModelSave);
		
		
		GridBagConstraints gbc_cmdBestModelView = new GridBagConstraints();
		gbc_cmdBestModelView.insets = new Insets(5, 5, 0, 5);
		gbc_cmdBestModelView.gridx = 1;
		gbc_cmdBestModelView.gridy = 0;
		pnlBestModelButtons.add(cmdBestModelView, gbc_cmdBestModelView);
		scpBestModel.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlbestmodel.add(scpBestModel, BorderLayout.CENTER);
		scpBestModel.setToolTipText("");
		txaBestModel.setWrapStyleWord(true);
		txaBestModel.setEditable(false);
		cmdBestModelView.addActionListener(new Frame_cmdBestModelView_actionAdapter(this));
		scpBestModel.setViewportView(txaBestModel);
		tpnResults.addTab("If-Then Rules", null, pnlIfThenRules, null);
		pnlIfThenRules.setLayout(new BorderLayout(0, 0));
		
		pnlIfThenRules.add(pnlIfThenRulesButtons, BorderLayout.SOUTH);
		GridBagLayout gbl_pnlIfThenRulesButtons = new GridBagLayout();
		gbl_pnlIfThenRulesButtons.columnWeights = new double[]{0.0, 0.0};
		gbl_pnlIfThenRulesButtons.rowWeights = new double[]{0.0};
		pnlIfThenRulesButtons.setLayout(gbl_pnlIfThenRulesButtons);
		
		GridBagConstraints gbc_cmdIfThenRulesSave = new GridBagConstraints();
		gbc_cmdIfThenRulesSave.anchor = GridBagConstraints.EAST;
		gbc_cmdIfThenRulesSave.weightx = 1.0;
		gbc_cmdIfThenRulesSave.insets = new Insets(5, 5, 0, 5);
		gbc_cmdIfThenRulesSave.gridx = 0;
		gbc_cmdIfThenRulesSave.gridy = 0;
		pnlIfThenRulesButtons.add(cmdIfThenRulesSave, gbc_cmdIfThenRulesSave);
		
		GridBagConstraints gbc_cmdIfThenRulesView = new GridBagConstraints();
		gbc_cmdIfThenRulesView.insets = new Insets(5, 5, 0, 5);
		gbc_cmdIfThenRulesView.gridx = 1;
		gbc_cmdIfThenRulesView.gridy = 0;
		pnlIfThenRulesButtons.add(cmdIfThenRulesView, gbc_cmdIfThenRulesView);
		scpIfThenRules.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		scpIfThenRules.setToolTipText("");
		cmdIfThenRulesView.addActionListener(new Frame_cmdIfThenRulesView_actionAdapter(this));
		pnlIfThenRules.add(scpIfThenRules);
		txaIfThenRules.setEditable(false);
		
		scpIfThenRules.setViewportView(txaIfThenRules);
		
		tpnResults.addTab("CV Results", null, pnlCVResults, null);
		pnlCVResults.setLayout(new BorderLayout(0, 0));
		
		pnlCVResults.add(pnlCVResultsButtons, BorderLayout.SOUTH);
		GridBagLayout gbl_pnlCVResultsButtons = new GridBagLayout();
		gbl_pnlCVResultsButtons.columnWeights = new double[]{0.0, 0.0};
		gbl_pnlCVResultsButtons.rowWeights = new double[]{0.0};
		pnlCVResultsButtons.setLayout(gbl_pnlCVResultsButtons);
		
		GridBagConstraints gbc_cmdCVResultsSave = new GridBagConstraints();
		gbc_cmdCVResultsSave.weightx = 1.0;
		gbc_cmdCVResultsSave.anchor = GridBagConstraints.EAST;
		gbc_cmdCVResultsSave.insets = new Insets(5, 5, 0, 5);
		gbc_cmdCVResultsSave.gridx = 0;
		gbc_cmdCVResultsSave.gridy = 0;
		pnlCVResultsButtons.add(cmdCVResultsSave, gbc_cmdCVResultsSave);
		
		GridBagConstraints gbc_cmdCVResultsView = new GridBagConstraints();
		gbc_cmdCVResultsView.insets = new Insets(5, 5, 0, 5);
		gbc_cmdCVResultsView.gridx = 1;
		gbc_cmdCVResultsView.gridy = 0;
		pnlCVResultsButtons.add(cmdCVResultsView, gbc_cmdCVResultsView);
		scpCVResults.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		cmdCVResultsView.addActionListener(new Frame_cmdCVResultsView_actionAdapter(this));
		pnlCVResults.add(scpCVResults, BorderLayout.CENTER);
		txaCVResults.setEditable(false);
		cmdBestModelView.setEnabled(false);
		cmdCVResultsView.setEnabled(false);
		cmdIfThenRulesView.setEnabled(false);
		cmdBestModelSave.setEnabled(false);
		cmdCVResultsSave.setEnabled(false);
		cmdIfThenRulesSave.setEnabled(false);
		scpCVResults.setViewportView(txaCVResults);
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(tpnResults, GroupLayout.PREFERRED_SIZE, 775, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(5)
					.addComponent(tpnResults, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);

		
	}
	class Frame_cmdLoadPhenotype_actionAdapter implements ActionListener {
		private Analysis adaptee;

		Frame_cmdLoadPhenotype_actionAdapter(Analysis adaptee) {
			this.adaptee = adaptee;
		}

		public void actionPerformed(ActionEvent e) {
			adaptee.cmdLoadphenotype_actionPerformed(e);
		}
	}
	public void cmdLoadphenotype_actionPerformed(ActionEvent e) 
	{
		JFileChooser chooser=new JFileChooser(GUIMDR.project_path);
		FileNameExtensionFilter filter=new FileNameExtensionFilter("phe file","phe");
		chooser.setFileFilter(filter);
		int returnVal =chooser.showOpenDialog(new JPanel());
		if (returnVal!=JFileChooser.APPROVE_OPTION) {
			return;
		}
		phenofile=new File(chooser.getSelectedFile().getAbsolutePath());
		txtphenopath.setText(chooser.getSelectedFile().getAbsolutePath());
		if (!GUIMDR.gmdrini_path.equals("")) 
		{
			if (GUIMDR.gmdrini.containsKey("phe")) {
				try {
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old phenotype "+GUIMDR.gmdrini.get("phe")+" from project successed\n", GUIMDR.myUI.keyWordwarning);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			GUIMDR.gmdrini.put("phe", phenofile.getAbsolutePath());
			try {
				GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tLoading new phenotype "+phenofile.getAbsolutePath()+" successed\n", GUIMDR.myUI.keyWordsuccessed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		
		
		initData221();
		
		table221=new ColumnSelectableJTable(data221,name221); 
		table221.setEnabled(false);
		scrollpane_table221=new JScrollPane(table221);
		table221.setPreferredScrollableViewportSize(new Dimension(720,100));
	    table221.setColumnSelectionAllowed(true);
	    table221.setRowSelectionAllowed(false);
	    pnlpheofile.removeAll();
		pnlpheofile.add(scrollpane_table221);
		pnlselectedresidual.removeAll();
		txascorefile.setText("");
		txascorefile.setBackground(UIManager.getColor("Button.background"));
		removeresponse.setEnabled(true);
		addresponse.setEnabled(true);
		removepredictor.setEnabled(true);
		addpredictor.setEnabled(true);
		saveanalysis.setEnabled(true);
		return;
	}
	class Frame_cmdSaveAnalysis_actionAdapter implements ActionListener {
		private Analysis adaptee;

		Frame_cmdSaveAnalysis_actionAdapter(Analysis adaptee) {
			this.adaptee = adaptee;
		}

		public void actionPerformed(ActionEvent e) {
			adaptee.cmdSaveAnalysis_actionPerformed(e);
		}
	}
	
	class Frame_cmdRunAnalysis_actionAdapter implements ActionListener {
		private Analysis adaptee;

		Frame_cmdRunAnalysis_actionAdapter(Analysis adaptee) {
			this.adaptee = adaptee;
		}

		public void actionPerformed(ActionEvent e) {
			try {
				adaptee.cmdRunAnalysis_actionPerformed(e);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	class Frame_cmdLoadAnalysis_actionAdapter implements ActionListener {
		private Analysis adaptee;

		Frame_cmdLoadAnalysis_actionAdapter(Analysis adaptee) {
			this.adaptee = adaptee;
		}

		public void actionPerformed(ActionEvent e) {
			adaptee.cmdLoadAnalysis_actionPerformed(e);
		}
	}
 
	public void cmdRunAnalysis_actionPerformed(ActionEvent e) throws IOException {
		
		if ((!GUIMDR.name_bed.isFile()|!GUIMDR.name_fam.isFile())&&(!GUIMDR.name_ped.isFile())) 
		{
				JOptionPane.showMessageDialog(null,"Please input  Genotype Files");
				try {
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tTry to run analysis \tFailed\n", GUIMDR.myUI.keyWordfailed);
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), "\t\tGenotype Files is not existed\n", GUIMDR.myUI.keyWordfailed);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return;
		} 
/*		
		*/
		Dataset dataset=new Dataset();
		dataset.IsloadScore(false);
		dataset.setPaired(ispaired.isSelected());
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
			GUIMDR.dataset=new Plink(genofiles);
		}
		dataset.read(GUIMDR.dataset);
		
		pnlGraphicalModel.setData(dataset);
		pnlGraphicalModel.setModel(null);
		pnlGraphicalModel.setEnabled(false);
		if (Isusescore.isSelected()) 
		{
			ArrayList instances = dataset.instances;
			dataset.IsloadScore(true);
			for (int i = 0; i < instances.size(); i++) {
				((AbstractDataset.Instance) instances.get(i)).iniMU(((Double) score_data[i][0]).doubleValue());
			}
			
		}else {
			dataset.IsloadScore(false);
		}
		data=dataset;
///		datafiles=genofiles;
        int min=Integer.valueOf(mincount.getText());
        int max=Integer.valueOf(maxcount.getText());
        if (min>max) 
        {
        	JOptionPane.showMessageDialog(null,"Error:min must less than max");
			return;
		}
    	int evaluations = 0;
		double runtime = 0;
		String units = "";
		int unitmult = 1;
		
		if (searchtype.getSelectedItem().equals("Forced")) {
			String value = searchtypetext.getText();
			forced = new AttributeCombination(value, dataset.getLabels());
			min = max = forced.size();
		} else if (searchtype.getSelectedItem().equals("Random")) {
			if (jrbevaluations.isSelected()) 
			{
				evaluations = Integer.valueOf(searchtypetext.getText());
			}
			else if (jrbruntime.isSelected()) {
				runtime =Double.valueOf(runtimetext.getText());
				switch (unit.getSelectedIndex()) {
				case 1:
					unitmult = 60;
					break;
				case 2:
					unitmult = 60 * 60;
				case 3:
					unitmult = 60 * 60 * 24;
				case 0:
					unitmult=1;
					break;
				}
			}
		}


		boolean computeLandscape = landscape.isSelected();
		boolean parallel = true;
		long seedana =Long.valueOf(seed.getText());
		int tieValue = 1;
		switch (tie.getSelectedItem().toString()) {
		case "Affected":
			tieValue = Main.defaultTieCells;
			break;
		case "Unaffected":
			tieValue = 0;

		case "Unknown":
			tieValue = -1;
			break;
		}
        int intervals=Integer.valueOf(cv.getText());

		if (intervals < 1 || intervals > dataset.getNumInstances()) {
			JOptionPane.showMessageDialog(null,"Error: cv must be > 0 and <= number " + "of instances in the data set.");
			return;
		}
		Runnable onModelEnd = null;
		TimerTask task = null;
		String search = searchtype.getSelectedItem().toString();

		if (search.equalsIgnoreCase("Exhaustive")) {
			long count = 0;

			for (int i = min; i <= max; ++i) {
				count += Utility.combinations(dataset.getNumAttributes(), i);
			}

			count *= intervals;

			onModelEnd = new SwingInvoker(new ProgressPanelUpdater(prgProgress, count), false);
		} else if (search.equalsIgnoreCase("Forced")) {
			onModelEnd = new SwingInvoker(new ProgressPanelUpdater(prgProgress, intervals), false);
			min = max = forced.size();

		} else if (search.equalsIgnoreCase("Random")) {
			if (jrbevaluations.isSelected()) {
				onModelEnd = new SwingInvoker(new ProgressPanelUpdater(prgProgress, evaluations * (max - min + 1)
						* intervals), false);
			} else {
				task = new TimerRunnableTask(new SwingInvoker(new ProgressPanelUpdater(prgProgress, (long) Math.ceil(runtime * unitmult)), false));
			}
		}
		
		analysis = new AnalysisThread(min, max, dataset, intervals, seedana, tieValue, onModelEnd, onEndAttribute, onEnd, computeLandscape, parallel);
  //      AnalysisThread analysis=new AnalysisThread(min, max, dataset, intervals, seedana, tieValue, null, onEndAttribute, onEnd, computeLandscape, parallel);
		if (search.equalsIgnoreCase("Exhaustive")) {
			for (int i = min; i <= max; ++i) {
				analysis.addProducer(new AbstractAnalysisThread.CombinatoricProducer(new AttributeCombinationGenerator(dataset.getLabels(), i), intervals));
			}
		} else if (search.equalsIgnoreCase("Forced")) {
			analysis.addProducer(new AbstractAnalysisThread.ForcedCombinationProducer(forced, intervals));
		} else if (search.equalsIgnoreCase("Random")) {
			Random rand = new Random(seedana);

			if (jrbevaluations.isSelected()) {
				for (int i = min; i <= max; ++i) {
					analysis.addProducer(new AbstractAnalysisThread.CombinatoricProducer(new FixedRandomACG(dataset.getLabels(), i, rand, evaluations), intervals));
				}
			} else if (jrbruntime.isSelected()) {
				long count = (long) Math.ceil(runtime * unitmult);

				for (int i = min; i <= max; ++i) {
					analysis.addProducer(new AbstractAnalysisThread.CombinatoricProducer(new TimedRandomACG(dataset.getLabels(), i, rand, count), intervals));
				}
			}
		}
		resetForm();
		analysis.start();

		if (computeLandscape) {
			tpnResults.add(pnlLandscape, "Landscape", intervals > 1 ? 4 : 3);
		} else {
			tpnResults.remove(pnlLandscape);
		}
		saveanalysis.setEnabled(true);
		cmdBestModelView.setEnabled(true);
		cmdCVResultsView.setEnabled(true);
		cmdIfThenRulesView.setEnabled(true);
		try {
			GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tRun GMDR analysis \n",GUIMDR.myUI.keyWordsuccessed);
			GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tparameter :\n ",GUIMDR.myUI.keyWordsuccessed);
			GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tUsing Residual: True;",GUIMDR.myUI.keyWordsuccessed);
			GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tMarker Count Range: min: "+mincount.getText()+" max: "+maxcount.getText()+";",GUIMDR.myUI.keyWordsuccessed);
			GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tCross Validation Count: "+cv.getText()+";\n",GUIMDR.myUI.keyWordsuccessed);
			GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tpermutation: "+permutation.getText()+";",GUIMDR.myUI.keyWordsuccessed);
			GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tIs paired: "+(ispaired.isSelected()?"True":"False")+";",GUIMDR.myUI.keyWordsuccessed);
			GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\t\tTie Cells: "+tie.getSelectedItem()+";\n",GUIMDR.myUI.keyWordsuccessed);
			GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tCompute fitness landscape: "+(landscape.isSelected()?"True":"False")+";",GUIMDR.myUI.keyWordsuccessed);
			GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\tSearch Type: "+searchtype.getSelectedItem()+";\n",GUIMDR.myUI.keyWordsuccessed);
			if (searchtype.getSelectedIndex()==1) {
				GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tForced marker combination: "+searchtypetext.getText()+"\n",GUIMDR.myUI.keyWordsuccessed);

			}
			if (searchtype.getSelectedIndex()==2) {
				if (jrbevaluations.isSelected()) {
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tEvaluations: "+searchtypetext.getText()+"\n",GUIMDR.myUI.keyWordsuccessed);

				}else {
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),"\t\tRuntime: "+runtimetext.getText()+" "+unit.getSelectedItem()+"\n",GUIMDR.myUI.keyWordsuccessed);

				}
				
			}
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void cmdSaveAnalysis_actionPerformed(ActionEvent e) {
		
//		PrintWriter out = FileSaver.openFileWriter(this, "Save Analysis");
		FileNameExtensionFilter filter=new FileNameExtensionFilter("result File", "result");
	    JFileChooser choose;
	    if (!GUIMDR.project_path.equals("")) {
	    	
	    	 choose=new JFileChooser(GUIMDR.project_path);
		}
		else {
			 choose=new JFileChooser();
		}
		choose.setFileFilter(filter);
		choose.showSaveDialog(new JPanel());
        String result_path=choose.getSelectedFile().getAbsolutePath()+".result";
		File resultfile=new File(result_path);
		PrintWriter out=null;
		try {
			out = new PrintWriter(resultfile);
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if (out == null) {
			return;
		}

		AnalysisFileManager afm = new AnalysisFileManager();

		if (unfiltered == null) {
			afm.setAnalysis(data, datafiles, analysis);
		} else {
			afm.setAnalysis(unfiltered, datafiles, analysis, rankerThread);
		}

		switch (searchtype.getSelectedIndex()) {
		case 0:
			afm.putCfg(AnalysisFileManager.cfgWrapper, AnalysisFileManager.cfgValExhaustive);
			break;
		case 1:
			afm.putCfg(AnalysisFileManager.cfgWrapper, AnalysisFileManager.cfgValForced);
			break;
		case 2:
			afm.putCfg(AnalysisFileManager.cfgWrapper, AnalysisFileManager.cfgValRandom);

			if (jrbevaluations.isSelected()) {
				afm.putCfg(AnalysisFileManager.cfgEvaluations, searchtypetext.getText());
			} else if (jrbruntime.isSelected()) {
				afm.putCfg(AnalysisFileManager.cfgRuntime, runtimetext.getText());
				afm.putCfg(AnalysisFileManager.cfgRuntimeUnits, unit.getSelectedItem().toString());
			}

			break;
		}

		afm.write(out);
		out.flush();
		// data2
		
		if (score_data != null) {
			// @ScorefileInformation
			out.println("@ScorefileInformation");
			out.println("Scorefile = " + txtphenopath.getText());
			out.print("Responses = ");
			for(int i=0;i<index_table241.size();i++)
			{
				out.print(index_table241.get(i)+"\t");
			}
			out.print("\n");
			out.print("Predicts = ");
			for(int i=0;i<index_table242.size();i++)
			{
				out.print(index_table242.get(i)+"\t");
			}
			out.print("\n");
			// @Scorefile
			out.println("@Scorefile");
			for(int i=0;i<scoredata_head.length;i++)
			{
				out.print(scoredata_head[i]+ "\t");
			}
			out.print("\n");
			for (int i = 0; i < score_data.length; i++) {
				for (int j = 0; j < score_data[i].length; j++) {
					out.print(score_data[i][j] + " ");
				}
				out.println();
			}
		}
		
		// end
		out.flush();
		out.close();
		try {
			GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tSave analysis result to"+resultfile.getAbsolutePath()+"\n",GUIMDR.myUI.keyWordsuccessed);
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	
	public void cmdLoadAnalysis_actionPerformed(ActionEvent e) {
		clearTabs();
		resetForm();
		if (data!=null) 
		{
			data.IsloadScore(false);
			data=null;
		}
		
		score_data=null;
		unfiltered=null;
		JFileChooser fc = new JFileChooser(GUIMDR.project_path);
		fc.setMultiSelectionEnabled(false);
		fc.setDialogTitle("Load Analysis");
		BufferedReader in=null;
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				in = new BufferedReader(new FileReader(fc.getSelectedFile()));
				AnalysisFileManager afm = new AnalysisFileManager();
				afm.read(in);

				if (afm.getFiltered() == null) {
					unfiltered = null;
					data = afm.getDataset();
				} else {
					unfiltered = afm.getDataset();
					data = afm.getFiltered();
				}


				mincount.setText(""+afm.getMin());
				maxcount.setText(""+afm.getMax());
				cv.setText(""+afm.getIntervals());
				searchtypetext.setText(afm.getForced() == null ? "" : afm.getForced().getComboString());
				ispaired.setSelected(afm.isPaired());
				landscape.setSelected(afm.getModelFilter().getLandscape() != null);

				switch (afm.getTieValue()) {
				case 1:
					tie.setSelectedIndex(0);
					break;
				case 0:
					tie.setSelectedIndex(1);
					break;
				case -1:
					tie.setSelectedIndex(2);
					break;
				}

				loadedModelFilter = afm.getModelFilter();
				pnlGraphicalModel.setData(data);
				pnlGraphicalModel.setModel(null);
				pnlGraphicalModel.setEnabled(false);

				if (loadedModelFilter.getLandscape() != null && !loadedModelFilter.getLandscape().isEmpty()) {
					tpnResults.add(pnlLandscape, "Landscape", afm.getIntervals() > 1 ? 4 : 3);
					pnlLandscape.setLandscape(loadedModelFilter.getLandscape(), loadedModelFilter.getLabelMap(), false);
					pnlLandscape.setEnabled(true);
				} else {
					tpnResults.remove(pnlLandscape);
				}

				for (int i = afm.getMin(); i <= afm.getMax(); ++i) {
					addTableRow((CVSummaryModel) loadedModelFilter.getCVSummaryModel(i), afm.getIntervals());
				}

				String wrapper = afm.getCfg(AnalysisFileManager.cfgWrapper);

				if (wrapper == null || wrapper.equalsIgnoreCase(AnalysisFileManager.cfgValExhaustive)) {
					searchtype.setSelectedIndex(0);
				} else if (wrapper.equalsIgnoreCase(AnalysisFileManager.cfgValForced)) {
					searchtype.setSelectedIndex(1);
				} else if (wrapper.equalsIgnoreCase(AnalysisFileManager.cfgValRandom)) {
					searchtype.setSelectedIndex(2);

					String s = afm.getCfg(AnalysisFileManager.cfgEvaluations);

					if (s != null) {
						jrbevaluations.setSelected(true);
						searchtypetext.setText(s);
					} else {
						s = afm.getCfg(AnalysisFileManager.cfgRuntime);
						jrbruntime.setSelected(true);
						runtimetext.setText(s);

						s = afm.getCfg(AnalysisFileManager.cfgRuntimeUnits);

						if (s == null || s.equalsIgnoreCase("MINUTES")) {
							unit.setSelectedIndex(1);
						} else if (s.equalsIgnoreCase("SECONDS")) {
							unit.setSelectedIndex(0);
						} else if (s.equalsIgnoreCase("HOURS")) {
							unit.setSelectedIndex(2);
						} else if (s.equalsIgnoreCase("DAYS")) {
							unit.setSelectedIndex(3);
						} else {
							unit.setSelectedIndex(1);
						}
					}
				}
/*
				if (unfiltered != null) {
					String filtername = (String) afm.getFilterConfig().get("FILTER");

					if (filtername.equalsIgnoreCase("RELIEFF")) {
						cboFilter.setSelectedIndex(0);
						pnlFilterLandscape.setYAxisLabel("ReliefF");
						String samples = (String) afm.getFilterConfig().get("SAMPLES");
						if (samples.equalsIgnoreCase("ALL")) {
							chkReliefFWholeDataset.setSelected(true);
						} else {
							chkReliefFWholeDataset.setSelected(false);
							spnReliefFSampleSize.setValue(Integer.valueOf(samples));
						}

						if (afm.getFilterConfig().containsKey("NEIGHBORS")) {
							spnReliefFNeighbors.setValue(Integer.valueOf((String) afm.getFilterConfig().get("NEIGHBORS")));
						}
					} else if (filtername.equalsIgnoreCase("CHISQUARED")) {
						cboFilter.setSelectedIndex(1);
						pnlFilterLandscape.setYAxisLabel("\u03a7\u00b2");

						if (afm.getFilterConfig().containsKey("PVALUE")) {
							chkChiSquaredPValue.setSelected(Boolean.valueOf((String) afm.getFilterConfig().get("PVALUE")).booleanValue());
						}
					} else if (filtername.equalsIgnoreCase("ODDSRATIO")) {
						cboFilter.setSelectedIndex(1);
						pnlFilterLandscape.setYAxisLabel("OddsRatio");
					}

					String selection = (String) afm.getFilterConfig().get("SELECTION");
					String selectionvalue = (String) afm.getFilterConfig().get("SELECTIONVALUE");

					if (selection != null) {
						if (selection.equalsIgnoreCase("TOPN")) {
							cboCriterion.setSelectedIndex(0);

							if (selectionvalue != null) {
								spnCriterionFilter.setValue(Integer.valueOf(selectionvalue));
							}
						} else if (selection.equalsIgnoreCase("TOP%")) {
							cboCriterion.setSelectedIndex(1);

							if (selectionvalue != null) {
								spnCriterionFilter.setValue(Double.valueOf(selectionvalue));
							}
						}

						else if (selection.equalsIgnoreCase("THRESHOLD")) {
							cboCriterion.setSelectedIndex(2);

							if (selectionvalue != null) {
								spnCriterionFilter.setValue(Double.valueOf(selectionvalue));
							}
						}
					}

					pnlFilterLandscape.setLandscape(afm.getFilterScores());
					pnlFilterLandscape.setEnabled(true);
				}
				*/
				// data2
				
				String line = in.readLine();
				if (line != null && line.contains("Scorefile = ")) {
					Isusescore.setSelected(true);
					data.IsloadScore(true);
					// @ScorefileInformation
				
					phenofile=new File(line.substring("Scorefile = ".length()));
					
					line = in.readLine();
					String[] reponses=line.substring("Responses = ".length()).split("\t");
					index_table241.clear();
				
					for(String rep : reponses)
					{
						index_table241.add(Integer.valueOf(rep));
					}
					line = in.readLine();
					index_table242.clear();
					String[] predicts=line.substring("Predicts = ".length()).split("\t");
					for(String pre : predicts)
					{
						index_table242.add(Integer.valueOf(pre));
					}
					if (phenofile.exists()) {
						txtphenopath.setText(phenofile.getAbsolutePath());
						initData221();
						///load pheno
						table221=new ColumnSelectableJTable(data221,name221); 
						table221.setEnabled(false);
						scrollpane_table221=new JScrollPane(table221);
						table221.setPreferredScrollableViewportSize(new Dimension(720,100));
					    table221.setColumnSelectionAllowed(true);
					    table221.setRowSelectionAllowed(false);
					    pnlpheofile.removeAll();
						pnlpheofile.add(scrollpane_table221);
						pnlselectedresidual.removeAll();
						txascorefile.setText("");
						txascorefile.setBackground(UIManager.getColor("Button.background"));
						removeresponse.setEnabled(true);
						addresponse.setEnabled(true);
						removepredictor.setEnabled(true);
						addpredictor.setEnabled(true);
						saveanalysis.setEnabled(true);
						//////load responese///
						name241=new Object[index_table241.size()];
						data241=new Object[num_of_data][index_table241.size()];
						for(int i=0;i<index_table241.size();i++)
						{
							name241[i]=name221[(int)index_table241.elementAt(i)];
							for(int j=0;j<num_of_data;j++)
							{
								data241[j][i]=data221[j][(int)index_table241.elementAt(i)];
							}
						}
						table241=new ColumnSelectableJTable(data241,name241);table241.setEnabled(false);
						table241.setEnabled(false);
						table241.setColumnSelectionAllowed(true);
						table241.setRowSelectionAllowed(false);
						scrollpane_table241=new JScrollPane(table241);
						table241.setPreferredScrollableViewportSize(new Dimension(220,60));
						pnlresponse.removeAll();
						pnlresponse.add(scrollpane_table241);
						pnlresponse.updateUI();
						//////load predicts///
						
						name242=new Object[index_table242.size()];
						data242=new Object[num_of_data][index_table242.size()];
						for(int i=0;i<index_table242.size();i++)
						{
							name242[i]=name221[(int)index_table242.elementAt(i)];
							for(int j=0;j<num_of_data;j++)
							{
								data242[j][i]=data221[j][(int)index_table242.elementAt(i)];
							}
						}
						table242=new ColumnSelectableJTable(data242,name242);table242.setEnabled(false);
						table242.setEnabled(false);
						scrollpane_table242=new JScrollPane(table242);
						table242.setPreferredScrollableViewportSize(new Dimension(220,60));
						table242.setColumnSelectionAllowed(true);
						table242.setRowSelectionAllowed(false);
						pnlpredictor.removeAll();
						pnlpredictor.add(scrollpane_table242);
						pnlpredictor.updateUI();
						
						
					}
				
					
					// @Scorefile
					line=in.readLine();
					line = in.readLine();
					scoredata_head = line.split("\t");
					ArrayList data_2 = new ArrayList(0);
					while ((line = in.readLine()) != null) {
						String[] s_ = line.split("\\s");
						Double[] data_double = new Double[s_.length];
						for (int i = 0; i < s_.length; i++) {
							data_double[i] = new Double(Double.parseDouble(s_[i]));
						}
						data_2.add(data_double);
					}
					score_data = (Object[][]) data_2.toArray(new Object[data_2.size()][scoredata_head.length]);
					ColumnSelectableJTable temp_table=new ColumnSelectableJTable(score_data,scoredata_head);
					JScrollPane scrollpane_temp_table=new JScrollPane(temp_table);
					temp_table.setPreferredScrollableViewportSize(new Dimension(750,90));
					temp_table.setColumnSelectionAllowed(true);
					temp_table.setRowSelectionAllowed(false);
					savescore.setEnabled(true);
					Isusescore.setEnabled(true);
					Isusescore.setSelected(true);
					pnlselectedresidual.removeAll();
					pnlselectedresidual.add(scrollpane_temp_table);
					pnlselectedresidual.updateUI();
				
				}
				
			//	cmdViewScorefile.setEnabled(data2 != null);
		//		cmdLoadScorefile.setEnabled(true);
		//		lockdown(false);
				prgProgress.setValue(1);
				runanalysis.setEnabled(true);
				saveanalysis.setEnabled(true);
//				cmdExportFiltered.setEnabled(true);
				try {
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tLoad analysis results from  "+fc.getSelectedFile().getAbsolutePath()+"  successed.\n",GUIMDR.myUI.keyWordsuccessed);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (FileNotFoundException ex) {
				JOptionPane.showMessageDialog(this, "File '" + fc.getSelectedFile() + "'not found.", "Error", JOptionPane.ERROR_MESSAGE);
				try {
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tFile '" + fc.getSelectedFile() + "'not found.\n", GUIMDR.myUI.keyWordfailed);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Unable to read File '" + fc.getSelectedFile() + "'.", "Error", JOptionPane.ERROR_MESSAGE);
				try {
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tUnable to read File '" + fc.getSelectedFile() + "'.\n", GUIMDR.myUI.keyWordfailed);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
				try {
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tUnable to read File '" + ex.getMessage() + "'.\n", GUIMDR.myUI.keyWordfailed);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
		}
		try {
			in.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	class Frame_tblSummaryTable_selectionAdapter implements ListSelectionListener {
		private Analysis adaptee;

		Frame_tblSummaryTable_selectionAdapter(Analysis adaptee) {
			this.adaptee = adaptee;
		}

		public void valueChanged(ListSelectionEvent e) {
			adaptee.jtsummary_selectedChanged(e);
		}
	}
	
	private void clearTabs() {
		txaBestModel.setText("");
		cmdBestModelSave.setEnabled(false);
		txaCVResults.setText("");
		cmdCVResultsSave.setEnabled(false);
		txaIfThenRules.setText("");
		cmdIfThenRulesSave.setEnabled(false);
	
		pnlGraphicalModel.setModel(null);
		pnlLandscape.setEnabled(false);
		pnlLandscape.setLandscape(null);
	}

	public void jtsummary_selectedChanged(ListSelectionEvent e) {
		int row = jtsummary.getSelectedRow();
		int attrCount = row + (forced == null ? Integer.valueOf(mincount.getText()) : forced.size());
		int intervals = Integer.valueOf(cv.getText());

		ModelFilter modelFilter = loadedModelFilter == null ? (ModelFilter) analysis.getModelFilter() : loadedModelFilter;

		if (row < 0) {
			clearTabs();
		} else {
			new TextComponentUpdaterThread(txaBestModel, new BestModelTextGenerator((CVSummaryModel) modelFilter.getCVSummaryModel(attrCount), intervals,
					Main.nf, Main.pValueTol), new ComponentEnabler(cmdBestModelSave, true)).start();

			if (intervals > 1) {
				new TextComponentUpdaterThread(txaCVResults, new CVResultsTextGenerator(modelFilter, attrCount, intervals, Main.nf, Main.pValueTol),
						new ComponentEnabler(cmdCVResultsSave, true)).start();
			}

			new TextComponentUpdaterThread(txaIfThenRules, new IfThenRulesTextGenerator((CVSummaryModel) modelFilter.getCVSummaryModel(attrCount),
					data.getLabels()), new ComponentEnabler(cmdIfThenRulesSave, true)).start();

			pnlGraphicalModel.setModel((Model) modelFilter.getCVSummaryModel(attrCount).getModel());
	    	pnlGraphicalModel.setEnabled(true);
		}
		
		if (viewBestModelResultwindows!=null) 
		{
			viewBestModelResultwindows.setText(txaBestModel.getText(),"BestModel");
		}
		if (viewCVResultsResultwindows!=null) 
		{
			viewCVResultsResultwindows.setText(txaCVResults.getText(),"CV");
		}
		if (viewIfThenRuleswindows!=null) 
		{
			viewIfThenRuleswindows.setText(txaIfThenRules.getText(),"IfThenRule");
		}
	}
	class Frame_cmdScoreSave_actionAdapter implements ActionListener
	{
		private Analysis adaptee;
		public Frame_cmdScoreSave_actionAdapter(Analysis adaptee) 
		{
			this.adaptee=adaptee;
			// TODO Auto-generated constructor stub
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			adaptee.cmdScoreSave_actionPerformed(e);
		}
		
	}
	public void cmdScoreSave_actionPerformed(ActionEvent e) 
	{
	    FileNameExtensionFilter filter=new FileNameExtensionFilter("Residual File", "residual");
	    JFileChooser choose;
	    if (!GUIMDR.project_path.equals("")) {
	    	
	    	 choose=new JFileChooser(GUIMDR.project_path);
		}
		else {
			 choose=new JFileChooser();
		}
		choose.setFileFilter(filter);
		choose.setAcceptAllFileFilterUsed(true);
		choose.showSaveDialog(new JPanel());
        String residual_path=choose.getSelectedFile().getPath()+".residual";
		File temp_file=new File(residual_path);
		FileWriter fw;;
		try 
		{
			fw=new FileWriter(temp_file);
			fw.write(Comblinkfunc.getSelectedItem().toString()+" "+Comblinkfunc.getSelectedItem().toString()+"(");
			for(int i=0;i<name241.length;i++)
			{
				fw.write(name241[i].toString());
				if(i!=name241.length-1)
				{
					fw.write("+");
				}
			}
			fw.write("=mu");
			for(int i=0;i<name242.length;i++)
			{
				fw.write("+"+name242[i]);
			}
			fw.write(")");
			fw.write(13);
			fw.write(10);
			for(int i=0;i<score_data.length;i++)
			{

				for(int j=0;j<score_data[i].length;j++)
				{
					fw.write(score_data[i][j].toString());
				}
				fw.write(13);
				fw.write(10);
			}
			fw.close();
		} 
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		if (!GUIMDR.gmdrini_path.equals("")) {
			if (GUIMDR.gmdrini.containsKey("residual_file")) {
				try {
					GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemoving old residual file "+GUIMDR.gmdrini.get("residual_file")+" from project successed", GUIMDR.myUI.keyWordwarning);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			GUIMDR.gmdrini.put("residual_file", residual_path);
			try {
				GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tLoading new residual file "+residual_path+" successed", GUIMDR.myUI.keyWordsuccessed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		txascorefile.setText(residual_path);
		txascorefile.enable(false);
		txascorefile.updateUI();
		return;	
	}
	class Frame_cmdLoadScore_actionAdapter implements ActionListener
	{
		private Analysis adaptee;
		public Frame_cmdLoadScore_actionAdapter(Analysis adaptee) 
		{
			this.adaptee=adaptee;
			// TODO Auto-generated constructor stub
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			adaptee.cmdLoadScore_actionPerformed(e);
		}
		
	}
	public void cmdLoadScore_actionPerformed(ActionEvent e) 
	{
		 FileNameExtensionFilter filter=new FileNameExtensionFilter("Residual File", "residual");
		 JFileChooser choose = new JFileChooser(GUIMDR.project_path);
		 choose.setFileFilter(filter);
		 int returnVal =choose.showOpenDialog(new JPanel());
		 if (returnVal!=JFileChooser.APPROVE_OPTION) 
		 {
			return;
		 }
		
		 File readscore=choose.getSelectedFile();
		 FileReader fileReader=null;
		 BufferedReader bufferedReader=null;
		 Vector<Double> residuals=new Vector<>();
		 try {
			 fileReader=new FileReader(readscore);
			 bufferedReader=new BufferedReader(fileReader);
			 String line=bufferedReader.readLine();
			 scoredata_head=new Object[1];
			 scoredata_head[0]=line;
			 while ((line=bufferedReader.readLine())!=null)
			 {	
				residuals.add(Double.valueOf(line));
			 }
			 fileReader.close();
			 bufferedReader.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		 score_data=new Object[residuals.size()][1];
		 for(int i=0;i<residuals.size();i++)
		 {
			 score_data[i][0]=residuals.get(i);
		 }
		 
		ColumnSelectableJTable temp_table=new ColumnSelectableJTable(score_data,scoredata_head);
		JScrollPane scrollpane_temp_table=new JScrollPane(temp_table);
		temp_table.setPreferredScrollableViewportSize(new Dimension(750,90));
		temp_table.setColumnSelectionAllowed(true);
		temp_table.setRowSelectionAllowed(false);
		txtphenopath.setText("");
		pnlpheofile.removeAll();
		pnlresponse.removeAll();
		pnlpredictor.removeAll();
		savescore.setEnabled(true);
		Isusescore.setEnabled(true);
		Isusescore.setSelected(true);
		pnlselectedresidual.removeAll();
		pnlselectedresidual.add(scrollpane_temp_table);
		pnlselectedresidual.updateUI();
		txascorefile.setText(readscore.getAbsolutePath());
		txascorefile.enable(false);
		txascorefile.updateUI();
		if (GUIMDR.gmdrini.containsKey("residual")) {
			try {
				GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tRemove old residual file "+GUIMDR.gmdrini.get("residual")+"  successed\n", GUIMDR.myUI.keyWordsuccessed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		GUIMDR.gmdrini.put("residual", readscore.getAbsolutePath());
		try {
			GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(), Main.dateFormat.format(Main.date.getTime())+"\tLoad residual file "+readscore.getAbsolutePath()+"  successed\n", GUIMDR.myUI.keyWordsuccessed);
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	class Frame_cmdScoreCalculate_actionAdapter implements ActionListener
	{
		private Analysis adaptee;
		public Frame_cmdScoreCalculate_actionAdapter(Analysis adaptee) 
		{
			this.adaptee=adaptee;
			// TODO Auto-generated constructor stub
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			adaptee.cmdScoreCalculate_actionPerformed(e);
		}
		
	}
	public void cmdScoreCalculate_actionPerformed(ActionEvent e) 
	{
		File temp_file=new File(txtphenopath.getText());
		if(!temp_file.exists())
		{
			JOptionPane.showMessageDialog(null,"Please input a phenotype file");
			return;
		}
		int data1[]=new int[index_table241.size()];
		int data2[]=new int[index_table242.size()];
		for (int i = 0; i < data1.length; i++) 
		{
			data1[i]=(int)index_table241.get(i);
			
		}
		for (int i = 0; i < data2.length; i++) 
		{
			data2[i]=(int)index_table242.get(i);
			
		}
		if (data1.length==0||data2.length==0) 
		{
			return;	
		}
		Residual_cal theResidual_cal=new Residual_cal(txtphenopath.getText(),data1,data2,Comblinkfunc.getSelectedItem().toString(),combestimatemethod.getSelectedItem().toString());
		try
		{
			score_data=theResidual_cal.CalofResidual().clone();
			scoredata_head=new Object[score_data[0].length];
			for(int i=0;i<scoredata_head.length;i++)
			{
				scoredata_head[i]=Comblinkfunc.getSelectedItem().toString()+" "+Comblinkfunc.getSelectedItem().toString()+"(";
				for(int index=0;index<name241.length;index++)
				{
					scoredata_head[i]+=name241[index].toString();
					if(index!=name241.length-1)
					{
						scoredata_head[index]+="+";
					}
				}
				scoredata_head[i]+="=mu";
				for(int index=0;index<name242.length;index++)
				{
					scoredata_head[i]+="+"+name242[index];
				}
				scoredata_head[i]+=")";
			}
			ColumnSelectableJTable temp_table=new ColumnSelectableJTable(score_data,scoredata_head);
			JScrollPane scrollpane_temp_table=new JScrollPane(temp_table);
			temp_table.setPreferredScrollableViewportSize(new Dimension(750,90));
			temp_table.setColumnSelectionAllowed(true);
			temp_table.setRowSelectionAllowed(false);
			temp_table.setEnabled(false);
			savescore.setEnabled(true);
			Isusescore.setEnabled(true);
			Isusescore.setSelected(true);
			pnlselectedresidual.removeAll();
			pnlselectedresidual.add(scrollpane_temp_table);
			pnlselectedresidual.updateUI();
		}
		catch(IOException e1)
		{
			
		}
		return;
	}
	class Frame_cmdBestmodelSave_actionAdapter implements ActionListener
	{
		private Analysis adaptee;
		public Frame_cmdBestmodelSave_actionAdapter(Analysis adaptee) 
		{
			this.adaptee=adaptee;
			// TODO Auto-generated constructor stub
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			adaptee.cmdBestmodelSave_actionPerformed(e);
		}
		
	}
	
	public void cmdBestmodelSave_actionPerformed(ActionEvent e) 
	{
		 JFileChooser choose=new JFileChooser();
		 choose.setCurrentDirectory(new File(GUIMDR.project_path));
		 FileNameExtensionFilter filter=new FileNameExtensionFilter("Save best model result", "result");
		 choose.addChoosableFileFilter(filter);
		 int resultval=choose.showSaveDialog(new Panel());
		 if (resultval!=JFileChooser.APPROVE_OPTION) 
		 {
			return;
		 }
		 File resultfile=new File(choose.getSelectedFile().getAbsolutePath()+"_BestModel.result");
		 try {
			PrintWriter pWriter=new PrintWriter(resultfile);
			pWriter.println(txaBestModel.getText());
			pWriter.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 try {
			GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tSave best model to  "+resultfile.getAbsolutePath()+"  successed.\n",GUIMDR.myUI.keyWordsuccessed);
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 
	}
	class Frame_cmdCVResults_actionAdapter implements ActionListener
	{
		private Analysis adaptee;
		public Frame_cmdCVResults_actionAdapter(Analysis adaptee) 
		{
			this.adaptee=adaptee;
			// TODO Auto-generated constructor stub
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			adaptee.cmdCVResults_actionPerformed(e);
		}
		
	}

	public void cmdCVResults_actionPerformed(ActionEvent e) 
	{
		 JFileChooser choose=new JFileChooser();
		 choose.setCurrentDirectory(new File(GUIMDR.project_path));
		 FileNameExtensionFilter filter=new FileNameExtensionFilter("Save cross validation result", "result");
		 choose.addChoosableFileFilter(filter);
		 int resultval=choose.showSaveDialog(new Panel());
		 if (resultval!=JFileChooser.APPROVE_OPTION) 
		 {
			return;
		 }
		 File resultfile=new File(choose.getSelectedFile().getAbsolutePath()+"_CV.result");
		 try {
			PrintWriter pWriter=new PrintWriter(resultfile);
			pWriter.println(txaCVResults.getText());
			pWriter.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 try {
				GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tSave cross validation results to  "+resultfile.getAbsolutePath()+"  successed.\n",GUIMDR.myUI.keyWordsuccessed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
	class Frame_cmdIfThenRules_actionAdapter implements ActionListener
	{
		private Analysis adaptee;
		public Frame_cmdIfThenRules_actionAdapter(Analysis adaptee) 
		{
			this.adaptee=adaptee;
			// TODO Auto-generated constructor stub
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			adaptee.cmdIfThenRules_actionPerformed(e);
		}
		
	}
	public void cmdIfThenRules_actionPerformed(ActionEvent e) 
	{
		 JFileChooser choose=new JFileChooser();
		 choose.setCurrentDirectory(new File(GUIMDR.project_path));
		 FileNameExtensionFilter filter=new FileNameExtensionFilter("Save If-Then Rules result", "result");
		 choose.addChoosableFileFilter(filter);
		 int resultval=choose.showSaveDialog(new Panel());
		 if (resultval!=JFileChooser.APPROVE_OPTION) 
		 {
			return;
		 }
		 File resultfile=new File(choose.getSelectedFile().getAbsolutePath()+"_IfThenRules.result");
		 try {
			PrintWriter pWriter=new PrintWriter(resultfile);
			pWriter.println(txaIfThenRules.getText());
			pWriter.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 try {
				GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tSave if-then rules result to  "+resultfile.getAbsolutePath()+"  successed.\n",GUIMDR.myUI.keyWordsuccessed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
	
	private void addTableRow(CVSummaryModel summary, int intervals) {
		Vector v = new Vector(5);
		v.add(summary.getModel().getAttributes());
		summary.getAverageTrainResult().setNumberFormat(Main.nf);
		v.add(summary.getAverageTrainResult());

		if (intervals > 1) {
			summary.getAverageTestResult().setNumberFormat(Main.nf);
			v.add(summary.getAverageTestResult());

			if (summary.getSignTestCount() < 0) {
				v.add("N/A");
			} else {
				v.add(Integer.toString(summary.getSignTestCount()) + " (" + Main.nf.format(summary.getSignTestPValue()) + ")");
			}

			v.add(Integer.toString(summary.getCVC()) + '/' + intervals);
		}

		dtmSummaryTable.addRow(v);

		if (jtsummary.getSelectedRow() < 0) {
			jtsummary.getSelectionModel().setSelectionInterval(0, 0);
		}
	}

	class Frame_cmdBestModelView_actionAdapter implements ActionListener
	{
		private Analysis adaptee;
		public Frame_cmdBestModelView_actionAdapter(Analysis adaptee) 
		{
		     this.adaptee=adaptee;	
			// TODO Auto-generated constructor stub
		}
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			this.adaptee.cmdBestModelView_actionPerformed(e);
		}
		
	}
	public void cmdBestModelView_actionPerformed(ActionEvent e) 
	{
		if (viewBestModelResultwindows==null)
			viewBestModelResultwindows=new ViewFrame();
		viewBestModelResultwindows.setText(txaBestModel.getText(),"BestModel");
		viewBestModelResultwindows.setVisible(true);
		
	}
	class Frame_cmdCVResultsView_actionAdapter implements ActionListener
	{
		private Analysis adaptee;
		public Frame_cmdCVResultsView_actionAdapter(Analysis adaptee) 
		{
		     this.adaptee=adaptee;	
			// TODO Auto-generated constructor stub
		}
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			this.adaptee.cmdCVResultsView_actionPerformed(e);
		}
		
	}
	public void cmdCVResultsView_actionPerformed(ActionEvent e) 
	{
		if (viewCVResultsResultwindows==null)
			viewCVResultsResultwindows=new ViewFrame();
		viewCVResultsResultwindows.setText(txaCVResults.getText(),"CV");
		viewCVResultsResultwindows.setVisible(true);
	}
	class Frame_cmdIfThenRulesView_actionAdapter implements ActionListener
	{
		private Analysis adaptee;
		public Frame_cmdIfThenRulesView_actionAdapter(Analysis adaptee) 
		{
		     this.adaptee=adaptee;	
			// TODO Auto-generated constructor stub
		}
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			this.adaptee.cmdIfThenRulesView_actionPerformed(e);
		}
		
	}
	public void cmdIfThenRulesView_actionPerformed(ActionEvent e) 
	{
		if (viewIfThenRuleswindows==null)
			viewIfThenRuleswindows=new ViewFrame();
		viewIfThenRuleswindows.setText(txaIfThenRules.getText(),"IfThenRules");
		viewIfThenRuleswindows.setVisible(true);
	}
	
	public void initScoreCalculation()
	{
		pnlScorePhenopath.setBorder(BorderFactory.createTitledBorder("File Information"));
		pnlScorePhenopath.setPreferredSize(new Dimension(785,70));
		pnlScorePhenopath.add(labelphenofile);
		pnlScorePhenopath.add(txtphenopath);
		txtphenopath.setEnabled(false);
		pnlScorePhenopath.add(loadphenotype);
		loadphenotype.addActionListener(new Frame_cmdLoadPhenotype_actionAdapter(this));
		pnlScorePhenopath.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		pnlpheofile.setBorder(BorderFactory.createTitledBorder("Phenotype Information"));
		pnlpheofile.setPreferredSize(new Dimension(785,160));
		
		pnlloadvariable.setLayout(new BoxLayout(pnlloadvariable,BoxLayout.Y_AXIS));
		pnlloadvar.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlloadvar.add(removeresponse);
		removeresponse.setEnabled(false);
		removeresponse.addActionListener(this);
		pnlloadvar.add(addresponse);
		addresponse.setEnabled(false);
		addresponse.addActionListener(this);
		pnlloadvar.add(pnlblank);
		pnlblank.setPreferredSize(new Dimension(169,27));
		pnlloadvar.add(removepredictor);
		removepredictor.setEnabled(false);
		removepredictor.addActionListener(this);
		pnlloadvar.add(addpredictor);
		addpredictor.setEnabled(false);
		addpredictor.addActionListener(this);
		pnlloadvariable.add(pnlloadvar);
		pnlloadvariable.setPreferredSize(new Dimension(785,40));
		pnldealresidual.setPreferredSize(new Dimension(785, 50));
		pnlselectedvar.add(pnlresponse);
		pnlselectedvar.add(pnlpredictor);
		pnlselectedvar.add(pnlestimationfunc);
		pnlresponse.setPreferredSize(new Dimension(266,128));
		pnlresponse.setBorder(BorderFactory.createTitledBorder("response"));
		pnlpredictor.setPreferredSize(new Dimension(266,128));
		pnlpredictor.setBorder(BorderFactory.createTitledBorder("predictor"));
		pnlestimationfunc.setPreferredSize(new Dimension(236,158));
		Comblinkfunc.setPreferredSize(new Dimension(200, 25));
		Comblinkfunc.addItem("Linear Model");
		Comblinkfunc.addItem("Logistic Model");
		Comblinkfunc.addItem("Poisson Model");
		Comblinkfunc.addItem("Multinomial Logit Model");
		Comblinkfunc.addItem("Proportional Odds Model");
		Comblinkfunc.addItem("Proportional Hazards Model");
		combestimatemethod.setMaximumRowCount(8);
		combestimatemethod.setPreferredSize(new Dimension(200, 25));
		combestimatemethod.addItem("Maximum likelihood method");
		combestimatemethod.addItem("Generalized estimating equations");
		combestimatemethod.addItem("Quasi-maximum likelihood method");
		panel_select.setLayout(new BoxLayout(panel_select,BoxLayout.Y_AXIS));
		JPanel temp_panel=new JPanel();
		temp_panel.setPreferredSize(new Dimension(200,40));
		pnlestimationfunc.add(label241);
		pnlestimationfunc.add(Comblinkfunc);
		pnlestimationfunc.add(label242);
		pnlestimationfunc.add(combestimatemethod);
		pnlestimationfunc.add(bntrunscorecalc);
		bntrunscorecalc.addActionListener(new Frame_cmdScoreCalculate_actionAdapter(this));
		pnlselectedvar.setPreferredSize(new Dimension(785,155));
		
		pnlselectedresidual.setBorder(BorderFactory.createTitledBorder("Residual"));
		pnlselectedresidual.setPreferredSize(new Dimension(785,150));
		cmdloadscore.addActionListener(new Frame_cmdLoadScore_actionAdapter(this));
		Isusescore.addChangeListener(this);
		pnldealresidual.setBackground(new Color(240, 240, 240));
		pnldealresidual.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		pnldealresidual.add(Isusescore);
		Isusescore.setEnabled(false);
		pnldealresidual.add(pnlresidual_path);
		pnlresidual_path.setPreferredSize(new Dimension(550, 29));
		pnlresidual_path.setLayout(new GridLayout(0, 1, 0, 0));
		txascorefile.setBackground(UIManager.getColor("Button.background"));
		
		pnlresidual_path.add(txascorefile);
		pnldealresidual.add(cmdloadscore);
		if (Analysis.phenofile!=null) {
			txtphenopath.setText(phenofile.getAbsolutePath());
			initData221();
			table221=new ColumnSelectableJTable(data221,name221); 
			table221.setEnabled(false);
			scrollpane_table221=new JScrollPane(table221);
			table221.setPreferredScrollableViewportSize(new Dimension(720,100));
		    table221.setColumnSelectionAllowed(true);
		    table221.setRowSelectionAllowed(false);
		    pnlpheofile.removeAll();
			pnlpheofile.add(scrollpane_table221);
			removeresponse.setEnabled(true);
			addresponse.setEnabled(true);
			removepredictor.setEnabled(true);
			addpredictor.setEnabled(true);
		}
	
		score_calculation.add(pnlScorePhenopath);
		score_calculation.add(pnlpheofile);
		score_calculation.add(pnlloadvariable);
		score_calculation.add(pnlselectedvar);
		score_calculation.add(pnlselectedresidual);
		score_calculation.add(pnldealresidual);
		pnldealresidual.add(savescore);
		savescore.setEnabled(false);
		savescore.addActionListener(new Frame_cmdScoreSave_actionAdapter(this));
		
		
	}
	
	public void initConfiguration()
	{	
		
		set31.setPreferredSize(new Dimension(800,200));
		set31.setLayout(new BoxLayout(set31,BoxLayout.Y_AXIS));
		set31.setBorder(BorderFactory.createTitledBorder(""));
		btg2.add(radiobutton311);
		radiobutton311.setSelected(true);
		btg2.add(radiobutton312);
		btg2.add(radiobutton313);
		set31.add(set301);
		set301.add(checkbox301);
		checkbox301.addItemListener(this);
		checkbox301.setSelected(false);
		set301.setPreferredSize(new Dimension(800,40));
		set301.setLayout(new FlowLayout(FlowLayout.LEFT));
		set31.add(set3010);
		set3010.add(label3010);
		label3010.setEnabled(false);
		set3010.add(textarea3010);
		textarea3010.setEnabled(false);
		set3010.add(button3010);
		button3010.setEnabled(false);
		set3010.setPreferredSize(new Dimension(800,40));
		set3010.setLayout(new FlowLayout(FlowLayout.CENTER));
		JPanel temp=new JPanel();
		temp.setPreferredSize(new Dimension(800,40));
		set31.add(temp);
		set31.add(set311);
		set311.add(radiobutton311);
		radiobutton311.addItemListener(this);
		set311.setPreferredSize(new Dimension(800,40));
		set311.setLayout(new FlowLayout(FlowLayout.LEFT));
		set31.add(set312);
		set312.add(radiobutton312);
		radiobutton312.addItemListener(this);
		set312.setPreferredSize(new Dimension(800,40));
		set312.setLayout(new FlowLayout(FlowLayout.LEFT));
		set31.add(set313);
		set313.add(radiobutton313);
		radiobutton313.addItemListener(this);
		set313.setPreferredSize(new Dimension(800,40));
		set313.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		
		
		pnlconfiguration.add(set31);
	}
	
	public void itemStateChanged(ItemEvent e) 
	{
		if (e.getSource()==Isusescore) 
		{
			boolean status=Isusescore.isSelected();
		}
		if(e.getSource()==searchtype)
		{
			switch(searchtype.getSelectedItem().toString())
			{
			case "Exhaustive":
				pnlconfig1.removeAll();
				pnlconfig2.removeAll();
				pnlsearchconfig.updateUI();
				break;
			case "Forced":
				pnlconfig1.removeAll();
				pnlconfig1.add(markercombination);
				pnlconfig1.add(searchtypetext);
				pnlconfig2.removeAll();
				pnlsearchconfig.updateUI();
				break;
			case "Random":
				pnlconfig1.removeAll();
				pnlconfig1.add(jrbevaluations);
				pnlconfig1.add(searchtypetext);
				pnlconfig2.removeAll();
				pnlconfig2.add(jrbruntime);
				pnlconfig2.add(runtimetext);
				pnlconfig2.add(label1231);
				pnlconfig2.add(unit);
				pnlsearchconfig.updateUI();
				break;
			default:
				break;
			}
			return;
		}
		if(e.getSource()==jrbruntime || e.getSource()==jrbevaluations)
		{
			if(jrbevaluations.isSelected())
			{
				markercombination.setEnabled(true);
				searchtypetext.setEnabled(true);
				runtimetext.setEnabled(false);
				label1231.setEnabled(false);
				unit.setEnabled(false);
			}
			else
			{
				markercombination.setEnabled(false);
				searchtypetext.setEnabled(false);
				runtimetext.setEnabled(true);
				label1231.setEnabled(true);
				unit.setEnabled(true);
			}
			return;
		}
		
		if(e.getSource()==checkbox301)
		{
			if(checkbox301.isSelected())
			{
				label3010.setEnabled(true);
				textarea3010.setEnabled(true);
				button3010.setEnabled(true);
			}
			else
			{
				label3010.setEnabled(false);
				textarea3010.setEnabled(false);
				button3010.setEnabled(false);
			}
			return;
		}
		if(e.getSource()==radiobutton311)
		{
			returnString=new String("Unrelated subjects");
			return;
		}
		if(e.getSource()==radiobutton312)
		{
			returnString=new String("Non-founders");
			return;
		}
		if(e.getSource()==radiobutton313)
		{
			returnString=new String("Pooled samples of unrelated subjects and non-founders");
			return;
		}
	}

	public static void refresh() {
		initData221();

		table221=new ColumnSelectableJTable(data221,name221); 
		table221.setEnabled(false);
		scrollpane_table221=new JScrollPane(table221);
		table221.setPreferredScrollableViewportSize(new Dimension(720,100));
	    table221.setColumnSelectionAllowed(true);
	    table221.setRowSelectionAllowed(false);
	    pnlpheofile.removeAll();
		pnlpheofile.add(scrollpane_table221);
		removeresponse.setEnabled(true);
		addresponse.setEnabled(true);
		removepredictor.setEnabled(true);
		addpredictor.setEnabled(true);

	}


	private void resetForm() {
		dtmSummaryTable.setRowCount(0);
//		prgFilterProgress.setValue(unfiltered == null ? 0 : 1);
//		Isusescore.setSelected(false);
//		Isusescore.updateUI();
		clearTabs();
		tpnResults.setSelectedIndex(0);
	}
	
	
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource()==removeresponse)
		{
			int index=table241.getSelectedColumn();
			if(index==-1)
			{
				return;
			}
			else
			{
				index_table241.removeElementAt(index);
				name241=new Object[index_table241.size()];
				data241=new Object[num_of_data][index_table241.size()];
				for(int i=0;i<index_table241.size();i++)
				{
					name241[i]=name221[(int)index_table241.elementAt(i)];
					for(int j=0;j<num_of_data;j++)
					{
						data241[j][i]=data221[j][(int)index_table241.elementAt(i)];
					}
				}
				table241=new ColumnSelectableJTable(data241,name241);
				table241.setEnabled(false);
				scrollpane_table241=new JScrollPane(table241);
				table241.setColumnSelectionAllowed(true);
				table241.setRowSelectionAllowed(false);
				table241.setPreferredScrollableViewportSize(new Dimension(220,60));
				pnlresponse.removeAll();
				pnlresponse.add(scrollpane_table241);
				pnlresponse.updateUI();
			}
			return;
		}
		if(e.getSource()==addresponse)
		{
			int index=table221.getSelectedColumn();
			for(int i=0;i<index_table241.size();i++)
			{
				if(index==(int)index_table241.get(i))
				{
					return;
				}
			}
			for(int i=0;i<index_table242.size();i++)
			{
				if(index==(int)index_table242.get(i))
				{
					return;
				}
			}
			if(index==-1)
			{
				return;
			}
			else
			{
				index_table241.addElement(index);
				name241=new Object[index_table241.size()];
				data241=new Object[num_of_data][index_table241.size()];
				for(int i=0;i<index_table241.size();i++)
				{
					name241[i]=name221[(int)index_table241.elementAt(i)];
					for(int j=0;j<num_of_data;j++)
					{
						data241[j][i]=data221[j][(int)index_table241.elementAt(i)];
					}
				}
				table241=new ColumnSelectableJTable(data241,name241);
				table241.setEnabled(false);
				table241.setColumnSelectionAllowed(true);
				table241.setRowSelectionAllowed(false);
				scrollpane_table241=new JScrollPane(table241);
				table241.setPreferredScrollableViewportSize(new Dimension(220,60));
				pnlresponse.removeAll();
				pnlresponse.add(scrollpane_table241);
				pnlresponse.updateUI();

			}
			return;
		}
		if(e.getSource()==removepredictor)
		{
			int index=table242.getSelectedColumn();
			if(index==-1)
			{
				return;
			}
			else
			{
				index_table242.removeElementAt(index);
				name242=new Object[index_table242.size()];
				data242=new Object[num_of_data][index_table242.size()];
				for(int i=0;i<index_table242.size();i++)
				{
					name242[i]=name221[(int)index_table242.elementAt(i)];
					for(int j=0;j<num_of_data;j++)
					{
						data242[j][i]=data221[j][(int)index_table242.elementAt(i)];
					}
				}
				table242=new ColumnSelectableJTable(data242,name242);table242.setEnabled(false);
				scrollpane_table242=new JScrollPane(table242);
				table242.setPreferredScrollableViewportSize(new Dimension(220,60));
				table242.setColumnSelectionAllowed(true);
				table242.setRowSelectionAllowed(false);
				pnlpredictor.removeAll();
				pnlpredictor.add(scrollpane_table242);
				pnlpredictor.updateUI();
			}
			return;
		}
		if(e.getSource()==addpredictor)
		{
			int index=table221.getSelectedColumn();
			for(int i=0;i<index_table241.size();i++)
			{
				if(index==(int)index_table241.get(i))
				{
					return;
				}
			}
			for(int i=0;i<index_table242.size();i++)
			{
				//if(index==(int)index_table241.get(i) || index==(int)index_table242.get(i))
				if( index==(int)index_table242.get(i)) //remove the incident when input predictor column first
				{
					return;
				}
			}
			if(index==-1)
			{
				return;
			}
			else
			{
				index_table242.addElement(index);
				name242=new Object[index_table242.size()];
				data242=new Object[num_of_data][index_table242.size()];
				for(int i=0;i<index_table242.size();i++)
				{
					name242[i]=name221[(int)index_table242.elementAt(i)];
					for(int j=0;j<num_of_data;j++)
					{
						data242[j][i]=data221[j][(int)index_table242.elementAt(i)];
					}
				}
				table242=new ColumnSelectableJTable(data242,name242);table242.setEnabled(false);
				scrollpane_table242=new JScrollPane(table242);
				table242.setPreferredScrollableViewportSize(new Dimension(220,60));
				table242.setColumnSelectionAllowed(true);
				table242.setRowSelectionAllowed(false);
				pnlpredictor.removeAll();
				pnlpredictor.add(scrollpane_table242);
				pnlpredictor.updateUI();
			}
			return;
		}

		
	}


	public static void initData221()
	{
		try
		{
			BufferedReader read_number = new BufferedReader(new FileReader(phenofile));
			String temp_string=new String(read_number.readLine());
			names221(temp_string);
			datas221();
			
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			try {
				GUIMDR.myUI.doc.insertString(GUIMDR.myUI.doc.getLength(),Main.dateFormat.format(Main.date.getTime())+"\tUnable to read File '" + e.getMessage() + "'.\n", GUIMDR.myUI.keyWordfailed);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public static void names221(String temp_string)
	{
        String tmp1=temp_string.replace(" ","\t");
        String[] tmp2=tmp1.split("\t");
        int i=0;
		for (int j = 0; j < tmp2.length; j++) 
			{
			   
			    	if (!tmp2[j].equals("")) 
					{
						i++;
					}
					
				
			}
		name221=new Object[i];
		int k=0;
		for (int j = 0; j < tmp2.length; j++) 
			{
			    if (k<name221.length) {
			    	if (!tmp2[j].equals("")) 
					{
						name221[k++]=tmp2[j];
					}
					
				}
				else {
					break;
				}
			}
/*		int number=1;
		boolean isNULL=true;
		for(int i=0;i<temp_string.length();i++)
		{
			if(temp_string.charAt(i)==32 | temp_string.charAt(i)==9)
			{
				if(isNULL==false)
				{
					number++;
					isNULL=true;
				}
			}
			else
			{
				isNULL=false;
			}
		}
		
		isNULL=true;
		name221=new Object[number];
		for(int i=0;i<name221.length;i++)
		{
			name221[i]="";
		}
		
		int now_number=0;
		for(int i=0;i<temp_string.length();i++)
		{
			
			if(temp_string.charAt(i)==32 | temp_string.charAt(i)==9)
			{
				if(isNULL==false)
				{
					now_number++;
					isNULL=true;
				}
			}
			else
			{
				name221[now_number]=name221[now_number].toString()+temp_string.charAt(i);
				isNULL=false;
			}
		}
		*/
	}
	public static void datas221()
	{
		
		BufferedReader read_number;
		int number=0;
		try {
			read_number = new BufferedReader(new FileReader(phenofile.getAbsolutePath()));
			String temp=read_number.readLine();
			temp=read_number.readLine();
			while(temp!=null)
			{
				number++;
				temp=read_number.readLine();
			}
		} catch (IOException e) {
			// TODO 锟皆讹拷锟斤拷锟缴碉拷 catch 锟斤拷
			e.printStackTrace();
		}
		
		
		data221=new Object[number][name221.length];
		num_of_data=number;
		
/*		BufferedReader read_data;
		try {
			read_data = new BufferedReader(new FileReader(file21.getAbsolutePath()));
			read_data.readLine();
			for(int i=0;i<number;i++)
			{
				String tmp1=read_data.readLine().replace(" ", "\t");
				String tmp2=tmp1.replace("\t\t", newChar)
				data221[i]=tmp1.split("\t");;
			}
		} catch (IOException e) {
			// TODO 锟皆讹拷锟斤拷锟缴碉拷 catch 锟斤拷
			e.printStackTrace();
		}
		
		
	*/
		try
		{
		/*	BufferedReader read_number = new BufferedReader(new FileReader(file21.getAbsolutePath()));
			int number=0;
			String temp=read_number.readLine();
			temp=read_number.readLine();
			while(temp!=null)
			{
				number++;
				temp=read_number.readLine();
			}
			data221=new Object[number][name221.length];
			num_of_data=number;
			
			*/
			BufferedReader read_data = new BufferedReader(new FileReader(phenofile.getAbsolutePath()));
			
			read_data.readLine();
			for(int i=0;i<number;i++)
			{
				
					data221[i]=returnfix(read_data);
				
			}
		}
		catch(IOException e)
		{
			
		}
		
	}
	public static String[] returnfix(BufferedReader rd)
	{
		int temp=0;
		String temp_string=new String("");
		String[] dataline=new String[name221.length];
		try {
			String tmp1=rd.readLine().replace(" ", "\t");
			String[] tmp2=tmp1.split("\t");
			int i=0;
			for (int j = 0; j < tmp2.length; j++) 
				{
				    if (i<dataline.length) {
				    	if (!tmp2[j].equals("")) 
						{
							dataline[i++]=tmp2[j];
						}
						
					}
					else {
						break;
					}
				}
			
			
		} catch (IOException e1) {
			// TODO 锟皆讹拷锟斤拷锟缴碉拷 catch 锟斤拷
			e1.printStackTrace();
		}
	/*	try
		{
			temp=rd.read();
			if(temp==32 | temp==9 |temp==10 |temp==-1)
			{
				while(temp==32 | temp==9 |temp==10 |temp==-1)
				{
					temp=rd.read();
				}
			}
			
			while(temp!=32 & temp!=9 & temp!=10 & temp!=-1 )
			{
					
					temp_string+=(char)temp;
					temp=rd.read();
			}
			
			
			while(temp==32 | temp==9 |temp==10 |temp==-1)
			{
				temp=rd.read();
			}
			
		
		}
		catch(IOException e)
		{
			
		}
		*/
		return dataline;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO 锟皆讹拷锟斤拷锟缴的凤拷锟斤拷锟斤拷锟�
		
	}
}



