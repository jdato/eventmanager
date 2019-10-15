# eventmanager for Follower Maze

The Java application provided with this "eventmanager" 
package is one possible solution for the backend developer 
coding challenge provided by the SoundCloud engineering team. \o/

### Overview

The eventmanager application is a program that is listening for clients and events that
could come from any source on a network. Clients can register themselves on **port 9099** 
by simply sending their id followed by a new line (*e.g. 35\n*).<br>
When registered they will be supplied with notifications triggered
by an event stream which the application receives on **port 9090**.
<br><br>**IMPORTANT:** The eventmanager needs to be started before 
clients or events can be registered. 

### Usage of the eventmanager

To use the eventmanager application with the provided 
follower maze event streaming test program, the application 
simply needs to be built and executed. It is configured 
to work with the default configuration of the 
'follower-maze-2.0.jar', which can be found under 
'eventmanager/src/main/resources'. 

There is one parameter that needs to be configured in the 
eventmanager as well, in case the configuration of the
'follower-maze-2.0.jar' differs from the default values. The parameter is
the **maxEventSourceBatchSize**. In case it is being
set to a different value for the execution of 
the 'follower-maze-2.0.jar', the equal parameter value 
needs to be supplied to eventmanager as well. In order to do 
so it can directly be passed to the main class of the 
application as an argument. However there are different options
to do so.

- If you're using an IDE to run the application you can simply 
add the desired integer value to the configuration as a
Program Argument.
- If you would like to run the application from the command
line you need 
[Apache maven](https://maven.apache.org/install.html) 
in your terminal environment. Then follow the steps below.
<br>
    1. Navigate to 'eventmanager' project root folder in 
    terminal
    2. Execute '**mvn compile**'
    3. Execute '**mvn exec:java -Dexec.mainClass=com.soundcloud.devchallenge.eventmanager.EventManagerApplication -Dexec.args="335"**' (This would be necessary if the maxEventSourceBatchSize in the follower maze test program ist set to 335)  

Once the application has been started, it listens to the 
default configured ports 9090 and 9099. Those are for simplicity 
reasons non configurable. They could easily be made configurable in a future release
if those would be the customers requirements.

### Implementation Details

The business logic of the application is only utilizing the 
functionality and features of the Java 8 platform. Only for 
Logging and Testing third party libraries are being used. Further for
dependency management the [Apache Maven software project management and comprehension tool](https://maven.apache.org/)
is being utilized.

### Third party libraries

A few third party libraries are supplementing the 
application as well, which are the following.

- SLF4J and LOG4J are used as a logging framework for 
best practice reasons
- JUnit provides the necessary Test Environment for 
the developer side unit testing