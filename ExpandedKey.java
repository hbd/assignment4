public class ExpandedKey {
    byte[][] expandedKey;

    public ExpandedKey(byte[][] inputKey) {
	//Method to expand key K
     	byte[][] expandedKeyMatrix = new byte[4][60];
     	byte[] temp = new byte[4];
     	byte[] temp2 = new byte[4];
     	byte[] rconCol = new byte[4];
     	int rconCounter = 1;
     	SBox sbox = new SBox();

	//Declaration of RCON table
     	char rconSingle[] = {
     	    0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a,
     	    0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39,
     	    0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a,
     	    0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8,
     	    0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef,
     	    0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc,
     	    0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b,
     	    0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3,
     	    0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94,
     	    0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20,
     	    0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35,
     	    0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f,
     	    0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04,
     	    0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63,
     	    0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd,
     	    0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d
     	};

     	//byte[][] rcon = ByteArray.charToByte(rconSingle, 4, 60);

     	// ByteArray.printByteMatrix(rcon);
     	// Define first 4 columns of expandedKey as inputKey
     	for (int i = 0; i < inputKey.length; i++){
     	    for (int j = 0; j < inputKey[i].length; j++){
     		expandedKeyMatrix[i][j] = inputKey[i][j];
     	    }
     	}
     	
     	//ByteArray.printByteMatrix(rcon);

	for (int j = 8; j < 60; j++){

	    //If column is a multiple of 4, do extra steps
	    if (j %8 == 0){
	 	//Build temp array from this column
	 	for (int i = 0; i < 4; i++){
	 	    temp[i] = expandedKeyMatrix[i][j-1];
	 	}
	 	
//	 	System.out.println("*****************J IS " + j  + "******************");
// 	    System.out.println("Temp is:");
// 	    printArray(temp);
	 	//rotWord - shift bytes up one (to left)
	 	temp = rotWord(temp);
	 	
//	 	System.out.println("RotWord:");
//	 	printArray(temp);
	 	
		//System.out.printf("%02X\n", subByte(temp[0],sbox.sbox));
	 	//Use subByte to replace temp with subBytes values
	 	for (int i = 0; i < 4; i++){
	 	    temp[i] = subByte(temp[i],sbox.sbox);
	 	}
//	 	System.out.println("Test subBytes...:");
//	 	printArray(temp);
	 	
	 	
	 	//build temp array from column j-4
	 	for (int i = 0; i < 4; i++){
	 	    temp2[i] = expandedKeyMatrix[i][j-8];
	 	}
//	 	System.out.println("W i-4 is:");
//	 	printArray(temp2);

	 	
	 	//Grab RCON Matrix
	 	rconCol[0] = (byte) rconSingle[rconCounter++];
	 	rconCol[1] = 0;
	 	rconCol[2] = 0;
	 	rconCol[3] = 0;
	 	
//	 	System.out.println("RCON matrix is:");
//	 	printArray(rconCol);
//	 	
//	 	System.out.println("Xoring:");
//	 	printArray(temp);
//	 	System.out.println("__");
//	 	printArray(temp2);
//	 	System.out.println("__");
//	 	printArray(rconCol);
	 	//System.out.println(printArray(temp) + " ^ " + printArray(temp2) + " ^ " + printArray(rconCol));
	 	//temp1^temp2^rcon
	 	for (int i = 0; i < 4 ; i++){
	 	    temp[i] = (byte) (temp[i] ^ temp2[i]);
	 	    temp[i] = (byte) (temp[i] ^ rconCol[i]);
	 	}

	 	//System.out.println("Temp after xor is:");
	 	//printArray(temp);
	 	//Write this new column to w
	 	for (int i =0; i < 4; i++){
		    expandedKeyMatrix[i][j] = temp[i];
	 	}
	    }
	    else if (j % 8 == 4){
	    	for (int i = 0; i < 4; i++){
	    		expandedKeyMatrix[i][j] = (byte) (expandedKeyMatrix[i][j-8] ^ subByte(expandedKeyMatrix[i][j-1],sbox.sbox));
	    	}
	    }
	    //all other cases
	    else{
	 	for (int i = 0 ; i < 4; i++){
		    expandedKeyMatrix[i][j] = (byte) (expandedKeyMatrix[i][j-8] ^ expandedKeyMatrix[i][j-1]);
	 	}
	    }
	}

	expandedKey = expandedKeyMatrix;
    }

     public byte[] rotWord(byte[] x){
     	byte[] retArr = new byte[x.length];
     	for (int i = 0; i < x.length-1; i++){
     	    retArr[i] = x[i+1];
     	}

     	retArr[retArr.length-1] = x[0];

     	return retArr;
    }

    // we return the byte found at the address in subMatrix using these two indexes
    public byte subByte(byte word, byte[][] subMatrix) {
	int x, y;

	x = (word & 0xF0) >>> 4;
	y = (word & 0x0F);

	return subMatrix[x][y];
    }
    
    public static void printArray(byte[] x){
    	for(int i = 0; i < x.length; i++){
    		System.out.printf("%02X\n", x[i]);
    	}
    }
}
