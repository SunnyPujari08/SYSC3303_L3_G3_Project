package elevatorsim.elevator;

import java.util.List;

import elevatorsim.EventData;
import elevatorsim.EventType;
import elevatorsim.Constants;

public class ElevatorStateTwo extends ElevatorState {

	public ElevatorStateTwo(Elevator elevator) {
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
		if(event.eventType == EventType.ELEVATOR_ARR_FLOOR_UP) {
			elevator.currentFloor = event.floorNum;
			if((event.floorNum == elevator.pickupFloor) && !elevator.pickedUpPassenger) {
				elevator.pickedUpPassenger = true;
				return Constants.ELEVATOR_STATE_THREE;
			} else if((event.floorNum == elevator.destFloor && elevator.pickedUpPassenger)) {
				return Constants.ELEVATOR_STATE_THREE;
			} else{
				return Constants.ELEVATOR_STATE_TWO;	
			}
			
		}
		if(event.eventType == EventType.ELEVATOR_ARRIVED) {
			return Constants.ELEVATOR_STATE_THREE;
		}
		else if(event.eventType == EventType.OPEN_DOOR) {
			
			return Constants.ELEVATOR_STATE_THREE;
		}
		// Will need handle more events eventually
		
		// Default to staying in same state
		return Constants.ELEVATOR_STATE_TWO;
	}
	
	@Override
	/*
	 * This method should only perform actions that will happen every single time this state is entered
	 */
	public void entranceActions() {
		// Already at pickup floor
		if((elevator.currentFloor == elevator.pickupFloor) && !elevator.pickedUpPassenger) {
			elevator.eventList.add(new EventData(EventType.ELEVATOR_ARRIVED));
		}
		// Perform all entrance action
		elevator.moveUpOneFloor();
		
		return;
	}

	@Override
	/*
	 * This method should only perform actions that will happen every single time this state is exited.
	 */
	public void exitActions() {
		return;
	}
	
	public long getMaxTime() {
		return (2*Constants.MOVE_TIME-1)/1000;
	}
}
