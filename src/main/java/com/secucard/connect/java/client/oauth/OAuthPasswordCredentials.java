/**
 * OAuthPasswordCredentials.java class file
 */
package com.secucard.connect.java.client.oauth;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Password credentials used for authorization
 */
public class OAuthPasswordCredentials implements OAuthUserCredentials {

	private final String username;

	private final String password;

	/**
	 * Constructor
	 * @param username
	 * @param password
	 */
	public OAuthPasswordCredentials(String username, String password) {
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

	public String getType() {
		return "password";
	}

	/**
	 * Function that adds parameters to the map
	 */
	public void addParameters(MultivaluedMap<String, String> map) {
		map.add("username", username);
		map.add("password", password);
	}

}