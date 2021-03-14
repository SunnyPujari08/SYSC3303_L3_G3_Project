package elevatorsim;

import elevatorsim.elevator.Elevator;

public class SchedulerStateTwo extends SchedulerState {

	public SchedulerStateTwo(Scheduler scheduler, int elevatorID) {
		super(scheduler, elevatorID);
	}

	@Override
	public int handleEvent(EventData event) {
		Constants.formattedPrint("Event got: " + event.eventType);
		if((event.eventType == EventType.ELEVATOR_ARR_FLOOR_UP || event.eventType == EventType.ELEVATOR_ARR_FLOOR_DOWN) && event.floorNum == scheduler.currentTripEvent.floorNum){
			//scheduler.sendResponseToFloor(event.floorNum);
			scheduler.elevatorCurrentFloor = event.floorNum;
			return Constants.SCHEDULER_STATE_THREE;
		}
		
		return Constants.SCHEDULER_STATE_TWO;
	}

	@Override
	public void entranceActions() {
		Constants.formattedPrint("GOING TO: " + scheduler.currentTripEvent.destinationFloor);
		// TODO Auto-generated method stub

	}

	@Override
	public void exitActions() {
		// TODO Auto-generated method stub

	}

}
