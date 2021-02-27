package elevatorsim;

import java.util.Collections;
import java.util.List;

import elevatorsim.elevator.Elevator;

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
	private Thread[] floorThreads;
	private Thread[] elevatorThreads;
	
	public Scheduler() {
		this.setupFloorLists();
		this.setupElevatorLists();
		this.setupElevatorThreads();
		this.setupFloorThreads();
	}

	private void setupFloorLists() {
		for(int i = 0; i <= Constants.NUMBER_OF_FLOORS; i++) {
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
			for(int i = 0; i <= Constants.NUMBER_OF_ELEVATORS; i++) {
				elevatorThreads[i] = new Thread(new Elevator(i+1, this.masterElevatorEventList.get(i)));
			}
			for(int i = 0; i <= Constants.NUMBER_OF_ELEVATORS; i++) {
				elevatorThreads[i].start();
			}
		}
	}
	
	private void setupFloorThreads() {
		if(this.masterFloorEventList.size() >= Constants.NUMBER_OF_FLOORS) {
			for(int i = 0; i <= Constants.NUMBER_OF_FLOORS; i++) {
				floorThreads[i] = new Thread(new Floor(i+1, this.masterFloorEventList.get(i)));
			}
			for(int i = 0; i <= Constants.NUMBER_OF_FLOORS; i++) {
				floorThreads[i].start();
			}
		}
	}
	
	// Main function for project
	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();
		
		EventData eventReadFromFloor;
		EventData eventReadFromElevator;

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
					eventReadFromElevator =    scheduler.readFromElevator(1);
					if(eventReadFromElevator != null){
						System.out.println("SCHEDULER: Event read from elevator.");
						break;
					}
				}                                                                                                                                                                                                                                                                                                                                                                  
			}
		}
	}
	

	
	/*
	 * Function reads from list related to specified elevator, for Iteration 1 it also checks that the event was acknowledged
	 * 
	 * Arguments:
	 * elevatorNumber - Specifies which elevator to read from
	 * Returns:
	 * EventData - Returns one event if one exists and it has been acknowledged by the elevator or null if none exist
	 */
	public EventData readFromElevator(Integer elevatorNumber) {
		//read from index of listOfLists
		// Check that there is an event and that it has been acknowledged
		if(masterElevatorEventList.size()>0 && (masterElevatorEventList.get(elevatorNumber-1)).size()>0) {
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
	public void writeToFloor(Integer floorNumber, EventData eventToWrite) {
		(masterFloorEventList.get(floorNumber-1)).add(eventToWrite);
	}
	
	/* Function adds eventToWrite to list specified by elevatorNumber
	 * 
	 * Arguments:
	 * elevatorNumber - Specifies which elevator to write to
	 * eventToWrite - Event object to be added to list
	 */
	public void writeToElevator(Integer elevatorNumber, EventData eventToWrite) {
		(masterElevatorEventList.get(elevatorNumber-1)).add(eventToWrite);
	}

	// NOT USED IN ITERATION 1
	private void sortEventList() {
		
	}

}
