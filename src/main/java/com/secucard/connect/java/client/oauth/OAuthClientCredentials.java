/**
 * OAuthClientCredentials.java class file
 */
package com.secucard.connect.java.client.oauth;

/**
 * Class that handles client credentials for authorization
 */
public final class OAuthClientCredentials {

	private final String clientId;

	private final String clientSecret;

	/**
	 * Constructor
	 * @param clientId
	 * @param clientSecret
	 */
	public OAuthClientCredentials(String clientId, String clientSecret) {
		super();
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	@Override
	public String toString() {
		return "OAuthClientCredentials [clientId=" + clientId
				+ ", clientSecret=" + clientSecret + "]";
	}

	/**
	 * Getters
	 */
	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}
}