package elevatorsim;
import java.util.Date;

public class EventData {
	public Date timestamp;
	public int floorNum;
	public boolean upButton; // True=Up, False = Down
	public boolean downButton;
	public int carButton;
	public int destinationFloor;
	public EventType eventType;
	public boolean fromScheduler;
	
    public static String convertEventToString(EventData event) {
		String s = "";
		s += event.eventType.name() + ";";
		s += String.valueOf(event.floorNum) + ";";
		if(event.destinationFloor>0)
			s += String.valueOf(event.destinationFloor) + ";";
		return s;
	}
    
    public static EventData convertStringToEvent(String eventString) {
    	String s[] = eventString.split(";");
    	EventType eventType;
    	if(s[0].contains("MOVE_REQUEST_UP")) 
    		eventType = EventType.MOVE_REQUEST_UP;
    	else if(s[0].contains("MOVE_REQUEST_DOWN")) 
    		eventType = EventType.MOVE_REQUEST_DOWN;
    	else if(s[0].contains("ELEVATOR_ARR_FLOOR_UP")) 
    		eventType = EventType.ELEVATOR_ARR_FLOOR_UP;
    	else if(s[0].contains("ELEVATOR_ARR_FLOOR_DOWN")) 
    		eventType = EventType.ELEVATOR_ARR_FLOOR_DOWN;
    	else if(s[0].contains("ELEVATOR_PICK_FLOOR")) 
    		eventType = EventType.ELEVATOR_PICK_FLOOR;
    	else if(s[0].contains("ELEVATOR_ARRIVED")) 
    		eventType = EventType.ELEVATOR_ARRIVED;
    	else
    		return null;
    	if(s.length==3) {
    		EventData newEvent = new EventData(eventType, Integer.parseInt(s[1]), Integer.parseInt(s[2]));
    		return newEvent;
    	} else if(s.length == 2) {
    		EventData newEvent = new EventData(Integer.parseInt(s[1]), eventType);
    		return newEvent;
    	}
    	return null;
    }
	
	// TODO clean up all these constructors

	
	public EventData(EventType eventType, int destinationFloor) {
		this.eventType = eventType;
		this.destinationFloor = destinationFloor;
		this.fromScheduler = false;
	}
	
	public EventData(EventType eventType, int destinationFloor, boolean simulated) {
		this.eventType = eventType;
		this.destinationFloor = destinationFloor;
		this.fromScheduler = simulated;
	}
	
	public EventData(int floorNum, EventType eventType, boolean simulated) {
		this.eventType = eventType;
		this.floorNum = floorNum;
		this.fromScheduler = simulated;
	}
	
	public EventData(EventType eventType) {
		this.eventType = eventType;
		this.fromScheduler = false;
	}
	
	public EventData(EventType eventType, boolean simulated) {
		this.eventType = eventType;
		this.fromScheduler = simulated;
	}
	
	public EventData(int floorNum, EventType eventType) {
		this.floorNum = floorNum;
		this.eventType = eventType;
		this.fromScheduler = false;
	}
	
	public EventData(EventType eventType, int floorNum, int destinationFloor) {
		this.eventType = eventType;
		this.floorNum = floorNum;
		this.destinationFloor = destinationFloor;
		this.fromScheduler = false;
	}
	
	// Constructor for floor events
	public EventData(Date timestamp, int floorNum, EventType eventType, int destinationFloor) {
		this.timestamp = timestamp;
		this.floorNum = floorNum;
		this.eventType = eventType;
		this.destinationFloor = destinationFloor;
		this.fromScheduler = false;
		// this.carButton = -1;
	}
	public EventData(Date timestamp, int floorNum, boolean upButton, boolean downButton, EventType eventType) {
		this.timestamp = timestamp;
		this.floorNum = floorNum;
		this.upButton = upButton;
		this.downButton = downButton;
		this.eventType = eventType;
		this.fromScheduler = false;
		// this.carButton = -1;
	}
	// Constructor for elevator events
	public EventData(Date timestamp, int floorNum, int carButton, EventType eventType) {
		this.timestamp = timestamp;
		this.floorNum = floorNum;
		this.eventType = eventType;
		this.carButton = carButton;
		this.fromScheduler = false;
	}
}
