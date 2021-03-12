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
			// Parse a packet and add to corresponding list
			scheduler.populateEvents();
		
			event = scheduler.readFromAllFloors();
			if(event != null) {
				// handle event accordingly depending on state and return next state
				Constants.formattedPrint("Scheduler picked up event: " + String.valueOf(event.eventType));
				nextState = handleEvent(event);
				done = true;
			}
			event = scheduler.readFromElevator(this.elevator.elevatorID);
			if(event != null) {
				// handle event accordingly depending on state and return next state
				Constants.formattedPrint("Scheduler picked up event: " + String.valueOf(event.eventType));
				nextState = handleEvent(event);
				done = true;
			}
		exitActions();
		return nextState;
	}
	
	
	public abstract int handleEvent(EventData event);
	public abstract void entranceActions();
	public abstract void exitActions();
	
	
	

	

}
