package com.apinninti.ticketService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.apinninti.ticketService.entity.SeatHold;
import com.apinninti.ticketService.service.TicketService;

/**
 * @author apinninti
 *
 */
@RestController
@RequestMapping(value = "ticketService/v1")
public class TicketController {

	@Autowired
	private TicketService ticketService;

	/**
	 * 
	 * The number of seats in the venue that are neither held nor reserved
	 * 
	 * @return the number of tickets available in the venue
	 */
	@RequestMapping(value = "/seatsAvailable", method = RequestMethod.GET)
	public int numSeatsAvailable() {
		return ticketService.numSeatsAvailable();
	}

	/**
	 * 
	 * Find and hold the best available seats for a customer
	 * 
	 * @param numSeats
	 *            the number of seats to find and hold
	 * 
	 * @param customerEmail
	 *            unique identifier for the customer
	 * 
	 * @return a SeatHold object identifying the specific seats and related
	 * 
	 *         information
	 */
	@RequestMapping(value = "/findAndHold/numSeats/{numSeats}/customerEmail/{customerEmail:.+}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public SeatHold findAndHoldSeats(@PathVariable Integer numSeats,
			@PathVariable String customerEmail) {
		return ticketService.findAndHoldSeats(numSeats, customerEmail);
	}

	/**
	 * 
	 * Commit seats held for a specific customer
	 * 
	 *
	 * 
	 * @param seatHoldId
	 *            the seat hold identifier
	 * 
	 * @param customerEmail
	 *            the email address of the customer to which the
	 * 
	 *            seat hold is assigned
	 * 
	 * @return a reservation confirmation code
	 */
	@RequestMapping(value = "/reserve/seatHoldId/{seatHoldId}/customerEmail/{customerEmail:.+}", method = RequestMethod.POST)
	public String reserveSeats(@PathVariable Integer seatHoldId,
			@PathVariable String customerEmail) {
		return ticketService.reserveSeats(seatHoldId, customerEmail);
	}

	/**
	 * This a convenience method to reset the seats in Venue.
	 */
	@RequestMapping(value = "/reset", method = RequestMethod.DELETE)
	public void reset() {
		ticketService.reset();
	}
}
