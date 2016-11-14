package com.apinninti.ticketService.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Model POJO representing SeatHold table in DB.
 * 
 * @author apinninti
 *
 */
@Entity
@Table(name = "SEAT_HOLD")
public class SeatHold implements Serializable {

	private static final long serialVersionUID = -5987506603682578599L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "HOLD_TIME")
	private Date holdTime;

	@ManyToOne
	@JoinColumn(name = "CUSTOMER_ID", nullable = false)
	@JsonIgnore
	private Customer customer;

	@Column(name = "CONFIRMATION_CODE")
	private String confirmationCode;

	@Column(name = "RESERVATION_TIME")
	private Date reservationTime;

	@OneToMany(cascade = { CascadeType.REFRESH, CascadeType.MERGE }, mappedBy = "seatHold", fetch = FetchType.EAGER)
	private Set<Seat> seats = new HashSet<>();

	public SeatHold() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getHoldTime() {
		return holdTime;
	}

	public void setHoldTime(Date holdTime) {
		this.holdTime = holdTime;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getConfirmationCode() {
		return confirmationCode;
	}

	public void setConfirmationCode(String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}

	public Date getReservationTime() {
		return reservationTime;
	}

	public void setReservationTime(Date reservationTime) {
		this.reservationTime = reservationTime;
	}

	public Set<Seat> getSeats() {
		return seats;
	}

	public void setSeats(Set<Seat> seats) {
		this.seats = seats;
	}

}
