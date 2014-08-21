/**
 * OAuthApi class file
 */
package com.secucard.connect.java.client.oauth;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.secucard.connect.java.client.lib.BaseApi;
import com.secucard.connect.java.client.lib.ResourceFactory;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Class used for call authorization requests
 */
public class OAuthApi extends BaseApi {

	/**
	 * Constructor
	 * @param resourceFactory
	 */
	public OAuthApi(ResourceFactory resourceFactory) {
		super(resourceFactory);
	}

	/**
	 * Function to get authorization token
	 * @param clientCredentials
	 * @param userCredentials
	 * @return
	 */
	public OAuthToken getToken(OAuthClientCredentials clientCredentials, OAuthUserCredentials userCredentials) {
		MultivaluedMap<String, String> parameters = new MultivaluedMapImpl();
		parameters.add("grant_type", userCredentials.getType());
		parameters.add("client_id", clientCredentials.getClientId());
		parameters.add("client_secret", clientCredentials.getClientSecret());
		userCredentials.addParameters(parameters);

		WebResource resource = getResourceFactory().getApiResource("/oauth/token", false);

		return resource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(OAuthToken.class, parameters);
	}
}