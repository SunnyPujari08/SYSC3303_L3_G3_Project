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
	public int destination; // True=Event being sent to scheduler, False=Event being sent from scheduler
	
	
	// Constructor for floor events
	public EventData(Date timestamp, int floorNum, boolean upButton, boolean downButton, int destination) {
		this.timestamp = timestamp;
		this.floorNum = floorNum;
		this.upButton = upButton;
		this.downButton = downButton;
		this.destination = destination;
		// this.carButton = -1;
	}
	// Constructor for elevator events
	public EventData(Date timestamp, int floorNum, int carButton, int destination) {
		this.timestamp = timestamp;
		this.floorNum = floorNum;
		this.destination = destination;
		this.carButton = carButton;
	}
}
