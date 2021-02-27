package elevatorsim;

import elevatorsim.elevator.Elevator;

public abstract class SchedulerState {
	public Elevator elevator;
	public Scheduler scheduler;
	
    public SchedulerState(Elevator elevator, Scheduler scheduler) {
        this.elevator = elevator;
        this.scheduler = scheduler;
    }
	
	/*
	 * This is the main function for the state. It should return the next state.
	 * This function should have a while loop that is continuously checking for events from sensors or scheduler
	 *  and reacting accordingly.
	 */
	public int run() {
		entranceActions();
		
		int nextState = -1;
		boolean done = false;
		EventData event = null;
		
		// This loop will continuously check for new events and react accordingly
		while(!done) {
			event = readFromAllFloors();
			if(event != null) {
				// handle event accordingly depending on state and return next state
				nextState = handleEvent(event);
				done = true;
			}
			event = readFromElevator(elevator.elevatorID);
			if(event != null) {
				// handle event accordingly depending on state and return next state
				nextState = handleEvent(event);
				done = true;
			}
		}
		exitActions();
		return nextState;
	}
	
	
	public abstract int handleEvent(EventData event);
	public abstract void entranceActions();
	public abstract void exitActions();
	
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
		if((scheduler.masterFloorEventList.get(floorNumber-1)).size()>0) {
			EventData newEvent = (EventData)(scheduler.masterFloorEventList.get(0)).remove(0);
			return newEvent;
		}
		return null;
	}
	
	public EventData readFromAllFloors() {
		for(int i = 1; i <= Constants.NUMBER_OF_FLOORS; i++) {
			if((scheduler.masterFloorEventList.get(i)).size()>0) {
				EventData newEvent = (EventData)(scheduler.masterFloorEventList.get(i)).remove(0);
				return newEvent;
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
	public EventData readFromElevator(Integer elevatorNumber) {
		//read from index of listOfLists
		// Check that there is an event
		if(scheduler.masterElevatorEventList.size()>0 && (scheduler.masterElevatorEventList.get(elevatorNumber-1)).size()>0) {
			EventData newEvent = (EventData)(scheduler.masterElevatorEventList.get(0)).remove(0);
			return newEvent;
		}
		return null;
	}
}
