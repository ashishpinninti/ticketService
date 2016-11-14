package com.apinninti.ticketService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.apinninti.ticketService.entity.Seat;
import com.apinninti.ticketService.entity.SeatHold;

public interface SeatJpaRepository extends JpaRepository<Seat, Integer> {

	int countBySeatHoldIsNull();

	@Modifying
	@Query("UPDATE Seat sr SET sr.seatHold = NULL WHERE sr.seatHold IN :expiredSeatHolds")
	int resetExpiredSeats(
			@Param("expiredSeatHolds") List<SeatHold> expiredSeatHolds);

	@Modifying
	@Query("UPDATE Seat sr SET sr.seatHold = NULL")
	int resetAllSeats();
}
