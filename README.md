UTEID: jrf2954; zak348;
FIRSTNAME: Jeff; Zak;
LASTNAME: Fajay; Keener;
CSACCOUNT: jfajay; zak;
EMAIL: jfajay@cs.utexas.edu; zak@cs.utexas.edu;

[Program 4]
[Description]
AES: Our main class. Hold methods for reading input to determine is requested operation is encryption or decryption, and hte key and input
     file names. It creates instances of each State, Key, SBox, and ExpandedKey objects. Depending in the operation, it uses these objects
     to manage the state of the input, and run it through the sequence of addRoundKey, subBytes, shiftRows, and mixColumns (doing the
     inverse. It prints the Cipher Text at the end of the sequence.

ByteArray: The parent function for ExpandedKey, Key, SBox, and State. This holds functions for printing byte matrices, converting character
	   arrays to two dimensional bytes arrays, and verifying a single line of input as valid hex code.


Decryption & Encryption: Encrypt approppriate functions for subBytes, shiftRows, mixColumns, and addRoundKey. Decrypt holds these functions, but with 

ExpandedKey
Key
SBox
State

[Finish]

[Command line]

[Timing Information]

[Input Filenames]

[Output Filenames]



[Test Case 2]

[Command line]

[Timing Information]

[Input Filenames]

[Output Filenames]



[Test Case 3]

[Command line]

[Timing Information]

[Input Filenames]

[Output Filenames]



[Test Case 4]

[Command line]

[Timing Information]

[Input Filenames]

[Output Filenames]
