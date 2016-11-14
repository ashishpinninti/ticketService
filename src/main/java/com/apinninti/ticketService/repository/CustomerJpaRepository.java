package com.apinninti.ticketService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apinninti.ticketService.entity.Customer;

public interface CustomerJpaRepository extends JpaRepository<Customer, Integer> {
	Customer findByEmail(String email);
}
