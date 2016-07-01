import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

class AES {

    public static void main(String[] args) {
	boolean isEncryption;
	String keyFilename, inputFilename;
	byte[][] state, sbox;

	isEncryption = isEncryption(args);
	keyFilename = getKeyFilename(args);
	inputFilename = getInputFilename(args);

	state = inputToBytes(inputFilename);
	sbox = createSBox();
	if (sbox == null) {
	    System.out.printf("error creating sbox\n");
	    System.exit(1);
	}

	for (int i = 0; i < 14; i++) {
	    System.out.printf("----- State %d  -----\n", i);
	    state = subAllBytes(state, sbox);
	}
    }

    public static boolean isEncryption(String[] args) {
	// Get input file name
	if (args[0] == null) {
	    System.out.println("Invalid input.");
	    printInputError();
	} else if (args[0].equals("e")) {
	    return true;
	} else if (args[0].equals("d")) {
	    return false;
	} else printInputError();

	// unreachable
	return false;
    }

    public static String getKeyFilename(String[] args) {
	// Get input file name
	if (args[1] == null) {
	    System.out.println("Invalid input.");
	    printInputError();
	}

	return args[1];
    }

    public static String getInputFilename(String[] args) {
	// Get input file name
	if (args[2] == null) {
	    System.out.println("Invalid input.");
	    printInputError();
	}

	return args[2];
    }

    public static void printInputError() {
	System.out.printf("Correct input format:\n\tjava AES [d or e] [keyFile] [inputFile]");
	System.exit(1);
    }

    public static byte[][] inputToBytes(String inputFilename) {
	byte[][] inputMatrix;

	// read probabilities of each character, starting with 'A'
	try (BufferedReader br = new BufferedReader(new FileReader(inputFilename))) {
	    String line;

	    while ((line = br.readLine()) != null) {
		line = line.toUpperCase();
		// if not valid line, skip line
		if (!verifyInputLine(line)) {
		    continue; // skip line
		} else {
		    System.out.println("Return inputMatrix");
		    inputMatrix = formatInputMatrix(line); // get formatted line
		    return inputMatrix;
		}
	    }
	} catch (FileNotFoundException e) {
	    System.out.println("Could not find input file.");
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return null;
    }

    // verifyInputLine returns true if line is valid for further formatting
    public static boolean verifyInputLine(String line) {
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

    // check line for correct length, shrink or add padding if necessary
    public static byte[][] formatInputMatrix(String line) {
	int i, k;
	char[] initialChars;
	char[] finalChars = new char[32]; // 64 characters long, bc 1 line is 32 bytes = 64 hex chars
	byte[][] byteMatrix = new byte[4][4];
	byte bite;

	initialChars = line.toCharArray();

	if (initialChars.length != 32) {
	    // if more than 32 chars, cut down to 32 chars
	    if (initialChars.length > 32) {
		i = 0;

		while (i < 32) {
		    finalChars[i] = initialChars[i];
		    i++;
		}
	    }
	    // if less than 32 chars, add padding
	    else if (initialChars.length < 32) {
		int numChars = initialChars.length;
		i = 0;

		// copy first several characters
		while (i < numChars) {
		    finalChars[i] = initialChars[i];
		    i++;
		}

		// fill the rest with 0s
		while (i < finalChars.length) {
		    finalChars[i] = 0;
		    i++;
		}
	    }
	} else { // input was already in proper format, copy into final
	    i = 0;
	    while (i < finalChars.length) {
		finalChars[i] = initialChars[i];
		i++;
	    }
	}

	k = 0;

	for (i = 0; i < 4; i++) {
	    for (int j = 0; j < 4; j++) {
		bite = 0; // clear byte
		// add the first hex value to left-most 4 bits
		bite = (byte) (bite ^ (Character.digit(finalChars[k++], 16) << 4));
		// add second hex value to right-most 4 bits
		bite = (byte) (bite ^ Character.digit(finalChars[k++], 16));
		// assign newly calculated value in byte matrix
		byteMatrix[i][j] = bite;
	    }
	}

	System.out.println("The Plaintext is:");
	printByteMatrix(byteMatrix);

	return byteMatrix;
    }

    public static void printByteMatrix(byte[][] wordMatrix) {
	for (int i = 0; i < wordMatrix.length; i++) {
	    for (int j = 0; j < wordMatrix[i].length; j++) {
		System.out.printf("%02X ", wordMatrix[i][j]);
	    }
	    System.out.printf("\n");
	}
	System.out.printf("\n");
    }

    public static byte[][] createSBox() {
	int i, j, k;
	byte[][] lookupTable = new byte[16][16];
	byte bite;

	char[] sbox = {
	    0x63, 0x7C, 0x77, 0x7B, 0xF2, 0x6B, 0x6F, 0xC5, 0x30, 0x01, 0x67, 0x2B, 0xFE, 0xD7, 0xAB, 0x76,
	    0xCA, 0x82, 0xC9, 0x7D, 0xFA, 0x59, 0x47, 0xF0, 0xAD, 0xD4, 0xA2, 0xAF, 0x9C, 0xA4, 0x72, 0xC0,
	    0xB7, 0xFD, 0x93, 0x26, 0x36, 0x3F, 0xF7, 0xCC, 0x34, 0xA5, 0xE5, 0xF1, 0x71, 0xD8, 0x31, 0x15,
	    0x04, 0xC7, 0x23, 0xC3, 0x18, 0x96, 0x05, 0x9A, 0x07, 0x12, 0x80, 0xE2, 0xEB, 0x27, 0xB2, 0x75,
	    0x09, 0x83, 0x2C, 0x1A, 0x1B, 0x6E, 0x5A, 0xA0, 0x52, 0x3B, 0xD6, 0xB3, 0x29, 0xE3, 0x2F, 0x84,
	    0x53, 0xD1, 0x00, 0xED, 0x20, 0xFC, 0xB1, 0x5B, 0x6A, 0xCB, 0xBE, 0x39, 0x4A, 0x4C, 0x58, 0xCF,
	    0xD0, 0xEF, 0xAA, 0xFB, 0x43, 0x4D, 0x33, 0x85, 0x45, 0xF9, 0x02, 0x7F, 0x50, 0x3C, 0x9F, 0xA8,
	    0x51, 0xA3, 0x40, 0x8F, 0x92, 0x9D, 0x38, 0xF5, 0xBC, 0xB6, 0xDA, 0x21, 0x10, 0xFF, 0xF3, 0xD2,
	    0xCD, 0x0C, 0x13, 0xEC, 0x5F, 0x97, 0x44, 0x17, 0xC4, 0xA7, 0x7E, 0x3D, 0x64, 0x5D, 0x19, 0x73,
	    0x60, 0x81, 0x4F, 0xDC, 0x22, 0x2A, 0x90, 0x88, 0x46, 0xEE, 0xB8, 0x14, 0xDE, 0x5E, 0x0B, 0xDB,
	    0xE0, 0x32, 0x3A, 0x0A, 0x49, 0x06, 0x24, 0x5C, 0xC2, 0xD3, 0xAC, 0x62, 0x91, 0x95, 0xE4, 0x79,
	    0xE7, 0xC8, 0x37, 0x6D, 0x8D, 0xD5, 0x4E, 0xA9, 0x6C, 0x56, 0xF4, 0xEA, 0x65, 0x7A, 0xAE, 0x08,
	    0xBA, 0x78, 0x25, 0x2E, 0x1C, 0xA6, 0xB4, 0xC6, 0xE8, 0xDD, 0x74, 0x1F, 0x4B, 0xBD, 0x8B, 0x8A,
	    0x70, 0x3E, 0xB5, 0x66, 0x48, 0x03, 0xF6, 0x0E, 0x61, 0x35, 0x57, 0xB9, 0x86, 0xC1, 0x1D, 0x9E,
	    0xE1, 0xF8, 0x98, 0x11, 0x69, 0xD9, 0x8E, 0x94, 0x9B, 0x1E, 0x87, 0xE9, 0xCE, 0x55, 0x28, 0xDF,
	    0x8C, 0xA1, 0x89, 0x0D, 0xBF, 0xE6, 0x42, 0x68, 0x41, 0x99, 0x2D, 0x0F, 0xB0, 0x54, 0xBB, 0x16
	};

	k = 0;

	// convet 1d byte array to 16x16 byte array
	for (i = 0; i < lookupTable.length; i++) {
	    for (j = 0; j < lookupTable[i].length; j++) {
		lookupTable[i][j] = (byte) sbox[k++];
	    }
	}

	System.out.println("The lookup table/sbox is:");
	printByteMatrix(lookupTable);

	return lookupTable;
    }

    public static byte subByte(byte word, byte[][] sbox) {
	int x, y;

	x = (word & 0xF0) >>> 4;
	y = (word & 0x0F);

	return sbox[x][y];
    }

    public static byte[][] subAllBytes(byte[][] state, byte[][] sbox) {
	byte[][] newState = new byte[state.length][state[0].length];

	for (int i = 0; i < state.length; i++) {
	    for (int j = 0; j < state[i].length; j++) {
		newState[i][j] = subByte(state[i][j], sbox);
	    }
	}

	System.out.println("Old state:");
	printByteMatrix(state);

	System.out.println("New state:");
	printByteMatrix(newState);

	return newState;
    }
}

/*
Graveyard

		// convert each char to hex value & append to StringBuffer
		for (int i = 0; i < chars.length; i++) {
		    hexChars.append(Integer.toHexString((int) chars[i]));
		    System.out.printf(Integer.toHexString((int) chars[i]));
		}



	String[][] wordMatrix = new String[4][4];


	k = 0;

	// convert 1d 64 char array to 2d 32 String array, with each two chars concatenated
	for (i = 0; i < 4; i++) {
	    for (int j = 0; j < 4; j++) {
		wordMatrix[i][j] = Character.toString(finalChars[k++]) + Character.toString(finalChars[k++]);
	    }
	}

	System.out.println("The Plaintext is:");
	printStringMatrix(wordMatrix);
	System.out.printf("\n");

	// return formatted chars
	return wordMatrix;

    public static void printStringMatrix(String[][] wordMatrix) {
	for (int i = 0; i < wordMatrix.length; i++) {
	    for (int j = 0; j < wordMatrix[i].length; j++) {
		System.out.printf("%s ", wordMatrix[i][j]);
	    }
	    System.out.printf("\n");
	}
    }

		// bite = (byte) (bite ^ Character.digit(sbox[k++], 16));
		// System.out.printf("%c | %X ", sbox[k], (byte) Character.digit(sbox[k++], 16));

		// lookupTable[i][j] = (byte) Character.digit(sbox[k++], 16);

		// add second hex value to right-most 4 bits
		System.out.printf(Integer.toHexString((int) sbox[k]));
	// try {
	//     singleDByte = new String(sbox).getBytes("ASCII");
	//     k = 0;

	//     // convet 1d byte array to 16x16 byte array
	//     for (i = 0; i < lookupTable.length; i++) {
	// 	for (j = 0; j < lookupTable[i].length; j++) {
	// 	    bite = 0;

	// 	    lookupTable[i][j] = singleDByte[k++];
	// 	    System.out.printf("%X ", lookupTable[i][j]);
	// 	}
	//     }

	//     printByteMatrix(lookupTable);

	// } catch (UnsupportedEncodingException e) {
	//     e.printStackTrace();
	// }

	// k = 0;
	// while (k < sbox.length) {
	//     System.out.printf("%X", (byte) sbox[k]);
	//     k++;
	// }
		// System.out.printf("%X", (wordMatrix[i][j] >>> 4));
		// System.out.printf("%X ", (wordMatrix[i][j] & 0x0F));

		// if ((wordMatrix[i][j] & x0F) == 0) {
		//     System.out.printf("00 ");
		// } else System.out.printf("%X ", wordMatrix[i][j]);

	// byte[][] newState = Arrays.copyOf(state, state.length);

 */
