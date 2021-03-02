package elevatorsim;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * This class represent a floor thread
 * 
 * @author Jeong Won Kim
 *
 */
public class Floor implements Runnable {
	private Scheduler scheduler;
	private int floorNum;
	//private static String filename = "input.txt";
	private List<EventData> eventList;
    private boolean UP_BUTTON = false;
    private boolean DOWN_BUTTON = false;
    private int lamp;
    
    public Floor(int floorNum, List<EventData> floorEventList) {
    	this.floorNum = floorNum;
    	this.eventList = floorEventList;
    }
	
    public static String readEventFromTextFile(String filename) {
    	String rawData = "";
        try {
        	File file = new File(filename);
            Scanner reader = new Scanner(file);
            rawData = reader.nextLine();
            // Constants.formattedPrint(rawData);
            reader.close();
        } catch (FileNotFoundException e) {
            Constants.formattedPrint("File does not exist!");
            e.printStackTrace();
        }
        return rawData;
    }

    public EventData convertTextEvent(String rawData) throws ParseException {
    	String[] eInfo = rawData.split(" ");
    	DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss.mmm");
    	Date timeStamp = dateFormat.parse(eInfo[0]);
    	int floorNum = Integer.parseInt(eInfo[1]);
    	if (eInfo[2].equals("Up"))
    		UP_BUTTON = true;
    	if (eInfo[2].equals("Down"))
    		DOWN_BUTTON = true;
    	int destination = Integer.parseInt(eInfo[3]);
    	
    	EventData eData = new EventData(timeStamp, floorNum, UP_BUTTON, DOWN_BUTTON, EventType.FLOOR_REQUEST);
    	return eData;
    }

    public void sendEventToScheduler(EventData eData) {
    	eData.fromScheduler = false;
		eData.floorNum = this.floorNum;
    	this.eventList.add(eData);
    	Constants.formattedPrint("FLOOR: Event sent to scheduler.");
    }
    
    public synchronized EventData checkForEvents(){
        // If there are events available, return the first one
	        if(this.eventList.size() > 0) {
	        	for(int i = 0; i < this.eventList.size(); i++) {
	        		if(this.eventList.get(i).fromScheduler) {
	        			EventData newEvent = this.eventList.remove(i);
	    	        	return newEvent;
	        		}
	        	}
	        } 
	   return null;
    }

    /*
    public EventData readFromScheduler() {
    	// Check if empty
    	// Logic if/else checking eventType
    	// event = eventList.remove(0);
    	// return event;
    }
    */
    
	public void run() {
		EventData event;
        while(true){
        	event = this.checkForEvents();
        	if(event != null) {
        		// Should only add button presses to event list
        		if(event.fromScheduler) {
        			event.fromScheduler = false;
        			if(event.eventType == EventType.FLOOR_REQUEST || event.eventType == EventType.FLOOR_REQUEST_UP || event.eventType == EventType.FLOOR_REQUEST_DOWN) {
        				event.eventType = EventType.FLOOR_REQUEST;
        				this.sendEventToScheduler(event);
        			}
        		}
        	}
        }
	}
}
