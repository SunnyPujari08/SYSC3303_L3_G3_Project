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
		
		//TODO Set-up other threads
		//Each thread will be given their own synchronized event list to utilize
		
		// Floor FloorOne = new Floor(floorEventList);
		// Elevator ElevatorOne = new Elevator(elevatorEventList);
		
		
		while(true) {
			//TODO Check for events from other threads
			// for loop reading from each floor  and adding events to master list of floor events
			// same thing for elevator events
			// sort list
			// send most pertinent events to floor/elevator
			
		}

	}
	
	private void readFromFloor(Integer floorNumber) {
		//read from index of listOfLists
	}
	private void readFromElevator(Integer elevatorNumber) {
		//read from index of listOfLists
	}
	private void writeToFloor(Integer floorNumber) {
		//write to idx of...
	}
	private void writeToElevator(Integer elevatorNumber) {
		//write to idx of...
	}

	private void sortEventList() {
		
	}
}
