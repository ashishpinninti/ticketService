# Ticket Service

This application is a RESTful service that facilitates finding the number of seats available within the venue, hold the best available 
seats on behalf of a customer and reserve and commit a specific group of held seats for a customer.

The held seats will expire in 60 seconds by default, however the expiry time is configurable. The number seats in the Venue is 50 by default.
However, the number seats is also configurable. We can hold best available seats,
that is when the user tries to hold 25 seats when only 20 seats are available, the best available remaining 20 seats will be held.
If we are unable to reserve any seats, then empty SeatHold object is returned. If user attempts to reserve seats by sending 
SeatHoldId and customer email, then we will send a reservation confirmation code if the seats held didn't expire. 
The application handles various exception cases such as attempting to reserve with expired SeatHoldId, irrelevant customer etc 
are handled returning proper HTTP Codes and messages.


## Technologies and Frameworks

- External Framework: Spring Boot
- Data Store: Embedded H2 Database

## Building application by Maven

Run 'mvn clean install' in project root folder build solution. This will create jar file at ./target/ticketService-0.0.1-SNAPSHOT.jar

## Running tests by Maven

Run 'mvn test' to run all unit and integration tests. The integration tests perform end-to-end tests by placing rest calls using rest client.
Integration tests  use independent test configuration to run integration tests so that production state will not be affected.

## Running application

- Run 'mvn spring-boot:run' to run the application. This is run the embedded tomcat server on port 8080 by default, however this is configurable.
- As we are packaging app as a jar file, we can run the app using 'java -jar target/ticketService-0.0.1-SNAPSHOT.jar --venue.numberOfSeats=13 --holdTime.expireInSeconds=90' command.
  If we specify command line properties venue.numberOfSeats and holdTime.expireInSeconds, then default values will be applied. 

## Interacting with application

    curl -i -X GET http://localhost:8080/ticketService/v1/seatsAvailable
      50

egregtrgrtg
