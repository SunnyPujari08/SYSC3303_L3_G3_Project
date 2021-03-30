/*
 * NOT IN USE CURRENTLY, keeping in case it comes in handy later on
 */

package elevatorsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import elevatorsim.elevator.ElevatorState;

public class buttonSimulator implements Runnable {
	
    private EventData eData;
    public List<List> masterFloorEventList;
    public List<List> masterElevatorEventList;
	
	public buttonSimulator(List<List> masterFloorEventList, List<List> masterElevatorEventList) {
		// prompt
		// parse input
		// add to corresponding event list
		// add external/internal to ED
		this.masterFloorEventList = masterFloorEventList;
		this.masterElevatorEventList = masterElevatorEventList;
	}


    @Override
    public void run() {
    	Constants.formattedPrint("Enter new events: ");
    	Scanner scan = new Scanner(System.in);
    	String s;
    	String[] inputs;
    	EventData event;
        while(true){
        	// .run() call will block until state change occurs
        	s = scan.nextLine();
        	inputs = s.split(" ");
        	if(inputs.length == 3) {
        		if(inputs[0].equals("E")) {
        			Constants.formattedPrint("Received elevator event from user.");
        			sendEventToElevator(Integer.parseInt(inputs[1]), new EventData(EventType.ELEVATOR_PICK_FLOOR, Integer.parseInt(inputs[2]), true));
        		} else if(inputs[0].equals("F")) {
        			Constants.formattedPrint("Received floor event from user.");
        			if(inputs[2].equals("up")) {
        				sendEventToFloor(Integer.parseInt(inputs[1]), new EventData(Integer.parseInt(inputs[1]), EventType.FLOOR_REQUEST_UP, true));
        			} else if(inputs[2].equals("down")) {
        				sendEventToFloor(Integer.parseInt(inputs[1]), new EventData(Integer.parseInt(inputs[1]), EventType.FLOOR_REQUEST_DOWN, true));
        			} else {
        				System.out.println("Button Simulator: Invalid input.");
        			}
        			
        		} else {
        			System.out.println("Button Simulator: Invalid input.");
        		}
        	} else {
        		System.out.println("Button Simulator: Invalid input.");
        	}

        }
    }

    private void sendEventToElevator(int elevatorID, EventData event) {
    	(masterElevatorEventList.get(elevatorID-1)).add(event);
    }
    private void sendEventToFloor(int floorNum, EventData event) {
    	(masterFloorEventList.get(floorNum-1)).add(event);
    }
}
