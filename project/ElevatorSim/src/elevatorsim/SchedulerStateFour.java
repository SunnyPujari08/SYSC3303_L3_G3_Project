package elevatorsim;

import elevatorsim.elevator.Elevator;

public class SchedulerStateFour extends SchedulerState {

	public SchedulerStateFour(Scheduler scheduler, int elevatorID) {
		super(scheduler, elevatorID);
	}

	@Override
	public int handleEvent(EventData event) {
		if(event.eventType == EventType.ELEVATOR_PICK_FLOOR) {
			int destFloor = scheduler.currentTripEvent.destinationFloor;
			if(scheduler.elevatorCurrentFloor > destFloor) {
				scheduler.sendDownRequestToElevator(this.elevatorID, destFloor);
				return Constants.SCHEDULER_STATE_FIVE;
			}
			else if(scheduler.elevatorCurrentFloor < destFloor) {
				scheduler.sendUpRequestToElevator(this.elevatorID, destFloor);
				return Constants.SCHEDULER_STATE_SIX;
			}
		}
		
		return Constants.SCHEDULER_STATE_FOUR;
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
