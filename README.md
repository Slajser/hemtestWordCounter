# REST api Word Counter

## How to Setup
Make sure you have java otherwise no setup required for the jar, just download the jar.

## How to Run

### Run as a Jar 
Start the jar from command line / terminal with

    java -jar wordCounter.jar

to start the application. It will start on port 3000.
Ensure the port is available.

## How to Use
When the application is running, sending an http post request with text to the 
server, http://localhost:3000/count, will take the string input and return a 
parsed string with the most common words and their occurrence.
