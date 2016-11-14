package com.apinninti.ticketService.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.apinninti.ticketService.controller.TicketController;
import com.apinninti.ticketService.entity.Seat;
import com.apinninti.ticketService.entity.SeatHold;
import com.apinninti.ticketService.exception.CustomerNotValidException;
import com.apinninti.ticketService.exception.SeatHoldNotValidException;
import com.apinninti.ticketService.service.TicketService;

/**
 * @author apinninti
 *
 */
public class TicketControllerTest {

	@Mock
	private TicketService ticketService;

	@InjectMocks
	TicketController ticketController = new TicketController();

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();
	}

	public SeatHold getBasicSeatHold1() {
		SeatHold seatHold = new SeatHold();
		seatHold.setId(1);
		seatHold.getSeats().add(new Seat());
		return seatHold;
	}

	@Test
	public void testNumSeatsAvailable() throws Exception {
		int expected = 5;
		int actual = 5;
		when(ticketService.numSeatsAvailable()).thenReturn(expected);

		mockMvc.perform(get("/ticketService/v1/seatsAvailable"))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType(
								MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$", is(actual)));

		verify(ticketService).numSeatsAvailable();
	}

	@Test
	public void testFindAndHoldSeats() throws Exception {
		String customerEmail = "a@a.com";
		int numSeats = 5;
		SeatHold basicSeatHold1 = getBasicSeatHold1();
		when(
				ticketService.findAndHoldSeats(any(Integer.class),
						any(String.class))).thenReturn(basicSeatHold1);

		mockMvc.perform(
				post("/ticketService/v1/findAndHold/numSeats/" + numSeats
						+ "/customerEmail/" + customerEmail))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType(
								MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$['id']", is(basicSeatHold1.getId())));

		verify(ticketService).findAndHoldSeats(numSeats, customerEmail);
	}

	@Test
	public void testReserveSeats() throws Exception {
		String customerEmail = "a@a.com";
		int seatHoldId = 1;
		String confirmationCode = UUID.randomUUID().toString();
		when(ticketService.reserveSeats(seatHoldId, customerEmail)).thenReturn(
				confirmationCode);

		mockMvc.perform(
				post("/ticketService/v1/reserve/seatHoldId/" + seatHoldId
						+ "/customerEmail/" + customerEmail))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", is(confirmationCode)));

		verify(ticketService).reserveSeats(seatHoldId, customerEmail);
	}

	@Test
	public void testReserveSeatsSeatHoldNotValidException() throws Exception {
		String customerEmail = "a@a.com";
		int seatHoldId = 1;
		when(ticketService.reserveSeats(seatHoldId, customerEmail)).thenThrow(
				SeatHoldNotValidException.class);

		mockMvc.perform(
				post("/ticketService/v1/reserve/seatHoldId/" + seatHoldId
						+ "/customerEmail/" + customerEmail)).andExpect(
				status().isBadRequest());

		verify(ticketService).reserveSeats(seatHoldId, customerEmail);
	}

	@Test
	public void testReserveSeatsCustomerNotValidException() throws Exception {
		String customerEmail = "a@a.com";
		int seatHoldId = 1;
		when(ticketService.reserveSeats(seatHoldId, customerEmail)).thenThrow(
				CustomerNotValidException.class);

		mockMvc.perform(
				post("/ticketService/v1/reserve/seatHoldId/" + seatHoldId
						+ "/customerEmail/" + customerEmail)).andExpect(
				status().isBadRequest());

		verify(ticketService).reserveSeats(seatHoldId, customerEmail);
	}

}
