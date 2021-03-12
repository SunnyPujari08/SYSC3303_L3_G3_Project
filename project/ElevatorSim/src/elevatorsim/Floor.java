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
import java.util.ArrayList;
import java.util.Collections;
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
    
    public Floor(int floorNum) {
    	this.floorNum = floorNum;
    	try {
			networkSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
    }
	
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

    public static EventData[] convertTextEvent(String rawData) throws ParseException {
    	String[] eString = rawData.split(";");
    	
    	EventData[] eData = new EventData[eString.length]; 
    	for (int i = 0; i < eString.length; i++) {
    		String[] eInfo = eString[i].split(" ");
    		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss.mmm");
        	Date timeStamp = dateFormat.parse(eInfo[0]);
        	EventType eType = null;
        	int floorNum = Integer.parseInt(eInfo[1]);
        	if (eInfo[2].equals("Up"))
        		eType = EventType.FLOOR_REQUEST_UP;
        	else if (eInfo[2].equals("Down"))
        		eType = EventType.FLOOR_REQUEST_DOWN;
        	int destination = Integer.parseInt(eInfo[3]);
        	eData[i] = new EventData(timeStamp, floorNum, eType, destination);
    	}
    	
    	return eData;
    }
    
    /*
    public EventData readFromScheduler() {
    	// Check if empty
    	// Logic if/else checking eventType
    	// event = eventList.remove(0);
    	// return event;
    }
    */
    
    
    /**
     * Below: UDP functions===================================================================
     */
    
    private void formPacket(String info) {
    	byte[] data = info.getBytes();
    	try {
			packetOut = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 101);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    }
    
    private void rpc_send() {
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
    }
    
    public static void printPacketInfo(DatagramPacket p) {
		int len = p.getLength();
		String dataString = new String(p.getData(), 0, len);
		
		Constants.formattedPrint("Data in string: " + dataString);
    }
    
	public void run() {
		String rawData = readEventFromTextFile(FILENAME);
		if (rawData.length() > 0)
			formPacket(rawData);
		while (packetOut != null)
			rpc_send();
		// Maybe wait for reply?
	}
}
