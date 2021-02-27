package elevatorsim;

import java.text.SimpleDateFormat;
import java.util.Date;

// This class contains all global constants
public class Constants {
	public static Integer NUMBER_OF_FLOORS = 7;
	public static Integer NUMBER_OF_ELEVATORS = 1;
	
	public static Integer ELEVATOR_STATE_ONE = 0;
	public static Integer ELEVATOR_STATE_TWO = 1;
	public static Integer ELEVATOR_STATE_THREE = 2;
	public static Integer ELEVATOR_STATE_FOUR = 3;
	public static Integer ELEVATOR_STATE_FIVE = 4;
	public static Integer ELEVATOR_STATE_SIX = 5;
	public static Integer ELEVATOR_STATE_SEVEN = 6;
	public static Integer ELEVATOR_STATE_EIGHT = 7;
	public static Integer ELEVATOR_STATE_NINE = 8;
	public static Integer ELEVATOR_STATE_TEN = 9;
	public static Integer ELEVATOR_STATE_ELEVEN = 10;
	public static Integer ELEVATOR_STATE_TWELVE = 11;
	
    public static void formattedPrint(String toPrint) {
    	// TODO add formatted print statement with thread & timestamp
    	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": " + toPrint + ": " + timeStamp);
    }
}
