package com.secucard.connect.auth;

import com.secucard.connect.auth.model.ClientCredentials;
import com.secucard.connect.auth.model.OAuthCredentials;

/**
 * Returns the credentials used to obtain OAuth access tokens.
 * To be implemented by the SDK user to have control how credentials are stored in the system.
 */
public interface CredentialsProvider {

  /**
   * Returns the credentials needed to obtain an new access token and refresh token.
   * The returned type depends on the authorisation type used with the client.
   */
  OAuthCredentials getCredentials();

  /**
   * Returns the client credentials needed to obtained an access token with an existing refresh token.
   */
  ClientCredentials getClientCredentials();
}
