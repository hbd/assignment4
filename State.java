public class State extends ByteArray {
    // inputToBytes return the input file as a 2d byte array
    public static byte[][] inputToBytes(String inputFilename) {
	byte[][] inputMatrix;

	// read probabilities of each character, starting with 'A'
	try (BufferedReader br = new BufferedReader(new FileReader(inputFilename))) {
	    String line;

	    while ((line = br.readLine()) != null) {
		line = line.toUpperCase();
		// if not valid line, skip line
		if (!verifyHexLine(line)) {
		    continue; // skip line
		} else {
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

}
