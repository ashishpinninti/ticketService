package com.apinninti.ticketService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * Custom Exception raised when Seat Hold is not valid, may be it is expired!
 * 
 * @author apinninti
 *
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Seat Hold is not valid, may be it is expired!")
public class SeatHoldNotValidException extends RuntimeException {

	private static final long serialVersionUID = -1438173625417956697L;

	public SeatHoldNotValidException(String message) {
		super(message);
	}

	public SeatHoldNotValidException(String message, Throwable cause) {
		super(message, cause);
	}

}
