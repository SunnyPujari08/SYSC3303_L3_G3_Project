package elevatorsim;

import elevatorsim.elevator.Elevator;

public class SchedulerStateIdle extends SchedulerState {


	public SchedulerStateIdle(Scheduler scheduler, int elevatorID) {
		super(scheduler, elevatorID);
	}
	
	@Override
	public int handleEvent(EventData event) {
		if(event.eventType == EventType.ELEVATOR_PICK_FLOOR) {
			if(elevator.currentFloor < event.destinationFloor) {
				// Move up
				scheduler.sendUpRequestToElevator(elevator.elevatorID, event.destinationFloor);
				return Constants.SCHEDULER_STATE_SIX;
			} else {
				scheduler.sendDownRequestToElevator(elevator.elevatorID, event.destinationFloor);
				return Constants.SCHEDULER_STATE_FIVE;
			}
		}
		
		if(event.eventType == EventType.FLOOR_REQUEST) {
			if(elevator.currentFloor < event.floorNum) {
				// Move up
				scheduler.sendUpRequestToElevator(elevator.elevatorID, event.floorNum);
				
				return Constants.SCHEDULER_STATE_TWO;
			} else {
				System.out.println("here" + String.valueOf(event.floorNum));
				scheduler.sendDownRequestToElevator(elevator.elevatorID, event.floorNum);
				return Constants.SCHEDULER_STATE_ONE;
			}
		}
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
