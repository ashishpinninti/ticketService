package com.apinninti.ticketService.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Model POJO representing Seat table in DB.
 * 
 * @author apinninti
 *
 */
@Entity
@Table(name = "SEAT")
public class Seat implements Serializable {

	private static final long serialVersionUID = -7608423221857680734L;

	@Id
	@Column(name = "ID")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "SEAT_HOLD_ID")
	@JsonIgnore
	private SeatHold seatHold;

	public Seat() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public SeatHold getSeatHold() {
		return seatHold;
	}

	public void setSeatHold(SeatHold seatHold) {
		this.seatHold = seatHold;
	}
}
