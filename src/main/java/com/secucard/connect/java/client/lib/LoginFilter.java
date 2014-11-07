/**
 * LoginFilter.java class file
 */
package com.secucard.connect.java.client.lib;

import com.secucard.connect.java.client.oauth.AuthProviderImpl;
import org.eclipse.jetty.http.HttpHeaders;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

/**
 * Filter for supplying the access token to the end point
 */
public class LoginFilter implements ClientRequestFilter {

	private final AuthProviderImpl authProvider;

	/**
	 * Constructor
	 * @param authProvider
	 */
	public LoginFilter(AuthProviderImpl authProvider) {
		super();
		this.authProvider = authProvider;
	}

  @Override
  public void filter(ClientRequestContext cr) throws IOException {
		// this line cares for API authorization and auth_token refreshing when needed
		cr.getHeaders().add(HttpHeaders.AUTHORIZATION, "OAuth2 " + authProvider.getToken().getAccessToken());
	}
}
