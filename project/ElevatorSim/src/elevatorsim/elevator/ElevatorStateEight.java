package ElevatorSim.src.elevatorsim.elevator;

import java.util.List;

import elevatorsim.EventData;
import elevatorsim.EventType;
import elevatorsim.Constants;

public class ElevatorStateEight extends ElevatorState {

	public ElevatorStateEight(Elevator elevator) {
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
		if(event.eventType == EventType.OPEN_DOOR) {
			elevator.openElevatorDoor();
			return Constants.ELEVATOR_STATE_NINE;
		}
		// Will need handle more events eventually
		
		// Default to staying in same state
		return Constants.ELEVATOR_STATE_EIGHT;
	}
	
	@Override
	/*
	 * This method should only perform actions that will happen every single time this state is entered
	 */
	public void entranceActions() {
		// Perform all entrance actions
		return;
	}

	@Override
	/*
	 * This method should only perform actions that will happen every single time this state is exited.
	 */
	public void exitActions() {
		return;
	}
	
}
