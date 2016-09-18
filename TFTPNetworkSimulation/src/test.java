	import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
public class test {
	private static final int  WRQ = 2, RRQ = 1, arraySize = 516, maxBlockNum=255,dataPosition=4;
	public test(File f)
	{
        byte array[];
        array = readFromFile(f);
        System.out.println(new String(array));
	}
	/*
	 * sends data from the file to the server
	 */
/*	private void writeFileToServerProcedure(File tempFile)
	{
		//readFromFile();
		while(true)
		{
			readFromPort();

			if(sendPacket.getData()[1]==4)
			{
			initBlock();
			formDataToSend(tempFile);
			System.out.print("here before");
			System.out.println(sendPacket.getData()[1]);
			System.out.println(sendPacket.getData()[2]);
			System.out.println(sendPacket.getData()[3]);
			   String received = new String(sendPacket.getData(),0,sendPacket.getLength());
			   System.out.print(received);
			   System.out.println(sendPacket.getData()[3]);
			
			}
			else{
				//System.out.print("accidentally here");
				//error packet received was not an ack
				info = new byte[arraySize];
				info[1]=5;
				initTransmitDatagramPacket(info,servicePort);
			}
		}
		
	}
	
	*/
	/*
	 * turns the data in the file into an array of bytes
	 * @param fileToConvert -the file to convert into a byte array
	 */
		public byte[] readFromFile(File fileToConvert)
		{   byte convertFileToByteArray[] = null;
			try {
				System.out.println(fileToConvert.toPath());
				convertFileToByteArray = Files.readAllBytes(fileToConvert.toPath());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return convertFileToByteArray;
		}
	/*
	 * breaking down the data of the file into 512 bytes to send to the server
	 * @param File 
	 */
	private ArrayList<byte[]> formDataToSend(byte fileData[])
	{
		ArrayList<byte[]> fileDataArrays = new ArrayList<byte[]>();
		int currlength=0, maxlength=512;
		while()
		try {

			if(maxlength==fileData.length){
				//write the file no need to copy a subsection of the list
				//equalsMaxLength=true;
				byte[] subArray=Arrays.copyOfRange(fileData, currlength, maxlength-1);
				fileDataArrays.add(subArray);
				//System.arraycopy(subArray,0, data,dataPosition,subArray.length);
				//initTransmitDatagramPacket(info,servicePort);
				//send byte 0 to know that the client is at the end of a file
				subArray= new byte[1];
				fileDataArrays.add()
			}
			else if(maxlength<fileData.length)
			{
			byte[] subArray=Arrays.copyOfRange(fileData, currlength, maxlength-1);
			fileDataArrays.add(subArray);
			//System.arraycopy(subArray,0, data,dataPosition,subArray.length);
			//initTransmitDatagramPacket(info,servicePort);
			//System.out.println(maxlength);
			//System.out.println(currlength);
			maxlength = maxlength+512;
			currlength = currlength+512;
			}
			else{
				//System.out.println(fileData.length);
				maxlength=fileData.length;
				//equalsMaxLength=true;
				//System.out.println(currlength);
				byte[] subArray=Arrays.copyOfRange(fileData, currlength, maxlength);
				fileDataArrays.add(subArray);
				//System.arraycopy(subArray,0, data,dataPosition,subArray.length);
				//initTransmitDatagramPacket(info,servicePort);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return fileDataArrays;
	}

	public static void add(int i, int b)
	{
		i=i+1;
		b=b+1;
	}



	    public static void main(String[] args)  {
	    	String dir=System.getProperty("user.home" ) + "/desktop/Client/test.txt";
	    	File f= new File(dir);
	    	//test t= new test(f);
	        //File file = new File("C:/Users/Fady/Documents/data.txt");
	    	int a=0,  b=0;
	        add(a,b);
	        System.out.print(a);
	        //byte b = (byte) 255;
	        //int a = b & 0xFF;
	    	//System.out.print(dir);
	        //String st = "hello testing";

	        //array=t.readFromFile(array,file);
	        //String temp=new String(array);
	        //System.out.print(temp);
			//System.out.print(file.toPath());
				
	        

	    }
	    
}
