package elevatorsim;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;


// This class contains all global constants
public class Constants {
	private static Instant START_TIMER;
	public static Integer NUMBER_OF_FLOORS = 22;
	public static Integer NUMBER_OF_ELEVATORS = 4;
	
	public static Integer UDP_PORT_NUMBER = 78;
	
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
	
	public static Integer SCHEDULER_STATE_IDLE = 0;
	public static Integer SCHEDULER_STATE_ONE = 1;
	public static Integer SCHEDULER_STATE_TWO = 2;
	public static Integer SCHEDULER_STATE_THREE = 3;
	public static Integer SCHEDULER_STATE_FOUR = 4;
	public static Integer SCHEDULER_STATE_FIVE = 5;
	public static Integer SCHEDULER_STATE_SIX = 6;
	public static Integer SCHEDULER_STATE_SEVEN = 7;
	public static Integer SCHEDULER_STATE_EIGHT = 8;
	
	public static Integer MOVE_TIME = 2880;
	public static Integer DOOR_TIME = 1500;
	
	
    public static void formattedPrint(String toPrint) {

    	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": " + toPrint);
    }

    public static void startTimer() {
    	START_TIMER = Instant.now();
    }
    
    public static int getTime() {
    	Instant end = Instant.now();
    	Duration interval = Duration.between(START_TIMER, end);
    	return (int) interval.toSeconds();
    }
}
