package elevatorsim;

import elevatorsim.elevator.Elevator;

public class SchedulerStateThree extends SchedulerState {

	public SchedulerStateThree(Scheduler scheduler, int elevatorID) {
		super(scheduler, elevatorID);
	}

	@Override
	public int handleEvent(EventData event) {
		if((event.eventType == EventType.ELEVATOR_ARR_FLOOR_UP || event.eventType == EventType.ELEVATOR_ARR_FLOOR_DOWN) && event.floorNum == scheduler.currentTripEvent.destinationFloor){
			scheduler.elevatorCurrentFloor = event.floorNum;
			Constants.formattedPrint("Trip done.");
			return Constants.SCHEDULER_STATE_ONE;
		}
		
		return Constants.SCHEDULER_STATE_THREE;
	}

	@Override
	public void entranceActions() {
		if(scheduler.elevatorCurrentFloor < scheduler.currentTripEvent.destinationFloor)
			scheduler.sendUpRequestToElevator(0, scheduler.currentTripEvent.destinationFloor);
		else
			scheduler.sendDownRequestToElevator(0, scheduler.currentTripEvent.destinationFloor);

	}

	@Override
	public void exitActions() {
		// TODO Auto-generated method stub

	}

}
