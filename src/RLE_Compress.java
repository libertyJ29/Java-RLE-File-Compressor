import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

// Compress the opened file with RLE Encoding

public class RLE_Compress {

	private static int numBytesProcessed = 0; // counter of number of bytes processed in the input file
	private static String readDec = ""; // current read in decimal value left padded
	private static int readInt = 0; // current read in value as an int
	private static String readDecHex = "";
	private static String previousDec = ""; // the previous read in decimal value
	private static int runLength = 0;
	private static String runLengthHex = ""; // written to the output file for each rle run

	private static int minRLELength = 5; // minimum run length needed to encode

	// Constructor
	public RLE_Compress(String filename, long filelength, String outputFilename) {
		main(filename, filelength, outputFilename);
	}

	public static void main(String filename, long filelength, String outputFilename) {

		System.out.println("Size of input file=" + filelength);

		System.out.println("RLE Compressor Start");

		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		try {
			is = new FileInputStream(filename);
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);

			// create read buffer
			byte[] buffer = new byte[1];

			// read stream data into buffer
			while (is.read(buffer) != -1) {

				// for each byte in the buffer as a decimal value
				for (byte newByte : buffer) {

					readInt = newByte;

					// left pad the read decimal value
					readDec = String.format("%02d", newByte);

					// print out the byte
					System.out.println("Next byte=" + readDec);
				}

				rle_process(outputFilename, filelength);
			}

			writeEOFByte(filelength, outputFilename);

			showEncodingResultinNewDialog(filelength, outputFilename);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// release resources associated with the streams
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	// main RLE processing for the current byte
	public static void rle_process(String outputFilename, long filelength) {

		if (!(readDec.matches(previousDec))) {

			// Write runLength if its greater than 5, or else write the original bytes
			// This first writes rle id byte fd0e/0f, then the runLength value
			if (runLength >= minRLELength) {

				String rleid = "fd";
				String rleid1 = "0e"; // rle id bytes for when run of 5 or more bytes occur, with length LESS than ffh
				String rleid2 = "0f"; // rle id bytes for when run of 5 or more bytes occur, with length MORE than ffh

				// convert decimal runLength to hex
				runLengthHex = Integer.toHexString(0x100 | runLength).substring(1);

				if (runLength <= 255) { // if less than 255d / ffh ie 1 byte runLength, use this rleid
					// write the number of times the previous character ran for
					int[] outputArray3 = new int[3];
					outputArray3[0] = Integer.parseInt(rleid, 16);
					outputArray3[1] = Integer.parseInt(rleid1, 16);
					outputArray3[2] = Integer.parseInt("" + runLengthHex, 16);
					writeToOutputFileArray(outputArray3, outputFilename);
				} else { // for 2 byte hex runlength use this id
					int[] outputArray3 = new int[3];
					outputArray3[0] = Integer.parseInt(rleid, 16);
					outputArray3[1] = Integer.parseInt(rleid2, 16);
					outputArray3[2] = Integer.parseInt("" + runLengthHex, 16);
					writeToOutputFileArray(outputArray3, outputFilename);
				}
				System.out.println("Runlength=" + runLength);
			}

			if ((runLength < minRLELength) && (runLength >= 1)) {
				// if run is less than 5, just write the original bytes, no rle.
				// Loop for runlength times.

				int runLengthWriteCounter = 1;

				while (runLengthWriteCounter != runLength) {
					// write the byte for the length of this small run
					writeToOutputFile(readDecHex, outputFilename);
					runLengthWriteCounter++;
				}
				System.out.println("Run is too small, byte=" + readDecHex + ", runlength=" + runLength);
				runLength = 0; // reset run length
			}

			System.out.println("END OF RUN");

			// write the next byte that has been read
			if (numBytesProcessed != filelength) {
				readDecHex = Integer.toHexString(0x100 | readInt).substring(1);

				// write the next read hex byte to output
				writeToOutputFile(readDecHex, outputFilename);
				System.out.println("Current byte=" + readDec);
			}

			runLength = 0; // reset run length
			previousDec = readDec;
		}

		// if readDec == previousDec, inc runLength & numBytesProcessed & read next byte
		runLength++;
		numBytesProcessed++;
	}

	// write the last read byte and the EOF id bytes to the output file
	public static void writeEOFByte(long filelength, String outputFilename) {

		// invalidate the current string so that it will now write the last byte or run
		// of bytes read from the file
		readInt++;
		// left pad the read decimal value
		readDec = String.format("%02d", readInt);
		rle_process(outputFilename, filelength);

		// write an end of file id byte FFFE, which decompression routine can
		// check for end of compressed data

		int[] outputArray2 = new int[2];
		outputArray2[0] = Integer.parseInt("ff", 16);
		outputArray2[1] = Integer.parseInt("fe", 16);
		writeToOutputFileArray(outputArray2, outputFilename);

		System.out.println("##EOF##\n");
	}

	// display information about the RLE Encoding in a new dialog window
	public static void showEncodingResultinNewDialog(long filelength, String outputFilename) {

		// calculate rle encoding result %
		long outputfilelength = 0;
		File file = new File(outputFilename);
		if (file.exists()) {
			outputfilelength = file.length();
		}
		System.out.println("Original String Length " + filelength);
		System.out.println("RLE Encoded Size " + outputfilelength);

		float percent = (float) 100.0 - (outputfilelength * (float) 100.0) / filelength;
		String percent_2dp = String.format(java.util.Locale.US, "%.2f", percent);
		System.out.println("Compression : " + percent_2dp + "%");

		// display the rle encoding results in a dialog window
		if (numBytesProcessed > 0) {
			RLE_Results_Dialog rleresultsdialog = new RLE_Results_Dialog(filelength, outputfilelength, percent_2dp);
		}
	}

	// write the passed in byte to the output file
	public static void writeToOutputFile(String str, String outputFilename) {
		try {
			FileOutputStream outputStream = new FileOutputStream(outputFilename, true);

			int intByteDecimal = Integer.parseInt("" + str, 16);

			outputStream.write(intByteDecimal);

			outputStream.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	// write the passed in array of bytes to the output file
	public static void writeToOutputFileArray(int[] outputArray, String outputFilename) {
		try {
			FileOutputStream outputStream = new FileOutputStream(outputFilename, true);

			for (int i = 0; i < outputArray.length; i++) {
				outputStream.write(outputArray[i]);
			}

			outputStream.close();

		} catch (

		Exception ex) {
			ex.printStackTrace();
		}

	}

}
