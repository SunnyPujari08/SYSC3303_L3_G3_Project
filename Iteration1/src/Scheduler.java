/**
 * 
 */
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Ezra Pierce
 *
 */
public class Scheduler {
	
	private Integer numberOfFloors = 1;
	private Integer numberOfElevators = 1;
	// private masterEventList ...
	private static List<List> masterFloorEventList = new ArrayList<>();
	private static List<List> masterElevatorEventList = new ArrayList<>();

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Create empty lists for each floor and each elevator
		// TODO write for loop to create lists for multiple floors and elevators
		List<EventData> floorEventList = Collections.synchronizedList(new ArrayList<>());
		List<EventData> elevatorEventList = Collections.synchronizedList(new ArrayList<>());
		
		// Create list of lists for floors and elevators
		//TODO write for loop to add all lists to respective master lists
		masterFloorEventList.add(floorEventList);
		masterElevatorEventList.add(elevatorEventList);
		
		//Each thread is given their own synchronized event list to utilize
		Floor FloorOne = new Floor(1, floorEventList);
		Elevator ElevatorOne = new Elevator(1, elevatorEventList);
		FloorOne.start();
		ElevatorOne.start();
		
		EventData eventReadFromFloor;
		EventData eventReadFromElevator;
		while(true) {
			// TODO in future iterations
			// for loop reading from each floor  and adding events to master list of floor events
			// same thing for elevator events
			// sort list
			// send most pertinent events to floor/elevator
			
			// read floor event -> write event to elevator -> read event back from elevator
			eventReadFromFloor = readFromFloor(1);
			if(eventReadFromFloor != null) {
				System.out.println("Event read from floor.");
				writeToElevator(1, eventReadFromFloor);
				System.out.println("Event written to elevator.");
				//TODO add timeout, or interrupt?
				while(true) {
					eventReadFromElevator = readFromElevator(1);
					if(eventReadFromElevator != null) {
						System.out.println("Event read from elevator.");
						break;
					}
				}
				
			}
			
		}

	}
	
	private static EventData readFromFloor(Integer floorNumber) {
		//read from index of listOfLists
		// Just one floor for iteration 1
		if((masterFloorEventList.get(floorNumber-1)).size()>0) {
			EventData newEvent = (EventData)(masterFloorEventList.get(0)).remove(0);
			return newEvent;
		}
		return null;
	}
	
	private static EventData readFromElevator(Integer elevatorNumber) {
		//read from index of listOfLists
		if((masterElevatorEventList.get(elevatorNumber-1)).size()>0) {
			EventData newEvent = (EventData)(masterElevatorEventList.get(0)).remove(0);
			return newEvent;
		}
		return null;
	}
	
	private static void writeToFloor(Integer floorNumber, EventData eventToWrite) {
		(masterFloorEventList.get(floorNumber-1)).add(eventToWrite);
	}
	
	private static void writeToElevator(Integer elevatorNumber, EventData eventToWrite) {
		(masterElevatorEventList.get(elevatorNumber-1)).add(eventToWrite);
	}

	private void sortEventList() {
		
	}

}
