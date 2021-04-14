package elevatorsim;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import elevatorsim.elevator.Elevator;
import junit.framework.TestCase;

public class UnitTest extends TestCase {

	
	/**
	 * Test if floor.convertTextEvent() is converting the data properly
	 * @throws ParseException
	 */
	public void testDataConversion() throws ParseException {
		String testInputFile = "JUnitTest.txt";
		//ArrayList<EventData> floorEventList = new ArrayList<>();
		Floor floor = new Floor(2);
		
		String rawData = floor.readEventFromTextFile(testInputFile);
		
		assertEquals(rawData, "0 2 Up 17 None;");
		floor.closeSocket();
	}

	/**
	 * Test if scheduler reading the right format of event from event list
	 * @throws ParseException 
	 */
	public void testSchedulerReadingFromFloor() throws ParseException {
		Scheduler scheduler = new Scheduler();
		Floor floor = new Floor(2);
		
		String testWriteEvent = "f;" + 2 + ";" + "0 2 Up 17 None;";
		
		floor.formPacket(testWriteEvent); 
		floor.send();
		scheduler.manageEvent();
		
		assertEquals(scheduler.futureEvents.get(0), testWriteEvent);
		floor.closeSocket();
		scheduler.closeSockets();
	}
	
	/**
	 * Test if elevator reading the right event from eventlist
	 * @throws ParseException
	 */
	public void testReading() throws ParseException {
		Scheduler scheduler = new Scheduler();
		Elevator elevator = new Elevator(1);
		
		String testWriteEvent = "f;" + 2 + ";" + "0 2 Up 17 None;";
		
		scheduler.formSendPacket(testWriteEvent, 201); 
		scheduler.send();
		elevator.recv();
		
		
		assertEquals(elevator.events.get(0), testWriteEvent);
		elevator.closeSocket();
		scheduler.closeSockets();
	}
	
	//UDP test (message between floor and scheduler
	
	//udp test (message between scheduler and elevator
	
	//Elevator State Machine test
	public void testStateMachine() throws ParseException {
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
		
		assertEquals(0, elevator.startState());
		assertNotNull(elevator.stateList());
		
		scheduler.closeSockets();
		elevator.closeSocket();
	}
	
	public void testFault() {
		Elevator elevator = new Elevator(1);
		
		elevator.pickupFloor = 3;
		elevator.destFloor = 5;
		elevator.moveFaultInjected = true;
		EventData event = new EventData(EventType.MOVE_REQUEST_UP);
		
		elevator.eventList.add(event);
		int result = elevator.stateList().get(Constants.ELEVATOR_STATE_TWO).run();
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertEquals(result, -1);
		elevator.closeSocket();
	}
	
	public void testSchedulerMessaging() {
		Elevator elevator = new Elevator(1);
		Scheduler scheduler = new Scheduler();
		elevator.currentFloor = 2;
		elevator.direction = 1;
		elevator.formPacket();
		elevator.sendUpdate();
		scheduler.manageEvent();
		elevator.recv();
		byte[] replyMsg = elevator.packetIn.getData();
		int len = replyMsg.length;
		assertEquals(len, 100);
		
		elevator.closeSocket();
		scheduler.closeSockets();
	}
}