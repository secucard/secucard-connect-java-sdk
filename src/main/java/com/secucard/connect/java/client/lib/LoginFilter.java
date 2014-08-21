/**
 * LoginFilter.java class file
 */
package com.secucard.connect.java.client.lib;

import org.eclipse.jetty.http.HttpHeaders;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * Filter for supplying the access token to the end point
 */
public class LoginFilter extends ClientFilter {

	private final AuthProvider authProvider;

	/**
	 * Constructor
	 * @param authProvider
	 */
	public LoginFilter(AuthProvider authProvider) {
		super();
		this.authProvider = authProvider;
	}

	@Override
	public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {

		// this line cares for API authorization and auth_token refreshing when needed
		cr.getHeaders().add(HttpHeaders.AUTHORIZATION, "OAuth2 " + authProvider.getToken().getAccessToken());

		return getNext().handle(cr);
	}
}
