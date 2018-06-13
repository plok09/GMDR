package org.epistasis;

import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import GUI.GUIMDR;

public class FileSaver {
	private static File fSaveFolder = new File("");

	public static File getSaveFolder() {
		return fSaveFolder;
	}

	public static void setSaveFolder(File fSaveFolder) {
		FileSaver.fSaveFolder = fSaveFolder;
	}

	/**
	 * Open a PrintWriter to a file. This function opens a file chooser dialog
	 * to ask the user the filename to use.
	 * 
	 * @param title
	 *            The title for the dialog
	 * @return PrintWriter to a file, or null if user cancelled
	 */
	public static PrintWriter openFileWriter(Component parent, String title) {
		JFileChooser fc = new JFileChooser();
		File f = null;
		boolean overwrite = false;

		// configure the file chooser
		fc.setDialogTitle(title);
		fc.setMultiSelectionEnabled(false);
		fc.setCurrentDirectory(fSaveFolder);

		// keep asking until we get a satisfactory answer, which could be
		// one of: cancel, write a new file, or a confirmed overwrite
		while (f == null || (f.exists() && !overwrite)) {
			// ask for which file to write, and if the user cancels,
			// we're done
			if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
				return null;
			}

			// get the user's selection
			f = fc.getSelectedFile();

			// if the file the user chose exists, ask if the file should
			// be overwritten
			if (f.exists()) {
				switch (JOptionPane.showConfirmDialog(parent, "File '" + f.toString() + "' exists.  Overwrite?", title, JOptionPane.YES_NO_CANCEL_OPTION)) {
				// user chose yes, so write the file
				case JOptionPane.YES_OPTION:
					overwrite = true;
					break;

				// user chose no, so ask again what file to write
				case JOptionPane.NO_OPTION:
					overwrite = false;
					break;

				// user chose cancel, so we're done
				case JOptionPane.CANCEL_OPTION:
					return null;
				}
			}
		}

		// keep track of where to save these files
		fSaveFolder = f.getParentFile();

		// open the file, and display any errors
		try {
			return new PrintWriter(new FileWriter(f));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(parent, e.getMessage(), "I/O Error", JOptionPane.ERROR_MESSAGE);
		}

		return null;
	}
}
