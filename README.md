# Encode Decode

##### Data Hiding in Image using LSB Technique [Steganography]

The 'EncodeDecode' is a Java Swing application that allows the user to either encode a message into an image or decode a hidden message from an image. The encoding process involves taking an input image, converting it into a byte array, and then modifying the least significant bit of each byte to store a character of the input message. The message length is also stored in the first 4 bytes of the image. The decoding process involves extracting the message from the least significant bits of the bytes in the image based on the length stored in the first 4 bytes. The GUI provides options for the user to either encode or decode, select an image file to work with, and enter the text to be encoded (if applicable). The Java AWT and Swing libraries are used to create various UI components such as toggle buttons, text areas, and file chooser dialogs. The 'EncodeDecode' class also includes various helper methods to assist in image manipulation and I/O operations. Overall, this class provides a simple and effective way to hide and retrieve messages within image files.

## Instructions

1. [Download](https://github.com/neeleshpandey/EncodeDecode/archive/refs/heads/main.zip) or Clone this Repository in your Local Environment

2. Download and install [`Java 8`](https://www.oracle.com/java/technologies/downloads/#java8)

## UI

![image](https://user-images.githubusercontent.com/87470414/233799442-317f3b83-af7e-4f34-a26f-cf4f50db1398.png)


