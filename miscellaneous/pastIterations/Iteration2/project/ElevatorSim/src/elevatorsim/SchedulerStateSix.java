package elevatorsim;

import elevatorsim.elevator.Elevator;

public class SchedulerStateSix extends SchedulerState {

	public SchedulerStateSix(Elevator elevator, Scheduler scheduler) {
		super(elevator, scheduler);
	}

	@Override
	public int handleEvent(EventData event) {
		if(event.eventType == EventType.ELEVATOR_ARR_FLOOR_DOWN) {
			scheduler.sendResponseToFloor(event.destinationFloor);
			return Constants.SCHEDULER_STATE_EIGHT;
		}
		
		return Constants.SCHEDULER_STATE_SIX;
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
