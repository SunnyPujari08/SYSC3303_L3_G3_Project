package elevatorsim;

import elevatorsim.elevator.Elevator;

public class SchedulerStateTwo extends SchedulerState {

	public SchedulerStateTwo(Scheduler scheduler) {
		super(scheduler);
	}

	@Override
	public int handleEvent(EventData event) {
		if((event.eventType == EventType.ELEVATOR_ARR_FLOOR_UP || event.eventType == EventType.ELEVATOR_ARR_FLOOR_DOWN) && event.floorNum == scheduler.currentTripEvent.destinationFloor){
			//scheduler.sendResponseToFloor(event.floorNum);
			return Constants.SCHEDULER_STATE_THREE;
		}
		
		return Constants.SCHEDULER_STATE_TWO;
	}

	@Override
	public void entranceActions() {
		// TODO Auto-generated method stub

	}

	@Override
	public void exitActions() {
		// TODO Auto-generated method stub

	}

}
