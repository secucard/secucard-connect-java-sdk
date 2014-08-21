/**
 * OAuthUserCredentials.java interface file
 */
package com.secucard.connect.java.client.oauth;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Interface for UserCredentials for authorization
 */
public interface OAuthUserCredentials {

	/**
	 * Function to return the type of OAuthUserCredentials
	 * @return
	 */
	String getType();

	/**
	 * Function to ass parameters for Authorization
	 * @param map
	 */
	void addParameters(MultivaluedMap<String, String> map);
}