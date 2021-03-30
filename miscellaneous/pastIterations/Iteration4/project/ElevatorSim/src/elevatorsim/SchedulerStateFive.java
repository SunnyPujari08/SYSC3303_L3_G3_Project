package elevatorsim;

import elevatorsim.elevator.Elevator;

public class SchedulerStateFive extends SchedulerState {

	public SchedulerStateFive(Scheduler scheduler) {
		super(scheduler);
	}

	@Override
	public int handleEvent(EventData event) {
		if(event.eventType == EventType.ELEVATOR_ARR_FLOOR_UP) {
			scheduler.sendResponseToFloor(event.destinationFloor);
			return Constants.SCHEDULER_STATE_SEVEN;
		}
		
		return Constants.SCHEDULER_STATE_FIVE;
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
