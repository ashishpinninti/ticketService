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
### Find the number of seats available within the venue    
curl -i -X GET http://localhost:8080/ticketService/v1/seatsAvailable

50

### Find and hold the best available seats on behalf of a customer
curl -X POST http://localhost:8080/ticketService/v1/findAndHold/numSeats/3/customerEmail/ashish@dummy.com 

{"id":20,"holdTime":1479226392277,"confirmationCode":null,"reservationTime":null,"seats":[{"id":3},{"id":1},{"id":2}]}

### Reserve and commit a specific group of held seats for a customer
####Successful reservation returns confirmation code:
curl -X POST http://localhost:8080/ticketService/v1/reserve/seatHoldId/22/customerEmail/ashish@dummy.com
7cbab576-e04a-4976-8788-199489588a4f

####If the SeatHoldId is not valid:
curl -X POST http://localhost:8080/ticketService/v1/reserve/seatHoldId/20/customerEmail/ashish@dummy.com
  {
    "timestamp": 1479228013323,
    "status": 400,
    "error": "Bad Request",
    "exception": "com.apinninti.ticketService.exception.SeatHoldNotValidException",
    "message": "Seat Hold is not valid, may be it is expired!",
    "path": "/ticketService/v1/reserve/seatHoldId/20/customerEmail/ashish@dummy.com"
  }

