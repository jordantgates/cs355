package cs355;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * A collection of static functions for reading/writing images and drawings.
 * 
 * @author talonos
 */
public class FileChooser {

	// Static strings for approve buttons.
	private static final String OPEN = "Open";
	private static final String SAVE = "Save";

	// We need a file chooser.
	private static final JFileChooser fc = new JFileChooser(".");

	// Different types of filters.
	private static final ImageFilter imgFilter = new ImageFilter();
	private static final JsonFilter jsonFilter = new JsonFilter();
	private static final SceneFilter scnFilter = new SceneFilter();

	/**
	 * Gets the image file that the user wants to open.
	 * 
	 * @param open
	 *            = true if we're opening the file, false for save.
	 * @return the file that the user wants to open.
	 */
	public static File getImageFile(boolean open) {
		return getFile(imgFilter, open ? OPEN : SAVE);
	}

	/**
	 * Gets the drawing file that the user wants to open.
	 * 
	 * @param open
	 *            true if we're opening the file, false for save.
	 * @return the file that the user wants to open.
	 */
	public static File getJsonFile(boolean open) {
		return getFile(jsonFilter, open ? OPEN : SAVE);
	}

	/**
	 * Gets the scene file that the user wants to open.
	 * 
	 * @return the file that the user wants to open.
	 */
	public static File getSceneFile() {
		return getFile(scnFilter, OPEN);
	}

	/**
	 * Puts the common code into one place.
	 * 
	 * @param filter
	 *            the FileFilter to use.
	 * @param message
	 *            the message to put on the approve button.
	 * @return the file that the user selected.
	 */
	private static File getFile(FileFilter filter, String message) {

		// Set the filter.
		fc.setFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(true);

		// Show the dialog.
		int val = fc.showDialog(CS355Frame.inst(), message);

		// Make sure the user didn't cancel.
		if (val != JFileChooser.APPROVE_OPTION) {
			return null;
		}

		// Return the selected file.
		return fc.getSelectedFile();
	}
}
