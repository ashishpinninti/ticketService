package com.apinninti.ticketService.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apinninti.ticketService.entity.Customer;
import com.apinninti.ticketService.entity.SeatHold;
import com.apinninti.ticketService.exception.CustomerNotValidException;
import com.apinninti.ticketService.exception.SeatHoldNotValidException;
import com.apinninti.ticketService.repository.CustomerJpaRepository;
import com.apinninti.ticketService.repository.SeatHoldJpaRepository;
import com.apinninti.ticketService.repository.SeatJpaRepository;
import com.apinninti.ticketService.repository.SeatRepository;

/**
 * @author apinninti
 *
 */
@Service
@Transactional
public class TicketServiceImpl implements TicketService {

	private static Logger log = Logger.getLogger(TicketServiceImpl.class);

	@Value("${holdTime.expireInSeconds}")
	private int NO_OF_EXPIRE_SECONDS;

	@Autowired
	private SeatHoldJpaRepository seatHoldJpaRepository;

	@Autowired
	private SeatJpaRepository seatJpaRepository;

	@Autowired
	private SeatRepository seatRepository;

	@Autowired
	private CustomerJpaRepository customerJpaRepository;

	/**
	 * 
	 * The number of seats in the venue that are neither held nor reserved
	 * 
	 * @return the number of tickets available in the venue
	 */
	@Override
	public int numSeatsAvailable() {
		refreshExpiredSeatHolds();
		int seatsNotReserved = (int) seatJpaRepository.countBySeatHoldIsNull();
		return seatsNotReserved;
	}

	/**
	 * 
	 * Reset the expired seats and delete any expired seat holds.
	 * 
	 */
	private void refreshExpiredSeatHolds() {
		log.info("Refreshing the expired seats in Venue");
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int expire_seconds = NO_OF_EXPIRE_SECONDS;
		calendar.add(Calendar.SECOND, -expire_seconds);
		List<SeatHold> expiredSeatHolds = seatHoldJpaRepository
				.findByConfirmationCodeIsNullAndHoldTimeBefore(calendar
						.getTime());
		if (!expiredSeatHolds.isEmpty()) {
			seatJpaRepository.resetExpiredSeats(expiredSeatHolds);
			seatHoldJpaRepository.delete(expiredSeatHolds);
		}
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
	@Override
	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
		Customer customer = customerJpaRepository.findByEmail(customerEmail);
		if (customer == null) {
			log.info("Creating new customer");
			customer = customerJpaRepository.save(new Customer(customerEmail));
		}
		SeatHold seatHold = new SeatHold();
		seatHold.setCustomer(customer);
		if (numSeats > 0) {
			int numSeatsAvailable = numSeatsAvailable();
			if (numSeatsAvailable > 0) {
				seatHold.setHoldTime(new Date());
				seatHold = seatHoldJpaRepository.save(seatHold);
				if (numSeatsAvailable >= numSeats) {
					log.info("Sufficient seats are available to find and hold");
					seatRepository.holdSeats(seatHold, numSeats);
				} else {
					log.info("Sufficient seats are not available to find and hold, holding best available seats");
					seatRepository.holdSeats(seatHold, numSeatsAvailable);
				}
				seatHold = seatHoldJpaRepository.findOne(seatHold.getId());
			}
		} else {
			log.info("Invalid numSeats to find and hold");
		}
		return seatHold;
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
	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {
		String result = new String();
		refreshExpiredSeatHolds();
		SeatHold seatHold = seatHoldJpaRepository.findOne(seatHoldId);
		if (seatHold == null) {
			log.error("No Seat Hold with id: " + seatHoldId
					+ " found, may be it is expired");
			throw new SeatHoldNotValidException("No Seat Hold with id: "
					+ seatHoldId + " found, may be it is expired");
		}
		Customer customer = seatHold.getCustomer();
		if (customer == null
				|| !StringUtils.equalsIgnoreCase(customerEmail,
						customer.getEmail())) {
			log.error("Customer doesn't exist or doesn't hold this Seat Hold");
			throw new CustomerNotValidException(
					"Customer doesn't exist or doesn't hold this Seat Hold");
		}

		if (StringUtils.isNotEmpty(seatHold.getConfirmationCode())) {
			log.info("The Seat Hold is already reserved with confirmation code: "
					+ seatHold.getConfirmationCode());
			result = "The Seat Hold is already reserved with confirmation code: "
					+ seatHold.getConfirmationCode();
			return result;
		}
		result = UUID.randomUUID().toString();
		seatHold.setReservationTime(new Date());
		seatHold.setConfirmationCode(result);
		seatHoldJpaRepository.save(seatHold);
		log.info("Successfully reserved seats with confirmation code: "
				+ result);
		return result;
	}

	@Override
	public void reset() {
		log.info("Request to reset the seats in Venue");
		seatJpaRepository.resetAllSeats();
		seatHoldJpaRepository.deleteAll();
		customerJpaRepository.deleteAll();
	}
}
