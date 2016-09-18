// TFTPSim.java 
// This class is the beginnings of an error simulator for a simple TFTP server 
// based on UDP/IP. The simulator receives a read or write packet from a client and
// passes it on to the server.  Upon receiving a response, it passes it on to the 
// client.
// One socket (68) is used to receive from the client, and another to send/receive
// from the server.  A new socket is used for each communication back to the client.   

import java.io.*;
import java.net.*;
import java.util.*;

/*
 * simulates a network and the potential errors that can occur in a network. 
 * It will add, remove, and delay datagrams to the client and server.
 */
public class Networksimulator {
   
   // UDP datagram packets and sockets used to send / receive
   private static final int portNum = 68;	
   private static final int arraySize = 516;	
   private ArrayList<Thread> listOfClientServerSenders;
   private DatagramPacket receivePacket;
   private DatagramSocket readSocket, writeReadSocket;
   private boolean exit;

   public Networksimulator()
   {
	   listOfClientServerSenders = new ArrayList<Thread>();
	   exit=false;
		initDatagramSocket(portNum);
	   //exit will change value when the server is about to exit after joining all threads.
	   while(exit!=true){
		   listenToPort();
		   //will exit the while loop when the server sends
		   if(receivePacket.getData()[0]==-1 && receivePacket.getData()[1]==-1)
		   {
			   System.out.print("here at exit");
			   exit=true;
		   }
		   else{
		   NetworkSendClientServer tempNSCS =new NetworkSendClientServer(receivePacket, writeReadSocket);
		   listOfClientServerSenders.add(tempNSCS);
		   tempNSCS.start();
		   }
		   //remember to remove
		   exit=true;
	   }
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
 
   private void initDatagramSocket(int numPort)
	{
		try {

				if(readSocket==null)
				{
					readSocket = new DatagramSocket(numPort);
				}
				writeReadSocket = new DatagramSocket();
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
   private void listenToPort(){
		initDatagramSocket(portNum);
		byte[] tempData= new byte[arraySize];
		receivePacket=new DatagramPacket(tempData, tempData.length);
		try {
			readSocket.receive(receivePacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
   
   public static void main( String args[] )
   {
      Networksimulator s = new Networksimulator();
      
   }
}

/*
 * Threads that act as the server to the client
 * Each thread has it's own unique port to receive data from
 * Each thread keeps track of the client port and the server port
 */
class NetworkSendClientServer extends Thread{
	private static final int serverPort=69, arraySize = 516;	
	private DatagramPacket packetToReceive, packetToSend;
	private DatagramSocket clientServerSendReceiveSocket;
	private int portClient, portCommunicationServer;
	private boolean killCommand;
	public NetworkSendClientServer(DatagramPacket SendReceive, DatagramSocket portToReceive){
		clientServerSendReceiveSocket = portToReceive;
		packetToSend = SendReceive;
		portClient = SendReceive.getPort();
		packetToReceive=SendReceive;
	}
	
	/*
	 * Initializes a datagram 
	 */
	private void initDatagramPacket(byte[] info, int portNum)
		{
	       try {
	    	   //System.out.print(portNum);
	    	   packetToSend = new DatagramPacket(info, info.length,
	                                         InetAddress.getLocalHost(), portNum);
	        } catch (UnknownHostException e) {
	           e.printStackTrace();
	           System.exit(1);
	        }
		}
	
	/*
	 * This sends the datagram to the given port
	 */
	private void TransmitDatagramPacket(DatagramPacket dataToSend)
	  {
		  try {
			clientServerSendReceiveSocket.send(dataToSend);
			//System.out.print("GOT HERE!!!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	
	/*
	 * This transfers the data or ack recieved from the server to the client
	 */
	private void sendDataAckToClient(){
		//sends to the client what ever the server send
		//this could be an ack or data
		initDatagramPacket(packetToSend.getData(),portClient);
		TransmitDatagramPacket(packetToSend);
		readProcedure();
		packetToSend = packetToReceive;
	}
	
	/*
	 * This transfers the data or ack recieved from the client to the server
	 */
	private void sendDataAckToServer(){
		initDatagramPacket(packetToSend.getData(),portCommunicationServer);
		TransmitDatagramPacket(packetToSend);
		readProcedure();
		packetToSend = packetToReceive;
	}
	private void packetCorrupt()
	{
		
	}
	/*
	 * This sends the read or write received from the
	 * client to the user request to the server
	 */
	private void sendReadWriteRequest()
	{
		initDatagramPacket(packetToSend.getData(),serverPort);
		TransmitDatagramPacket(packetToSend);
		readProcedure();
		portCommunicationServer = packetToReceive.getPort();
		packetToSend = packetToReceive;
		
	}
	
	/*
	 * This reads from the port and receieves the data sent by the client or server
	 */
	private void readProcedure()
	{
		byte[] tempData= new byte[arraySize];
		packetToReceive=new DatagramPacket(tempData, tempData.length);
		try {
			clientServerSendReceiveSocket.receive(packetToReceive);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(packetToReceive.getData()[1]==4){
		String received = new String(packetToReceive.getData(),0,packetToReceive.getLength());
		System.out.println(packetToReceive.getData()[2]);
		System.out.println(packetToReceive.getData()[3]);
		System.out.println(packetToReceive.getData()[1]);
		System.out.println(packetToReceive.getData().length);
		}
	}
	
	/*
	 * this simulates duplicateData being sent by the network.
	 */
	private void duplicateDataPacket()
	{
		TransmitDatagramPacket(packetToSend);
		TransmitDatagramPacket(packetToSend);
	}
	
	/*
	 * This simulates a datapacket lost in the netowrk
	 */
	private void deleteDataPacket()
	{
		
	}
	/*
	 * This simulates a dataPacket being delayed by the network
	 */
	private void delayDataPacket()
	{
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TransmitDatagramPacket(packetToSend);
	}
	@Override
	public void run(){
			
			//send a WRQ packet to the server and the server will reply with ack, pass the act to the client.
			//Then the client should send a data packet, send the data packet to the server.
			sendReadWriteRequest();
			while(true)
			{	
				//sends to the client what ever the server send
				//this could be an ack or data
				sendDataAckToClient();
				duplicateDataPacket();
				String received = new String(packetToReceive.getData(),0,packetToReceive.getLength());
				System.out.print(received);
				deleteDataPacket();
				delayDataPacket();
				//sends to the server what ever the client sent
				sendDataAckToServer();
				
			}

		
	}
}