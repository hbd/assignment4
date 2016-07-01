import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class Key extends ByteArray {
    byte[][] keyMatrix;

    // inputToBytes return the input file as a 2d byte array
    public Key(String keyFilename) {
	// read probabilities of each character, starting with 'A'
	try (BufferedReader br = new BufferedReader(new FileReader(keyFilename))) {
	    String line;

	    while ((line = br.readLine()) != null) {
		line = line.toUpperCase();
		// if not valid line, skip line
		if (!verifyHexLine(line)) {
		    continue; // skip line
		} else {
		    keyMatrix = formatKeyMatrix(line); // get formatted line
		}
	    }
	} catch (FileNotFoundException e) {
	    System.out.println("Could not find input file.");
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    // formatInputMatrix checks line for correct length, shrink or add padding if necessary
    public static byte[][] formatKeyMatrix(String key) {
	int i, k;
	char[] initialKeyChars = null; // Key String to Char array, before 
	char[] finalKeyChars = new char[initialKeyChars.length]; // 64 characters long, bc 1 line is 32 bytes = 64 hex chars
	byte[][] byteMatrix = new byte[16][16]; // 16x16 matrix for 256-bit key
	byte bite;

	initialKeyChars = key.toCharArray();

	if (initialKeyChars.length != 32) {
	    // if more than 32 chars, cut down to 32 chars
	    if (initialKeyChars.length > 32) {
		i = 0;

		while (i < 32) {
		    finalKeyChars[i] = initialKeyChars[i];
		    i++;
		}
	    }
	    // if less than 32 chars, add padding
	    else if (initialKeyChars.length < 32) {
		int numChars = initialKeyChars.length;
		i = 0;

		// copy first several characters
		while (i < numChars) {
		    finalKeyChars[i] = initialKeyChars[i];
		    i++;
		}

		// fill the rest with 0s
		while (i < finalKeyChars.length) {
		    finalKeyChars[i] = 0;
		    i++;
		}
	    }
	} else { // input was already in proper format, copy into final
	    i = 0;
	    while (i < finalKeyChars.length) {
		finalKeyChars[i] = initialKeyChars[i];
		i++;
	    }
	}

	k = 0;

	// fill byteMatrix with formatted character bits
	for (i = 0; i < 4; i++) {
	    for (int j = 0; j < 4; j++) {
		bite = 0; // clear byte
		// add the first hex value to left-most 4 bits
		bite = (byte) (bite ^ (Character.digit(finalKeyChars[k++], 16) << 4));
		// add second hex value to right-most 4 bits
		bite = (byte) (bite ^ Character.digit(finalKeyChars[k++], 16));
		// assign newly calculated value in byte matrix
		byteMatrix[i][j] = bite;
	    }
	}

	System.out.println("The Plaintext is:");
	printByteMatrix(byteMatrix);

	return byteMatrix;
    }

}
