/**
 * LoginFilter.java class file
 */
package com.secucard.connect.channel.rest;

import com.secucard.connect.auth.AuthProvider;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Filter for supplying the access token to the end point
 */
@Provider
public class LoginFilter implements ClientRequestFilter {
	private final AuthProvider authProvider;

	public LoginFilter(AuthProvider authProvider) {
		this.authProvider = authProvider;
	}

  @Override
  public void filter(ClientRequestContext cr) throws IOException {
		cr.getHeaders().add(HttpHeaders.AUTHORIZATION, "OAuth2 " + authProvider.getToken().getAccessToken());
	}
}
