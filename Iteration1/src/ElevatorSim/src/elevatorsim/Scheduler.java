package ElevatorSim.src.elevatorsim;

import java.util.Collections;
import java.util.List;
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
	
	private Integer numberOfFloors = 1;
	private Integer numberOfElevators = 1;
	// private masterEventList ...
	private List<List> masterFloorEventList = new ArrayList<>();
	private List<List> masterElevatorEventList = new ArrayList<>();
	
	public Scheduler(int numberOfFloors, int numberOfElevators) {
		this.setupLists(numberOfFloors, numberOfElevators);
		this.setupThreads(numberOfFloors, numberOfElevators);
	}

	private void setupLists(int numberOfFloors, int numberOfElevators) {
		// Create empty lists for each floor and each elevator
		// TODO write for loop to create lists for multiple floors and elevators
		List<EventData> floorEventList = Collections.synchronizedList(new ArrayList<>());
		List<EventData> elevatorEventList = Collections.synchronizedList(new ArrayList<>());
		// Create list of lists for floors and elevators
		//TODO write for loop to add all lists to respective master lists
		this.masterFloorEventList.add(floorEventList);
		this.masterElevatorEventList.add(elevatorEventList);
	}
	
	private void setupThreads(int numberOfFloors, int numberOfElevators) {
		//TODO create specified number of threads in for loop
		// For now just create one elevator and one floor
		
		Thread FloorOne, ElevatorOne;
		//Each thread is given their own synchronized event list to pass events back and forth
		FloorOne = new Thread(new Floor(1, this.masterFloorEventList.get(0)));
		ElevatorOne = new Thread(new Elevator(1,this.masterElevatorEventList.get(0)));
		FloorOne.start();
		ElevatorOne.start();
	}
	
	// Main function for project
	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler(1,1);
		
		EventData eventReadFromFloor;
		EventData eventReadFromElevator;
		
		// For iteration 1 the scheduler just reads one event from the floor, writes that same event to the
		// Elevator which changes the event type to acknowledged and writes it back to the scheduler.
		// The scheduler then reads the event.
		while(true) {
			// TODO in future iterations
			// for loop reading from each floor  and adding events to master list of floor events
			// same thing for elevator events
			// sort list
			// send most pertinent events to floor/elevator
			
			// read floor event -> write event to elevator -> read event back from elevator
			eventReadFromFloor = scheduler.readFromFloor(1);
			if(eventReadFromFloor != null) {
				System.out.println("SCHEDULER: Event read from floor.");
				scheduler.writeToElevator(1, eventReadFromFloor);
				System.out.println("SCHEDULER: Event written to elevator.");
				//TODO add timeout, or interrupt?
				while(true) {
					eventReadFromElevator = scheduler.readFromElevator(1);
					if(eventReadFromElevator != null){
						System.out.println("SCHEDULER: Event read from elevator.");
						break;
					}
				}
				
			}
			
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
	public EventData readFromFloor(Integer floorNumber) {
		//read from index of listOfLists
		// Just one floor for iteration 1
		if((masterFloorEventList.get(floorNumber-1)).size()>0) {
			EventData newEvent = (EventData)(masterFloorEventList.get(0)).remove(0);
			return newEvent;
		}
		return null;
	}
	
	/*
	 * Function reads from list related to specified elevator, for Iteration 1 it also checks that the event was acknowledged
	 * 
	 * Arguments:
	 * elevatorNumber - Specifies which elevator to read from
	 * Returns:
	 * EventData - Returns one event if one exists and it has been acknowledged by the elevator or null if none exist
	 */
	private EventData readFromElevator(Integer elevatorNumber) {
		//read from index of listOfLists
		// Check that there is an event and that it has been acknowledged
		if((masterElevatorEventList.get(elevatorNumber-1)).size()>0) {
			if(((EventData)(masterElevatorEventList.get(0)).get(0)).eventType == EventType.ACK_FLOOR_BUTTON_PRESSED) {
				EventData newEvent = (EventData)(masterElevatorEventList.get(0)).remove(0);
				return newEvent;
			}
		}
		return null;
	}
	
	/* Function adds eventToWrite to list specified by floorNumber
	 * NOT USED IN ITERATION 1
	 * Arguments:
	 * floorNumber - Specifies which floor to write to
	 * eventToWrite - Event object to be added to list
	 */
	private void writeToFloor(Integer floorNumber, EventData eventToWrite) {
		(masterFloorEventList.get(floorNumber-1)).add(eventToWrite);
	}
	
	/* Function adds eventToWrite to list specified by elevatorNumber
	 * 
	 * Arguments:
	 * elevatorNumber - Specifies which elevator to write to
	 * eventToWrite - Event object to be added to list
	 */
	private void writeToElevator(Integer elevatorNumber, EventData eventToWrite) {
		(masterElevatorEventList.get(elevatorNumber-1)).add(eventToWrite);
	}

	// NOT USED IN ITERATION 1
	private void sortEventList() {
		
	}

}
