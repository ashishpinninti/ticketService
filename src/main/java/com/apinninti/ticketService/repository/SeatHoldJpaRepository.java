package com.apinninti.ticketService.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apinninti.ticketService.entity.SeatHold;

public interface SeatHoldJpaRepository extends JpaRepository<SeatHold, Integer> {
	List<SeatHold> findByConfirmationCodeIsNullAndHoldTimeBefore(Date date);
}
