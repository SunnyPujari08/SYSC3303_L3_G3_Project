package elevatorsim;

import elevatorsim.elevator.Elevator;

public class SchedulerStateOne extends SchedulerState {


	public SchedulerStateOne(Scheduler scheduler, int elevatorID) {
		super(scheduler, elevatorID);

	}

	@Override
	public int handleEvent(EventData event) {
		scheduler.currentTripEvent = event;
		if(event.eventType == EventType.FLOOR_REQUEST || event.eventType == EventType.FLOOR_REQUEST_UP ||event.eventType == EventType.FLOOR_REQUEST_DOWN) {
			Constants.formattedPrint("Scheduler got FR.");
			// scheduler.sendResponseToFloor(event.floorNum);
			if(scheduler.elevatorCurrentFloor < event.floorNum)
				scheduler.sendUpRequestToElevator(0, event.floorNum);
			else
				scheduler.sendDownRequestToElevator(0, event.floorNum);
			return Constants.SCHEDULER_STATE_TWO;
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
