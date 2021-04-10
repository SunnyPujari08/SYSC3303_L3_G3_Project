package elevatorsim.elevator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import elevatorsim.Constants;
import elevatorsim.EventData;
import elevatorsim.EventType;
import elevatorsim.Floor;

/**
 * The Elevator class represents an Elevator in the Elevator
 * Control System.
 *
 * @author Kashish Saxena - 101107204, Sunjeevani Pujari - 101110032, Ezra Pierce - 100991590
 * @version February 27, 2021
 */

public class Elevator extends Thread {
	private static final int MAX_MESSAGE_LEN = 100;		// Maximum message length
	private DatagramPacket packetOut, packetIn;			// Packet going out and packet coming in
	private DatagramSocket sendSocket, receiveSocket;

	private int elevatorID;
	public int currentFloor;
	private int direction; // -1 = going down; 0 = stop; 1 = going up  
	public Integer destFloor;
	public Integer pickupFloor;
	public boolean pickedUpPassenger = false;
	public boolean moveFaultInjected = false;
	public boolean doorFaultInjected = false;
	private boolean isDoorOpen = false;
	private int numOfStates = 12;
	public List<EventData> eventList = new ArrayList<EventData>();;
    private ArrayList<ElevatorState> stateList;
    private int startState = Constants.ELEVATOR_STATE_ONE;
    private ElevatorState currentState;
    private ArrayList<String> events = new ArrayList<String>();


    /*
     * Creates new Elevator with corresponding elevator ID. Also opens a socket.
     * NOTE: Elevators are created at floor 1.
     */
    public Elevator(int elevatorID) {
    	try {
    		sendSocket = new DatagramSocket();
    		receiveSocket = new DatagramSocket(200 + elevatorID);
		} catch (SocketException e) {
			e.printStackTrace();
		}
    	
        this.elevatorID = elevatorID;
        currentFloor = 1;
        direction = 0;
        setupStateMachine();
    }
    
    public void closeSocket() {
    	sendSocket.close();
    	receiveSocket.close();
    }

    @Override
    public void run() {
    	currentState= stateList.get(startState);
    	int nextStateID;
        while(true){
        	// .run() call will block until state change occurs
        	nextStateID = currentState.run();
        	//Constants.formattedPrint("Elevator moving to state " + String.valueOf(nextStateID + 1));
        	if(nextStateID < 0) { break;}
        	currentState = stateList.get(nextStateID);
        }
        Constants.formattedPrint("FAULT DETECTED, ELEVATOR STUCK: Thread exiting.");
        this.sendFault();
    }
    
    private void sendFault() {
    	String info = "e;" + elevatorID + ";" + "fault";
    	byte[] data = info.getBytes();
    	try {
			packetOut = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), Constants.UDP_PORT_NUMBER);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    	sendUpdate();
    }

    public void openElevatorDoor() {
    	//OpenElevatorDoor - Open timer starts (Occurs when OpenDoor event is true && CloseDoor event is false)
    	sleep(Constants.DOOR_TIME);
    	if(this.doorFaultInjected) {
    		Constants.formattedPrint("Door open timed out. Retrying.");
    		this.doorFaultInjected = false;
    		this.openElevatorDoor();
    		return;
    	}
    	isDoorOpen = true;

    	Constants.formattedPrint("Door Opened");
    }
    
    public void closeElevatorDoor() {
    	//CloseElevatorDoor - Close timer starts (Occurs when OpenDoor event is true && CloseDoor event is false)
    	sleep(Constants.DOOR_TIME);
    	if(this.doorFaultInjected) {
    		Constants.formattedPrint("Door close timed out. Retrying.");
    		this.doorFaultInjected = false;
    		this.closeElevatorDoor();
    		return;
    	}
    	
    	isDoorOpen = false;
    	Constants.formattedPrint("Door Closed");
    }
    
    
    public static void sleep(long millies) {
    	try {
			Thread.sleep(millies);
		} catch (InterruptedException e) {
			System.out.println("Thread is interrupted");
			Thread.currentThread().interrupt();
		}
    }


    /*
     * This method reads from the elevators event list. Currently the only event
     * that should be in the event list is the ELEVATOR_ARR_UP & ELEVATOR_ARR_DOWN events,
     * which are self generated whenever the moveUpOneFloor() and moveDownOneFloor() methods are called.
     */
	public EventData checkForSensorEvents() {
		// Check for sensor events, arriving at floor, button presses etc...
       if(eventList.size() > 0) {
        	EventData newEvent = eventList.remove(0);
        	return newEvent;
        } else {
        	return null;
        }
	}
	
	
	/*
	 * This methods changes the current floor variable and creates an event accordingly
	 * TODO: Add timing variables according to Iteration 0
	 */
	public void moveUpOneFloor() {
		if(this.currentFloor < Constants.NUMBER_OF_FLOORS) {
			this.currentFloor++;
			Constants.formattedPrint("Elevator moving up one floor. Now at: " + String.valueOf(this.currentFloor));
			EventData event = new EventData(this.currentFloor, EventType.ELEVATOR_ARR_FLOOR_UP);
			// CHECK FOR FAULT FLAG
			if(this.moveFaultInjected) {
				sleep(Constants.MOVE_TIME * 2);
			} else {
				sleep(Constants.MOVE_TIME);
			}
			this.eventList.add(event);
			//this.sendEventToScheduler(event);
		} else {
			Constants.formattedPrint("Elevator can't move up one floor. Still at: " + String.valueOf(this.currentFloor));
		}
	}
	
	
	/*
	 * This methods changes the current floor variable and creates an event accordingly
	 * TODO: Add timing variables according to Iteration 0
	 */
	public void moveDownOneFloor() {
		if(this.currentFloor > 1) {
			this.currentFloor--;
			Constants.formattedPrint("Elevator moving down one floor. Now at: " + String.valueOf(this.currentFloor));
			EventData event = new EventData(this.currentFloor, EventType.ELEVATOR_ARR_FLOOR_DOWN);
			// CHECK FOR FAULT FLAG
			if(this.moveFaultInjected) {
				sleep(Constants.MOVE_TIME * 2);
			} else {
				sleep(Constants.MOVE_TIME);
			}
			this.eventList.add(event);
		} else {
			Constants.formattedPrint("Elevator can't move down one floor. Still at: " + String.valueOf(this.currentFloor));
		}
	}
	
	 public void pickFloor() {
		 Constants.formattedPrint("Floor picked in elevator");
	    	if(this.currentFloor < this.destFloor) {
	    		EventData event = new EventData(EventType.MOVE_REQUEST_UP, this.currentFloor, this.destFloor);
	    		this.eventList.add(event);
	    	} else if(this.currentFloor > this.destFloor){
	    		EventData event = new EventData(EventType.MOVE_REQUEST_DOWN, this.currentFloor, this.destFloor);
	    		this.eventList.add(event);
	    	}
	    	
	}
    
    
    private void setupStateMachine() {
    	stateList = new ArrayList<ElevatorState>(numOfStates);
        stateList.add(Constants.ELEVATOR_STATE_ONE, new ElevatorStateOne(this));
        stateList.add(Constants.ELEVATOR_STATE_TWO, new ElevatorStateTwo(this));
        stateList.add(Constants.ELEVATOR_STATE_THREE, new ElevatorStateThree(this));
        stateList.add(Constants.ELEVATOR_STATE_FOUR, new ElevatorStateFour(this));
    }
    
    public int startState() {
    	return(startState);
    }
    
    public ElevatorState ElevatorState() {
    	return(currentState);
    }
    
    public ArrayList<ElevatorState> stateList(){
    	return(stateList);
    }
    
    //formPacket("f;" + floorNum + ";" + curEvent);
    //0 2 Up 4
    public String getFloor(String event) {
    	String eData = event.split(";")[2];
    	return eData.split(" ")[1];
    }
    
    public String getDestination(String event) {
    	String eData = event.split(";")[2];
    	return eData.split(" ")[3];
    }
    
    public void shutDownThread() {
    	this.interrupt();
    }
    
   
    
    public EventData parseSchedulerReply() {
    	// TODO: add parsing for faults
    	List<String> msgArgs = new ArrayList<String>();
    	EventData event = null;
    	if(events.size()>0) {
    		msgArgs.addAll(Arrays.asList(events.remove(0).split(";")));
    		msgArgs.addAll(Arrays.asList(msgArgs.remove(2).split(" "))); // expanding text from text file
    		if(msgArgs.size()>=7) {
	    		// First argument is 'f' for floor
	    		// Second argument is floor number
	    		// Third argument is timestamp
	    		// Fourth argument is floor number
	    		// Fifth argument is up/down
	    		// Sixth argument is destination floor
    			// Seventh is for faults
	    		if(this.currentFloor > Integer.parseInt(msgArgs.get(3))) {
	    			event = new EventData(EventType.MOVE_REQUEST_DOWN, this.currentFloor, Integer.parseInt(msgArgs.get(3)));
	    		} else if(this.currentFloor < Integer.parseInt(msgArgs.get(3))) {
	    			event = new EventData(EventType.MOVE_REQUEST_UP, this.currentFloor, Integer.parseInt(msgArgs.get(3)));
	    		} 
	    		this.pickupFloor = Integer.parseInt(msgArgs.get(3));
	    		this.destFloor = Integer.parseInt(msgArgs.get(5));
	    		if(msgArgs.get(6).equalsIgnoreCase("MoveFault")) {
	    			this.moveFaultInjected = true;
	    		} else if(msgArgs.get(6).equalsIgnoreCase("DoorFault")) {
	    			this.doorFaultInjected = true;
	    		}
    		}
    	}
    	return event;
    }
    
    /**
     * Below: UDP functions===================================================================
     */
    /*
     * Forms a new packet with specified string data, assigns it to 'this.packetOut'
     */

    public void formPacket() {
    	String info = "e;" + elevatorID + ";" + currentFloor + ";" + direction;
    	byte[] data = info.getBytes();
    	try {
			packetOut = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), Constants.UDP_PORT_NUMBER);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    }
    
    public synchronized void sendUpdate() {
    	// Send a packet with current elevator state
		try {
			sendSocket.send(packetOut);
			packetOut = null;
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
    }
    
    // NOTE: This method has a timeout specified by 'this.udpTimeoutInMilliSeconds'
    public synchronized void recv() {
		// Receive a reply packet
		byte data[] = new byte[MAX_MESSAGE_LEN];
		packetIn = new DatagramPacket(data, data.length);
		
		try {
			receiveSocket.receive(packetIn);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		int len = packetIn.getLength();
		if (len > 0) {
			Floor.printPacketInfo(packetIn);
			String message = new String(packetIn.getData(), 0, len);
			events.add(message);
		}
    }
    
    /*
     * 1. Sends packet 'this.packetOut' over 'this.networkSocket'
     * 2. Waits for reply packet over 'this.networkSocket'
     * 3. If timeout expires then returns null
     * 4. If packet is received, assign to 'this.packetIn' and return packet data.
     */
    public EventData rpc_send() {
    	formPacket();
    	sendUpdate();
    	recv();
    	return parseSchedulerReply();
    }
    
    
	public static void main(String args[]) {
		
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter the number of elevators");
		Constants.NUMBER_OF_ELEVATORS = keyboard.nextInt();
		List<Elevator> elevators = new ArrayList<Elevator>();
		
		Thread[] elevatorThreads = new Thread[Constants.NUMBER_OF_ELEVATORS];
		Elevator newElevator;
		for(int i = 0; i < Constants.NUMBER_OF_ELEVATORS; i++) {
			newElevator = new Elevator(i+1);
			elevators.add(newElevator);
			elevatorThreads[i] = new Thread(newElevator, "Elevator" + (i+1));

		}
		for(int i = 0; i < Constants.NUMBER_OF_ELEVATORS; i++) {
			elevatorThreads[i].start();
		}
	}
}