package com.secucard.connect.auth;


import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.auth.Token;

/**
 * Provides a interface for getting authorization tokens.
 */
public interface AuthProvider {
  public static final String EVENT_CODE_AUTH_OK = "AUTH_OK";
  public static final String EVENT_CODE_AUTH_PENDING = "AUTH_PENDING";

  /**
   * Requesting a authorization token.
   * The registered event listener may receive events during the process.
   */
  Token getToken();

  Token getToken(boolean extend);

  /**
   * Cancel a pending authorization request. Only useful for auth processes which involves token polling step.
   * Provider throws {@link com.secucard.connect.auth.AuthCanceledException} if successfully canceled.
   */
  void cancelAuth();

  /**
   * Registering a listener getting events occurring during the auth process. This is the case when a auth.
   * process is done in multiple steps and user input is required.
   */
  void registerEventListener(EventListener eventListener);
}
