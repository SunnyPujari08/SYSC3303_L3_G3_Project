package ElevatorSim.src.elevatorsim;
import java.util.Date;
import java.util.List;

/**
 * The Elevator class represents an Elevator in the Elevator
 * Control System.
 *
 * @author Kashish Saxena - 101107204, Sunjeevani Pujari - 101110032
 * @version February 6, 2021
 */

public class Elevator implements Runnable {

    private EventData eData;
    //private Scheduler scheduler;
    private int floor;
    private int elevatorID;
    private int currentFloor;
    private int destFloor;
    private List<EventData> eventList;


    public Elevator(int elevatorID, List<EventData> eventList) {
        this.elevatorID = elevatorID;
        this.eventList = eventList;
    }

    public EventData checkWorkFromScheduler(/*int destFloor*/) //throws InterruptedException
    {
    	
    	/*
        // Pick up Event
        if (eventList.get(0).upButton == true) {
            for (int i = 0; i < ((eventList.get(0).floorNum) - currentFloor); i++) {
                System.out.print("Currently at Floor: "+ currentFloor + ".");
                wait(5000); // Waiting to pass a floor
            }
        }
        // Pick up Event
        if (eventList.get(0).downButton == true) {
            for (int i = 0; i < (currentFloor - (eventList.get(0).floorNum)); i++) {
                System.out.print("Currently at Floor: "+ currentFloor + ".");
                wait(5000); // Waiting to pass a floor
            }
        }
        currentFloor = destFloor;
        // make an EventData here (once the destination floor is reached)
        //Date timestamp = new Date(); // not needed
        eData = new EventData(timestamp, currentFloor, 0, EventType.FLOOR_BUTTON_PRESSED);
        */
        
        // If there are events available
        if(eventList.size() > 0) {
        	EventData newEvent = eventList.remove(0);
        	return newEvent;
        } else {
        	return null;
        }
        
        
    }

    public void sendWorkDoneToScheduler(EventData eData)
    {
    	eventList.add(eData);
    }

    /*
    // send pressed button to scheduler
    public EventData sendEventToScheduler() {
        //scheduler.readFromElevator(elevatorID);
        // read event from a text file, convert that to a data structure and send it to scheduler
        //scheduler.readFromElevator(datastructure);
        // assuming that in the readFromElevator() in scheduler, it takes the datastructure and stores it and calls the writeToElevator() to send an event back
        Date timestamp = new Date(); // not needed
        EventData data = new EventData(timestamp, currentFloor, 0, EventType.FLOOR_BUTTON_PRESSED);
        return data;
    }
    */

    @Override
    public void run()
    {
        while(true){
            // System.out.println(Thread.currentThread().getName() + " took an event");
            //scheduler.readFromElevator(elevatorID);
            eData = checkWorkFromScheduler();
            if(eData != null) {
            	System.out.println("ELEVATOR: Event picked up from scheduler.");
            	if(eData.eventType == EventType.FLOOR_BUTTON_PRESSED) {
            		eData.eventType = EventType.ACK_FLOOR_BUTTON_PRESSED;
            		sendWorkDoneToScheduler(eData);
            	}
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
        }
    }
}

