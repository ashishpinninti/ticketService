package com.apinninti.ticketService.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Model POJO representing Customer table in DB.
 * 
 * @author apinninti
 *
 */
@Entity
@Table(name = "CUSTOMER")
public class Customer implements Serializable {

	private static final long serialVersionUID = -940793280525178708L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "EMAIL", nullable = false, unique = true)
	private String email;

	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "customer", fetch = FetchType.LAZY)
	private Set<SeatHold> seatHolds = new HashSet<>();

	public Customer() {
	}

	public Customer(String email) {
		this.email = email;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<SeatHold> getSeatHolds() {
		return seatHolds;
	}

	public void setSeatHolds(Set<SeatHold> seatHolds) {
		this.seatHolds = seatHolds;
	}

}
