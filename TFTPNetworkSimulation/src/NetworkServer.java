// TFTPServer.java 
// This class is the server side of a simple TFTP server based on
// UDP/IP. The server receives a read or write packet from a client and
// sends back the appropriate response without any actual file transfer.
// One socket (69) is used to receive (it stays open) and another for each response. 

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.*;

public class NetworkServer {
	   private static final int portReceiveNum = 69, WRQ = 2, RRQ = 1,arraySize = 516;	
	   private ArrayList<Thread> listOfClientServerSenders;
	   private DatagramPacket receivePacket;
	   private DatagramSocket readSocket, readWriteSocket;
	   private boolean exit=false;
	public NetworkServer()   {
		   listOfClientServerSenders = new ArrayList<Thread>();
		   //exit will change value when the server is about to exit after joining all threads.
		   while(exit!=true){
			   listenToPort();
			   //System.out.print(receivePacket.getData().length);
			   //String received = new String(receivePacket.getData(),0,receivePacket.getLength());
			   //System.out.print(received);
			   //will exit the while loop when the client sends -1 & -1
			   if(receivePacket.getData()[0]==-1 && receivePacket.getData()[1]==-1)
			   {
				   exit=true;
				   shutDownServer();
			   }
			   else if(receivePacket.getData()[1]==WRQ)
			   {
				   NetworkTFTPService tempNSCS =new NetworkTFTPService(receivePacket,readWriteSocket, WRQ);
				   listOfClientServerSenders.add(tempNSCS);
				   tempNSCS.start();
			   }
			   else if (receivePacket.getData()[1]==RRQ)
			   {
				   NetworkTFTPService tempNSCS =new NetworkTFTPService(receivePacket,readWriteSocket,RRQ);
				   listOfClientServerSenders.add(tempNSCS);
				   tempNSCS.start();
			   }
			   //remove here
			   exit=true;
		   }
		   //remove here
		   shutDownServer();
	   }
	
	 
	   private void initDatagramSocket(int numPort)
		{
			try {
				if(readSocket==null)
				{
					readSocket = new DatagramSocket(numPort);
				}
				readWriteSocket = new DatagramSocket();
				
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	   private void listenToPort(){
			initDatagramSocket(portReceiveNum);
			
			byte[] tempData= new byte[arraySize];
			receivePacket=new DatagramPacket(tempData, tempData.length);
			try {
				readSocket.receive(receivePacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	private void shutDownServer()
	{
		//see if I can join threads, to shut down safely.
		   for(Thread cs: listOfClientServerSenders)
		   {
			   	try {
			   		cs.join();
			   	} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			   		e.printStackTrace();
			   	}
		   }
	}
	public static void main( String args[] )
	{
	NetworkServer c = new NetworkServer();
	}
}



class NetworkTFTPService extends Thread {
	private static final int  WRQ = 2, RRQ = 1, arraySize = 516, maxBlockNum=127;
	private int rW, sendPort;
	private byte firstBlockNum, secondBlockNum;
	private DatagramPacket receivePacket;
	private DatagramSocket readWriteSocket;
	private FileOutputStream fileToWrite;
	private File tempFile;
	private String received;
	private byte[] fileToRead, info;
	public NetworkTFTPService(DatagramPacket receivePacket,
			DatagramSocket readWriteSocket, int readWrite) {
		rW = readWrite;
		this.readWriteSocket = readWriteSocket;
		this.receivePacket = receivePacket;
		sendPort = receivePacket.getPort();
		firstBlockNum=0;
		secondBlockNum=0;
	} 
	@Override
	public void run(){
		if(rW==WRQ)
		{
			if (tempFile==null)
			{
			received = new String(receivePacket.getData(),4,receivePacket.getLength()-4);
			tempFile = new File(received);
			//received = new String(receivePacket.getData(),0,receivePacket.getLength());
			try {
				new FileWriter(tempFile.getName(), false);
				fileToWrite = new FileOutputStream(tempFile.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print(received);
			}
			while(true)
			{
				 initSendAckPacket();
				 readFromPort();
				   String received = new String(receivePacket.getData(),0,receivePacket.getLength());
				   System.out.print(received);
				 //writeToFile();
			}
		}
		else if(rW==RRQ){
			readFromFile();
			while(true)
			{
				formDataToSend();
				readFromPort();
				initSendAckPacket();
			}
		}
	}
	private void sendDataToClient(byte[] dataToSend) {
        try {
        	receivePacket = new DatagramPacket(dataToSend, dataToSend.length,InetAddress.getLocalHost(), sendPort);
        	readWriteSocket.send(receivePacket);
         } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
         }
		
	}
	private void initBlock(){

		info = new byte[arraySize];
		secondBlockNum=(byte)(secondBlockNum +1);
		if(firstBlockNum==maxBlockNum && secondBlockNum==maxBlockNum)
		{
			firstBlockNum=(byte)0;
			secondBlockNum=(byte)0;
			info[2]=firstBlockNum;
			info[3]=secondBlockNum;
		}
		if(secondBlockNum==maxBlockNum)
		{
			//do something here to capture 127
			info[2]=firstBlockNum;
			info[3]=secondBlockNum;
			secondBlockNum=(byte)0;
			firstBlockNum=(byte)(firstBlockNum+1);
		}
		else
		{

			info[2]=firstBlockNum;
			info[3]=secondBlockNum;
		}
		
		info[1]=4;
		//System.out.print(info[1]);
	//	System.out.print(info[2]);
	//	System.out.print(info[3]);
	}
	private void initSendAckPacket()
	{
		initBlock();
		sendDataToClient(info);
	}
	private void formDataToSend()
	{
		int maxlength = 0, currlength = 0;
		boolean equalsMaxLength;
		if(maxlength==fileToRead.length){
			//write the file no need to copy a subsection of the list
			equalsMaxLength=true;
			byte[] subArray=Arrays.copyOfRange(fileToRead, currlength, maxlength-1);
			System.arraycopy(subArray,0, info,2,subArray.length);
			sendDataToClient(info);
			//send byte 0 to know that the client is at the end of a file
			info = new byte[1];
			info[0] = 0;
			sendDataToClient(info);
		}
		else if(maxlength<fileToRead.length)
		{
		byte[] subArray=Arrays.copyOfRange(fileToRead, currlength, maxlength-1);
		System.arraycopy(subArray,0, info,2,subArray.length);
		sendDataToClient(info);
		System.out.println(maxlength);
		System.out.println(currlength);
		maxlength = maxlength+512;
		currlength = currlength+512;
		}
		else{
			System.out.println(fileToRead.length);
			maxlength=fileToRead.length;
			equalsMaxLength=true;
			System.out.println(currlength);
			byte[] subArray=Arrays.copyOfRange(fileToRead, currlength, maxlength-1);
			System.arraycopy(subArray,0, info,2,subArray.length);
			sendDataToClient(info);
		}
		
	}
	private void readFromFile()
	{
		try {
			fileToRead = Files.readAllBytes(tempFile.toPath());
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	private void writeToFile()
	{
		byte[] dataArray = new byte[arraySize];
			
		try {
			System.arraycopy(receivePacket.getData(),0, dataArray,4,receivePacket.getLength()-4);
				fileToWrite.write(dataArray);
				fileToWrite.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void readFromPort()
	{
		byte[] tempData= new byte[arraySize];
		receivePacket=new DatagramPacket(tempData, tempData.length);
		try {
			 //System.out.print("here");
			readWriteSocket.receive(receivePacket);
			received = new String(receivePacket.getData(),0,receivePacket.getLength());
			System.out.print(received);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
