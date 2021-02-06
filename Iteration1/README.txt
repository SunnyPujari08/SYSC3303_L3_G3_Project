SYSC 3303
Project Iteration 1
L3_G3

Members:
- Sunjeevani Pujari
- Kashish Saxena
- Ezra Pierce
- Jeong Won Kim
- Cameron Chung

Hello World!

To whomever reads this,

You have the pleasure of opening and trying out
our new elevator program that allows you
to simulate an elevator, how exciting!!!

Contents List:
- Iteration 1 Sequence Diagram.vpd.png
- Iteration 1 Class Diagram.vpd.png
- input.txt
- Elevator.java
- Floor.java
- EventData.java
- Scheduler.java
- UnitTest.java

Setup Instructions: 
To get this all ready, open the package in the above folder
"src->ElevatorSim->src->elevatorsim->Scheduler.java", run this program then you'll
see just how this program runs. If you want to see more you can look through each of
the other classes we made for this program and use "UnitTest.java" to test the program.

Testing Details:
Test 1: We will test if the Floor class can convert the events contained in an input.txt.file
	to the correct format. We do this by first calling the readEventFromTextFile()
	method then we call the converTextEvent() method. If returned event from the
	method is correct with what we orignally inputted then we know that the Floor
	class is able to read any incoming events.
Test 2: In this test, we check to see if the Scheduler is reading the events correctly
	from the event list. We do this by comparing the eventReadFromFloor variable
	to our hardcoded value we have in place.
Test 3: In our third test, we are testing to ensure that the elevator class is reading
	the correct event from the event list. We do this my comparing a newEvent, of type EventData,
	with the result from the checkWorkFromScheduler() elevator method. 
Test 4: In our final test, we check to ensure that the elevator can send newEvents to
	the event list by changing the eventType of the event received. We can check this
	by checking if the newEvent is of the correct eventType is acknowledge floor button
	pressed (ACK_FLOOR_BUTTON_PRESSED).

Breakdown of responsibilities:
Ezra Pierce:
	- Implementation of Scheduler class
	- Rough draft of UML class diagram

Cameron Chung: 
	- Implementation of UML class diagram
	- Implementation of UML Sequence diagram
	- Implementation of README text file

Kashish Saxena
	- Implementation of Elevator class
	- Review of UML diagrams and README file

Sunjeevani Pujari
	- Implementation of Elevator class
	- Implementation of JUnit Test
	
Jeong Won Kim
	- Implementation of Floor class
	- Implementation of JUnit Test
	

