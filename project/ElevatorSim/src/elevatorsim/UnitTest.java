package elevatorsim;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import elevatorsim.elevator.Elevator;
import junit.framework.TestCase;

public class UnitTest extends TestCase {


	
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
