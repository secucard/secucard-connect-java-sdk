/**
 * ExceptionFilter.java class file
 */
package com.secucard.connect.java.client.lib;

import java.util.Map;

import javax.ws.rs.core.Response.Status.Family;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * Class that handle runtime exceptions and transforms them to our exceptions
 */
public class ExceptionFilter extends ClientFilter {

	@SuppressWarnings("unchecked")
	@Override
	public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
		try {
			ClientResponse response = getNext().handle(cr);
			if (response.getClientResponseStatus() == null || response.getClientResponseStatus().getFamily() != Family.SUCCESSFUL) {
				Map<String, Object> errorData = response .getEntity(new GenericType<Map<String, Object>>() {});

				throw new ResponseException(
						response.getClientResponseStatus(),
						(String) errorData.get("error"),
						(String) errorData.get("error_description"),
						(Map<String, Object>) errorData.get("parameters"));
			} else {
				return response;
			}
		} catch (ClientHandlerException e) {
			throw new RequestException(e.getMessage(), e.getCause());
		}
	}
}