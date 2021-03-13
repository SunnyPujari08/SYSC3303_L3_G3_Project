package elevatorsim;

import elevatorsim.elevator.Elevator;

public class SchedulerStateEight extends SchedulerState {

	public SchedulerStateEight(Scheduler scheduler) {
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
