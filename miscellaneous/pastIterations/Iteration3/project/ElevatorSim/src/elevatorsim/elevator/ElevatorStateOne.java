package elevatorsim.elevator;

import java.util.List;

import elevatorsim.EventData;
import elevatorsim.EventType;
import elevatorsim.Constants;

public class ElevatorStateOne extends ElevatorState {

	public ElevatorStateOne(Elevator elevator) {
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
		if(event.eventType == EventType.MOVE_REQUEST_DOWN) { // && elevator.currentFloor != 1) {
			return Constants.ELEVATOR_STATE_FOUR;
		} else if (event.eventType == EventType.MOVE_REQUEST_UP) { // && elevator.currentFloor != Constants.NUMBER_OF_FLOORS) {
			return Constants.ELEVATOR_STATE_TWO;
		}
		// Will need handle more events eventually
		
		// Default to staying in same state
		return Constants.ELEVATOR_STATE_ONE;
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
		Constants.formattedPrint("Elevator closing door.");
		return;
	}
	
}
