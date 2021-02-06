package ElevatorSim.src.elevatorsim;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

public class UnitTest extends TestCase {

	
	/**
	 * Test if floor.convertTextEvent() is converting the data properly
	 * @throws ParseException
	 */
	public void testDataConversion() throws ParseException {
		String testInputFile = "input.txt";
		ArrayList<EventData> floorEventList = new ArrayList<>();
		Floor floor = new Floor(1, floorEventList);
		
		//Test time stamp
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss.mmm");
    	Date testTimeStamp = dateFormat.parse("14:05:15.0");
		
		String rawData = Floor.readEventFromTextFile(testInputFile);
		EventData eventData = floor.convertTextEvent(rawData);
		
		assertEquals(testTimeStamp, eventData.timestamp);
		assertEquals(2, eventData.floorNum);
		assertTrue(eventData.upButton);
		assertFalse(eventData.downButton);
		assertEquals(EventType.FLOOR_BUTTON_PRESSED, eventData.eventType);
	}

	/**
	 * Test if scheduler reading the right format of event from event list
	 */
	public void testSchedulerReading() {
		Scheduler scheduler = new Scheduler();
		
		//EventData testEvent = scheduler.readFromFloor(1);
		
	}
	
	public void testElevatorSendEvent() {
		ArrayList<EventData> elevatorEventList = new ArrayList<>();
		//Elevator elevator = new Elevator(1, elevatorEventList);
		
		
	}
}
/*
Test 2: test if scheduler reading the right format of event from event list 
    - can do this by comparing the eventReadFromFloor variable of type Event data with hard coded value 
        -ex: scheduler eventReadFromFloor.timeStamp == “hard-coded expected time”, etc 
Test 3: test if elevator reading the right event from eventlist
    -can do this by comparing the newEvent of type EvenData in the checkWorkFromScheduler()
    - probably need to declare a test EventData to compare since its a function call 
        -ex: EventData test;
            test = elevator.checkWorkFromScheduler();
            test.timeStamp == “hard-coded expected time”, etc 
Test 4: test if elevator sending new event to eventlist by changing the eventType of the event received
    - can do this by testing the eventType of the eventReadFromElevator in scheduler
        ex:  EventData test;
            test = scheduler.readFromElevator(1);
            test.eventType == EventType.ACK_FLOOR_BUTTON_PRESSED;
*/