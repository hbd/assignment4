import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.io.PrintWriter;

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

	System.out.println("The Sbox is:");
	key.printByteMatrix(sbox.sbox);

	ExpandedKey expandedKey = new ExpandedKey(key.keyMatrix);

	System.out.println("The expanded key is:");
	ByteArray.printByteMatrix(expandedKey.expandedKey);

	final int ROUNDS = 14;
	int i = 0;

	// Encrypt
	if (isEncryption) {
	    System.out.println("===== ENCRYPTION =====");
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
	else {
	    System.out.println("===== DECRYPTION =====");
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
		Decryption.invSubAllBytes(state,sbox);
		System.out.println("After invSubBytes:");
		state.printByteMatrix(state.stateMatrix);

		//invMixColumns
		for (int k = 0; k < state.stateMatrix[0].length; k++) {
		    Decryption.invMixColumn2(k, state.stateMatrix);
		}
		System.out.println("After invMixCols:");
		state.printByteMatrix(state.stateMatrix);
		j--;
	    }

	}

	state.printCipherText(isEncryption, inputFilename);
    }

    public static void printCipherText(State state, boolean isEncryption, String filename) {
	PrintWriter pw = null;
	try {
	    if (isEncryption) {
		pw = new PrintWriter(filename + ".enc", "ASCII");
		pw.printf("%s", state.printStateAsString());
		pw.close();
	    } else {
		pw = new PrintWriter(filename + ".dec", "ASCII");
		pw.printf("%s", state.printStateAsString());
		pw.close();
	    }
	} catch (FileNotFoundException e) {
	    System.out.println(e);
	} catch (UnsupportedEncodingException e) {
	    System.out.println(e);
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

    // rotate rows left by x amount
    public static void shiftRows(byte[][] state) {
	int i, j, k;
	byte[][] tmp = new byte[state.length][state[0].length];

	// first row skipped
	// second row rotate 1
	// third row rotate 2
	// fourth row rotate 3
	for (i = 0; i < tmp.length; i++) {
	    for (j = 0; j < tmp[i].length; j++) {
		tmp[i][j] = state[i][j];
	    }
	}

	i = 0;
	while (i < 4) {
	    j = 0;
	    switch (i) {
	    case 1:
		while (j < 4) {
		    if (j == 3) {
			state[i][j] = tmp[i][i-1];
		    } else {
			state[i][j] = tmp[i][j+i];
		    }
		    j++;
		}
		break;
	    case 2:
		while (j < 4) {
		    if (j == 2 || j == 3) {
			state[i][j] = tmp[i][j-i];
		    } else {
			state[i][j] = tmp[i][j+i];
		    }
		    j++;
		}
		break;
	    case 3:
		while (j < 4) {
		    if (j == 0) {
			state[i][j] = tmp[i][i];
		    } else {
			state[i][j] = tmp[i][j-1];
		    }
		    j++;
		}
		break;
	    default:
		break;
	    }
	    i++;
	}
    }

    // rotate rows left by x amount
    public static void invShiftRows(byte[][] state) {
	int i, j, k;
	byte[][] tmp = new byte[state.length][state[0].length];

	// first row skipped
	// second row rotate 1
	// third row rotate 2
	// fourth row rotate 3
	for (i = 0; i < tmp.length; i++) {
	    for (j = 0; j < tmp[i].length; j++) {
		tmp[i][j] = state[i][j];
	    }
	}

	i = 0;
	while (i < 4) {
	    j = 0;
	    switch (i) {
	    case 1:
		while (j < 4) {
		    if (j == 0) {
			state[i][j] = tmp[i][3];
		    } else {
			state[i][j] = tmp[i][j-1];
		    }
		    j++;
		}
		break;
	    case 2:
		while (j < 4) {
		    if (j == 2 || j == 3) {
			state[i][j] = tmp[i][j-2];
		    } else {
			state[i][j] = tmp[i][j+2];
		    }
		    j++;
		}
		break;
	    case 3:
		while (j < 4) {
		    if (j == 3) {
			state[i][j] = tmp[i][0];
		    } else {
			state[i][j] = tmp[i][j+1];
		    }
		    j++;
		}
		break;
	    default:
		break;
	    }
	    i++;
	}
    }

    ////////////////////////  the mixColumns Tranformation ////////////////////////


    final static int[] LogTable = {
	0,   0,  25,   1,  50,   2,  26, 198,  75, 199,  27, 104,  51, 238, 223,   3,
	100,   4, 224,  14,  52, 141, 129, 239,  76, 113,   8, 200, 248, 105,  28, 193,
	125, 194,  29, 181, 249, 185,  39, 106,  77, 228, 166, 114, 154, 201,   9, 120,
	101,  47, 138,   5,  33,  15, 225,  36,  18, 240, 130,  69,  53, 147, 218, 142,
	150, 143, 219, 189,  54, 208, 206, 148,  19,  92, 210, 241,  64,  70, 131,  56,
	102, 221, 253,  48, 191,   6, 139,  98, 179,  37, 226, 152,  34, 136, 145,  16,
	126, 110,  72, 195, 163, 182,  30,  66,  58, 107,  40,  84, 250, 133,  61, 186,
	43, 121,  10,  21, 155, 159,  94, 202,  78, 212, 172, 229, 243, 115, 167,  87,
	175,  88, 168,  80, 244, 234, 214, 116,  79, 174, 233, 213, 231, 230, 173, 232,
	44, 215, 117, 122, 235,  22,  11, 245,  89, 203,  95, 176, 156, 169,  81, 160,
	127,  12, 246, 111,  23, 196,  73, 236, 216,  67,  31,  45, 164, 118, 123, 183,
	204, 187,  62,  90, 251,  96, 177, 134,  59,  82, 161, 108, 170,  85,  41, 157,
	151, 178, 135, 144,  97, 190, 220, 252, 188, 149, 207, 205,  55,  63,  91, 209,
	83,  57, 132,  60,  65, 162, 109,  71,  20,  42, 158,  93,  86, 242, 211, 171,
	68,  17, 146, 217,  35,  32,  46, 137, 180, 124, 184,  38, 119, 153, 227, 165,
	103,  74, 237, 222, 197,  49, 254,  24,  13,  99, 140, 128, 192, 247, 112,   7};

    final static int[] AlogTable = {
	1,   3,   5,  15,  17,  51,  85, 255,  26,  46, 114, 150, 161, 248,  19,  53,
	95, 225,  56,  72, 216, 115, 149, 164, 247,   2,   6,  10,  30,  34, 102, 170,
	229,  52,  92, 228,  55,  89, 235,  38, 106, 190, 217, 112, 144, 171, 230,  49,
	83, 245,   4,  12,  20,  60,  68, 204,  79, 209, 104, 184, 211, 110, 178, 205,
	76, 212, 103, 169, 224,  59,  77, 215,  98, 166, 241,   8,  24,  40, 120, 136,
	131, 158, 185, 208, 107, 189, 220, 127, 129, 152, 179, 206,  73, 219, 118, 154,
	181, 196,  87, 249,  16,  48,  80, 240,  11,  29,  39, 105, 187, 214,  97, 163,
	254,  25,  43, 125, 135, 146, 173, 236,  47, 113, 147, 174, 233,  32,  96, 160,
	251,  22,  58,  78, 210, 109, 183, 194,  93, 231,  50,  86, 250,  21,  63,  65,
	195,  94, 226,  61,  71, 201,  64, 192,  91, 237,  44, 116, 156, 191, 218, 117,
	159, 186, 213, 100, 172, 239,  42, 126, 130, 157, 188, 223, 122, 142, 137, 128,
	155, 182, 193,  88, 232,  35, 101, 175, 234,  37, 111, 177, 200,  67, 197,  84,
	252,  31,  33,  99, 165, 244,   7,   9,  27,  45, 119, 153, 176, 203,  70, 202,
	69, 207,  74, 222, 121, 139, 134, 145, 168, 227,  62,  66, 198,  81, 243,  14,
	18,  54,  90, 238,  41, 123, 141, 140, 143, 138, 133, 148, 167, 242,  13,  23,
	57,  75, 221, 124, 132, 151, 162, 253,  28,  36, 108, 180, 199,  82, 246,   1};

    private static byte mul (int a, byte b) {
	int inda = (a < 0) ? (a + 256) : a;
	int indb = (b < 0) ? (b + 256) : b;

	if ( (a != 0) && (b != 0) ) {
	    int index = (LogTable[inda] + LogTable[indb]);
	    byte val = (byte)(AlogTable[ index % 255 ] );
	    return val;
	}
	else
	    return 0;
    } // mul

    // In the following two methods, the input c is the column number in
    // your evolving state matrix st (which originally contained
    // the plaintext input but is being modified).  Notice that the state here is defined as an
    // array of bytes.  If your state is an array of integers, you'll have
    // to make adjustments.

    public static void mixColumn (int c, byte[][] st) {
	// This is another alternate version of mixColumn, using the
	// logtables to do the computation.

	byte a[] = new byte[4];

	// note that a is just a copy of st[.][c]
	for (int i = 0; i < 4; i++)
	    a[i] = st[i][c];

	// This is exactly the same as mixColumns1, if
	// the mul columns somehow match the b columns there.
	st[0][c] = (byte)(mul(2,a[0]) ^ a[2] ^ a[3] ^ mul(3,a[1]));
	st[1][c] = (byte)(mul(2,a[1]) ^ a[3] ^ a[0] ^ mul(3,a[2]));
	st[2][c] = (byte)(mul(2,a[2]) ^ a[0] ^ a[1] ^ mul(3,a[3]));
	st[3][c] = (byte)(mul(2,a[3]) ^ a[1] ^ a[2] ^ mul(3,a[0]));
    } // mixColumn2

    public static void invMixColumn (int c, byte[][] st) {
	byte a[] = new byte[4];

	// note that a is just a copy of st[.][c]
	for (int i = 0; i < 4; i++)
	    a[i] = st[i][c];

	st[0][c] = (byte)(mul(0xE,a[0]) ^ mul(0xB,a[1]) ^ mul(0xD, a[2]) ^ mul(0x9,a[3]));
	st[1][c] = (byte)(mul(0xE,a[1]) ^ mul(0xB,a[2]) ^ mul(0xD, a[3]) ^ mul(0x9,a[0]));
	st[2][c] = (byte)(mul(0xE,a[2]) ^ mul(0xB,a[3]) ^ mul(0xD, a[0]) ^ mul(0x9,a[1]));
	st[3][c] = (byte)(mul(0xE,a[3]) ^ mul(0xB,a[0]) ^ mul(0xD, a[1]) ^ mul(0x9,a[2]));
    } // invMixColumn2
}

/*
Graveyard
	// Print ciphertext in hex to output file

	System.out.printf("----- State -----\n");
	System.out.println("Old state:");
	state.printByteMatrix(state.stateMatrix);

	for (i = 0; i < 14; i++) {

	    // subBytes, shiftRows,
	    subAllBytes(state, sbox);
	    shiftRows(state.stateMatrix);
	    for (int j = 0; j < state.stateMatrix[0].length; j++) {
		mixColumn(j, state.stateMatrix);
	    }
	}

	System.out.println("New state:");
	state.printByteMatrix(state.stateMatrix);

	invShiftRows(state.stateMatrix);
	System.out.println("New inv state:");
	state.printByteMatrix(state.stateMatrix);

		System.out.printf("Row %d\n", i);
		System.out.printf("tmp[%d][%d] = %02X\n", i, 0, tmp[i][0]);


			System.out.printf("state[%d][%d] = %02X\n", i, j, state[i][3]);
			System.out.printf("tmp[%d][%d] = %02X\n", i, 0, tmp[i][0]);
			System.out.printf("bite: %02X\n", bite);


		    System.out.printf("[%d][%d]\n", i, j);
		    if (j == 3) {
			tmp = state[i][j];
			state[i][j] = state[i][0];
			state[i][0] = tmp;
			j++;
			continue;
		    }
		    tmp = state[i][j];
		    state[i][j] = state[i][j+1];
		    state[i][j+1] = tmp;
		    j++;


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
