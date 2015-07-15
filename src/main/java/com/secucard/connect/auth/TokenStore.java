package com.secucard.connect.auth;

import com.secucard.connect.auth.model.Token;

/**
 * Interface for retrieving and persisting an authentication token from/to any kind of store.
 * To be implemented by SDK users to control how a token is stored in the system.
 */
public interface TokenStore {

  /**
   * Retrieves an OAuth token from the store.
   *
   * @return The token instance or null when no token is available yet.
   */
  Token get();

  /**
   * Persist a given token.
   *
   * @param token The new token data.
   */
  void set(Token token);
}
