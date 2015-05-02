package com.secucard.connect.auth;


import com.secucard.connect.event.EventListener;

import java.util.Map;

/**
 * Provides a interface for getting authorization tokens.
 * Instances implementing this interface are meant to be stateful and should
 * maintain just a single access token at a time.
 */
public interface AuthProvider {
  public static final String EVENT_AUTH_OK = "AUTH_OK";
  public static final String EVENT_AUTH_PENDING = "AUTH_PENDING";

  /**
   * Sets the current credentials for which a token should be obtained.
   * Each time the given credentials changes this AuthProvider will take care if already authenticated or not.
   *
   * @param credentials The credentials.
   */
  void setCredentials(OAuthCredentials credentials);

  /**
   * Returns the current authorization token for the credentials given by {@link #setCredentials(OAuthCredentials)}.
   * <p/>
   * If NO current token exist the token is first freshly obtained by performing an authentication request to
   * an OAuth server. If a current token exist this method takes also care of all aspects of the token validity,
   * for example token expiring, and will perform all necessary operations to keep the token valid before returning.
   * So any client is supposed to use only this method when a token is needed, and must NOT store the token on its
   * own or alike.
   * <p/>
   * The authentication of some credentials types may require user activities, the registered event listener may
   * receive events during the process to trigger this user activities or show status.
   * This method will also block if such a long running, multi step auth process is triggered.
   *
   * @param forceAuth If true an authentication will be automatically performed strictly every time if needed,
   *                  for instance when the credentials changed or no current token exist yet.
   *                  If false an authentication is also performed when needed but only if the current credential type
   *                  requires no user interaction or don't blocks very long - an AuthException is thrown instead.
   *                  This behaviour can be used to ensure authentication is performed only by special code which can
   *                  handle blocking and user activities.
   * @return The access token string or null if no token is needed for the credentials.
   * @throws AuthException         If the authentication or token maintenance fails for some reason. The status
   *                               field may contain details about failure.
   * @throws AuthCanceledException If the authentication was canceled by the user.
   */
  String getToken(boolean forceAuth);

  /**
   * Cancel a pending authorization request triggered by {@link #getToken(boolean)}.
   * These methods will throw {@link com.secucard.connect.auth.AuthCanceledException} if successfully canceled.
   * Only useful for auth processes which involves token polling steps like the OAuth device flow.
   * Note: Since the mentioned methods block until completion it is necessary to call this method from another thread.
   */
  void cancelAuth();

  /**
   * Registering a listener which gets notified about events during the auth process.
   * This is for example the case when a auth. process is done in multiple steps and user input is required.
   */
  void registerEventListener(EventListener eventListener);


  /**
   * Clear the current obtained access token.
   */
  void clearToken();

  /**
   * Returns a map with additional data to pass on authentication.
   */
  Map<String, String> getAdditionalInfo();
}
