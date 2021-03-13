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

	// private masterEventList ...
	public List<List> masterFloorEventList = new ArrayList<>();
	public List<List> masterElevatorEventList = new ArrayList<>();
	private Floor[] floors;
	public Elevator[] elevators;
	private Thread buttonSimulator;
	private long startTimeInSeconds;
	private int numOfStates = 9;
	private ArrayList<SchedulerState> stateList;
	private int startState = Constants.SCHEDULER_STATE_ONE;
	public List<String> rawEvents = Collections.synchronizedList(new ArrayList<>());
	private List<String> futureEvents = new ArrayList<>();
	private List<String> currentEvents = new ArrayList<>();
	public EventData currentTripEvent;
	
	public Scheduler() {
		floors = new Floor[Constants.NUMBER_OF_FLOORS];
		elevators = new Elevator[Constants.NUMBER_OF_ELEVATORS];
		
		try {
			receiveSocket = new DatagramSocket(101);
			replySocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.setupFloorLists();
		this.setupElevatorLists();
		//this.setupButtonSimulator();
		this.setupStateMachine();
	}
	

	// Main function for project
	public static void main(String[] args) throws ParseException {
		Scheduler scheduler = new Scheduler();
		
	    SchedulerState currentState= scheduler.stateList.get(scheduler.startState);
    	int nextStateID;
    	Constants.formattedPrint("Starting Scheduler SM.");
    	scheduler.readOverUDP(); 
    	scheduler.futureEvents = scheduler.parseEvents();
    	scheduler.startTimeInSeconds = System.currentTimeMillis()/1000;
        while(true){
        	
        	
        	// .run() call will block until state change occurs
        	nextStateID = currentState.run();
        	Constants.formattedPrint("Scheduler moving to state " + String.valueOf(nextStateID));
        	if(nextStateID < 0) { break;}
        	currentState = scheduler.stateList.get(nextStateID);                                                                                                                                                                                                                                                                                                                                                                
		}
		//Constants.formattedPrint("Scheduler state machine failed, thread exiting.");
	}
	
	/* Function adds eventToWrite to list specified by floorNumber
	 * Arguments:
	 * floorNumber - Specifies which floor to write to
	 * eventToWrite - Event object to be added to list
	 */
	public void writeToFloor(Integer floorNumber, EventData eventToWrite) {
		eventToWrite.fromScheduler = true;
		(this.masterFloorEventList.get(floorNumber-1)).add(eventToWrite);
		// Send over UDP
	}
	
	/* Function converts eventToWrite into String,
	 * Sends it over UDP to elevator w/ elevatorNumber
	 * 
	 * Arguments:
	 * elevatorNumber - Specifies which elevator to write to
	 * eventToWrite - Event object to be added to list
	 */
	public void writeToElevator(Integer elevatorNumber, EventData eventToWrite) {
		// Convert to string
		// Send over UDP To elevator
		
		
		
		
		
		//(this.masterElevatorEventList.get(elevatorNumber-1)).add(eventToWrite);
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
	
			
	private void setupButtonSimulator() {
		buttonSimulator = new Thread(new buttonSimulator(this.masterFloorEventList, this.masterElevatorEventList));
		buttonSimulator.start();
	}
	
	
	/*
	 * Function reads from list related to specified floor, for Iteration 1 it also checks that the event was acknowledged
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
	 * Function reads from list related to specified elevator
	 * 
	 * Arguments:
	 * elevatorNumber - Specifies which elevator to read from
	 * Returns:
	 * EventData - Returns one event if one exists and it has been acknowledged by the elevator or null if none exist
	 */
	public synchronized EventData readFromElevator(Integer elevatorNumber) {
		//read from index of listOfLists
		// Check that there is an event
//			for(int i = 0; i < (this.masterElevatorEventList.get(elevatorNumber-1)).size(); i++){
//				if(this.masterElevatorEventList.size()>0 && (this.masterElevatorEventList.get(elevatorNumber-1)).size()>0) { //&& !((EventData)(this.masterElevatorEventList.get(elevatorNumber-1)).get(i)).fromScheduler) {
//						EventData newEvent = (EventData)(this.masterElevatorEventList.get(elevatorNumber-1)).remove(i);
//						return newEvent;
//					}
//			}	
		
		
		// Needs to be redone with UDP
		// Read one packet from elevator
		// Parse string into an EventData object
		// Return event
		// Return null if there are no packets
		
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
		// TODO instantiate all states
    	stateList = new ArrayList<SchedulerState>(numOfStates);
		stateList.add(new SchedulerStateIdle(this));
		stateList.add(new SchedulerStateOne(this));
		stateList.add(Constants.SCHEDULER_STATE_TWO, new SchedulerStateTwo(this));
		stateList.add(Constants.SCHEDULER_STATE_THREE, new SchedulerStateThree(this));
		stateList.add(Constants.SCHEDULER_STATE_FOUR, new SchedulerStateFour(this));
		stateList.add(Constants.SCHEDULER_STATE_FIVE, new SchedulerStateFive(this));
		stateList.add(Constants.SCHEDULER_STATE_SIX, new SchedulerStateSix(this));
		stateList.add(Constants.SCHEDULER_STATE_SEVEN, new SchedulerStateSeven(this));
		stateList.add(Constants.ELEVATOR_STATE_EIGHT, new SchedulerStateEight(this));

	}
	
//	private void addEvents(String rawData) {
//    	String[] eString = rawData.split(";");
//    	
//    	EventData[] eData = new EventData[eString.length]; 
//    	for (int i = 0; i < eString.length; i++) {
//    		String[] eInfo = eString[i].split(" ");
//    		
//        	int floorNum = Integer.parseInt(eInfo[1]);
//        	eventList.add(new EventData(EventType.ELEVATOR_PICK_FLOOR, floorNum, true));
//    	}
//    }
	
	public void populateEvents() {
		String eventString;
		String[] eventStringArgs;
		long currentTimeInSeconds = System.currentTimeMillis()/1000;
		for(int i = 0; i < this.futureEvents.size(); i++) {
			eventString = this.futureEvents.get(i);
			eventStringArgs = eventString.split(" ");
			if(eventStringArgs.length>0 && Integer.parseInt(eventStringArgs[0]) <= (currentTimeInSeconds-this.startTimeInSeconds)) {
				// add to corresponding list
				EventData event = new EventData(EventType.FLOOR_REQUEST, Integer.parseInt(eventStringArgs[1]), Integer.parseInt(eventStringArgs[3]));
				this.masterFloorEventList.get(Integer.parseInt(eventStringArgs[1])).add(event);
			}
		}
	}
	
	public List<String> parseEvents() {
		if(this.rawEvents.size() > 0) {
			List<String> eventStrings = new ArrayList<String>(Arrays.asList(this.rawEvents.get(0).split(";")));
			return eventStrings;
		}
		return null;
	}

    /**
     * Below: UDP functions===================================================================
     */
	public void readOverUDP() {
		int receivePort = this.receive();
		int len = this.receivePacket.getLength();
		String dataString = new String(this.receivePacket.getData(), 0, len);
		// this is when message is from an elevator
		try {
			int i = Integer.parseInt(dataString);
			if (this.rawEvents.size() > 0) {
	    		this.formReplyPacket(this.rawEvents.remove(0), receivePort);
	    		this.reply();
			}
		// this is when message is from a floor
		} catch (NumberFormatException e) {
			this.formReplyPacket("Data received from floor", receivePort);
	    	this.reply();
	    	this.rawEvents.add(dataString);
		}
	}
	
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
		Floor.printPacketInfo(receivePacket);
		return receivePacket.getPort();
	}
	
	private void formReplyPacket(String data, int replyPort) {
		byte[] msg = data.getBytes();
		try {
			replyPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), replyPort);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
	}
	
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
