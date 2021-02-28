SYSC 3303
Project Iteration 2
L3_G3

Members:
- Sunjeevani Pujari
- Kashish Saxena
- Ezra Pierce
- Jeong Won Kim
- Cameron Chung


To whomever reads this,

You have the pleasure of opening and trying out
our new elevator program that allows you
to simulate an elevator, how exciting!!!

Contents List:
- State_Machine_Elevator.png
- State_Machine_Scheduler.png
- UML_Class_Diagram.png
- UML_Sequence_Diagram.png
- SchedulerStateIdle.java
- SchedulerState.java
- SchedulerStateX.java (X: Idle, 1-8)
- Constants.java
- Elevator.java
- ElevatorStateX.java (X: One - Twelve)

Setup Instructions: 
To get this all ready, open the package in the folder
"src->ElevatorSim->src->elevatorsim->Scheduler.java", run this program then you'll
see how this program runs. If you want to see more you can look through each of
the other classes we made for this program.

Note: The implementation of our state machines is complete, all states added with the handlers for the events shown in the diagrams.
Currently our program does not have a way of simulating external events (button presses etc.). With this limitation, we haven't yet
been able to run through all states in our system. Currently our system reacts to one button press by triggering the
expected state changes listed in our diagrams. We are working on being able to simulate button presses but we prioritized
finishing the required state machines and did not leave enough time for setting up our simulation. We are working a
solution to simulate button presses and will have it ready for the Iteration 2 demo


Testing Details:
Test 1:  Elevator is at current floor, then moves up to a higher floor to pick up and drop off user at a lower floor.
Test 2: Elevator is at current floor, then moves up to a higher floor to pick up and drop off user at an even higher floor. 
	(ex: car at ground floor, goes to 2nd floor to pick up and drops off at 6th floor)
Test 3: Elevator pick up from current floor and drops off the user to an upper floor (exception for highest floor)
Test 4: Elevator picks up user from current floor and drops off user to lower floor (exception for ground floor)

To test the code, run the UnitTest.java file. 

Breakdown of responsibilities:
Ezra Pierce:
	- Scheduler state machine code design and implementation
	- Elevator state machine code design and implementation
	- Refactoring Scheduler and Elevator classes to work with state machines

Cameron Chung: 
	- Implementation of Elevator state machine code

Kashish Saxena
	- Implementation of the Elevator and Scheduler state machines

Sunjeevani Pujari
	- Implementation of the Elevator and Scheduler state machines
	
Jeong Won Kim
	- Implementation of Elevator state machine code
	- Implementation of Scheduler state machine code