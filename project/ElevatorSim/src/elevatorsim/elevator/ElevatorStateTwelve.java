package elevatorsim.elevator;

import java.util.List;

import elevatorsim.EventData;
import elevatorsim.EventType;
import elevatorsim.Constants;

public class ElevatorStateTwelve extends ElevatorState {

	public ElevatorStateTwelve(Elevator elevator) {
		super(elevator);
	}
	
	@Override
	/*
	 * Picks which state to go to next and/or runs any actions
	 * Should return state id (Use the constants for this)
	 * 
	 * TODO this method should forward any pertinent events to
	 * the scheduler using the sendEventToScheduler(EventData eD)
	 */
	public int handleEvent(EventData event) {
		// Check state machine diagram for what state to go to and what actions to take
		if(event.eventType == EventType.ELEVATOR_ARR_FLOOR_DOWN) {
			elevator.currentFloor = event.floorNum;
			elevator.sendElevatorArrivingAtFloorMovingDown(event.floorNum);
			System.out.println("CF: " + String.valueOf(elevator.currentFloor));
			System.out.println("DF: " + String.valueOf(elevator.destFloor));
			if(elevator.currentFloor == elevator.destFloor) {
				return Constants.ELEVATOR_STATE_FOUR;
			} else {
				return Constants.ELEVATOR_STATE_TWELVE;
			}
		}
		if(event.eventType == EventType.OPEN_DOOR) {
			elevator.openElevatorDoor();
			return Constants.ELEVATOR_STATE_FOUR;
		}
		// Will need handle more events eventually
		
		// Default to staying in same state
		System.out.println("e12 default");
		return Constants.ELEVATOR_STATE_TWELVE;
	}
	
	@Override
	/*
	 * This method should only perform actions that will happen every single time this state is entered
	 */
	public void entranceActions() {
		// Perform all entrance actions
		elevator.moveDownOneFloor();
	}

	@Override
	/*
	 * This method should only perform actions that will happen every single time this state is exited.
	 */
	public void exitActions() {
		return;
	}
	
}
