# Message processing

Implement a small message processing application that satisfies the below requirements for
processing sales notification messages. You should assume that an external company will be sending
you the input messages, but for the purposes of this exercise you are free to define the interfaces.
Processing requirements

 All sales must be recorded

 All messages must be processed

 After every 10th message received your application should log a report detailing the number
of sales of each product and their total value.

 After 20 messages your application should log that it is pausing, stop accepting new
messages and log a report of the adjustments that have been made to each sale type while
the application was running.

#Build Instructions

Install the Maven client (version 3.* or better). Then clone from GIT and then use Maven:

$ git clone

$ mvn install

#Java version
1.8 or above

# Sample Message Types for Testing using Postman
Message Type 1 : 
{
	"productName":"Samsung",
    "price":"1",
    "messageType":"SINGLE_SELL_EVENT"
}

Message Type 2 : 
{
	"productName":"Samsung",
    "price":"1",
    "noOfQuantity":2,
    "messageType":"MULTI_SELL_EVENT"
}

Message Type 3 : 
{
	"productName":"Samsung",
    "messageType":"ADJUSTMENT_EVENT",
    "adjustmentPrice":1,
    "operationType":"ADD"
}

#Rest Endpoint
http://localhost:9087/processor/processMessage/

#Test classes
MessageProcessingControllerTest

#Assumptions
- Based on understanding created common message structure 
- Message will always contains MessageType attribute value
- Added dummy method for incoming message validation 
- No Security has been implemented

#Executable jar

1. Open a command window in the projects base directory.
2. Run the command: mvn package
3. In the folder: target\ the executable jar will be created with the name: 'MessageProcessingService-1.0-SNAPSHOT'
4. from the directory with the executable jar run the command: java -jar MessageProcessingService-1.0-SNAPSHOT.jar

