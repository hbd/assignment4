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


Decryption & Encryption: Encrypt approppriate functions for subBytes, shiftRows, mixColumns, and addRoundKey. Decrypt holds these functions,
	   but does the inverse for subBytes, and mixColumns

ExpandedKey: Creates an expanded key to use, given a key.
Key: Key read in from keyFile sourced from user
SBox: SBpx constant that holds a substitution box and inverse sub box.
State: Holds the state matrix. This is used for all of the state alterations, and holds the code for printing the matrix as a string, and to a file.

[Finish]
We finished all of the necessary parts of the AES 256 encryption and decryption.
No extra credit was completed.

[Command line]
$ java AES e key1 input1
$ java AES d key1 input1.enc

[Timing Information]
size of input & output file: 3.2K

java AES e key1 input1
time: 3.06s user 0.67s system 138% cpu 2.696 total

java AES d key1 input1.enc
time:  2.51s user 0.64s system 99% cpu 3.162 total

[Input Filenames]
output1

[Output Filenames]
output1.enc


[Test Case 2]

[Command line]
$ java AES e key2 input2
$ java AES d key2 input2.enc

[Timing Information]
size of input file: 8.0K

java AES e key2 input2
time: 3.32s user 1.08s system 123% cpu 3.568 total

java AES d key2 input2.enc
time:  3.15s user 1.13s system 74% cpu 5.721 total

[Input Filenames]
output2

[Output Filenames]
output2.enc


[Test Case 3]

[Command line]
$ java AES e key3 input3
$ java AES d key3 input3.enc

[Timing Information]
size of input file: 16K

java AES e key3 input3
time: 4.01s user 1.74s system 119% cpu 4.792 total

java AES d key3 input3.enc
time: 4.70s user 1.77s system 119% cpu 5.400 total

[Input Filenames]
output3

[Output Filenames]
output3.enc


[Test Case 4]

[Command line]
$ java AES e key4 input4
$ java AES d key4 input4.enc

[Timing Information]
size of input file: 32K

java AES e key4 input4
time: 5.51s user 2.95s system 117% cpu 7.185 total

java AES d key4 input4.enc
time: 5.28s user 3.00s system 110% cpu 7.508 total

[Input Filenames]
output4

[Output Filenames]
output4.enc
