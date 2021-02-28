package elevatorsim;

import elevatorsim.elevator.Elevator;

public class SchedulerStateTwo extends SchedulerState {

	public SchedulerStateTwo(Elevator elevator, Scheduler scheduler) {
		super(elevator, scheduler);
	}

	@Override
	public int handleEvent(EventData event) {
		Constants.formattedPrint("S-TWO picked up event.");
		if(event.eventType == EventType.ELEVATOR_ARR_FLOOR_UP) {
			scheduler.sendResponseToFloor(event.floorNum);
			return Constants.SCHEDULER_STATE_FOUR;
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
