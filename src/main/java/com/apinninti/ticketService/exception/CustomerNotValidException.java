package com.apinninti.ticketService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * Custom Exception raised when customer is not valid, may be he/she doesn't
 * hold this Seat Hold!
 * 
 * @author apinninti
 *
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Customer is not valid, may be he/she doesn't hold this Seat Hold!")
public class CustomerNotValidException extends RuntimeException {

	private static final long serialVersionUID = -1438173625417956697L;

	public CustomerNotValidException(String message) {
		super(message);
	}

	public CustomerNotValidException(String message, Throwable cause) {
		super(message, cause);
	}

}
