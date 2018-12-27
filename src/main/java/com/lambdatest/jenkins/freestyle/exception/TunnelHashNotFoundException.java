package com.lambdatest.jenkins.freestyle.exception;

public class TunnelHashNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TunnelHashNotFoundException(String message) {
		super(message);
	}

	public TunnelHashNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
