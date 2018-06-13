package org.epistasis.gui;

import javax.swing.table.DefaultTableModel;

/**
 * Table model that removes the ability to edit cells.
 */
public class ReadOnlyTableModel extends DefaultTableModel {

	/**
	 * Overriden function that simply disallows all cell editing.
	 * 
	 * @param row
	 *            ignored
	 * @param col
	 *            ignored
	 * @return false
	 */
	public boolean isCellEditable(int row, int col) {
		return false;
	}

}
