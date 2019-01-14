import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

// Display Swing dialog window for the user to select a file

public class FileChooser_Dialog extends JFrame {
	private static String fileToCompress;
	private static FileChooser_Dialog dialogWindow;

	private static String filename; // source file to compress
	private static long length = 0; // length of the source file
	private static String outputFilename; // output file

	// Constructor
	public FileChooser_Dialog() {

	}

	// Dialog window with 2 buttons allowing the user to use Filechooser to select
	// a file, and additionally to run the RLE_Compress Class on the selected file
	public static void file_chooser_gui() {

		int buttonPressed;

		// show the initial navigation window
		dialogWindow = new FileChooser_Dialog();
		String[] options = { "Select File to Compress", "Run Compressor" };
		buttonPressed = JOptionPane.showOptionDialog(dialogWindow,
				"File to compress : " + fileToCompress + "\nFile Size : " + length + " bytes\nOutput File : "
						+ outputFilename,
				"RLE File Compressor", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
				options[0]);

		// show the file chooser
		if (buttonPressed == 0) {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

			int returnValue = jfc.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = jfc.getSelectedFile();
				System.out.println(selectedFile.getAbsolutePath());

				// store the path of the selected file
				fileToCompress = selectedFile.getAbsoluteFile().getName();
				filename = selectedFile.getAbsolutePath();
				outputFilename = filename + ".RLE_Compressed";

				// get the length of the selected file, to display in the dialog
				try {
					File file = new File(filename);

					if (file.exists()) {
						length = file.length();
					}
				} finally {
				}
			}
			file_chooser_gui();
		}
		// run the rle compressor
		if (buttonPressed == 1) {
			// ensure a file has been selected by the user
			if (fileToCompress == null) {
				JOptionPane.showMessageDialog(null, "No File Selected!", "Error", JOptionPane.ERROR_MESSAGE);
				dialogWindow.dispose();// close the dialog window

				// load the main dialog window again
				file_chooser_gui();
			} else {
				dialogWindow.dispose(); // close the dialog window

				// delete the output file if it exists already
				try {
					File file = new File(outputFilename);

					if (file.exists()) {
						file.delete();
					}
				} finally {
				}

				// run the RLE_Compressor on the selected file
				RLE_Compress rle = new RLE_Compress(filename, length, outputFilename);
			}
		}
	}

	// The entry main() method
	public static void main(String[] args) {

		file_chooser_gui();
	}

}
