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

	isEncryption = isEncryption(args);
	keyFilename = getKeyFilename(args);
	inputFilename = getInputFilename(args);

	// get state for a single line of input
	State state = new State(inputFilename);
	SBox sbox = new SBox();
	Key key = new Key(keyFilename);

	System.out.println("The plaintext is:");
	state.printByteMatrix(state.stateMatrix);

	System.out.println("The CipherKey is:");
	key.printByteMatrix(key.keyMatrix);

	ExpandedKey expandedKey = new ExpandedKey(key.keyMatrix);

	System.out.println("The expanded key is:");
	ByteArray.printByteMatrix(expandedKey.expandedKey);

	final int ROUNDS = 14;
	int i = 0;
	// Encrypt
		if (isEncryption) {
			while (i <= ROUNDS) {
				if (i == 0) {
					Encryption.addRoundkey(i, expandedKey.expandedKey, state.stateMatrix);
					System.out.println("After addRoundKey(" + i + "):");
					state.printByteMatrix(state.stateMatrix);
					i++;
					continue;
				}

				// subBytes
				Encryption.subAllBytes(state, sbox); // subBytes
				System.out.println("After subBytes:");
				state.printByteMatrix(state.stateMatrix);

				// shiftRows
				Encryption.shiftRows(state.stateMatrix);
				System.out.println("After shiftRows:");
				state.printByteMatrix(state.stateMatrix);

				// mixCols - skip on last round
				if( i != 14){
				for (int j = 0; j < state.stateMatrix[0].length; j++) {
					Encryption.mixColumn(j, state.stateMatrix);// MixCols
				}
				System.out.println("After mixCols:");
				state.printByteMatrix(state.stateMatrix);
				}
				// addRoundKey
				Encryption.addRoundkey(i, expandedKey.expandedKey, state.stateMatrix);
				System.out.println("After addRoundKey(" + i + "):");
				state.printByteMatrix(state.stateMatrix);

				i++;
			}
		}
		// Decrypt
		else{
			int j = 14;
			while (j > 0) {
				//addRoundKey
				Decryption.addRoundkey(ROUNDS, expandedKey.expandedKey, state.stateMatrix);
				System.out.println("After addRoundKey(" + j + "):");
				state.printByteMatrix(state.stateMatrix);
				
				//invShiftRows
				Decryption.invShiftRows(state.stateMatrix);
				System.out.println("After invShiftRows:");
				state.printByteMatrix(state.stateMatrix);
				
				//invSubBytes
				Decryption.invsubAllBytes(state,sbox);
				System.out.println("After invSubBytes:");
				state.printByteMatrix(state.stateMatrix);
				
				//invMixColumns
				for (int k = 0; k < state.stateMatrix[0].length; k++) {
					Decryption.invMixColumn2(k, state.stateMatrix);
				}
				System.out.println("After InvmixCols:");
				state.printByteMatrix(state.stateMatrix);
				j--;	
			}
			Decryption.addRoundkey(ROUNDS, expandedKey.expandedKey, state.stateMatrix);
			System.out.println("After addRoundKey(" + j + "):");
			state.printByteMatrix(state.stateMatrix);
			
		}

	// Print ciphertext in hex to output file

	// for (int i = 0; i < 15; i++) {
	//     System.out.printf("----- State %d  -----\n", i);
	//     subAllBytes(state, sbox);
	// }

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

    public static void subAllBytes(State state, SBox sbox) {
	byte[][] newState = new byte[state.stateMatrix.length][state.stateMatrix[0].length];

	for (int i = 0; i < state.stateMatrix.length; i++) {
	    for (int j = 0; j < state.stateMatrix[i].length; j++) {
		newState[i][j] = Encryption.subByte(state.stateMatrix[i][j], sbox.sbox);
	    }
	}

	System.out.println("Old state:");
	state.printByteMatrix(state.stateMatrix);

	// refresh state
	state.stateMatrix = newState;
	System.out.println("New state:");
	state.printByteMatrix(state.stateMatrix);
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
