package elevatorsim;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * This class represent a floor thread
 * 
 * @author Jeong Won Kim, Sunjeevani Pujari - 101110032
 *
 */
public class Floor implements Runnable {
	private static final int MAX_MESSAGE_LEN = 100;		// Maximum message length
	private DatagramPacket packetOut, packetIn;			// Packet going out and packet coming in
	private DatagramSocket networkSocket;
	
	private Scheduler scheduler;
	private int floorNum;
	private final static String FILENAME = "events.txt";
	private List<EventData> eventList;
    private boolean UP_BUTTON = false;
    private boolean DOWN_BUTTON = false;
    private int lamp;
    
    /*
     * Creates new Floor object with a floor number and open socket
     */
    public Floor(int floorNum) {
    	this.floorNum = floorNum;
    	try {
			this.networkSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
    }
    
    public void closeSocket() {
    	this.networkSocket.close();
    }
	
    /*
     * Reads full text file specified by input string.
     * Returns string with all events pertaining to it's floor number
     * Return string has events separated by the ';' character.
     */
    public String readEventFromTextFile(String filename) {
    	String rawData = "";
        try {
        	File file = new File(filename);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
            	String next = reader.nextLine();
            	String[] eInfo = next.split(" ");
            	int floor = Integer.parseInt(eInfo[1]);
            	if (floor == floorNum) {
            		rawData += next;
            		rawData += ";";
            	}
            }
            reader.close();
        } catch (FileNotFoundException e) {
            Constants.formattedPrint("File does not exist!");
            e.printStackTrace();
        }
        
        return rawData;
    }

    public String currentEvent(String rawData) {
    	String[] eString = rawData.split(";");
    	
    	for (int i = 0; i < eString.length; i++) {
    		String[] eInfo = eString[i].split(" ");
    		
    		String time = eInfo[0];
    		//String floorNum = eInfo[1];
    		//String direction = eInfo[2];
    		//String destination = eInfo[3];

    		if (Integer.parseInt(time) == Constants.tempTimer)
    			return eString[i];
    	}
    	
    	return "";
    }
    
    /**
     * Below: UDP functions===================================================================
     */
    /*
     * Assigns object variable 'this.packetOut' with a new packet. Packet data is specified in input string.
     */
    public void formPacket(String info) {
    	byte[] data = info.getBytes();
    	try {
			this.packetOut = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), Constants.UDP_PORT_NUMBER);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    }
    
    /*
     * 1. Sends 'this.packetOut' over 'this.networkSocket'
     * 2. Waits for response on 'this.networkSocket'
     * 3. Writes response to 'this.packetIn'
     * 
     * NOTE: Will block until packet is received.
     */
    private void rpc_send() {
    	// Send a packet
		this.send();
		
		// Receive a reply packet
		this.recv(); // Ignore return string, only used in testing
    }
    
    public void send() {
    	// Send a packet
		System.out.println("Floor" + floorNum + ": Sending packet to Scheduler...");
		printPacketInfo(packetOut);
		
		try {
			networkSocket.send(packetOut);
			packetOut = null;
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Floor" + floorNum + ": Packet sent.\n");
    }
    
    public String recv() {
		// Receive a reply packet
		byte data[] = new byte[MAX_MESSAGE_LEN];
		packetIn = new DatagramPacket(data, data.length);
		System.out.println("Floor" + floorNum + ": Waiting for response from Scheduler.");
		
		try {
			System.out.println("Waiting...");		// Waiting until packet comes
			networkSocket.receive(packetIn);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Floor" + floorNum + ": Packet received.\n");
		printPacketInfo(packetIn);
		int len = packetIn.getLength();
		return new String(packetIn.getData(), 0, len);
    }
    
    public static void printPacketInfo(DatagramPacket p) {
		int len = p.getLength();
		String dataString = new String(p.getData(), 0, len);
		
		Constants.formattedPrint("Data in string: " + dataString);
    }
    
/*
 * Thread reads file once, sends all events at once
 * TODO: Maybe send one event at a time until all events are sent? currently it sends all events at once
 * TODO: Might need to be able to read new events from scheduler at some point...
 */
	public void run() {
		String rawData = readEventFromTextFile(FILENAME);
		String curEvent = "";
		while (true) {
			if (rawData.length() > 0)
				curEvent = currentEvent(rawData);
			if (curEvent.length() > 0) {
				formPacket(curEvent);
				rpc_send();
			}
			//TODO: handle received info here
		}
	}
	
	/*
	 * Creates and starts all Floor Threads
	 */
	public static void main(String args[]) {
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter the number of floors");
		Constants.NUMBER_OF_FLOORS = keyboard.nextInt();
		
		Thread[] floorThreads = new Thread[Constants.NUMBER_OF_FLOORS];
		for(int i = 0; i < Constants.NUMBER_OF_FLOORS; i++) {
			floorThreads[i] = new Thread(new Floor(i+1), "Floor" + (i+1));
		}
		for(int i = 0; i < Constants.NUMBER_OF_FLOORS; i++) {
			floorThreads[i].start();
		}
	}
}
