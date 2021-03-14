package elevatorsim.elevator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

public class Elevator implements Runnable {
	private static final int MAX_MESSAGE_LEN = 100;		// Maximum message length
	private DatagramPacket packetOut, packetIn;			// Packet going out and packet coming in
	private DatagramSocket networkSocket;

    private EventData eData;
    public int elevatorID;
    public int currentFloor;
    public int destFloor;
    public boolean isDoorOpen = false;
    private int numOfStates = 12;
    public List<EventData> eventList = new ArrayList<EventData>();;
    private ArrayList<ElevatorState> stateList;
    private int startState = Constants.ELEVATOR_STATE_ONE;
    private ElevatorState currentState;


    public Elevator(int elevatorID) {
    	try {
			networkSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
    	
        this.elevatorID = elevatorID;
        this.currentFloor = 1;
        setupStateMachine();
    }
    
    public void closeSocket() {
    	networkSocket.close();
    }

    @Override
    public void run() {
    	currentState= stateList.get(startState);
    	int nextStateID;
        while(true){
        	//formPacket(String.valueOf(elevatorID));
        	//rpc_send();
        	
        	// .run() call will block until state change occurs
        	nextStateID = currentState.run();
        	Constants.formattedPrint("Elevator moving to state " + String.valueOf(nextStateID + 1));
        	if(nextStateID < 0) { break;}
        	currentState = stateList.get(nextStateID);
        }
        Constants.formattedPrint("Elevator state machine failed, thread exiting.");
    }
    
    public void sendElevatorArrivingAtFloorMovingUp(int floorNum) {
    	EventData newEvent = new EventData(floorNum, EventType.ELEVATOR_ARR_FLOOR_UP);
    	sendEventToScheduler(newEvent);
    	Constants.formattedPrint("This is the action: SendElevatorArrivingAtFloorMovingUp");
    }
    
    public void sendElevatorArrivingAtFloorMovingDown(int floorNum) {
    	EventData newEvent = new EventData(floorNum, EventType.ELEVATOR_ARR_FLOOR_DOWN);
    	sendEventToScheduler(newEvent);
    	Constants.formattedPrint("This is the action: SendElevatorArrivingAtFloorMovingDown");
    }
    
    public void sendElevatorPickFloor(int floorNum, int destinationFloor) {
    	EventData newEvent = new EventData(EventType.ELEVATOR_PICK_FLOOR, floorNum, destinationFloor);
    	newEvent.fromScheduler = false;
    	sendEventToScheduler(newEvent);
    	Constants.formattedPrint("This is the action: SendElevatorPickFloor");
    }
    
    public void openElevatorDoor() {
    	//OpenElevatorDoor - Open timer starts (Occurs when OpenDoor event is true && CloseDoor event is false)
    	isDoorOpen = true;

    	Constants.formattedPrint("This is the action: OpenElevatorDoor");
    }
    
    public void closeElevatorDoor() {
    	//CloseElevatorDoor - Close timer starts (Occurs when OpenDoor event is true && CloseDoor event is false)

    	isDoorOpen = false;
    	Constants.formattedPrint("This is the action: CloseElevatorDoor");

    }

    public void startElevatorAutoCloseTimer() {
    	//StartElevatorAutoCloseTimer
    	Constants.formattedPrint("This is the action: StartElevatorAutoCloseTimer");
    }
    
    public void sendEventToScheduler(EventData eData){
    	
    	// Convert event to string
    	// Send over UDP to scheduler
    	this.formPacket(EventData.convertEventToString(eData));
    	Constants.formattedPrint("Sending Event to sched from elevator");
    	String packetDataRead = this.rpc_send();
    }
    
    public synchronized EventData checkWorkFromScheduler() {
    	//Check for any new packets from scheduler
    	// Convert string event to EventData
    	// return event, return null if there are no new packets
    	this.formPacket("Read-Elevator-"+String.valueOf(this.elevatorID));
    	String packetDataRead = this.rpc_send(); // should block until event read, might need timeout
    	if(packetDataRead == null)
    		return null;
    	EventData eventRead = EventData.convertStringToEvent(packetDataRead);
    	return eventRead;
    }
    
	public EventData checkForSensorEvents() {
		// TODO create simulated sensor events
		// Check for sensor events, arriving at floor, button presses etc...
       if(eventList.size() > 0) {
        	EventData newEvent = eventList.remove(0);
        	return newEvent;
        } else {
        	return null;
        }
	}
	
	public void moveUpOneFloor() {
		if(this.currentFloor < Constants.NUMBER_OF_FLOORS) {
			this.currentFloor++;
			Constants.formattedPrint("Elevator moving up one floor. Now at: " + String.valueOf(this.currentFloor));
			EventData event = new EventData(this.currentFloor, EventType.ELEVATOR_ARR_FLOOR_UP);
			this.eventList.add(event);
			//this.sendEventToScheduler(event);
		} else {
			Constants.formattedPrint("Elevator can't move up one floor. Still at: " + String.valueOf(this.currentFloor));
		}
	}
	
	public void moveDownOneFloor() {
		if(this.currentFloor > 1) {
			this.currentFloor--;
			Constants.formattedPrint("Elevator moving down one floor. Now at: " + String.valueOf(this.currentFloor));
			EventData event = new EventData(this.currentFloor, EventType.ELEVATOR_ARR_FLOOR_DOWN);
			this.eventList.add(event);
		} else {
			Constants.formattedPrint("Elevator can't move down one floor. Still at: " + String.valueOf(this.currentFloor));
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
    
    /**
     * Below: UDP functions===================================================================
     */
    
    public void formPacket(String info) {
    	byte[] data = info.getBytes();
    	try {
			packetOut = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), Constants.UDP_PORT_NUMBER);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    }
    

    public void send() {
    	// Send a packet
		System.out.println("Elevator" + elevatorID + ": Sending packet to Scheduler...");
		
		try {
			networkSocket.send(packetOut);
			packetOut = null;
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Elevator" + elevatorID + ": Packet sent.\n");
    }
    
    public String recv() {
		// Receive a reply packet
		byte data[] = new byte[MAX_MESSAGE_LEN];
		packetIn = new DatagramPacket(data, data.length);
		System.out.println("Elevator" + elevatorID + ": Waiting for response from Scheduler.");
		
		try {
			System.out.println("Waiting...");		// Waiting until packet comes
			networkSocket.receive(packetIn);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Elevator" + elevatorID + ": Packet received.\n");
		int len = packetIn.getLength();
		return new String(packetIn.getData(), 0, len);
    }
    
    private String rpc_send() {
    	// Send a packet
		System.out.println("Elevator" + elevatorID + ": Sending packet to Scheduler...");
		Floor.printPacketInfo(packetOut);
		
		try {
			networkSocket.send(packetOut);
			packetOut = null;
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Elevator" + elevatorID + ": Packet sent.\n");
		
		// Receive a reply packet
		byte data[] = new byte[MAX_MESSAGE_LEN];
		packetIn = new DatagramPacket(data, data.length);
		System.out.println("Elevator" + elevatorID + ": Waiting for response from Scheduler.");
		
		try {
			networkSocket.setSoTimeout(100);
			networkSocket.receive(packetIn);
		} catch(SocketTimeoutException e) {
			return null;
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);

		} 

		System.out.println("Elevator" + elevatorID + ": Packet received.\n");
		
		// if len>0
		int len = packetIn.getLength();
		return new String(packetIn.getData(), 0, len);
		//addEvents(dataString);
    }
    
    private void addEvents(String rawData) {
    	String[] eString = rawData.split(";");
    	
    	EventData[] eData = new EventData[eString.length]; 
    	for (int i = 0; i < eString.length; i++) {
    		String[] eInfo = eString[i].split(" ");
    		
        	int floorNum = Integer.parseInt(eInfo[1]);
        	eventList.add(new EventData(EventType.ELEVATOR_PICK_FLOOR, floorNum, true));
    	}
    }
    
	public static void main(String args[]) {
		Thread[] elevatorThreads = new Thread[Constants.NUMBER_OF_ELEVATORS];
		Elevator[] elevators = new Elevator[Constants.NUMBER_OF_ELEVATORS];
		for(int i = 0; i < Constants.NUMBER_OF_ELEVATORS; i++) {
			elevators[i] = new Elevator(i+1);
			elevatorThreads[i] = new Thread(elevators[i]);
		}
		for(int i = 0; i < Constants.NUMBER_OF_ELEVATORS; i++) {
			elevatorThreads[i].start();
		}
	}
}


