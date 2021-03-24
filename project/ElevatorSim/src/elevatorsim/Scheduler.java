package elevatorsim;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import elevatorsim.elevator.Elevator;
import elevatorsim.elevator.ElevatorState;
import elevatorsim.elevator.ElevatorStateOne;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Ezra Pierce
 *
 * Scheduler class for SYSC3303 group project. L3-G3
 * 
 * For iteration 1 this class creates one floor thread and one elevator thread, then
 * waits for a new event from the Floor. Once an event is read, it is sent to the Elevator.
 * Then it waits for the event to be returned from the elevator and confirms that it has been acknowledged.
 */
public class Scheduler {
	private static final int MAX_MESSAGE_LEN = 100;		// Maximum message length
	private DatagramPacket receivePacket, replyPacket;
	private DatagramSocket receiveSocket, replySocket;	// Socket for receiving and replying

	public List<List> masterFloorEventList = new ArrayList<>();
	public List<List> masterElevatorEventList = new ArrayList<>();


	private boolean elevatorReadReq = false; // TODO: Change to array, need Read Request flag for each elevator
	public int elevatorCurrentFloor = 1, elevatorDestinationFloor = 1;
	// TODO: public int[] elevatorsCurrentFloors, elevatorsDestinationFloors = 1; TODO make it work with multiple elevators
	private long startTimeInMilliSeconds;

	private int numOfStates = 9;
	private ArrayList<SchedulerState> stateList;
	private int startState = Constants.SCHEDULER_STATE_ONE;
	public List<String> rawEvents = Collections.synchronizedList(new ArrayList<>());
	private List<String> futureEvents = new ArrayList<>();
	private List<String> sendQueueForElevator = new ArrayList<>(); // TODO make this into a list of lists, need one list queue for each elevator
	public EventData currentTripEvent; // TODO need to make into list, one for each elevator
	private SchedulerState currentState;
	
	public Scheduler() {
		try {
			receiveSocket = new DatagramSocket(Constants.UDP_PORT_NUMBER);
			replySocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		this.setupFloorLists();
		this.setupElevatorLists();
		this.setupStateMachine();
	}
	
    public void closeSockets() {
    	receiveSocket.close();
    	replySocket.close();
    }

	// Main function for project
	public static void main(String[] args) throws ParseException {
		Scheduler scheduler = new Scheduler();
		
		// Set starting state
	    SchedulerState currentState = scheduler.stateList.get(scheduler.startState);
    	int nextStateID;
    	Constants.formattedPrint("Starting Scheduler SM.");
    	scheduler.startTimeInMilliSeconds = System.currentTimeMillis();
    	Constants.formattedPrint("Start: "+ String.valueOf(scheduler.startTimeInMilliSeconds));
        while(true){
        	
        	// .run() call will block until state change occurs
        	nextStateID = currentState.run();
        	Constants.formattedPrint("Scheduler moving to state " + String.valueOf(nextStateID));
        	if(nextStateID < 0) { break;}
        	currentState = scheduler.stateList.get(nextStateID);                                                                                                                                                                                                                                                                                                                                                                
		}
		Constants.formattedPrint("Scheduler state machine failed, thread exiting.");
	}
	
	/* Function adds eventToWrite to list specified by floorNumber
	 * Arguments:
	 * floorNumber - Specifies which floor to write to
	 * eventToWrite - Event object to be added to list
	 */
	public void writeToFloor(Integer floorNumber, EventData eventToWrite) {
		eventToWrite.fromScheduler = true;
		(this.masterFloorEventList.get(floorNumber-1)).add(eventToWrite);
	}
	
	/* Function converts eventToWrite into String,
	 * Adds it to the outgoing event queue for specified elevator.
	 * 
	 * Arguments:
	 * elevatorNumber - Specifies which elevator to write to
	 * eventToWrite - Event object to be added to list
	 */
	public void writeToElevator(Integer elevatorNumber, EventData eventToWrite) {
		// Convert to string
		// Send over UDP To elevator
		String eventString = EventData.convertEventToString(eventToWrite);
		// Add string to a list
		this.sendQueueForElevator.add(eventString); // TODO: will need to add to specific queue for elevatorNumber
	}
	
	public void sendUpRequestToElevator(int elevatorID, int destinationFloor) {
		EventData reqEvent = new EventData(EventType.MOVE_REQUEST_UP, destinationFloor);
		this.writeToElevator(elevatorID, reqEvent);
		Constants.formattedPrint("MOVE_UP_REQUEST sent to elevator.");
	}
	
	public void sendDownRequestToElevator(int elevatorID, int destinationFloor) {
		// TODO Create event and add it to the elevator's event list
		EventData reqEvent = new EventData(EventType.MOVE_REQUEST_DOWN, destinationFloor);
		this.writeToElevator(elevatorID, reqEvent);
		Constants.formattedPrint("This is the action: SendDownRequestToElevator: " + String.valueOf(destinationFloor));
	}
	
	public void sendResponseToFloor(int floorNum) {
		// TODO Create event and add it to floor list
		EventData reqEvent = new EventData(EventType.ELEVATOR_ARRIVED);
		this.writeToFloor(floorNum, reqEvent);
		Constants.formattedPrint("This is the action: SendResponseToFloor");
	}
	
	private void setupFloorLists() {
		for(int i = 0; i < Constants.NUMBER_OF_FLOORS; i++) {
			List<EventData> floorEventList = Collections.synchronizedList(new ArrayList<>());
			this.masterFloorEventList.add(floorEventList);
		}
	}
	
	private void setupElevatorLists() {
		for(int i = 0; i <= Constants.NUMBER_OF_ELEVATORS; i++) {
			List<EventData> elevatorEventList = Collections.synchronizedList(new ArrayList<>());
			this.masterElevatorEventList.add(elevatorEventList);
		}
	}
	
	
	/*
	 * Function reads from list related to specified floor
	 * 
	 * Arguments:
	 * floorNumber - Specifies which floor to read from
	 * Returns:
	 * EventData - Returns one event if one exists or null if none exist
	 */
	public synchronized EventData readFromFloor(Integer floorNumber) {
		//read from index of listOfLists
		// Just one floor for iteration 1
		if((this.masterFloorEventList.get(floorNumber-1)).size()>0) {
			for(int i = 0; i < (this.masterFloorEventList.get(floorNumber-1)).size(); i++){
				if(!((EventData)(this.masterFloorEventList.get(0)).get(i)).fromScheduler){
					EventData newEvent = (EventData)(this.masterFloorEventList.get(0)).remove(i);
					return newEvent;
				}
			}
		}
		return null;
	}
	
	/*
	 * Function converts datastring from UDP packet into a new event.
	 * 
	 * Arguments:
	 * dataString - String from UDP packet sent by elevator
	 * Returns:
	 * EventData - Returns one event if one exists and it has been acknowledged by the elevator or null if none exist
	 */
	public synchronized EventData readFromElevator( String dataString) {
		Constants.formattedPrint("Reading event from elevator...");
	
		EventData newEvent = EventData.convertStringToEvent(dataString);
		if(newEvent != null) {
			return newEvent;
		}
	
		return null;
	}
	
	public synchronized EventData readFromAllFloors() {
		for(int i = 0; i < Constants.NUMBER_OF_FLOORS; i++) {
			if((this.masterFloorEventList.get(i)).size()>0) {
				for(int j = 0; j < (this.masterFloorEventList.get(i)).size(); j++){
					//if(!((EventData)(this.masterFloorEventList.get(i)).get(j)).fromScheduler){
						EventData newEvent = (EventData)(this.masterFloorEventList.get(i)).remove(j);
						return newEvent;
					//}
				}
			}
		}
		return null;
	}
	
	private void setupStateMachine() {
    	stateList = new ArrayList<SchedulerState>(numOfStates);
		stateList.add(Constants.SCHEDULER_STATE_IDLE, new SchedulerStateIdle(this, 0));
		stateList.add(Constants.SCHEDULER_STATE_ONE, new SchedulerStateOne(this, 0));
		stateList.add(Constants.SCHEDULER_STATE_TWO, new SchedulerStateTwo(this, 0));
		stateList.add(Constants.SCHEDULER_STATE_THREE, new SchedulerStateThree(this, 0));
		stateList.add(Constants.SCHEDULER_STATE_FOUR, new SchedulerStateFour(this, 0));
//		stateList.add(Constants.SCHEDULER_STATE_FIVE, new SchedulerStateFive(this));
//		stateList.add(Constants.SCHEDULER_STATE_SIX, new SchedulerStateSix(this));
//		stateList.add(Constants.SCHEDULER_STATE_SEVEN, new SchedulerStateSeven(this));
//		stateList.add(Constants.ELEVATOR_STATE_EIGHT, new SchedulerStateEight(this));


	}

	
	/*
	 * This method is used to add events into the system when the time listed in the input file has elapsed
	 * The variable 'this.futureEvents' contains all of the events read from the Floors. Each of these
	 * events has a time associated with it. Once that specified amount of time has passed, the event will be added 
	 * into the system by adding the event to the list corresponding to the floor which the event originated from.
	 * 
	 * NOTE: currently this method can only create FLOOR_REQUEST event types, more code will be needed if any
	 * other event types need to be supported
	 */
	public void populateEvents() {
		//TODO: Jeong-won is working on this
	}
	
	/*
	 * This method splits all the events in 'this.rawEvents' and returns them all as a list of strings.
	 */
	public List<String> parseEvents() {
		List<String> eventStrings = new ArrayList<String>();
		if(this.rawEvents.size() > 0) {
			for(int i =0; i< this.rawEvents.size(); i++) {
				eventStrings.addAll(Arrays.asList(this.rawEvents.remove(i).split(";")));
			}
			return eventStrings;
		}
		return null;
	}
	
	/*
	 * This method calls 'this.readOverUDP()'
	 * If there is a new event received over UDP then it returns that.
	 * (Currently only events from elevators can be read this way).
	 * 
	 * If there is no event then it checks if there are any new events from
	 * floor text file and parses them and adds them to future events. (This should only occur at the start
	 * of the program).
	 */
	public EventData readFromUDPSocket() {
		List<String> eventStrings;
		EventData newEvent;
    	newEvent = this.readOverUDP();
    	if(newEvent == null) {
	    	eventStrings = this.parseEvents();
	    	if(eventStrings != null && (eventStrings.size()>0))
	    		this.futureEvents = eventStrings;
	    	return null;
    	}
    	return newEvent;
	}

	public int startState() {
    	return(startState);
    }
	
	public SchedulerState SchedulerState() {
    	return(currentState);
    }
    
    public ArrayList<SchedulerState> stateList(){
    	return(stateList);
    }


    /**
     * Below: UDP functions===================================================================
     */
	public EventData readOverUDP() {
		int receivePort = this.receive();
		int len = this.receivePacket.getLength();
		String dataString = new String(this.receivePacket.getData(), 0, len);
		
		// If the packet being read is a read request(Elevator) then set read request flag
		// If flag is set then reply with the first event in the outgoing elevator event queue.
		// TODO: Will need to check which elevator is sending the request and respond accordingly
		if(dataString.startsWith("Read")) {
			this.elevatorReadReq = true;
		}
		if(this.elevatorReadReq) {
			if(this.sendQueueForElevator.size()>0) {
				this.formReplyPacket(this.sendQueueForElevator.remove(0), receivePort);
				this.reply();
			}
		}
		
		// TODO: Need to come up with better way to check if it's an event other than underscores
		// This if statement is for when the elevator has sent an event to the scheduler
		if(dataString.contains("_")) { // Underscore means event
			return this.readFromElevator(dataString);
		}
		
		// If neither of the above if statements are true then it must be a packet from a floor
		// Packets from floors are added to 'this.rawEvents'
		if(!dataString.startsWith("Read")) {
			this.formReplyPacket("Data received from floor", receivePort);
	    	this.reply();
	    	this.rawEvents.add(dataString);
		}
		//}
    	return null;
	}
	
	/*
	 * Method blocks until new UDP packet is received. Packet is set to variable 'this.receivePacket'.
	 * Port number that the packet was received on is returned.
	 */
	private synchronized int receive() {
		byte receivedData[] = new byte[MAX_MESSAGE_LEN];
		receivePacket = new DatagramPacket(receivedData, receivedData.length);
		System.out.println("Scheduler: Waiting for Packet...\n");
		
		try {
			System.out.println("Waiting...");		// Waiting until packet comes
			receiveSocket.receive(receivePacket);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Scheduler: Packet received.");
		if(new String(receivePacket.getData()).startsWith("Read-Ele"))
			this.elevatorReadReq = true;
		Floor.printPacketInfo(receivePacket);
		return receivePacket.getPort();
	}
	
	/*
	 * Sets variable this.replyPacket to a new packet with the specified data string and port #
	 */
	private void formReplyPacket(String data, int replyPort) {
		byte[] msg = data.getBytes();
		try {
			replyPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), replyPort);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
	}
	
	/*
	 * Sends out 'this.replyPacket' over 'this.replySocket'
	 * NOTE: this.formReplySocket() should be called before this function
	 */
	private synchronized void reply() {
		System.out.println("Scheduler: delivering reply packet");
		Floor.printPacketInfo(replyPacket);
		
		try {
			replySocket.send(replyPacket);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Scheduler: reply packet delivered.\n");
	}
}
