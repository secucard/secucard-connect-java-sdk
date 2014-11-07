/**
 * OAuthPasswordCredentials.java class file
 */
package com.secucard.connect.auth;

import com.secucard.connect.auth.OAuthUserCredentials;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Password credentials used for authorization
 */
public class OAuthUserCredentials {
	private final String username;
	private final String password;

	/**
	 * Constructor
	 * @param username
	 * @param password
	 */
	public OAuthUserCredentials(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	/**
	 * Getters
	 */
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}