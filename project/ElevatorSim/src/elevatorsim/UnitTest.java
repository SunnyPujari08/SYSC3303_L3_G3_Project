package elevatorsim;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import elevatorsim.elevator.Elevator;
import junit.framework.TestCase;

public class UnitTest extends TestCase {

	
	/**
	 * Test if floor.convertTextEvent() is converting the data properly
	 * @throws ParseException
	 */
	public void testDataConversion() throws ParseException {
		String testInputFile = "test_input.rtf";
		ArrayList<EventData> floorEventList = new ArrayList<>();
		Floor floor = new Floor(1);
		
		//Test time stamp
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss.mmm");
    	Date testTimeStamp = dateFormat.parse("14:05:15.0");
		
		String rawData = Floor.readEventFromTextFile(testInputFile);
		EventData eventData = floor.convertTextEvent(rawData);
		
		assertEquals(testTimeStamp, eventData.timestamp);
		assertEquals(2, eventData.floorNum);
		assertTrue(eventData.upButton);
		assertFalse(eventData.downButton);
		assertEquals(EventType.FLOOR_REQUEST, eventData.eventType);
	}

	/**
	 * Test if scheduler reading the right format of event from event list
	 * @throws ParseException 
	 */
	public void testSchedulerReadingFromFloor() throws ParseException {
		Scheduler scheduler = new Scheduler();
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss.mmm");
		Date testTimeStamp = dateFormat.parse("14:05:15.0");
		int testFloorNum = 1;
		boolean testUpButton = true;
		boolean testDownButton = false;
		EventType testEventType = EventType.FLOOR_REQUEST;
		
		EventData testWriteEvent = new EventData(testTimeStamp, testFloorNum, testUpButton, testDownButton, testEventType); 
		scheduler.writeToFloor(1, testWriteEvent);
		EventData testReadEvent = scheduler.readFromFloor(1);
		
		assertEquals(testWriteEvent.timestamp, testReadEvent.timestamp);
		assertEquals(testWriteEvent.floorNum, testReadEvent.floorNum);
		assertEquals(testWriteEvent.upButton, testReadEvent.upButton);
		assertEquals(testWriteEvent.downButton, testReadEvent.downButton);
		assertEquals(testWriteEvent.eventType, testReadEvent.eventType);
	}
	
	/**
	 * Test if elevator reading the right event from eventlist
	 * @throws ParseException
	 */
	public void testElevatorReading() throws ParseException {
		Scheduler scheduler = new Scheduler();
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss.mmm");
		Date testTimeStamp = dateFormat.parse("14:05:15.0");
		int testFloorNum = 1;
		boolean testUpButton = true;
		boolean testDownButton = false;
		EventType testEventType = EventType.FLOOR_REQUEST;
		
		EventData testWriteEvent = new EventData(testTimeStamp, testFloorNum, testUpButton, testDownButton, testEventType);
		ArrayList<EventData> elevatorEventList = new ArrayList<>();
		elevatorEventList.add(testWriteEvent);
		
		Elevator elevator = new Elevator(1);
		EventData newElevatorEvent = elevator.checkWorkFromScheduler();
		
		assertEquals(testWriteEvent.timestamp, newElevatorEvent.timestamp);
		assertEquals(testWriteEvent.floorNum, newElevatorEvent.floorNum);
		assertEquals(testWriteEvent.upButton, newElevatorEvent.upButton);
		assertEquals(testWriteEvent.downButton, newElevatorEvent.downButton);
		assertEquals(testWriteEvent.eventType, newElevatorEvent.eventType);
	}
	
	/**
	 * Test if elevator sending new event to eventlist by changing the eventType of the event received
	 * @throws ParseException
	 */
	/**
	public void testSchedulerReadingFromElevator() throws ParseException {
		Scheduler scheduler = new Scheduler();
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss.mmm");
		Date testTimeStamp = dateFormat.parse("14:05:15.0");
		int testFloorNum = 1;
		boolean testUpButton = true;
		boolean testDownButton = false;
		EventType testEventType = EventType.ACK_FLOOR_BUTTON_PRESSED;
		
		EventData testWriteEvent = new EventData(testTimeStamp, testFloorNum, testUpButton, testDownButton, testEventType); 
		scheduler.writeToElevator(1, testWriteEvent);
		EventData testReadEvent = scheduler.readFromElevator(1);
		
		assertEquals(testWriteEvent.timestamp, testReadEvent.timestamp);
		assertEquals(testWriteEvent.floorNum, testReadEvent.floorNum);
		assertEquals(testWriteEvent.upButton, testReadEvent.upButton);
		assertEquals(testWriteEvent.downButton, testReadEvent.downButton);
		assertEquals(testWriteEvent.eventType, testReadEvent.eventType);
		
	}
	**/
	
	//UDP test (message between floor and scheduler
	
	//udp test (message between scheduler and elevator
	
	//Elevator State Machine test
	public void testElevatorStateMachine() throws ParseException {
		Scheduler scheduler = new Scheduler();
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss.mmm");
		Date testTimeStamp = dateFormat.parse("14:05:15.0");
		int testFloorNum = 1;
		boolean testUpButton = false;
		boolean testDownButton = true;
		EventType testEventType = EventType.FLOOR_REQUEST;
		
		EventData testWriteEvent = new EventData(testTimeStamp, testFloorNum, testUpButton, testDownButton, testEventType);
		ArrayList<EventData> elevatorEventList = new ArrayList<>();
		elevatorEventList.add(testWriteEvent);
		
		Elevator elevator = new Elevator(1);
		
		assertEquals(elevator.startState(), 1);
		assertNotNull(elevator.stateList());
		
		elevator.run();
		assertEquals(elevator.ElevatorState(), 4);
	}
	
	public void testSchedulerStateMachine() throws ParseException {
		Scheduler scheduler = new Scheduler();
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss.mmm");
		Date testTimeStamp = dateFormat.parse("14:05:15.0");
		int testFloorNum = 1;
		boolean testUpButton = true;
		boolean testDownButton = false;
		EventType testEventType = EventType.FLOOR_REQUEST;
		
		EventData testWriteEvent = new EventData(testTimeStamp, testFloorNum, testUpButton, testDownButton, testEventType); 
		scheduler.writeToFloor(1, testWriteEvent);
		
		assertEquals(scheduler.startState, 1);
		assertNotNull(scheduler.stateList);
		
		Elevator.run();
		assertEquals(currentState, 4);
	}
	
	public void testFloorSchedulerMessaging() {
		Floor floor = new Floor(1);
		Scheduler scheduler = new Scheduler();
		
		floor.formPacket("testing");
		floor.send();
		
		scheduler.readOverUDP();
		String replyMsg = floor.recv();
		assertTrue(replyMsg.equals("Data received from floor"));
		floor.closeSocket();
		scheduler.closeSocket();
	}
	
	public void testElevatorSchedulerMessaging() {
		Elevator elevator = new Elevator(1);
		Scheduler scheduler = new Scheduler();
		
		elevator.formPacket("testing");
		elevator.send();
		
		scheduler.readOverUDP();
		String replyMsg = elevator.recv();
		assertTrue(replyMsg.equals("Data received from floor"));
		elevator.closeSocket();
		scheduler.closeSocket();
	}


}