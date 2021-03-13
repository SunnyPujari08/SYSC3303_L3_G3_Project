package elevatorsim;

import elevatorsim.elevator.Elevator;

public class SchedulerStateFour extends SchedulerState {

	public SchedulerStateFour(Scheduler scheduler) {
		super(scheduler);
	}

	@Override
	public int handleEvent(EventData event) {
		if(event.eventType == EventType.ELEVATOR_PICK_FLOOR) {
			if(elevator.currentFloor > elevator.destFloor) {
				scheduler.sendDownRequestToElevator(elevator.elevatorID, elevator.destFloor);
				return Constants.SCHEDULER_STATE_FIVE;
			}
			else if(elevator.currentFloor < elevator.destFloor) {
				scheduler.sendUpRequestToElevator(elevator.elevatorID, elevator.destFloor);
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
