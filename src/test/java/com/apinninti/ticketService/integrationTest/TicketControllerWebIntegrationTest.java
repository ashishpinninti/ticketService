package com.apinninti.ticketService.integrationTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.apinninti.TestConfig;
import com.apinninti.ticketService.entity.SeatHold;
import com.apinninti.ticketService.exception.CustomerNotValidException;
import com.apinninti.ticketService.exception.SeatHoldNotValidException;

/**
 * 
 * @author apinninti
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TestConfig.class)
@WebIntegrationTest
@ActiveProfiles("test")
public class TicketControllerWebIntegrationTest {

	@Before
	public void resetDB() {
		RestTemplate restTemplate = new TestRestTemplate();
		restTemplate.delete("http://localhost:8082/ticketService/v1/reset");
	}

	@Test
	public void testNumSeatsAvailableWIT() throws Exception {
		Integer expected = 50;
		RestTemplate restTemplate = new TestRestTemplate();
		ResponseEntity<Integer> response = restTemplate.getForEntity(
				"http://localhost:8082/ticketService/v1/seatsAvailable",
				Integer.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		Integer actual = response.getBody();
		assertThat(actual, equalTo(expected));

		Integer numSeats = 6;
		String customerEmail = "a@a.com";
		ResponseEntity<SeatHold> seatHold = restTemplate.postForEntity(
				"http://localhost:8082/ticketService/v1/findAndHold/numSeats/"
						+ numSeats + "/customerEmail/" + customerEmail, null,
				SeatHold.class);
		assertThat(seatHold.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(seatHold.getBody().getSeats().size(), equalTo(numSeats));

		ResponseEntity<Integer> availableSeatsAfterHold = restTemplate
				.getForEntity(
						"http://localhost:8082/ticketService/v1/seatsAvailable",
						Integer.class);
		assertThat(availableSeatsAfterHold.getStatusCode(),
				equalTo(HttpStatus.OK));
		assertThat(availableSeatsAfterHold.getBody(), equalTo(expected
				- numSeats));
	}

	@Test
	public void testFindAndHoldSeatsWIT() throws Exception {
		Integer numSeats = 7;
		Integer totalSeats = 50;
		String customerEmail = "a@a.com";
		RestTemplate restTemplate = new TestRestTemplate();
		ResponseEntity<SeatHold> seatHold = restTemplate.postForEntity(
				"http://localhost:8082/ticketService/v1/findAndHold/numSeats/"
						+ numSeats + "/customerEmail/" + customerEmail, null,
				SeatHold.class);
		assertThat(seatHold.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(seatHold.getBody().getSeats().size(), equalTo(numSeats));

		ResponseEntity<Integer> availableSeatsAfterHold = restTemplate
				.getForEntity(
						"http://localhost:8082/ticketService/v1/seatsAvailable",
						Integer.class);
		assertThat(availableSeatsAfterHold.getStatusCode(),
				equalTo(HttpStatus.OK));
		assertThat(availableSeatsAfterHold.getBody(), equalTo(totalSeats
				- numSeats));
	}

	@Test
	public void testFindAndHoldBestAvailableSeatsWIT() throws Exception {
		Integer numSeats = 55;
		Integer totalSeats = 50;
		String customerEmail = "a@a.com";
		RestTemplate restTemplate = new TestRestTemplate();
		ResponseEntity<SeatHold> seatHold = restTemplate.postForEntity(
				"http://localhost:8082/ticketService/v1/findAndHold/numSeats/"
						+ numSeats + "/customerEmail/" + customerEmail, null,
				SeatHold.class);
		assertThat(seatHold.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(seatHold.getBody().getSeats().size(), equalTo(totalSeats));

		ResponseEntity<Integer> availableSeatsAfterHold = restTemplate
				.getForEntity(
						"http://localhost:8082/ticketService/v1/seatsAvailable",
						Integer.class);
		assertThat(availableSeatsAfterHold.getStatusCode(),
				equalTo(HttpStatus.OK));
		assertThat(availableSeatsAfterHold.getBody(), equalTo(0));
	}

	@Test
	public void testReserveSeatsWIT() throws Exception {
		Integer numSeats = 7;
		String customerEmail = "a@a.com";
		RestTemplate restTemplate = new TestRestTemplate();
		ResponseEntity<SeatHold> seatHold = restTemplate.postForEntity(
				"http://localhost:8082/ticketService/v1/findAndHold/numSeats/"
						+ numSeats + "/customerEmail/" + customerEmail, null,
				SeatHold.class);
		assertThat(seatHold.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(seatHold.getBody().getSeats().size(), equalTo(numSeats));
		Integer seatHoldId = seatHold.getBody().getId();
		ResponseEntity<String> responseConfirmationCode = restTemplate
				.postForEntity(
						"http://localhost:8082/ticketService/v1/reserve/seatHoldId/"
								+ seatHoldId + "/customerEmail/"
								+ customerEmail, null, String.class);
		assertThat(responseConfirmationCode.getStatusCode(),
				equalTo(HttpStatus.OK));
		assertThat("Confirmation Code shouldn't be empty",
				!responseConfirmationCode.getBody().isEmpty());
	}

	@Test
	public void testReserveSeatsSeatHoldNotValidExceptionWIT() throws Exception {
		Integer numSeats = 7;
		String customerEmail = "a@a.com";
		Integer invalidSeatHoldId = 22;
		RestTemplate restTemplate = new TestRestTemplate();
		ResponseEntity<SeatHoldNotValidException> seatHoldNotValidException = restTemplate
				.postForEntity(
						"http://localhost:8082/ticketService/v1/reserve/seatHoldId/"
								+ invalidSeatHoldId + "/customerEmail/"
								+ customerEmail, null,
						SeatHoldNotValidException.class);
		assertThat(seatHoldNotValidException.getStatusCode(),
				equalTo(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void testReserveSeatsCustomerNotValidExceptionWIT() throws Exception {
		Integer numSeats = 7;
		String customerEmail = "a@a.com";
		String invalidCustomerEmail = "b@b.com";
		RestTemplate restTemplate = new TestRestTemplate();

		ResponseEntity<SeatHold> seatHold = restTemplate.postForEntity(
				"http://localhost:8082/ticketService/v1/findAndHold/numSeats/"
						+ numSeats + "/customerEmail/" + customerEmail, null,
				SeatHold.class);
		assertThat(seatHold.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(seatHold.getBody().getSeats().size(), equalTo(numSeats));
		Integer seatHoldId = seatHold.getBody().getId();

		ResponseEntity<CustomerNotValidException> CustomerNotValidException = restTemplate
				.postForEntity(
						"http://localhost:8082/ticketService/v1/reserve/seatHoldId/"
								+ seatHoldId + "/customerEmail/"
								+ invalidCustomerEmail, null,
						CustomerNotValidException.class);
		assertThat(CustomerNotValidException.getStatusCode(),
				equalTo(HttpStatus.BAD_REQUEST));
	}
}
