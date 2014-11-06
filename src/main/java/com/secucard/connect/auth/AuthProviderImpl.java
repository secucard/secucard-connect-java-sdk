/**
 * AuthProvider.java class file
 */
package com.secucard.connect.auth;

import com.secucard.connect.java.client.lib.ResourceFactory;
import com.secucard.connect.java.client.oauth.OAuthApi;
import com.secucard.connect.java.client.oauth.OAuthClientCredentials;
import com.secucard.connect.java.client.oauth.OAuthRefreshTokenCredentials;
import com.secucard.connect.java.client.oauth.OAuthToken;
import com.secucard.connect.java.client.oauth.OAuthUserCredentials;

/**
 * Manages tokens based on client credentials and user credentials
 */
public class AuthProviderImpl implements AuthProvider {

	private final OAuthClientCredentials clientCredentials;
	private final OAuthUserCredentials userCredentials;
	private final OAuthApi oauthApi;

	private OAuthToken token;

	private long expireTime;

	/**
	 * Constructor
	 * @param baseApi
	 * @param clientCredentials
	 * @param userCredentials
	 */
	public AuthProviderImpl(ResourceFactory baseApi, OAuthClientCredentials clientCredentials, OAuthUserCredentials userCredentials) {
		this.clientCredentials = clientCredentials;
		this.userCredentials = userCredentials;
		this.oauthApi = new OAuthApi(baseApi);
	}

	/**
	 * Function to update AuthToken
	 * @param credentials
	 */
	private void updateToken(OAuthUserCredentials credentials) {
		System.out.println("Updating auth token:");
		System.out.println(clientCredentials.toString());
		System.out.println(credentials.toString());
		this.token = oauthApi.getToken(clientCredentials, credentials);
		System.out.println("auth token:");
		System.out.println(this.token.toString());
		this.expireTime = System.currentTimeMillis() + token.getExpiresIn() * 1000;
	}

	/**
	 * Function to create new Token
	 */
	private void newToken() {
		updateToken(userCredentials);
	}

	/**
	 * Function to refresh a token
	 */
	private void refreshToken() {
		updateToken(new OAuthRefreshTokenCredentials(this.token.getRefreshToken()));
	}

	/**
	 * Token getter
	 * @return
	 */
	@Override
  public synchronized OAuthToken getToken() {
		if (token == null) {
			newToken();
		} else if (expireTime < System.currentTimeMillis() - 30 * 1000) {
			refreshToken();
		}

		return token;
	}
}