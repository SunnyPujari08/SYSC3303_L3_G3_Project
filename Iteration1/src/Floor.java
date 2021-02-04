import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private boolean UP_BUTTON = false;
    private boolean DOWN_BUTTON = false;
    
    public Floor(Scheduler scheduler, int floorNum) {
    	this.scheduler = scheduler;
    	this.floorNum = floorNum;
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
    	
    	EventData eData = new EventData(timeStamp, floorNum, UP_BUTTON, DOWN_BUTTON, destination);
    	return eData;
    }
    
    public void sendEventToScheduler() {
    	scheduler.readFromFloor(floorNum);
    }

    public void readFromScheduler() {
    	scheduler.writeToFloor(floorNum);
    }
    
	public void run() {
        while(true){
            System.out.println(Thread.currentThread().getName() + " took an event");
            //scheduler.writeToFloor();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
	}

	/**
	public static void main(String[] args) {

	}
	**/
}
