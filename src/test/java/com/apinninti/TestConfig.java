package com.apinninti;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.apinninti.ticketService.entity.Seat;
import com.apinninti.ticketService.repository.SeatHoldJpaRepository;
import com.apinninti.ticketService.repository.SeatJpaRepository;

/**
 * This is the Test Configuration class used in Integration Testing.
 * 
 * @author apinninti
 *
 */
@SpringBootApplication
public class TestConfig implements CommandLineRunner {

	@Autowired
	private SeatHoldJpaRepository seatHoldJpaRepository;

	@Autowired
	private SeatJpaRepository seatJpaRepository;

	public static void main(String[] args) {
		SpringApplication.run(TestConfig.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		seedData();
	}

	public void seedData() {
		Set<Seat> seats = new HashSet<Seat>();
		int seatReservedCount = (int) seatJpaRepository.count();
		if (seatReservedCount < 50) {
			for (int i = 0; i < 50; i++) {
				Seat seat = new Seat();
				seat.setId(i+1);
				seats.add(seat);
			}
			seatJpaRepository.save(seats);
		}
	}
}
