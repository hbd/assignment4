import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

class AES {
    public static void main(String[] args) {
	boolean isEncryption;
	String keyFilename, inputFilename;

	isEncryption = isEncryption(args);
	keyFilename = getKeyFilename(args);
	inputFilename = getInputFilename(args);

	inputToBytes(inputFilename);
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

    public static void inputToBytes(String inputFilename) {
	// read probabilities of each character, starting with 'A'
	try (BufferedReader br = new BufferedReader(new FileReader(inputFilename))) {
	    StringBuffer hexChars = new StringBuffer();
	    String line;
	    byte[][] inputMatrix;

	    while ((line = br.readLine()) != null) {
		line = line.toUpperCase();
		// if not valid line, skip line
		if (!verifyInputLine(line)) {
		    continue; // skip line
		} else inputMatrix = formatInputMatrix(line); // get formatted line
	    }
	} catch (FileNotFoundException e) {
	    System.out.println("Could not find input file.");
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return;
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
		bite = 0;
		bite = (byte) (bite ^ (Character.digit(finalChars[k++], 16) << 4));
		bite = (byte) (bite ^ Character.digit(finalChars[k++], 16));
		byteMatrix[i][j] = bite;
	    }
	}

	System.out.println("The Plaintext is:");
	printByteMatrix(byteMatrix);

	return byteMatrix;
    }

    public static void printStringMatrix(String[][] wordMatrix) {
	for (int i = 0; i < wordMatrix.length; i++) {
	    for (int j = 0; j < wordMatrix[i].length; j++) {
		System.out.printf("%s ", wordMatrix[i][j]);
	    }
	    System.out.printf("\n");
	}
    }

    public static void printByteMatrix(byte[][] wordMatrix) {
	for (int i = 0; i < wordMatrix.length; i++) {
	    for (int j = 0; j < wordMatrix[i].length; j++) {
		if (wordMatrix[i][j] == 0) {
		    System.out.printf("00 ");
		} else System.out.printf("%X ", wordMatrix[i][j]);
	    }
	    System.out.printf("\n");
	}
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


 */
