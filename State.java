import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class State extends ByteArray {

    byte[][] stateMatrix;

    public State() {
    }

    // inputToBytes return the input file as a 2d byte array
    public State(String inputFilename) {
	// read probabilities of each character, starting with 'A'
	try (BufferedReader br = new BufferedReader(new FileReader(inputFilename))) {
	    String line;

	    while ((line = br.readLine()) != null) {
		line = line.toUpperCase();
		// if not valid line, skip line
		if (!verifyHexLine(line)) {
		    continue; // skip line
		} else {
		    stateMatrix = formatInputMatrix(line); // get formatted line
		}
	    }
	} catch (FileNotFoundException e) {
	    System.out.println("Could not find input file.");
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    // public State(byte[][] newState, ) {
    // 	stateMatrix = newState;
    // }

    // formatInputMatrix checks line for correct length, shrink or add padding if necessary
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

	// fill byteMatrix with formatted character bits
	for (i = 0; i < 4; i++) {
	    for (int j = 0; j < 4; j++) {
		bite = 0; // clear byte
		System.out.printf("character: %d = %c \n", k, finalChars[k]);

		// add the first hex value to left-most 4 bits
		bite = (byte) (bite ^ (Character.digit(finalChars[k++], 16) << 4));
		System.out.printf("character: %d = %c \n", k, finalChars[k]);

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

}
