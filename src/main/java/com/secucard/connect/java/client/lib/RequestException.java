/**
 * RequestException.java class file
 */
package com.secucard.connect.java.client.lib;

/**
 * RequestException class is thrown when the http request fails
 */
public class RequestException extends RuntimeException {

	private static final long serialVersionUID = 5040888131399521336L;

	/**
	 * Constructor
	 * @param message
	 * @param cause
	 */
	public RequestException(String message, Throwable cause) {
		super(message, cause);
	}
}