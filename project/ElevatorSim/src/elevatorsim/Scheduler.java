package elevatorsim;

import java.util.Collections;
import java.util.List;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

/**
 * @author Ezra Pierce
 *
 * Scheduler class for SYSC3303 group project. L3-G3
 * 
 * For iteration 1 this class creates one floor thread and one elevator thread, then
 * waits for a new event from the Floor. Once an event is read, it is sent to the Elevator.
 * Then it waits for the event to be returned from the elevator and confirms that it has been acknowledged.
 */
public class Scheduler {
	private static final int MAX_MESSAGE_LEN = 100;		// Maximum message length
	private static final int MAX_FLOOR_NUM = 10;		// Maximum message length
	private DatagramPacket receivePacket, replyPacket;
	private DatagramSocket receiveSocket, replySocket;	// Socket for receiving and replying

	public List<List> masterFloorEventList = new ArrayList<>();
	public List<List> masterElevatorEventList = new ArrayList<>();


	public int elevatorCurrentFloor = 1, elevatorDestinationFloor = 1;


	private int numOfStates = 9;
	private ArrayList<SchedulerState> stateList;
	private int startState = Constants.SCHEDULER_STATE_ONE;
	public List<String> rawEvents = Collections.synchronizedList(new ArrayList<>());
	private List<String> sendQueueForElevator = new ArrayList<>(); 
	public EventData currentTripEvent; 
	private SchedulerState currentState;
	private boolean firstEventReceived = false;
	private int inputFileEventsDone = 0;
	private boolean allElevatorsIdle = true;
	private Instant START_TIME;
	
	private List<String> futureEvents = new ArrayList<String>();

	
	
	public Scheduler() {
		try {
			receiveSocket = new DatagramSocket(Constants.UDP_PORT_NUMBER);
			replySocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.setupStateMachine();
	}
	
    public void closeSockets() {
    	receiveSocket.close();
    	replySocket.close();
    }

	// Main function for project
	public static void main(String[] args) throws ParseException {
		Scheduler scheduler = new Scheduler();
		
		// Set starting state
	    SchedulerState currentState = scheduler.stateList.get(scheduler.startState);
    	int nextStateID;
    	scheduler.formattedPrint("Starting Scheduler SM.");
        while(true){
        	// .run() call will block until state change occurs
        	nextStateID = currentState.run();
        	if(nextStateID < 0) { break;}
        	currentState = scheduler.stateList.get(nextStateID);   
        	if((scheduler.futureEvents.size() == 0) && (scheduler.inputFileEventsDone >= Constants.NUMBER_OF_FLOORS) && scheduler.allElevatorsIdle) {
        		Instant end = Instant.now();
        		Duration totalTime = Duration.between(scheduler.START_TIME, end);
        		System.out.println("Simulation took " + String.valueOf(totalTime.toMillis()) + " milliseconds.");
        		System.out.println("Simulation done.");
        		return;
        	}
        	
		}
        scheduler.formattedPrint("Scheduler state machine failed, thread exiting.");
	}

	
	private void setupStateMachine() {
    	stateList = new ArrayList<SchedulerState>(this.numOfStates);
		stateList.add(0, new SchedulerStateOne(this, 0));
		stateList.add(Constants.SCHEDULER_STATE_ONE, new SchedulerStateOne(this, 0));
	}

	
	
	

	public int startState() {
    	return(startState);
    }
	
	public SchedulerState SchedulerState() {
    	return(currentState);
    }
    
    public ArrayList<SchedulerState> stateList(){
    	return(stateList);
    }


    /**
     * Below: UDP functions===================================================================
     */
    public void manageEvent() {
    	receive();
		int len = this.receivePacket.getLength();
		String dataString = new String(this.receivePacket.getData(), 0, len);
		String[] newMessage = dataString.split(";");
		
		// If the message is from floor, save it in the event ArrayList
		if (newMessage[0].equals("f")) {
			formattedPrint("Message arrived from floor " + newMessage[1]);
			if(newMessage[newMessage.length-1].equals("done")) {
				this.inputFileEventsDone++; // Keep count of floors that are done
			}else {
				futureEvents.add(dataString);
			}
			// If first event then start timer
			if(!this.firstEventReceived) {
				this.START_TIME = Instant.now(); 
				this.firstEventReceived = true;
			}
		}
		// If the message is from the elevator, check if the elevator is on its way to the destination or stopped.
		// If so, send an Event to it. If not, send just an confirm message.
		else if (newMessage[0].equals("e")) {
			if(newMessage[2].equalsIgnoreCase("fault")) {
				formattedPrint("FAULT RECEIVED FROM ELEVATOR"+ newMessage[1]);
				return;
			}
			if((futureEvents.size() == 0) && (Integer.parseInt(newMessage[3]) == 0)) {
				this.allElevatorsIdle = true;
			}
			if (futureEvents.size() > 0) {
				this.allElevatorsIdle = false;
				if (Integer.parseInt(newMessage[3]) == 0) {
					formattedPrint(newMessage[1] + " = stopped elevator");
					String properEvent = futureEvents.remove(0);
					int floorNo = Integer.parseInt(properEvent.split(";")[1]);
					formSendPacket(properEvent, 200 + Integer.parseInt(newMessage[1]));
					send();
					formSendPacket("Event sent to an elevator", 100 + floorNo);
					send();
				}
				else if (Integer.parseInt(newMessage[3]) == -1 && Integer.parseInt(newMessage[2]) >= Integer.parseInt(newMessage[1])) {
					formattedPrint(newMessage[1] + " = coming down elevator");
					/*
					String properEvent = futureEvents.remove(0);
					int floorNo = Integer.parseInt(properEvent.split(";")[1]);
					
					formSendPacket(properEvent, 200 + Integer.parseInt(newMessage[1]));
					send();
					formSendPacket("Event sent to an elevator", 100 + floorNo);
					send();
					*/
				}
				else if (Integer.parseInt(newMessage[3]) == 1 && Integer.parseInt(newMessage[2]) < Integer.parseInt(newMessage[1])) {
					formattedPrint(newMessage[1] + " = coming up elevator");
					/*
					String properEvent = futureEvents.remove(0);
					int floorNo = Integer.parseInt(properEvent.split(";")[1]);
					
					formSendPacket(properEvent, 200 + Integer.parseInt(newMessage[1]));
					send();
					formSendPacket("Event sent to an elevator", 100 + floorNo);
					send();
					*/
				}
				else {
					formSendPacket("", 200 + Integer.parseInt(newMessage[1]));
					send();
				}
			}
			else {
				formSendPacket("", 200 + Integer.parseInt(newMessage[1]));
				send();
			}
		}
    }
    

	/*
	 * Method blocks until new UDP packet is received. Packet is set to variable 'this.receivePacket'.
	 * Port number that the packet was received on is returned.
	 */
	private synchronized void receive() {
		byte receivedData[] = new byte[MAX_MESSAGE_LEN];
		receivePacket = new DatagramPacket(receivedData, receivedData.length);
		
		try {
			receiveSocket.receive(receivePacket);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/*
	 * Sets variable this.replyPacket to a new packet with the specified data string and port #
	 */
	private void formSendPacket(String data, int replyPort) {
		byte[] msg = data.getBytes();
		try {
			replyPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), replyPort);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
	}
	
	/*
	 * Sends out 'this.replyPacket' over 'this.replySocket'
	 * NOTE: this.formReplySocket() should be called before this function
	 */
	private synchronized void send() {
		try {
			replySocket.send(replyPacket);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void formattedPrint(String s) {
		System.out.println("Scheduler:" + s);
	}
}
