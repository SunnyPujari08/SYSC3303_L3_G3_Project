package elevatorsim.elevator;

import java.util.List;

import elevatorsim.Constants;
import elevatorsim.EventData;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
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
		Instant time = Instant.now();
		long start = time.getEpochSecond();

		entranceActions();
		
		int nextState = -1;
		boolean done = false;
		EventData event = null;
		//long startTime = 
		
    	
		// This loop will continuously check for new events and react accordingly
		while(!done) {
			Instant time2 = Instant.now();
			long end = time2.getEpochSecond();
			
			if((end-start) > this.getMaxTime()) {
				return -1;
			}
			event = elevator.rpc_send();// from UDP socket
			if(event != null) {
				// handle event accordingly depending on state and return next state
				Constants.formattedPrint("Elevator picked up event: " + String.valueOf(event.eventType));
				nextState = handleEvent(event);
				done = true;
			}
			event = elevator.checkForSensorEvents(); // from eventList, should only be self-generated events like ELEVATOR_ARR_DOWN etc.
			if(event != null) {
				// handle event accordingly depending on state and return next state
				Constants.formattedPrint("Elevator picked up event: " + String.valueOf(event.eventType));
				nextState = handleEvent(event);
				done = true;
			}
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		exitActions();
		Instant time2 = Instant.now();
		long end = time2.getEpochSecond();
		
		if((end-start) > this.getMaxTime()) {
			return -1;
		}
		return nextState;
	}
	
	
	public abstract int handleEvent(EventData event);
	public abstract void entranceActions();
	public abstract void exitActions();
	public abstract long getMaxTime();
		
	


}
