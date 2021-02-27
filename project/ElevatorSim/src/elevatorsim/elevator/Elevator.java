package elevatorsim.elevator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import elevatorsim.Constants;
import elevatorsim.EventData;

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
        
        
        // TODO Create all the different states, each with access to eventList
        stateList = new ArrayList<ElevatorState>(numOfStates);
        stateList.set(Constants.ELEVATOR_STATE_ONE, new ElevatorStateOne(this));
        // stateList.set(Constants.ELEVATOR_STATE_TWO, new ElevatorStateTwo(this));
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
    
    public void sendElevatorArrivingAtFloorMovingUp() {
    	//SendElevatorArrivingAtFloorMovingUp- Elevator arrives at floor moving up
    	Constants.formattedPrint("This is the action: SendElevatorArrivingAtFloorMovingUp");
    }
    
    public void sendElevatorArrivingAtFloorMovingDown() {
    	//SendElevatorArrivingAtFloorMovingDown - Elevator arrives at floor moving down
    	Constants.formattedPrint("This is the action: SendElevatorArrivingAtFloorMovingDown");
    }
    
    public void sendElevatorPickFloor() {
    	//SendElevatorPickFloor- Elevator button pressed (picking floor)
    	Constants.formattedPrint("This is the action: SendElevatorPickFloor");
    }
    
    public void openElevatorDoor() {
    	//OpenElevatorDoor - Open timer starts (Occurs when OpenDoor event is true && CloseDoor event is false)

    	isDoorOpen = true;
    	System.out.println("This is the action: OpenElevatorDoor");

    	Constants.formattedPrint("This is the action: OpenElevatorDoor");
    }
    
    public void closeElevatorDoor() {
    	//CloseElevatorDoor - Close timer starts (Occurs when OpenDoor event is true && CloseDoor event is false)

    	isDoorOpen = false;
    	System.out.println("This is the action: CloseElevatorDoor");

    	Constants.formattedPrint("This is the action: CloseElevatorDoor");

    }

    public void startElevatorAutoCloseTimer() {
    	//StartElevatorAutoCloseTimer
    	Constants.formattedPrint("This is the action: StartElevatorAutoCloseTimer");
    }
}


