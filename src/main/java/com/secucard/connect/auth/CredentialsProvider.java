package com.secucard.connect.auth;

import com.secucard.connect.auth.model.OAuthCredentials;

/**
 * Returns the OAuth credentials to use for authentication.
 * To be implemented by the SDK user to have control how credentials are stored in the system.
 */
public interface CredentialsProvider {
  OAuthCredentials getCredentials();
}
