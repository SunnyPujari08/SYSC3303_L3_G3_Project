/**
 * The Elevator class represents an Elevator in the Elevator
 * Control System.
 *
 * @author Kashish Saxena - 101107204, Sunjeevani Pujari - 101110032
 * @version February 6, 2021
 */

public class Elevator implements Runnable {

    //private Scheduler scheduler;
    private int floor;
    private static final int UP_DIR = 1;
    private static final int DOWN_DIR = 0;
    private int elevatorID;
    private boolean isFree;
    private int currentFloor;
    private int destFloor;


    public Elevator(int elevatorID) {
        this.elevatorID = elevatorID;
        isFree = true;
    }

    public void checkWorkFromScheduler(int direction, int destFloor) throws InterruptedException
    {
        // Pick up Event
        if (direction == UP_DIR) {
            for (int i = 0; i < (destFloor - currentFloor); i++) {
                System.out.print("Currently at Floor: "+ currentFloor + ".");
                wait(5000); // Waiting to pass a floor
            }
        }
        // Pick up Event
        if (direction == DOWN_DIR) {
            for (int i = 0; i < (currentFloor - destFloor); i++) {
                System.out.print("Currently at Floor: "+ currentFloor + ".");
                wait(5000); // Waiting to pass a floor
            }
        }

        currentFloor = destFloor;
    }

    public void sendWorkDoneToScheduler()
    {

    }

    // send pressed button to scheduler
    public void sendEventToScheduler()
    {
        // read event from a text file, convert that to a data structure and send it to scheduler
        //scheduler.readFromElevator(datastructure);
        // assuming that in the readFromElevator() in scheduler, it takes the datastructure and stores it and calls the writeToElevator() to send an event back
    }

    /**
     * Returns if the Elevator is free or not.
     * @return True if elevator is free, False otherwise.
     */
    private boolean isFree()
    {
        return isFree;
    }

    private int getCurrentFloor()
    {
        return currentFloor;
    }

    private void setCurrentFloor(int newFloor)
    {
        currentFloor = newFloor;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while(true){
            System.out.println(Thread.currentThread().getName() + " is ready to make and eat a sandwich now.");
            //scheduler.writeToElevator();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
        }
    }
}

