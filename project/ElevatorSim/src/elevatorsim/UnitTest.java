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
		String testInputFile = "test_input.rtf";
		//ArrayList<EventData> floorEventList = new ArrayList<>();
		Floor floor = new Floor(1);
		
		String rawData = floor.readEventFromTextFile(testInputFile);
		
		assertEquals(rawData, "0 2 Up 17 None");
	}

	/**
	 * Test if scheduler reading the right format of event from event list
	 * @throws ParseException 
	 */
	public void testSchedulerReadingFromFloor() throws ParseException {
		Scheduler scheduler = new Scheduler();
		Floor floor = new Floor(1);
		
		String testWriteEvent = "0 2 Up 17 None";
		
		floor.formPacket(testWriteEvent); 
		floor.send();
		scheduler.manageEvent();
		
		assertEquals(scheduler.futureEvents.get(0), testWriteEvent);
	}
	
	/**
	 * Test if elevator reading the right event from eventlist
	 * @throws ParseException
	 */
	public void Reading() throws ParseException {
		Scheduler scheduler = new Scheduler();
		Elevator elevator = new Elevator(1);
		
		String testWriteEvent = "0 2 Up 17 None";
		
		scheduler.formSendPacket(testWriteEvent, 200); 
		scheduler.send();
		elevator.recv();
		
		assertEquals(elevator.events.get(0), testWriteEvent);
	}
	
	//UDP test (message between floor and scheduler
	
	//udp test (message between scheduler and elevator
	
	//Elevator State Machine test
	public void StateMachine() throws ParseException {
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
		
		//elevator.run();
		//assertEquals(elevator.ElevatorState(), 4);
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
		scheduler.manageEvent();
		
		assertEquals(scheduler.startState(), 1);
		assertNotNull(scheduler.stateList());
		
		//assertEquals(scheduler.SchedulerState(), 4);
	}
	
	public void testFloorSchedulerMessaging() {
		Floor floor = new Floor(1);
		Scheduler scheduler = new Scheduler();
		
		floor.formPacket("testing");
		floor.send();
		
		scheduler.manageEvent();
		String replyMsg = floor.recv();
		assertTrue(replyMsg.equals("Data received from floor"));
		floor.closeSocket();
		scheduler.closeSockets();
	}
	
	public void SchedulerMessaging() {
		Elevator elevator = new Elevator(1);
		Scheduler scheduler = new Scheduler();
		
		elevator.formPacket();
		elevator.rpc_send();
		
		scheduler.manageEvent();
		EventData replyMsg = elevator.parseSchedulerReply();
		assertTrue(replyMsg.equals("Data received from floor"));
		elevator.closeSocket();
		scheduler.closeSockets();
	}
	
	public void Fault() {

		Elevator elevator = new Elevator(1);
		Thread t1 = new Thread(elevator, "ElevatorOne");
		t1.run();
		elevator.pickupFloor = 3;
		elevator.destFloor = 5;
		elevator.moveFaultInjected = true;
		
		EventData event = new EventData(EventType.MOVE_REQUEST_UP);
		
		elevator.eventList.add(event);
		
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertEquals(false, t1.isAlive());

	}


}