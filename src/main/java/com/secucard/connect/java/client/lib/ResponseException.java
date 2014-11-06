/**
 * ResponseException.java class file
 */
package com.secucard.connect.java.client.lib;

import javax.ws.rs.core.Response;
import java.util.Map;


/**
 * Response Exception is thrown when response error happens
 */
public class ResponseException extends RuntimeException {

	private static final long serialVersionUID = -2782593287936100771L;

	private final Response.Status status;

	private final String error;

	private final String description;

	private final Map<String, Object> parameters;

	/**
	 * Constructor
	 * @param status
	 * @param error
	 * @param description
	 * @param parameters
	 */
	public ResponseException(Response.Status status, String error,
			String description, Map<String, Object> parameters) {
		super();
		this.status = status;
		this.error = error;
		this.description = description;
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		return "ResponseException [status=" + status + ", error=" + error + ", description=" + description + ", parameters=" + parameters + "]";
	}

	/**
	 * Getters
	 */
	public Response.Status getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getDescription() {
		return description;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}
}