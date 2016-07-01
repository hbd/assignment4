import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class Key extends ByteArray {
    byte[][] keyMatrix;

    public Key() {
    }

    // inputToBytes return the input file as a 2d byte array
    public Key(String keyFilename) {
	// read probabilities of each character, starting with 'A'
	try (BufferedReader br = new BufferedReader(new FileReader(keyFilename))) {
	    String line;

	    while ((line = br.readLine()) != null) {
		System.out.printf("Reading from %s\nReading line: %s\n", keyFilename, line);
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
	int i, j, k;
        char[] finalKeyChars = new char[64]; // 64 characters long, bc 1 line is 32 bytes = 64 hex chars = 256 bits
	byte[][] byteMatrix = new byte[4][8]; // 8byte x 8byte matrix for 256-bit key
	byte bite;

	char[] initialKeyChars = key.toCharArray();

	if (initialKeyChars.length != 64) {
		System.out.printf("Number of chars in key: %d\n", initialKeyChars.length);
	    System.out.println("Key is not long enough.");
	    System.exit(0);
	} else { // input was already in proper format, copy into final
	    i = 0;
	    while (i < finalKeyChars.length) {
		finalKeyChars[i] = initialKeyChars[i];
		i++;
	    }
	    System.out.printf("Number of chars in key: %d\n", i);
	}

	k = 0;

	// fill byteMatrix with formatted character bits
	for (i = 0; i < 4; i++) {
	    for (j = 0; j < 8; j++) {
		// System.out.printf("character: %d = %c |\n", k, finalKeyChars[k]);
		// System.out.printf("position: [%d,%d] | ", i, j);
		bite = 0; // clear byte

		// add the first hex value to left-most 4 bits
		bite = (byte) (bite ^ (Character.digit(finalKeyChars[k++], 16) << 4));

		// System.out.printf("character: %d = %c |\n", k, finalKeyChars[k]);
		// System.out.printf("position: [%d,%d] | ", i, j);

		// add second hex value to right-most 4 bits
		bite = (byte) (bite ^ Character.digit(finalKeyChars[k++], 16));

		// assign newly calculated value in byte matrix
		byteMatrix[i][j] = bite;
	    }
	}

	return byteMatrix;
    }

}
