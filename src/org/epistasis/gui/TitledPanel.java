package org.epistasis.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class TitledPanel extends JPanel {
	private TitledBorder tboTitle = new TitledBorder("");

	public TitledPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String getTitle() {
		return tboTitle.getTitle();
	}

	public void setTitle(String title) {
		tboTitle.setTitle(title);
	}

	public Font getTitleFont() {
		return tboTitle.getTitleFont();
	}

	public void setTitleFont(Font font) {
		tboTitle.setTitleFont(font);
	}

	public Border getTitleBorder() {
		return tboTitle.getBorder();
	}

	public void setTitleBorder(Border border) {
		tboTitle.setBorder(border);
	}

	public Color getTitleColor() {
		return tboTitle.getTitleColor();
	}

	public void setTitleColor(Color color) {
		tboTitle.setTitleColor(color);
	}

	public int getTitleJustification() {
		return tboTitle.getTitleJustification();
	}

	public void setTitleJustification(int titleJustification) {
		tboTitle.setTitleJustification(titleJustification);
	}

	public int getTitlePosition() {
		return tboTitle.getTitlePosition();
	}

	public void setTitlePosition(int titlePosition) {
		tboTitle.setTitlePosition(titlePosition);
	}

	private void jbInit() throws Exception {
		this.setBorder(tboTitle);
		tboTitle.setBorder(BorderFactory.createEtchedBorder());
	}
}
