package com.jmacd.commons;

@SuppressWarnings("serial")
public class JMacDCommonsRuntimeException extends RuntimeException {

	private final int errorNumber;

	public JMacDCommonsRuntimeException(int errorNumber) {
		super();

		this.errorNumber = errorNumber;
	}

	public JMacDCommonsRuntimeException(int errorNumber, String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

		this.errorNumber = errorNumber;
	}

	public JMacDCommonsRuntimeException(int errorNumber, String message, Throwable cause) {
		super(message, cause);

		this.errorNumber = errorNumber;
	}

	public JMacDCommonsRuntimeException(int errorNumber, String message) {
		super(message);

		this.errorNumber = errorNumber;
	}

	public JMacDCommonsRuntimeException(int errorNumber, Throwable cause) {
		super(cause);

		this.errorNumber = errorNumber;
	}

	@Override
	public String toString() {
		String message = getLocalizedMessage();

		if (message != null) {
			message = "jmacd-commons: " + errorNumber + ": " + message; //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			message = "jmacd-commons: " + errorNumber; //$NON-NLS-1$
		}

		return message;
	}

}
