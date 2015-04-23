package com.secucard.connect.auth;


import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.auth.Token;

import java.util.Map;

/**
 * Provides a interface for getting authorization tokens.
 */
public interface AuthProvider {
  public static final String EVENT_CODE_AUTH_OK = "AUTH_OK";
  public static final String EVENT_CODE_AUTH_PENDING = "AUTH_PENDING";

  /**
   * Provides the credentials to authenticate.
   *
   * @param credentials The credentials.
   */
  void setCredentials(OAuthCredentials credentials);

  /**
   * Requesting a authorization token for the credentials.
   * The registered event listener may receive events during the process.
   */
  Token getToken();

  /**
   * Cancel a pending authorization request. Only useful for auth processes which involves token polling step.
   * Provider throws {@link com.secucard.connect.auth.AuthCanceledException} if successfully canceled.
   */
  void cancelAuth();

  /**
   * Perform an authentication of the given credentials.
   */
  void authenticate();

  /**
   * Registering a listener which gets notified about events during the auth process.
   * This is for example the case when a auth. process is done in multiple steps and user input is required.
   */
  void registerEventListener(EventListener eventListener);

  /**
   * Clear the token cache.
   */
  void clearCache();

  /**
   * Returns a map with additional data to pass on authentication.
   */
  Map<String, String> getAdditionalInfo();
}
