package elevation;
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
	private static Scheduler scheduler;
	private static int floorNum;
	private List<EventData> eventList;
    private boolean UP_BUTTON = false;
    private boolean DOWN_BUTTON = false;
    private int lamp;
    
    public Floor(int floorNum, List<EventData> floorEventList) {
    	this.floorNum = floorNum;
    	eventList = floorEventList;
    }
	
    public static String readEventFromTextFile(String filename) {
    	String rawData = "";
        try {
        	File file = new File(filename);
            Scanner reader = new Scanner(file);
            rawData = reader.nextLine();
            System.out.println(rawData);
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist!");
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
    	
    	EventData eData = new EventData(timeStamp, floorNum, UP_BUTTON, DOWN_BUTTON, EventType.FLOOR_BUTTON_PRESSED);
    	return eData;
    }

    public void sendEventToScheduler(EventData eData) {

    }

    public void readFromScheduler() {

    }
    
	public void run() {
        while(true){
			System.out.println(Thread.currentThread().getName()	+ " reads a file");
			//buffer writing should come here
			
			try {
				Thread.sleep(1000);		//Sleep for 1 second
			} catch (InterruptedException e) {}
        }
	}
}
