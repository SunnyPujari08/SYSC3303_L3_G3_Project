package elevatorsim;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import elevatorsim.elevator.Elevator;

/**
 * This class represent a floor thread
 * 
 * @author Jeong Won Kim, Sunjeevani Pujari - 101110032
 *
 */
public class Floor extends JFrame implements Runnable {
	private static final int MAX_MESSAGE_LEN = 100;		// Maximum message length
	private DatagramPacket packetOut, packetIn;			// Packet going out and packet coming in
	private DatagramSocket sendSocket, receiveSocket;
	private int floorNum;
	private final static String FILENAME = "events.txt";
    private boolean UP_BUTTON = false;
    private boolean DOWN_BUTTON = false;
    private JPanel floorPanel;
    private JLabel nameLabel;
    private JLabel dirLabel;
    
    /*
     * Creates new Floor object with a floor number and open socket
     */
    public Floor(int floorNum, JPanel panel) {
    	this.floorNum = floorNum;
    	floorPanel = panel;
    	try {
    		sendSocket = new DatagramSocket();
    		receiveSocket = new DatagramSocket(100 + floorNum);
		} catch (SocketException e) {
			e.printStackTrace();
		}
    	initPanel();
    }
    
    public void closeSocket() {
    	sendSocket.close();
    	receiveSocket.close();
    }
	
    /*
     * Reads full text file specified by input string.
     * Returns string with all events pertaining to it's floor number
     * Return string has events separated by the ';' character.
     */
    public String readEventFromTextFile(String filename) {
    	// TODO : read faults
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

    public String currentEvent(ArrayList<String> rawData) {
    	for (int i = 0; i < rawData.size(); i++) {
    		String[] eInfo = rawData.get(i).split(" ");
    		
    		String time = eInfo[0];
    		//String floorNum = eInfo[1];
    		//String direction = eInfo[2];
    		//String destination = eInfo[3];

    		if (Integer.parseInt(time) == Constants.getTime()) {
    			String result = rawData.remove(i);
    			if (eInfo[2].equals("Up"))
    				UP_BUTTON = true;
    			else
    				DOWN_BUTTON = true;
    			return result;
    		}
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
    
    public synchronized void send() {
    	// Send a packet
    	Constants.formattedPrint("Sending packet to Scheduler...");
		printPacketInfo(packetOut);
		
		try {
			sendSocket.send(packetOut);
			packetOut = null;
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		Constants.formattedPrint("Packet sent.\n");
    }
    
    public synchronized String recv() {
		// Receive a reply packet
		byte data[] = new byte[MAX_MESSAGE_LEN];
		packetIn = new DatagramPacket(data, data.length);
		Constants.formattedPrint("Waiting for response from Scheduler.");
		
		try {
			receiveSocket.receive(packetIn);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		Constants.formattedPrint("Packet received.");
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
		ArrayList<String> rawData = new ArrayList<>(Arrays.asList(readEventFromTextFile(FILENAME).split(";")));
		if (rawData.get(0).equals(""))
			rawData.remove(0);
		while (true) {
			updatePanel();
			String curEvent = "";
			if (rawData.size() > 0) {
				curEvent = currentEvent(rawData);
				if (curEvent.length() > 0) {
					formPacket("f;" + floorNum + ";" + curEvent);
					rpc_send();
				}
			} else {
				// All events sent
				formPacket("f;" + floorNum + ";" + "done");
				rpc_send();
			}
			//TODO: handle received info here
		}
	}
	
	//Creating the panel for floor
	private void initPanel() {
		floorPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		floorPanel.setLayout(new BoxLayout(floorPanel, BoxLayout.Y_AXIS));
		floorPanel.setPreferredSize(new Dimension(70, 200));
		
		String button = "---";
		if (UP_BUTTON)
			button = "UP";
		else if (DOWN_BUTTON)
			button = "DOWN";
		
        nameLabel = new JLabel("Floor" + floorNum);
        nameLabel.setFont(new Font("Serif", Font.BOLD, 20));
        dirLabel = new JLabel("Button: " + button);
        dirLabel.setFont(new Font("Ariel", Font.PLAIN, 15));

        floorPanel.add(nameLabel);
        floorPanel.add(dirLabel);
	}
	
	private void updatePanel() {
		String button = "---";
		if (UP_BUTTON)
			button = "UP";
		else if (DOWN_BUTTON)
			button = "DOWN";
		
		nameLabel.setText("Floor" + floorNum);
		dirLabel.setText("Button: " + button);
	}
	
	/*
	 * Creates and starts all Floor Threads
	 */
	public static void main(String args[]) {
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter the number of floors");
		Constants.NUMBER_OF_FLOORS = keyboard.nextInt();
		
		//Creating the Frame
        JFrame frame = new JFrame("Floor Frame");
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(250, 800);
        frame.setResizable(false);
        frame.setLayout(new GridLayout(11, 2));
		
		Thread[] floorThreads = new Thread[Constants.NUMBER_OF_FLOORS];
		JPanel[] floorPanels = new JPanel[Constants.NUMBER_OF_FLOORS];
		for(int i = Constants.NUMBER_OF_FLOORS-1; i >= 0; i--) {
			floorPanels[i] = new JPanel();
			floorThreads[i] = new Thread(new Floor(i+1, floorPanels[i]), "Floor" + (i+1));
			frame.add(BorderLayout.CENTER, floorPanels[i]);
		}
		
		//frame.getContentPane().add(BorderLayout.SOUTH, ePanel);
        frame.setVisible(true);
        
		for(int i = 0; i < Constants.NUMBER_OF_FLOORS; i++) {
			floorThreads[i].start();
		}
		Constants.startTimer();
	}
}
