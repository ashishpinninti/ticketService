package com.apinninti.ticketService.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.apinninti.ticketService.entity.Seat;
import com.apinninti.ticketService.entity.SeatHold;

@Repository
public class SeatRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public void holdSeats(SeatHold seatHold, int noOfSeatsToHold) {
		List<Seat> seatsToHold = entityManager
				.createQuery(
						"SELECT s FROM Seat s WHERE s.seatHold IS NULL ORDER BY id",
						Seat.class).setMaxResults(noOfSeatsToHold)
				.getResultList();

		entityManager
				.createQuery(
						"UPDATE Seat s SET s.seatHold = :seatHold WHERE s IN :seatsToHold")
				.setParameter("seatHold", seatHold)
				.setParameter("seatsToHold", seatsToHold).executeUpdate();
		entityManager.detach(seatHold);
	}
}
