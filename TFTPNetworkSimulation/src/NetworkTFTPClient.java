// TFTPClient.java 
// This class is the client side for a very simple assignment based on TFTP on
// UDP/IP. The client uses one port and sends a read or write request and gets 
// the appropriate response from the server.  No actual file transfer takes place.   

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Scanner;



/*
 * This is responsible for handling the user interaction
 */
public class NetworkTFTPClient {
	private static final int serverPort = 68,arraySize = 516, maxBlockNum=127, dataPosition=4, typeOfAction=0,typeOfReq=1;
	//private byte firstBlockNum, secondBlockNum;
	private String typeOfRequest,nameOfFile, typeOfCommand, fileDirStr,received;
	private DatagramPacket sendPacket;
   	private DatagramSocket sendReceiveSocket;
   	private boolean fileExistsOnServer, fileExistsOnClient;
   	//private byte[] fileToRead, info;
   	private int servicePort;
    public static enum action{RRQ,WRQ,EXIT,YES, NO};
	private File dirFile,file;
	public NetworkTFTPClient(){
		sendReceiveSocket=initDatagramSocket();
		
		fileExistsOnClient=false;
		fileExistsOnServer=false;
		dirFile=directorySimulator.getClientFolder();
	}

	private String getVaildCommands()
	{
		boolean validCommands=false;
		String inputLine, userCommand=null; 
		Scanner tokenizer;
        Scanner reader = new Scanner(System.in); 
		while(!validCommands)
		{
			try{
				System.out.println("> Please enter a YES OR NO command");
		        inputLine = reader.nextLine();
		        tokenizer = new Scanner(inputLine);
		        userCommand=tokenizer.next();
		        if(action.YES.toString().equalsIgnoreCase(userCommand) || action.NO.toString().equalsIgnoreCase(userCommand))
		        {
		        	validCommands=true;
		        }
		        else
		        {
					System.out.println("Invalid Command.");
					System.out.println("First agrument must be either:'YES' or 'NO' command.");
					System.out.println("Examples: 1. YES  2.NO\n");
		        }
		        
		        
			}
			catch(Exception e)
			{
				System.out.println("Invalid Command. \n");
				System.out.println("Firse argument not entered. \n");
			}
		}
		return userCommand;
	}
	private String[] getValidFileCommands()
	{
		boolean validCommands=false;
		String[] commands= new String [2];
		String inputLine, thirdArgument=null; 
		Scanner tokenizer;
        Scanner reader = new Scanner(System.in); 
		while(!validCommands)
		{
			try{
				System.out.println("> Please enter the file name with type of request OR 'exit' to exit the program.");
		        inputLine = reader.nextLine();
		        tokenizer = new Scanner(inputLine);
		        commands[0]=tokenizer.next();
		        if(action.EXIT.toString().equalsIgnoreCase(commands[0]))
		        {
		        	validCommands=true;
		        }
		        else
		        {
		        	commands[1]=tokenizer.next();
		        	thirdArgument=tokenizer.next();
					System.out.println("Invalid Command.");
					System.out.println("First agrument must be either: a file name with extenstion Or an 'exit' command");
					System.out.println("second argument must be the type of request.");
					System.out.println("Examples: 1. Test.txt WRQ  2.exit \n");
		        }
		        
		        
			}
			catch(Exception e)
			{
				if(commands[1]!=null)
				{
					if((commands[1].equalsIgnoreCase(action.RRQ.toString()) || commands[1].equalsIgnoreCase(action.WRQ.toString()))&& thirdArgument==null)
	        		{
	        			validCommands=true;
	        		}
					else
					{
						System.out.println("Invalid Command. \n");
					}
				}
				else
				{
					System.out.println("Invalid Command. \n");
					System.out.println("Second argument (type of request) not entered. \n");
				}
			}
		}
		return commands;
	}
	public void runClient() {
			String commands[];
			String tempFilePath;
			boolean finished=false;
			while(!finished)
			{
				commands=getValidFileCommands();
				typeOfCommand=commands[typeOfAction];
				typeOfRequest=commands[typeOfReq];
				if(typeOfCommand.equalsIgnoreCase(action.EXIT.toString()))
				{

					exitNetworkSim(sendPacket,sendReceiveSocket);
					finished=true;
				}
				else if(typeOfRequest.equalsIgnoreCase(action.WRQ.toString()))
				{
					file = new File(dirFile.getPath() + "/" + typeOfCommand);
					System.out.println(file.getPath());
					if(file.exists())
					{
						System.out.println("true WRQ");
						finished=true;
					}
				}
				else if(typeOfRequest.equalsIgnoreCase(action.RRQ.toString()))
				{
					file = new File(dirFile.getPath() + "/" + typeOfCommand);
					System.out.println(file.getPath());
					if(file.exists())
					{
						System.out.println("Would you like to overwrite " + typeOfCommand +" ?");
						typeOfCommand=getVaildCommands();
						finished=true;
					}
					else
					{
						createFile(file);
						
					}
				}
			}
	}
	/*
	 * Checks to see if the file can be read from
	 * @param myFile -checks the file's permission
	 */
private static boolean isPermittedToRead(File myfile){
        try{
            
             // returns true if the file can be read 
             return myfile.canRead();
          
             
          }catch(Exception e){
             // if any I/O error occurs
             e.printStackTrace();
          }
 return false;
         
        }


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
 * initializes the block number
 */
private void initBlock(byte[] arrayinfo, byte firstBlockNum,byte secondBlockNum){
	secondBlockNum=(byte)(secondBlockNum +1);
	int countBlockOne=(firstBlockNum & 0xFF);
	int countBlockTwo=(secondBlockNum & 0xFF);
	//System.out.println(countBlockOne);
	//System.out.println(countBlockTwo);
	if(countBlockOne==maxBlockNum && countBlockTwo==maxBlockNum)
	{
		firstBlockNum=(byte)0;
		secondBlockNum=(byte)0;
		arrayinfo[2]=firstBlockNum;
		arrayinfo[3]=secondBlockNum;
	}
	else if(countBlockTwo==maxBlockNum)
	{
		//do something here to capture 127

		secondBlockNum=(byte)0;
		firstBlockNum=(byte)(firstBlockNum+1);
		arrayinfo[2]=firstBlockNum;
		arrayinfo[3]=secondBlockNum;
	}
	else
	{

		arrayinfo[2]=firstBlockNum;
		arrayinfo[3]=secondBlockNum;
	}
	
	//System.out.print(arrayinfo[1]);
	//System.out.print(arrayinfo[2]);
	//System.out.print("   "+countBlockOne);
	//System.out.print(arrayinfo[3]);
	//System.out.println("   "+ countBlockTwo);
}
	/*
	 * Checks to see if the file can be written to
	 * @param myFile -checks the file's permission
	 */
private static boolean isPermittedToWrite(File myfile){
        try{
             // returns true if the file can be read 
             return myfile.canWrite();
          
             
          }catch(Exception e){
             // if any I/O error occurs
             e.printStackTrace();
          }
 return false;
         
        }

private boolean doesFileExistOnServer(File file, byte[] arrayInfo)
{
	//byte[] info = new byte[arraySize];
	//info[1]=2;
	//tempFilePath = tempFile.getPath().replace("Client", "Server");;
	//System.arraycopy(tempFilePath.getBytes(),0, info,2,tempFilePath.length());
	//initTransmitDatagramPacket(info,serverPort);
	return true;
}
	 /*
	  * Creates a new file
	  * @param file - the file to create
	  */
	private void createFile(File file)
	{
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Appends data to a file
	 * @param data, writeToFile
	 * The data that will be saved in the file, this method does not overwrite the file.
	 */
	private void appendToAFile(byte[] data, File writeToFile){
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(writeToFile.getPath(), true)));
		    out.println(new String(data));
		    out.close();
		} catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
	}
	
	/*
	 * Over writes the contents of the file
	 * @param data, writeToFile
	 * Data to write to the file. 
	 * The file to over write.
	 * 
	 */
	private void overWriteFile(byte[] data, File writeToFile){
		            FileOutputStream fos;
					try {
						fos = new FileOutputStream(writeToFile, false);
			            fos.write(data);
			            fos.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

		    
		}
		 
		 
	private void readFileFromServerProcedure() {
		
	}

	/*
	 * Initializes the datagram packet and sends it to the known port of the server
	 */
	private void initTransmitDatagramPacket(byte[] tempInfo, int portNumToSend, DatagramPacket sendPacket, DatagramSocket clientSocket)
	{
        try {
            sendPacket = new DatagramPacket(tempInfo, tempInfo.length,InetAddress.getLocalHost(), portNumToSend);
            clientSocket.send(sendPacket);
         } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
         }
	}
	
	/*
	 * Initializes the socket port for the client (which is arbitrary)
	 * @return DatagramSocket - the client socket to be used
	 */
	private DatagramSocket initDatagramSocket()
	{
		DatagramSocket clientSocket=null;
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clientSocket;
	}
	
	/*
	 * Reads data from the client port
	 */
	private void readFromPort(DatagramPacket packetToReceive, DatagramSocket ClientSocket)
	{
		byte[] tempData= new byte[arraySize];
		packetToReceive=new DatagramPacket(tempData, tempData.length);
		try {
			ClientSocket.receive(packetToReceive);
			servicePort = packetToReceive.getPort();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * sends a kill message to the network simulator which then sends it to the server
	 */
	private void exitNetworkSim(DatagramPacket sendPacket, DatagramSocket clientSocket) 
	{
		byte[] arrayInfo = new byte[arraySize];
		arrayInfo[0]=-1;
		arrayInfo[1]=-1;
		initTransmitDatagramPacket(arrayInfo,serverPort,sendPacket, clientSocket);
	}
	public static void main(String args[])
	{
		directorySimulator ds=new directorySimulator();
		NetworkTFTPClient c = new NetworkTFTPClient();
		c.runClient();

	}
}
