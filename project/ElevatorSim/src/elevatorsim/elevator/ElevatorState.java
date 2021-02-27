package elevatorsim.elevator;

import java.util.List;
import elevatorsim.EventData;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is an abstract class for all states in elevator state machine
 *
 * @author Ezra Pierce - 100991590
 * @version February 26, 2021
 */

public abstract class ElevatorState {
	public Elevator elevator;
	
    public ElevatorState(Elevator elevator) {
        this.elevator = elevator;
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
			event = this.checkWorkFromScheduler();
			if(event != null) {
				// handle event accordingly depending on state and return next state
				nextState = handleEvent(event);
				done = true;
			}
			event = this.checkForSensorEvents();
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
		
	
    public EventData checkWorkFromScheduler(/*int destFloor*/) //throws InterruptedException
    {
        // If there are events available, return the first one
        if(elevator.eventList.size() > 0) {
        	EventData newEvent = elevator.eventList.remove(0);
        	return newEvent;
        } else {
        	return null;
        }
    }
    
    public void sendEventToScheduler(EventData eData)
    {
    	elevator.eventList.add(eData);
    }
    

    
	private EventData checkForSensorEvents() {
		// TODO create simulated sensor events
		// Check for sensor events, arriving at floor, button presses etc...
		return null;
	}

}
