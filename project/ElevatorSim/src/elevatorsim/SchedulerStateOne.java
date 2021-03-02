package elevatorsim;

import elevatorsim.elevator.Elevator;

public class SchedulerStateOne extends SchedulerState {

	public SchedulerStateOne(Elevator elevator, Scheduler scheduler) {
		super(elevator, scheduler);
	}

	@Override
	public int handleEvent(EventData event) {
		if(event.eventType == EventType.ELEVATOR_ARR_FLOOR_DOWN) {
			System.out.println("Floornum: " + String.valueOf(event.floorNum));
			scheduler.sendResponseToFloor(event.floorNum);
			return Constants.SCHEDULER_STATE_THREE;
		}
		
		return Constants.SCHEDULER_STATE_ONE;
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
