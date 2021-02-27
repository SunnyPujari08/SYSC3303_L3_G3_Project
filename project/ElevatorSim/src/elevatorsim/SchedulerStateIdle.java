package elevatorsim;

import elevatorsim.elevator.Elevator;

public class SchedulerStateIdle extends SchedulerState {

	public SchedulerStateIdle(Elevator elevator, Scheduler scheduler) {
		super(elevator, scheduler);
	}
	
	@Override
	public int handleEvent(EventData event) {
		if()
		return 0;
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
