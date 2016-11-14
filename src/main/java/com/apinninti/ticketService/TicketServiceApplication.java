package com.apinninti.ticketService;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.apinninti.ticketService.entity.Seat;
import com.apinninti.ticketService.repository.CustomerJpaRepository;
import com.apinninti.ticketService.repository.SeatHoldJpaRepository;
import com.apinninti.ticketService.repository.SeatJpaRepository;

/**
 * The is starting point of this application.
 * 
 * @author apinninti
 *
 */
@SpringBootApplication
public class TicketServiceApplication implements CommandLineRunner {

	@Value("${venue.numberOfSeats}")
	private int NO_OF_SEATS;
	
	@Autowired
	private SeatHoldJpaRepository seatHoldJpaRepository;

	@Autowired
	private SeatJpaRepository seatJpaRepository;
	
	@Autowired
	private CustomerJpaRepository customerJpaRepository;


	public static void main(String[] args) {
		SpringApplication.run(TicketServiceApplication.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		seedData();
	}

	/**
	 * Populate the Venue with seats if not present.
	 */
	public void seedData() {
		Set<Seat> seats = new HashSet<Seat>();
		int seatReservedCount = (int) seatJpaRepository.count();
		if (seatReservedCount != NO_OF_SEATS) {
			seatJpaRepository.deleteAll();
			seatHoldJpaRepository.deleteAll();
			customerJpaRepository.deleteAll();
			for (int i = 0; i < NO_OF_SEATS; i++) {
				Seat seat = new Seat();
				seat.setId(i+1);
				seats.add(seat);
			}
			seatJpaRepository.save(seats);
		}
	}
}
