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
