/**
 * OAuthRefreshTokenCredentials.java class file
 */
package com.secucard.connect.java.client.oauth;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Class that is used to refresh authorization token
 */
public class OAuthRefreshTokenCredentials implements OAuthUserCredentials {

	private final String refreshToken;

	/**
	 * Constructor
	 * @param refreshToken
	 */
	public OAuthRefreshTokenCredentials(String refreshToken) {
		super();
		this.refreshToken = refreshToken;
	}

	public String getType() {
		return "refresh_token";
	}

	public void addParameters(MultivaluedMap<String, String> map) {
		map.add("refresh_token", refreshToken);
	}
}