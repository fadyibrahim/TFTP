import java.nio.ByteBuffer;
import java.util.Scanner;


public class test {

	private byte[] info;
	private int arraySize=516, maxBlockNum=127;
	private byte secondBlockNum=0,firstBlockNum=0;
	public test()
	{
		byte b= (byte) 128;

		initSendAckPacket();
	}
	private void initSendAckPacket()
	{
		int i=0;
		while(i!=32513)
		{
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
		}else
		{

			info[2]=firstBlockNum;
			info[3]=secondBlockNum;
		}
		i++;
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//test t = new test();
		//System.out.print(args[0]);
		//System.out.print(args[1]);
        String inputLine;   // will hold the full input line

        Scanner reader = new Scanner(System.in);
        Byte b=10;
        Byte c= 10;
        String[] s= new String[3];
        s[0]="asdas";
        s[1]="asdas";
        s[2]="asdas";
        s[3]="asdas";
        System.out.print(b.compareTo(c));
		
	}

}
