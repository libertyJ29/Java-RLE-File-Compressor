import javax.swing.JOptionPane;

// Display Swing dialog window after the RLE encoding is complete

public class RLE_Results_Dialog {

	// Constructor
	public RLE_Results_Dialog(long filelength_, long outputfilelength_, String percent_2dp_) {

		long filelength = filelength_;
		long outputfilelength = outputfilelength_;
		String percent_2dp = percent_2dp_;

		main(filelength, outputfilelength, percent_2dp);
	}

	// The entry method
	public static void main(long filelength, long outputfilelength, String percent_2dp) {

		JOptionPane.showMessageDialog(null,
				"Original Size : " + filelength + " bytes\nCompressed Size : " + outputfilelength
						+ " bytes\nCompression : " + percent_2dp + "%",
				"RLE File Compressor", JOptionPane.INFORMATION_MESSAGE);
	}

}
