package com.jmacd.commons.exceptions;

@SuppressWarnings("serial")
public class NoResultsException extends RuntimeException {

	/**
	 * @param message
	 */
	public NoResultsException(String message) {
		super(message);
	}

}
