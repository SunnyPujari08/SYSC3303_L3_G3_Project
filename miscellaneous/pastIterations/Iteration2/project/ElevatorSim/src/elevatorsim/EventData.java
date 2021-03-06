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
	
	// TODO clean up all these constructors
	public EventData(EventType eventType, int destinationFloor) {
		this.eventType = eventType;
		this.destinationFloor = destinationFloor;
	}
	
	public EventData(EventType eventType) {
		this.eventType = eventType;
	}
	
	public EventData(int floorNum, EventType eventType) {
		this.floorNum = floorNum;
		this.eventType = eventType;
	}
	
	public EventData(EventType eventType, int floorNum, int destinationFloor) {
		this.eventType = eventType;
		this.floorNum = floorNum;
		this.destinationFloor = destinationFloor;
	}
	
	// Constructor for floor events
	public EventData(Date timestamp, int floorNum, boolean upButton, boolean downButton, EventType eventType, int destinationFloor) {
		this.timestamp = timestamp;
		this.floorNum = floorNum;
		this.upButton = upButton;
		this.downButton = downButton;
		this.eventType = eventType;
		this.destinationFloor = destinationFloor;
		// this.carButton = -1;
	}
	public EventData(Date timestamp, int floorNum, boolean upButton, boolean downButton, EventType eventType) {
		this.timestamp = timestamp;
		this.floorNum = floorNum;
		this.upButton = upButton;
		this.downButton = downButton;
		this.eventType = eventType;
		// this.carButton = -1;
	}
	// Constructor for elevator events
	public EventData(Date timestamp, int floorNum, int carButton, EventType eventType) {
		this.timestamp = timestamp;
		this.floorNum = floorNum;
		this.eventType = eventType;
		this.carButton = carButton;
	}
}
