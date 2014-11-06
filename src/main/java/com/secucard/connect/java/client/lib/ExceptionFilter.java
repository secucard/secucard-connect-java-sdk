/**
 * ExceptionFilter.java class file
 */
package com.secucard.connect.java.client.lib;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import java.io.IOException;
import java.util.Map;


/**
 * Class that handle runtime exceptions and transforms them to our exceptions
 */
public class ExceptionFilter implements ClientResponseFilter {

  @Override
  public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
/*
	try {

			if (responseContext.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
        responseContext.getEntityStream();

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
*/
	}
}