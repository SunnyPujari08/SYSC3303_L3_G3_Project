package elevation;
import java.util.Date;

// More event types can be added in further iterations
enum EventType {
  FLOOR_BUTTON_PRESSED,
  ACK_FLOOR_BUTTON_PRESSED,
}


public class EventData {
	public Date timestamp;
	public int floorNum;
	public boolean upButton; // True=Up, False = Down
	public boolean downButton;
	public int carButton;
	public EventType eventType;
	
	
	// Constructor for floor events
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
