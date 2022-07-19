# REST api Word Counter

## How to Setup
Download the 

## How to Run
### Run as a Java Project
Run the main method in the API class in the mattias.hemtest package.

### Run as a Jar
Start the jar from command line / terminal with

    java -jar wordCounter.jar

to start the application.

## How to Use
When the application is running, sending an http post request with text to the 
server, http://localhost:3000/count will take the string input and return a 
parsed string with the most common words and their occurrence.