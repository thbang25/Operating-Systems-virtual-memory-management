/* 
Thabang Sambo
13 april 2024
Operating Systems (OS1)
OS virtual memory management
*/

import java.io.*;
import java.nio.ByteBuffer; //try to read primitive data
import java.nio.ByteOrder; //in which bytes to read from little-endian
import java.nio.channels.FileChannel; //used to perfom file procedures

public class OS1Assignment {
    public static void main(String[] args) {
		
		//getting the binary file from terminal
        if (args.length != 1) {
			//show user how to run program
            System.out.println("Run Program: java OS1Assignment <filename> ");
			//system exit
            System.exit(1);
        }
        
		//define the variables for file access and data manipulation and convertion
        String BinaryFile = args[0]; //get binary file from arguments
        int[] pageTable = {2, 4, 1, 7, 3, 5, 6}; // The page table given in the assignment
        int PhysicalFrame = 128; // physical frame
        int PageOffset = PhysicalFrame - 1; //offset

        //try reading and writing the files invovled
        try (RandomAccessFile readBinaryFile = new RandomAccessFile(BinaryFile, "r"); //read the bytes in the binary file
             FileChannel open_data_channel = readBinaryFile.getChannel();
             BufferedWriter writeOutputfile = new BufferedWriter(new FileWriter("output-OS1.txt"))) {//initialize the file writeOutputfile
             ByteBuffer VirtualMemoryBuffer = ByteBuffer.allocate(8); // the bytes are 8 so we need to set 8 bytes per address
             VirtualMemoryBuffer.order(ByteOrder.LITTLE_ENDIAN); // use little-endian for conversion base

        //get the the data from the channel and read them in to the VirtualMemoryBuffer
            while (open_data_channel.read(VirtualMemoryBuffer) != -1) {
                VirtualMemoryBuffer.flip(); //switch from w to r
        //verify if the data being fed is complete
                if (VirtualMemoryBuffer.remaining() < 8) {
					//show user the issue
                    System.out.println("VirtualMemoryBuffer must be 8 bytes.");
                    break;}

                long virtualAddress = VirtualMemoryBuffer.getLong(); //getting the virtual memory address
                int pageNumber = (int) (virtualAddress >> 7); // use a bitwise shift to the right by 7
                int offset = (int) (virtualAddress & PageOffset); // calculate the offset in the page

                //verify if we have the correct page number
                if (pageNumber < 0 || pageNumber >= pageTable.length) {
					//display the issue to the user
                    System.out.println("Invalid page number: " + pageNumber);
					//try to clear VirtualMemoryBuffer data
                    VirtualMemoryBuffer.clear();
                    continue;}

                int frameNumber = pageTable[pageNumber]; //get frame number from the given table
                int TranslatedPhysicalAddress = (frameNumber << 7) | offset; //calculate the value of the physical address
 
                // show the user the conversion and show the virtual address and the translated address
                System.out.printf("Virtual Address: 0x%012X -> Translated Physical Address: 0x%03X\n", virtualAddress, TranslatedPhysicalAddress);
                
                // Write the hexidecimal data to the text file called osouput
                writeOutputfile.write(String.format("0x%03X%n", TranslatedPhysicalAddress));
				//try to clear VirtualMemoryBuffer data
                VirtualMemoryBuffer.clear();
            }
			//loop out
            System.out.print("Writing hexidecimal data to text file called outputOS txt complete");// success message
		//error handling
        } catch (FileNotFoundException e) {
			//check if the file can be found
            System.out.println("Error file not found check file path " + BinaryFile);
			//the exececution error
        } catch (IOException e) {
            System.out.println("IOException Error!! " + e.getMessage());
        }
    }
}
