public class ByteArray {

    public void ByteArray() {

    }

    public void printByteMatrix(byte[][] byteMatrix) {
	for (int i = 0; i < byteMatrix.length; i++) {
	    for (int j = 0; j < byteMatrix[i].length; j++) {
		System.out.printf("%02X ", byteMatrix[i][j]);
	    }
	    System.out.printf("\n");
	}
	System.out.printf("\n");
    }

    public static byte[][] charToByte(char[] input, int rows, int columns) {
	byte[][] output = new byte[rows][columns];
	int i, j, k;

	k = 0;
	// convet 1d byte array to 16x16 byte array
	for (i = 0; i < output.length; i++) {
	    for (j = 0; j < output[i].length; j++) {
		output[i][j] = (byte) input[k++];
	    }
	}

	return output;
    }

    // verifyHexLine returns true if line is valid for further formatting
    public static boolean verifyHexLine(String line) {
	// regex specifying all character outside of  appropriate hexademical characters
	Pattern p = Pattern.compile("[^a-fA-F0-9]");

	// Check that all characters are alphanumeric
	// if pattern matcher finds valid character, then input has invalid characters
	// because it is checking for all character outside of appropriate range
	if (p.matcher(line).find()) {
	    return false;
	}
	return true;
    }

}
