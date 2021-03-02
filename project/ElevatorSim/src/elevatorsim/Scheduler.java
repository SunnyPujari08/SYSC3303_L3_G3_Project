package elevatorsim;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import elevatorsim.elevator.Elevator;
import elevatorsim.elevator.ElevatorState;
import elevatorsim.elevator.ElevatorStateOne;

import java.util.ArrayList;

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
	// private masterEventList ...
	public List<List> masterFloorEventList = new ArrayList<>();
	public List<List> masterElevatorEventList = new ArrayList<>();
	private Floor[] floors;
	private Elevator[] elevators;
	private Thread[] floorThreads;
	private Thread[] elevatorThreads;
	private Thread buttonSimulator;
	private int numOfStates = 9;
	private ArrayList<SchedulerState> stateList;
	private int startState = Constants.SCHEDULER_STATE_IDLE;
	
	public Scheduler() {
		floors = new Floor[Constants.NUMBER_OF_FLOORS];
		elevators = new Elevator[Constants.NUMBER_OF_ELEVATORS];
		floorThreads = new Thread[Constants.NUMBER_OF_FLOORS];
		elevatorThreads = new Thread[Constants.NUMBER_OF_ELEVATORS];
		
		this.setupFloorLists();
		this.setupElevatorLists();
		this.setupButtonSimulator();
		this.setupElevatorThreads();
		this.setupFloorThreads();
		this.setupStateMachine(elevators[0]);
	}
	

	// Main function for project
	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();
		
	    SchedulerState currentState= scheduler.stateList.get(scheduler.startState);
    	int nextStateID;
    	Constants.formattedPrint("Starting Scheduler SM.");
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
	 * NOT USED IN ITERATION 1
	 * Arguments:
	 * floorNumber - Specifies which floor to write to
	 * eventToWrite - Event object to be added to list
	 */
	public void writeToFloor(Integer floorNumber, EventData eventToWrite) {
		eventToWrite.fromScheduler = true;
		(this.masterFloorEventList.get(floorNumber-1)).add(eventToWrite);
	}
	
	/* Function adds eventToWrite to list specified by elevatorNumber
	 * 
	 * Arguments:
	 * elevatorNumber - Specifies which elevator to write to
	 * eventToWrite - Event object to be added to list
	 */
	public void writeToElevator(Integer elevatorNumber, EventData eventToWrite) {
		eventToWrite.fromScheduler = true;
		(this.masterElevatorEventList.get(elevatorNumber-1)).add(eventToWrite);
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
	
	private void setupElevatorThreads() {
		if(this.masterElevatorEventList.size() >= Constants.NUMBER_OF_ELEVATORS) {
			for(int i = 0; i < Constants.NUMBER_OF_ELEVATORS; i++) {
				elevators[i] = new Elevator(i+1, this.masterElevatorEventList.get(i));
				elevatorThreads[i] = new Thread(elevators[i]);
			}
			for(int i = 0; i < Constants.NUMBER_OF_ELEVATORS; i++) {
				elevatorThreads[i].start();
			}
		}
	}
	
	private void setupFloorThreads() {
		if(this.masterFloorEventList.size() >= Constants.NUMBER_OF_FLOORS) {
			for(int i = 0; i < Constants.NUMBER_OF_FLOORS; i++) {
				floorThreads[i] = new Thread(new Floor(i+1, this.masterFloorEventList.get(i)));
			}
			for(int i = 0; i < Constants.NUMBER_OF_FLOORS; i++) {
				floorThreads[i].start();
			}
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
			for(int i = 0; i < (this.masterElevatorEventList.get(elevatorNumber-1)).size(); i++){
				if(this.masterElevatorEventList.size()>0 && (this.masterElevatorEventList.get(elevatorNumber-1)).size()>0) {
					if(!((EventData)(this.masterElevatorEventList.get(elevatorNumber-1)).get(i)).fromScheduler){
						EventData newEvent = (EventData)(this.masterElevatorEventList.get(elevatorNumber-1)).remove(i);
						return newEvent;
					}
			}	
		}
		return null;
	}
	
	public synchronized EventData readFromAllFloors() {
		for(int i = 0; i < Constants.NUMBER_OF_FLOORS; i++) {
			if((this.masterFloorEventList.get(i)).size()>0) {
				for(int j = 0; j < (this.masterFloorEventList.get(i)).size(); j++){
					//System.out.println(String.valueOf(((EventData)(masterFloorEventList.get(i)).get(j)).simulated));
					if(!((EventData)(this.masterFloorEventList.get(i)).get(j)).fromScheduler){
						EventData newEvent = (EventData)(this.masterFloorEventList.get(i)).remove(j);
						return newEvent;
					}
				}
			}
		}
		return null;
	}
	
	private void setupStateMachine(Elevator elevator) {
		// TODO instantiate all states
    	stateList = new ArrayList<SchedulerState>(numOfStates);
		stateList.add(new SchedulerStateIdle(elevator, this));
		stateList.add(new SchedulerStateOne(elevator, this));
		stateList.add(Constants.SCHEDULER_STATE_TWO, new SchedulerStateTwo(elevator, this));
		stateList.add(Constants.SCHEDULER_STATE_THREE, new SchedulerStateThree(elevator, this));
		stateList.add(Constants.SCHEDULER_STATE_FOUR, new SchedulerStateFour(elevator, this));
		stateList.add(Constants.SCHEDULER_STATE_FIVE, new SchedulerStateFive(elevator, this));
		stateList.add(Constants.SCHEDULER_STATE_SIX, new SchedulerStateSix(elevator, this));
		stateList.add(Constants.SCHEDULER_STATE_SEVEN, new SchedulerStateSeven(elevator, this));
		stateList.add(Constants.ELEVATOR_STATE_EIGHT, new SchedulerStateEight(elevator, this));

	}


}
