/**
 * OAuthApi class file
 */
package com.secucard.connect.java.client.oauth;

import com.secucard.connect.java.client.lib.BaseApi;
import com.secucard.connect.java.client.lib.ResourceFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Class used for call authorization requests
 */
public class OAuthApi extends BaseApi {

  /**
   * Constructor
   *
   * @param resourceFactory
   */
  public OAuthApi(ResourceFactory resourceFactory) {
    super(resourceFactory);
  }

  /**
   * Function to get authorization token
   *
   * @param clientCredentials
   * @param userCredentials
   * @return
   */
  public OAuthToken getToken(OAuthClientCredentials clientCredentials, OAuthUserCredentials userCredentials) {
    MultivaluedMap<String, String> parameters = new MultivaluedHashMap<>();
    parameters.add("grant_type", userCredentials.getType());
    parameters.add("client_id", clientCredentials.getClientId());
    parameters.add("client_secret", clientCredentials.getClientSecret());
    userCredentials.addParameters(parameters);

    WebTarget resource = getResourceFactory().getApiResource("/oauth/token", false);
    return resource.request(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(Entity.form(parameters))
        .readEntity(OAuthToken.class);
  }
}