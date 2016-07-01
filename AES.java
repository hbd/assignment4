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

	System.out.println("The Plaintext is:");
	state.printByteMatrix(state.stateMatrix);

	System.out.println("The Cipherkey is:");
	key.printByteMatrix(key.keyMatrix);

	System.out.println("The Sbox is:");
	sbox.printByteMatrix(sbox.sbox);
	// Encrypt

	// Decrypt

	// Print ciphertext in hex to output file

	// for (int i = 0; i < 15; i++) {
	//     System.out.printf("----- State %d  -----\n", i);
	//     subAllBytes(state, sbox);
	// }

	// expandKey();
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

    // find a byte in subMatrix using location derived from the two elements of word
    // the two elements are hex values; we convert them to integers which we use as indexes
    // we return the byte found at the address in subMatrix using these two indexes
    public static byte subByte(byte word, byte[][] subMatrix) {
	int x, y;

	x = (word & 0xF0) >>> 4;
	y = (word & 0x0F);

	return subMatrix[x][y];
    }

    public static void subAllBytes(State state, SBox sbox) {
	byte[][] newState = new byte[state.stateMatrix.length][state.stateMatrix[0].length];

	for (int i = 0; i < state.stateMatrix.length; i++) {
	    for (int j = 0; j < state.stateMatrix[i].length; j++) {
		newState[i][j] = subByte(state.stateMatrix[i][j], sbox.sbox);
	    }
	}

	System.out.println("Old state:");
	state.printByteMatrix(state.stateMatrix);

	// refresh state
	state.stateMatrix = newState;
	System.out.println("New state:");
	state.printByteMatrix(state.stateMatrix);
    }

    // //Method to expand key K
    // public static byte[][] expandKey(byte[][] inputKey) {
    // 	byte[][] expandedKeyMatrix = new byte[4][60]; //w is the expanded key matrix
    // 	byte[] temp = new byte[4];
    // 	byte[] temp2 = new byte[4];
    // 	byte[] rconCol = new byte[4];

    // 	//Declaration of RCON table
    // 	char rcon[] = {
    // 	    0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a,
    // 	    0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39,
    // 	    0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a,
    // 	    0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8,
    // 	    0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef,
    // 	    0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc,
    // 	    0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b,
    // 	    0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3,
    // 	    0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94,
    // 	    0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20,
    // 	    0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35,
    // 	    0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f,
    // 	    0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04,
    // 	    0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63,
    // 	    0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd,
    // 	    0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d
    // 	};

    // 	expandedKeyMatrix = charToByte(rcon, expandedKeyMatrix.length, expandedKeyMatrix[0].length);

    // 	System.out.println("Expanded Key Matrix:");
    // 	printByteMatrix(expandedKeyMatrix);

    // 	//Define first 4 columns of expandedKey as K
    // 	for (int i = 0; i < k.length; i++){
    // 	    for (int j = 0; j < k[i].length; j++){
    // 		expandedKeyMatrix[i][j] = inputKey[i][j];
    // 	    }
    // 	}

	// for (int j = 4; j < 60; j++){

	//     //If column is a multiple of 4, do extra steps
	//     if (j %4 == 0){
	// 	//Build temp array from this column
	// 	for (int i = 0; i < 4; i++){
	// 	    temp[i] = k[i][j];
	// 	}

	// 	//rotWord - shift bytes up one (to left)
	// 	temp = rotWord(temp);

	// 	//Use subByte to replace temp with subBytes values
	// 	for (int i = 0; i < 4; i++){
	// 	    temp[i] = subByte(temp[i]);
	// 	}

	// 	//build temp array from column j-4
	// 	for (int i = 0; i < 4; i++){
	// 	    temp2[i] = k[i][j-4];
	// 	}

	// 	//Grab RCON Matrix
	// 	for (int i = 0; i < 4; i++){
	// 	    rconCol[i]= rcon[i][j];
	// 	}

	// 	//temp1^temp2^rcon
	// 	for (int i = 0; i < 4 ; i++){
	// 	    temp[i] = (byte) (temp[i] ^ temp[2] ^ rconCol[i]);
	// 	}

	// 	//Write this new column to w
	// 	for (int i =0; i < 4; i++){
	// 	    w[i][j] = temp[i];
	// 	}
	//     }

	//     //all other cases
	//     else{
	// 	for (int i = 0 ; i < 4; i++){
	// 	    w[i][j] = (byte) (w[i][j-1] ^ w[i][j-4]);
	// 	}
	//     }
	// }

	// return expandedKeyMatrix;
    // }

    // public static byte[] rotWord(byte[] x){
    // 	byte[] retArr = new byte[x.length];
    // 	for (int i = 0; i < x.length-1; i++){
    // 	    retArr[i] = x[i+1];
    // 	}

    // 	retArr[retArr.length-1] = x[0];

    // 	return retArr;

    // }
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
