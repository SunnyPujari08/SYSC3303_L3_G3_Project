
public class EventData {
	public long timestamp;
	public int floorNum;
	public boolean floorButton; // True=Up, False = Down
	public int carButton;
	public boolean destination; // True=Event being sent to scheduler, False=Event being sent from scheduler
	
	
	// Constructor for floor events
	public EventData(long timestamp, int floorNum, boolean floorButton, boolean destination) {
		this.timestamp = timestamp;
		this.floorNum = floorNum;
		this.destination = destination;
		// this.carButton = -1;
		this.floorButton = floorButton;
	}
	// Constructor for elevator events
	public EventData(long timestamp, int floorNum, int carButton, boolean destination) {
		this.timestamp = timestamp;
		this.floorNum = floorNum;
		this.destination = destination;
		this.carButton = carButton;
	}
}
