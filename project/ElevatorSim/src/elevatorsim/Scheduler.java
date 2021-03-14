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

	private Thread[] floorThreads;
	private Thread[] elevatorThreads;
	private boolean elevatorReadReq = false;
	public int elevatorCurrentFloor = 1, elevatorDestinationFloor = 1;
	//public int[] elevatorsCurrentFloors, elevatorsDestinationFloors = 1; TODO make it work with multiple elevators
	private long startTimeInMilliSeconds;

	private int numOfStates = 9;
	private ArrayList<SchedulerState> stateList;
	private int startState = Constants.SCHEDULER_STATE_ONE;
	public List<String> rawEvents = Collections.synchronizedList(new ArrayList<>());
	private List<String> futureEvents = new ArrayList<>();
	private List<String> currentEvents = new ArrayList<>();
	private List<String> sendQueueForElevator = new ArrayList<>();
	public EventData currentTripEvent;
	private SchedulerState currentState;
	
	public Scheduler() {
		floors = new Floor[Constants.NUMBER_OF_FLOORS];
		elevators = new Elevator[Constants.NUMBER_OF_ELEVATORS];
		
		try {
			receiveSocket = new DatagramSocket(Constants.UDP_PORT_NUMBER);
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
	
    public void closeSocket() {
    	receiveSocket.close();
    	replySocket.close();
    }

	// Main function for project
	public static void main(String[] args) throws ParseException {
		Scheduler scheduler = new Scheduler();
		
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
		String eventString = EventData.convertEventToString(eventToWrite);
		// Add string to a list
		this.sendQueueForElevator.add(eventString);
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
	public synchronized EventData readFromElevator( String dataString) {
		Constants.formattedPrint("Reading event from elevator...");
//		int port = this.receive();
//		String packetMsg = new String(this.receivePacket.getData());
	
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
		// TODO instantiate all states
    	stateList = new ArrayList<SchedulerState>(numOfStates);

		stateList.add(new SchedulerStateIdle(this, 0));
		stateList.add(new SchedulerStateOne(this, 0));
		stateList.add(Constants.SCHEDULER_STATE_TWO, new SchedulerStateTwo(this, 0));
		stateList.add(Constants.SCHEDULER_STATE_THREE, new SchedulerStateThree(this, 0));
		stateList.add(Constants.SCHEDULER_STATE_FOUR, new SchedulerStateFour(this, 0));
//		stateList.add(Constants.SCHEDULER_STATE_FIVE, new SchedulerStateFive(this));
//		stateList.add(Constants.SCHEDULER_STATE_SIX, new SchedulerStateSix(this));
//		stateList.add(Constants.SCHEDULER_STATE_SEVEN, new SchedulerStateSeven(this));
//		stateList.add(Constants.ELEVATOR_STATE_EIGHT, new SchedulerStateEight(this));


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
		long eventTime;
		long currentTimeInMilliSeconds = System.currentTimeMillis();
		Constants.formattedPrint("time: "+ String.valueOf(currentTimeInMilliSeconds-this.startTimeInMilliSeconds));
		for(int i = 0; i < this.futureEvents.size(); i++) {
			eventString = this.futureEvents.get(i);
			eventStringArgs = eventString.split(" ");
			eventTime = Long.parseLong(eventStringArgs[0]);
			if((eventStringArgs.length>0 && ((1000*eventTime) <= (currentTimeInMilliSeconds-this.startTimeInMilliSeconds)))) {
				// add to corresponding list
				Constants.formattedPrint("Event entered");
				this.futureEvents.remove(i);
				EventData event = new EventData(EventType.FLOOR_REQUEST, Integer.parseInt(eventStringArgs[1]), Integer.parseInt(eventStringArgs[3]));
				this.masterFloorEventList.get(Integer.parseInt(eventStringArgs[1])).add(event);

			}
		}
	}
	
	public List<String> parseEvents() {
		List<String> eventStrings = new ArrayList<String>();
		if(this.rawEvents.size() > 0) {
			for(int i =0; i< this.rawEvents.size(); i++) {
				Constants.formattedPrint("JKASFF" + this.rawEvents.get(i));
				eventStrings.addAll(Arrays.asList(this.rawEvents.remove(i).split(";")));
			}

			return eventStrings;
		}
		return null;
	}
	

	public EventData readFromFloorTextFile() {
		List<String> eventStrings;
		EventData newEvent;
    	newEvent = this.readOverUDP(); // TODO: I think this only reads from first floor right now, should read until all floors have sent their events
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
		// this is when message is from an elevator
		if(dataString.startsWith("Read")|| this.elevatorReadReq) {
			this.elevatorReadReq = false;
			if(this.sendQueueForElevator.size()>0) {
				this.formReplyPacket(this.sendQueueForElevator.remove(0), receivePort);
				this.reply();
			}
			//return null;
		}
		if(dataString.contains("_")) { // Underscore means event
			System.out.println("HERE2");
			return this.readFromElevator(dataString);
		}
//		try {
//			int i = Integer.parseInt(dataString);
//			if (this.rawEvents.size() > 0) {
//	    		this.formReplyPacket(this.rawEvents.remove(0), receivePort);
//	    		this.reply();
//			}
		// this is when message is from a floor
		//} catch (NumberFormatException e) {
		if(!dataString.startsWith("Read")) {
			this.formReplyPacket("Data received from floor", receivePort);
	    	this.reply();
	    	this.rawEvents.add(dataString);
		}
		//}
    	return null;
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
		if(new String(receivePacket.getData()).startsWith("Read-Ele"))
			this.elevatorReadReq = true;
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
