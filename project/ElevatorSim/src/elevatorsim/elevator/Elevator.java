package elevatorsim.elevator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import elevatorsim.Constants;
import elevatorsim.EventData;
import elevatorsim.EventType;

/**
 * The Elevator class represents an Elevator in the Elevator
 * Control System.
 *
 * @author Kashish Saxena - 101107204, Sunjeevani Pujari - 101110032, Ezra Pierce - 100991590
 * @version February 27, 2021
 */

public class Elevator implements Runnable {

    private EventData eData;
    public int elevatorID;
    public int currentFloor;
    public int destFloor;
    public boolean isDoorOpen = false;
    private int numOfStates = 12;
    public List<EventData> eventList;
    private ArrayList<ElevatorState> stateList;
    private int startState = Constants.ELEVATOR_STATE_ONE;


    public Elevator(int elevatorID, List<EventData> eventList) {
        this.elevatorID = elevatorID;
        this.eventList = eventList;
        setupStateMachine();
    }

    @Override
    public void run() {

    	ElevatorState currentState= stateList.get(startState);
    	int nextStateID;
        while(true){
        	// .run() call will block until state change occurs
        	nextStateID = currentState.run();
        	if(nextStateID < 0) { break;}
        	currentState = stateList.get(nextStateID);
        }
        Constants.formattedPrint("Elevator state machine failed, thread exiting.");
    }
    
    public void sendElevatorArrivingAtFloorMovingUp(int floorNum) {
    	EventData newEvent = new EventData(floorNum, EventType.ELEVATOR_ARR_FLOOR_UP);
    	sendEventToScheduler(newEvent);
    	Constants.formattedPrint("This is the action: SendElevatorArrivingAtFloorMovingUp");
    }
    
    public void sendElevatorArrivingAtFloorMovingDown(int floorNum) {
    	EventData newEvent = new EventData(floorNum, EventType.ELEVATOR_ARR_FLOOR_DOWN);
    	sendEventToScheduler(newEvent);
    	Constants.formattedPrint("This is the action: SendElevatorArrivingAtFloorMovingDown");
    }
    
    public void sendElevatorPickFloor(int floorNum, int destinationFloor) {
    	EventData newEvent = new EventData(EventType.ELEVATOR_PICK_FLOOR, floorNum, destinationFloor);
    	sendEventToScheduler(newEvent);
    	Constants.formattedPrint("This is the action: SendElevatorPickFloor");
    }
    
    public void openElevatorDoor() {
    	//OpenElevatorDoor - Open timer starts (Occurs when OpenDoor event is true && CloseDoor event is false)
    	isDoorOpen = true;

    	Constants.formattedPrint("This is the action: OpenElevatorDoor");
    }
    
    public void closeElevatorDoor() {
    	//CloseElevatorDoor - Close timer starts (Occurs when OpenDoor event is true && CloseDoor event is false)

    	isDoorOpen = false;
    	Constants.formattedPrint("This is the action: CloseElevatorDoor");

    }

    public void startElevatorAutoCloseTimer() {
    	//StartElevatorAutoCloseTimer
    	Constants.formattedPrint("This is the action: StartElevatorAutoCloseTimer");
    }
    
    public void sendEventToScheduler(EventData eData){
    	this.eventList.add(eData);
    }
    
    public EventData checkWorkFromScheduler(/*int destFloor*/) //throws InterruptedException
    {
        // If there are events available, return the first one
        if(eventList.size() > 0) {
        	EventData newEvent = eventList.remove(0);
        	return newEvent;
        } else {
        	return null;
        }
    }
    
	public EventData checkForSensorEvents() {
		// TODO create simulated sensor events
		// Check for sensor events, arriving at floor, button presses etc...
		return null;
	}
    
    
    private void setupStateMachine() {
    	stateList = new ArrayList<ElevatorState>(numOfStates);
        stateList.add(Constants.ELEVATOR_STATE_ONE, new ElevatorStateOne(this));
        stateList.add(Constants.ELEVATOR_STATE_TWO, new ElevatorStateTwo(this));
        stateList.add(Constants.ELEVATOR_STATE_THREE, new ElevatorStateThree(this));
        stateList.add(Constants.ELEVATOR_STATE_FOUR, new ElevatorStateFour(this));
        stateList.add(Constants.ELEVATOR_STATE_FIVE, new ElevatorStateFive(this));
        stateList.add(Constants.ELEVATOR_STATE_SIX, new ElevatorStateSix(this));
        stateList.add(Constants.ELEVATOR_STATE_SEVEN, new ElevatorStateSeven(this));
        stateList.add(Constants.ELEVATOR_STATE_EIGHT, new ElevatorStateEight(this));
        stateList.add(Constants.ELEVATOR_STATE_NINE, new ElevatorStateNine(this));
        stateList.add(Constants.ELEVATOR_STATE_TEN, new ElevatorStateTen(this));
        stateList.add(Constants.ELEVATOR_STATE_ELEVEN, new ElevatorStateEleven(this));
        stateList.add(Constants.ELEVATOR_STATE_TWELVE, new ElevatorStateTwelve(this));
    }
}


