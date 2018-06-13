package org.epistasis.combinatoric.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.epistasis.FileSaver;
import org.epistasis.StringDoublePair;
import org.epistasis.StringObjectPair;
import org.epistasis.gui.AbstractChart;
import org.epistasis.gui.SelectionEvent;
import org.epistasis.gui.SelectionListener;

public class LandscapePanel extends JComponent implements ItemListener, SelectionListener, ActionListener, ChangeListener {
	public LandscapePanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		this.setLayout(bolThis);
		pnlDisplay.setLayout(crdDisplay);
		cmdSave.setText("Save");
		cmdSave.addActionListener(this);
		cmdUnzoom.addActionListener(this);
		cmdResetView.addActionListener(this);
		cmdUnzoom.setText("Unzoom");
		cmdResetView.setText("Reset View");
		pnlDisplayControls.setLayout(crdDisplayControls);
		pnlControls.setLayout(gblControls);
		pnlHistogramControls.setLayout(gblHistogramControls);
		lblBins.setText("Bins:");
		spnBins.setMinimumSize(new Dimension(60, 20));
		spnBins.setPreferredSize(new Dimension(60, 20));
		spnBins.addChangeListener(this);
		spnBins.setModel(new SpinnerNumberModel(new Integer(10), new Integer(1), null, new Integer(1)));
		pnlZoomControls.setLayout(crdZoomControls);
		pnlZoomable.setLayout(gblZoomable);
		scpLineChart.setBorder(BorderFactory.createLoweredBevelBorder());
		scpHistogram.setBorder(BorderFactory.createLoweredBevelBorder());
		scpRaw.setBorder(BorderFactory.createLoweredBevelBorder());
		cboDisplayType.addItemListener(this);
		txaRaw.setEditable(false);
		pnlHistogram.setYAxisLabel("Count");
		this.add(pnlControls, java.awt.BorderLayout.SOUTH);
		this.add(pnlDisplay, java.awt.BorderLayout.CENTER);

		pnlDisplay.add(scpLineChart, "Line Chart");
		pnlDisplay.add(scpHistogram, "Histogram");
		pnlDisplay.add(scpRaw, "Raw Text");

		scpLineChart.getViewport().add(pnlLineChart);
		scpHistogram.getViewport().add(pnlHistogram);
		scpRaw.getViewport().add(txaRaw);

		pnlLineChart.addSelectionListener(this);
		pnlHistogram.addSelectionListener(this);

		cboDisplayType.addItem(new StringObjectPair("Line Chart", scpLineChart));
		cboDisplayType.addItem(new StringObjectPair("Histogram", scpHistogram));
		cboDisplayType.addItem(new StringObjectPair("Raw Text", scpRaw));

		pnlControls.add(pnlZoomControls, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		pnlControls.add(cboDisplayType, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5,
				5, 0), 0, 0));
		pnlControls
				.add(cmdSave, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		pnlControls.add(pnlDisplayControls, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0,
				0, 0), 0, 0));

		pnlZoomControls.add(pnlZoomable, "Zoomable");
		pnlZoomControls.add(pnlUnzoomable, "Unzoomable");

		pnlZoomable.add(cmdUnzoom, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0,
				0));
		pnlZoomable.add(cmdResetView, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 0),
				0, 0));
		pnlHistogramControls.add(lblBins, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5,
				0), 0, 0));
		pnlHistogramControls.add(spnBins, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));

		pnlDisplayControls.add(pnlLineChartControls, "Line Chart");
		pnlDisplayControls.add(pnlHistogramControls, "Histogram");
		pnlDisplayControls.add(pnlRawControls, "Raw Text");
	}

	public void setFont(Font font) {
		super.setFont(font);

		if (cboDisplayType == null) {
			return;
		}

		cboDisplayType.setFont(font);
		cmdSave.setFont(font);
		cmdUnzoom.setFont(font);
		cmdResetView.setFont(font);
		lblBins.setFont(font);
	}

	public Font getTextFont() {
		return txaRaw.getFont();
	}

	public void setTextFont(Font font) {
		txaRaw.setFont(font);
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		if (cboDisplayType == null) {
			return;
		}

		checkEnabled(enabled);
	}

	public String getXAxisLabel() {
		return pnlLineChart.getXAxisLabel();
	}

	public void setXAxisLabel(String label) {
		pnlLineChart.setXAxisLabel(label);
	}

	public String getYAxisLabel() {
		return pnlLineChart.getYAxisLabel();
	}

	public void setYAxisLabel(String label) {
		pnlLineChart.setYAxisLabel(label);
		pnlHistogram.setXAxisLabel(label);
	}

	private void checkEnabled(boolean enabled) {
		cboDisplayType.setEnabled(enabled);
		cmdSave.setEnabled(enabled);
		lblBins.setEnabled(enabled);
		spnBins.setEnabled(enabled);
		pnlLineChart.setSelectionEnabled(enabled);
		pnlHistogram.setSelectionEnabled(enabled);

		StringObjectPair p = (StringObjectPair) cboDisplayType.getSelectedItem();

		if (enabled) {
			boolean zoomed = false;

			if (p.getObject() == scpLineChart) {
				zoomed = !stkLineChartZoom.empty();
			} else if (p.getObject() == scpHistogram) {
				zoomed = !stkHistogramZoom.empty();
			}

			cmdUnzoom.setEnabled(zoomed);
			cmdResetView.setEnabled(zoomed);
		} else {
			cmdUnzoom.setEnabled(false);
			cmdResetView.setEnabled(false);
		}
	}

	public void itemStateChanged(ItemEvent e) {
		StringObjectPair p = (StringObjectPair) e.getItem();

		crdDisplay.show(pnlDisplay, p.toString());
		crdDisplayControls.show(pnlDisplayControls, p.toString());

		if (p.getObject() == scpLineChart) {
			crdZoomControls.show(pnlZoomControls, "Zoomable");
		} else if (p.getObject() == scpHistogram) {
			crdZoomControls.show(pnlZoomControls, "Zoomable");
		} else if (p.getObject() == scpRaw) {
			crdZoomControls.show(pnlZoomControls, "Unzoomable");
		}

		checkEnabled(isEnabled());
	}

	public void selectionChanged(SelectionEvent e) {
		AbstractChart chart = (AbstractChart) e.getSource();
		Rectangle2D current = e.getViewport();

		if (chart == pnlLineChart) {
			stkLineChartZoom.push(current);
		} else if (chart == pnlHistogram) {
			stkHistogramZoom.push(current);
		}

		checkEnabled(isEnabled());
	}

	public void actionPerformed(ActionEvent e) {
		StringObjectPair p = (StringObjectPair) cboDisplayType.getSelectedItem();
		AbstractChart chart = null;
		Dimension chartSize = null;
		Stack stack = null;
		String saveTitle = "Save Landscape Raw Text";

		if (p.getObject() == scpLineChart) {
			stack = stkLineChartZoom;
			chart = pnlLineChart;
			chartSize = new Dimension(1200, 400);
			saveTitle = "Save Landscape Line Chart";
		} else if (p.getObject() == scpHistogram) {
			stack = stkHistogramZoom;
			chart = pnlHistogram;
			chartSize = new Dimension(1000, 1000);
			saveTitle = "Save Landscape Histogram";
		}

		if (e.getSource() == cmdUnzoom) {
			Rectangle2D viewport = (Rectangle2D) stack.pop();
			chart.setViewport(viewport);
			checkEnabled(isEnabled());
		} else if (e.getSource() == cmdResetView) {
			chart.setViewport((Rectangle2D) stack.firstElement());
			stack.clear();
			checkEnabled(isEnabled());
		} else if (e.getSource() == cmdSave) {
			PrintWriter out = FileSaver.openFileWriter(getTopLevelAncestor(), saveTitle);

			if (out == null) {
				return;
			}

			if (chart == null) {
				out.print(txaRaw.getText());
			} else {
				out.print(chart.getEpsText(chartSize.width, chartSize.height));
			}

			out.flush();
			out.close();
		} else {
			throw new IllegalArgumentException(e.getActionCommand());
		}
	}

	public void stateChanged(ChangeEvent e) {
		if (landscapeMin != null && landscapeMax != null) {
			pnlHistogram.setBins(binLandscape(((Number) spnBins.getValue()).intValue()), landscapeMin.doubleValue(), landscapeMax.doubleValue());
		} else {
			pnlHistogram.setBins(binLandscape(((Number) spnBins.getValue()).intValue()));
		}
	}

	private void setLandscapeCommon(List landscape) {
		this.landscape = landscape;

		if (!stkLineChartZoom.empty()) {
			pnlLineChart.setViewport((Rectangle2D) stkLineChartZoom.get(0));
			stkLineChartZoom.clear();
		}

		if (landscape == null) {
			landscapeMin = landscapeMax = null;
		} else {
			Comparator cmp = new StringDoublePair.DoubleComparator();

			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;

			for (Iterator i = landscape.iterator(); i.hasNext();) {
				double d = ((StringDoublePair) i.next()).getDouble();

				if (Double.isInfinite(d)) {
					continue;
				}

				if (d < min) {
					min = d;
				}
				if (d > max) {
					max = d;
				}
			}

			landscapeMin = new Double(min - max * 0.05);
			landscapeMax = new Double(max * 1.05);
		}

		if (!stkHistogramZoom.empty()) {
			pnlHistogram.setViewport((Rectangle2D) stkHistogramZoom.get(0));
			stkHistogramZoom.clear();
		}

		if (useMinMax && landscapeMin != null && landscapeMax != null) {
			pnlHistogram.setBins(binLandscape(((Number) spnBins.getValue()).intValue()), landscapeMin.doubleValue(), landscapeMax.doubleValue());
		} else {
			pnlHistogram.setBins(binLandscape(((Number) spnBins.getValue()).intValue()));
		}

		cboDisplayType.setSelectedIndex(0);

		if (landscape == null) {
			txaRaw.setText("");
		} else {
			List entries = new ArrayList(landscape);
			StringBuffer b = new StringBuffer();

			if (rawTextComparator != null) {
				Collections.sort(entries, rawTextComparator);
			}

			for (Iterator i = entries.iterator(); i.hasNext();) {
				StringDoublePair p = (StringDoublePair) i.next();

				b.append(p);
				b.append('\t');
				b.append(p.getDouble());

				if (i.hasNext()) {
					b.append('\n');
				}
			}

			txaRaw.setText(b.toString());
			txaRaw.select(0, 0);
		}
	}

	public void setLandscape(List landscape) {
		setLandscape(landscape, (Comparator) null);
	}

	public void setLandscape(List landscape, Comparator rawTextComparator) {
		this.rawTextComparator = rawTextComparator;
		useMinMax = true;
		setLandscapeCommon(landscape);

		if (landscapeMin != null && landscapeMax != null) {
			pnlLineChart.setViewport(new Rectangle2D.Double(0, landscapeMin.doubleValue(), 1, landscapeMax.doubleValue() - landscapeMin.doubleValue()));
		}

		pnlLineChart.setLandscape(landscape);
	}

	public void setLandscape(List landscape, Map xLabels) {
		setLandscape(landscape, xLabels, null);
	}

	public void setLandscape(List landscape, Map xLabels, Comparator rawTextComparator) {
		setLandscape(landscape, xLabels, true, rawTextComparator);
	}

	public void setLandscape(List landscape, Map xLabels, boolean scaleToFit) {
		setLandscape(landscape, xLabels, scaleToFit, null);
	}

	public void setLandscape(List landscape, Map xLabels, boolean scaleToFit, Comparator rawTextComparator) {
		this.rawTextComparator = rawTextComparator;
		useMinMax = scaleToFit;
		setLandscapeCommon(landscape);

		if (scaleToFit && landscapeMin != null && landscapeMax != null) {
			pnlLineChart.setViewport(new Rectangle2D.Double(0, landscapeMin.doubleValue(), 1, landscapeMax.doubleValue() - landscapeMin.doubleValue()));
		}

		pnlLineChart.setLandscape(landscape, xLabels);
	}

	private long[] binLandscape(int nBins) {
		if (landscape == null) {
			return null;
		}

		long[] bins = new long[nBins];

		for (Iterator i = landscape.iterator(); i.hasNext();) {
			double fitness = ((StringDoublePair) i.next()).getDouble();

			if (Double.isInfinite(fitness)) {
				continue;
			}

			if (useMinMax && landscapeMin != null && landscapeMax != null) {
				fitness = (fitness - landscapeMin.doubleValue()) / (landscapeMax.doubleValue() - landscapeMin.doubleValue());
			}

			if (fitness >= 1.0) {
				bins[bins.length - 1]++;
			} else {
				bins[(int) (fitness * nBins)]++;
			}
		}

		return bins;
	}

	private boolean useMinMax;
	private List landscape = null;
	private Double landscapeMin = null;
	private Double landscapeMax = null;
	private Comparator rawTextComparator;
	private BorderLayout bolThis = new BorderLayout();
	private JPanel pnlControls = new JPanel();
	private JPanel pnlDisplay = new JPanel();
	private CardLayout crdDisplay = new CardLayout();
	private JScrollPane scpLineChart = new JScrollPane();
	private JScrollPane scpHistogram = new JScrollPane();
	private JScrollPane scpRaw = new JScrollPane();
	private JComboBox cboDisplayType = new JComboBox();
	private JPanel pnlDisplayControls = new JPanel();
	private JButton cmdSave = new JButton();
	private JButton cmdUnzoom = new JButton();
	private JButton cmdResetView = new JButton();
	private JPanel pnlRawControls = new JPanel();
	private JPanel pnlHistogramControls = new JPanel();
	private JPanel pnlLineChartControls = new JPanel();
	private CardLayout crdDisplayControls = new CardLayout();
	private GridBagLayout gblControls = new GridBagLayout();
	private GridBagLayout gblHistogramControls = new GridBagLayout();
	private JLabel lblBins = new JLabel();
	private JSpinner spnBins = new JSpinner();
	private JPanel pnlZoomControls = new JPanel();
	private JPanel pnlZoomable = new JPanel();
	private JPanel pnlUnzoomable = new JPanel();
	private CardLayout crdZoomControls = new CardLayout();
	private GridBagLayout gblZoomable = new GridBagLayout();
	private JTextArea txaRaw = new JTextArea();
	private LandscapeHistogram pnlHistogram = new LandscapeHistogram();
	private LandscapeLineChart pnlLineChart = new LandscapeLineChart();
	private Stack stkLineChartZoom = new Stack();
	private Stack stkHistogramZoom = new Stack();
}
