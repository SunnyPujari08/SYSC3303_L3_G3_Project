package elevatorsim;

import elevatorsim.elevator.Elevator;

public class SchedulerStateSeven extends SchedulerState {

	public SchedulerStateSeven(Scheduler scheduler) {
		super(scheduler);
	}

	@Override
	public int handleEvent(EventData event) {
		return Constants.SCHEDULER_STATE_IDLE;
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
