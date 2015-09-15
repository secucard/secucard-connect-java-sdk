/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.auth;

import com.secucard.connect.auth.exception.AuthCanceledException;
import com.secucard.connect.auth.exception.AuthDeniedException;
import com.secucard.connect.auth.exception.AuthFailedException;
import com.secucard.connect.auth.exception.AuthTimeoutException;
import com.secucard.connect.auth.model.*;
import com.secucard.connect.event.EventDispatcher;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.util.Log;
import com.secucard.connect.util.ThreadSleep;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Management of OAuth tokens.
 * Provides all necessary methods to work with token, like obtaining or refreshing.
 * Uses instance of {@link com.secucard.connect.auth.AuthService} to actually access the OAuth API.
 */
public class TokenManager {
  private EventListener authEventListener;
  protected ClientAuthDetails clientAuthDetails;
  protected CancelCallback cancelCallback;
  private final Configuration configuration;
  private AuthService authService;

  protected static final Log LOG = new Log(TokenManager.class);

  public TokenManager(Configuration configuration, ClientAuthDetails clientAuthDetails,
                      CancelCallback cancelCallback, AuthService authService) {
    this.configuration = configuration;
    this.clientAuthDetails = clientAuthDetails;
    this.cancelCallback = cancelCallback;
    this.authService = authService;
  }

  /**
   * Registering a listener which gets notified about events during the auth process.
   * This is for example the case when a auth. process is done in multiple steps and user input is required.
   * The lister may receive the events defined by {@link Events}
   */
  public synchronized void registerListener(EventListener eventListener) {
    authEventListener = eventListener;
  }

  /**
   * Returns if a pending authorization request triggered by {@link #getToken(boolean)} should be canceled.
   * These methods will throw {@link com.secucard.connect.auth.exception.AuthCanceledException} if successfully canceled.
   * Only useful for auth processes which involves token polling steps like the OAuth device flow.
   * Note: Since the mentioned methods block until completion it is necessary to call this method from another thread.
   */
  public boolean cancelAuth() {
    return cancelCallback != null && cancelCallback.cancel();
  }

  private Map<String, String> getAdditionalInfo() {
    return configuration.deviceInfo;
  }

  /**
   * Performs any necessary step to return a valid OAuth token. The implementation must decide if a new token must
   * be obtained or if just a existing token should returned or if the validity of an existing token must maintained
   * before returning. Also decides how credentials are provided for that.
   * Since actually performing authorization may require external interactions like prompting for credentials this
   * method must support an parameter to explicitly allow such interactions for a token retrieval.
   * <p/>
   * To get notified about events happening during the token retrieval an event listener can registered, see
   * {@link #registerListener(EventListener)}.
   * <p/>
   * This is a synchronous interface, so it blocks when obtaining token is a long running process.
   *
   * @param allowInteractive If false no action should performed during token retrieval which requires any kind
   *                         of external interaction like triggering user input. If still required an
   *                         {@link com.secucard.connect.auth.exception.AuthFailedException} must be thrown instead.
   *                         True for allowing any kind of action.
   * @return The access token string or null if no token is needed.
   * @throws AuthFailedException   If the retrieval fails for some reason.
   * @throws AuthDeniedException   If the retrieval fails because of wrong credentials
   * @throws AuthCanceledException If the running authentication was canceled by the user.
   * @throws AuthTimeoutException  If the running authentication was timed out.
   */
  public String getToken(boolean allowInteractive) throws AuthFailedException, AuthCanceledException,
      AuthTimeoutException, AuthDeniedException {

    Token token = getCurrent();

    boolean authenticate = false;

    if (token == null) {
      // no token, authenticate first
      authenticate = true;
    } else if (token.isExpired()) {
      // try refresh if just expired, authenticate new if no refresh possible or failed
      LOG.debug("Token expired: ", token.getExpireTime() == null ? "null" : new Date(token.getExpireTime()),
          ", original: ", token.getOrigExpireTime() == null ? "null" : new Date(token.getOrigExpireTime()));
      if (token.getRefreshToken() == null) {
        LOG.debug("No token refresh possible, try obtain new.");
        authenticate = true;
      } else {
        try {
          refresh(token, clientAuthDetails.getClientCredentials());
          setCurrentToken(token);
        } catch (Throwable t) {
          LOG.debug("Token refresh failed, try obtain new.", t);
          authenticate = true;
        }
      }
    } else {
      // we should have valid token in cache, no new auth necessary
      if (configuration.extendExpire) {
        LOG.debug("Extend token expire time.");
        token.setExpireTime();
        setCurrentToken(token);
      }
      LOG.debug("Return current token: ", token);
    }

    if (authenticate) {
      OAuthCredentials credentials = clientAuthDetails.getCredentials();

      if (credentials instanceof AnonymousCredentials) {
        return null;
      }

      // new authentication is needed but only if allowed
      if ((credentials instanceof AppUserCredentials || credentials instanceof DeviceCredentials)
          && !allowInteractive) {
        throw new AuthFailedException("Invalid acess token, please authenticate again.");
      }

      token = authenticate(credentials);
      token.setExpireTime();
      token.setId(credentials.getId());
      setCurrentToken(token);
      LOG.debug("Return new token: ", token);
    }

    return token.getAccessToken();
  }

  private synchronized void setCurrentToken(Token token) {
    if (clientAuthDetails != null) {
      clientAuthDetails.onTokenChanged(token);
    }
  }

  private synchronized Token getCurrent() {
    if (clientAuthDetails != null) {
      return clientAuthDetails.getCurrent();
    }
    return null;
  }

  private Token authenticate(OAuthCredentials credentials) throws AuthFailedException, AuthCanceledException,
      AuthTimeoutException, AuthDeniedException {
    if (credentials == null) {
      throw new AuthFailedException("Missing credentials");
    }

    LOG.debug("Authenticate credentials: ", credentials.asMap());

    int pollInterval = 0;
    long timeout = System.currentTimeMillis();
    boolean deviceAuth = credentials instanceof DeviceCredentials;

    if (deviceAuth) {
      DeviceAuthCode codes = requestCodes((DeviceCredentials) credentials);

      EventDispatcher.fireEvent(codes, authEventListener, true);
      LOG.debug("Retrieved codes for device auth: ", codes, ", now polling for auth.");

      // set poll timeout, either by config or by expire time of code
      int t = codes.getExpiresIn();
      if (t <= 0 || configuration.authWaitTimeoutSec < t) {
        t = configuration.authWaitTimeoutSec;
      }
      timeout = System.currentTimeMillis() + t * 1000;

      pollInterval = codes.getInterval();
      if (pollInterval <= 0) {
        pollInterval = 5; // poll default 5s
      }

      DeviceCredentials dc = (DeviceCredentials) credentials;
      dc.setDeviceCode(codes.getDeviceCode());
      dc.setDeviceId(null); // device id must be empty for next auth. step!
    }

    do {
      // this is not supposed to be repeated when not device auth

      if (deviceAuth) {
        final boolean[] canceled = {false};
        new ThreadSleep() {
          @Override
          protected boolean cancel() {
            if (cancelAuth()) {
              canceled[0] = true;
              return true;
            }
            return false;
          }
        }.execute(pollInterval, 1, TimeUnit.SECONDS);
        if (canceled[0]) {
          throw new AuthCanceledException();
        }
      }

      Token token = null;
      try {
        token = authService.getToken(credentials);
      } catch (AuthDeniedException e) {
        if (deviceAuth) {
          // expected for device auth, map to event
          EventDispatcher.fireEvent(Events.EVENT_AUTH_PENDING, authEventListener, true);
        } else {
          throw e;
        }
      }

      if (token != null) {
        if (deviceAuth) {
          EventDispatcher.fireEvent(Events.EVENT_AUTH_OK, authEventListener, true);
        }
        return token;
      }

    } while (System.currentTimeMillis() < timeout);

    if (deviceAuth) {
      throw new AuthTimeoutException();
    }

    throw new IllegalStateException("Unexpected failure of authentication.");
  }

  protected DeviceAuthCode requestCodes(DeviceCredentials credentials)
      throws AuthFailedException, AuthDeniedException {
    DeviceAuthCode codes = authService.getCodes(credentials);

    if (StringUtils.isAnyBlank(codes.getDeviceCode(), codes.getUserCode(), codes.getVerificationUrl())) {
      throw new AuthFailedException("Authentication failed, got no valid codes or URL.");
    }
    return codes;
  }

  /**
   * Refresh token, but copy ony access token and time of the refreshed token.
   *
   * @param token The token to refresh.
   */
  protected void refresh(Token token, ClientCredentials credentials) throws AuthFailedException, AuthCanceledException {
    if (credentials == null) {
      throw new AuthFailedException("Missing credentials");
    }

    LOG.debug("Refresh token: ", credentials);
    Token refreshToken = authService.refresh(new RefreshCredentials(credentials, token.getRefreshToken()));
    token.setAccessToken(refreshToken.getAccessToken());
    token.setExpiresIn(refreshToken.getExpiresIn());
    if (StringUtils.isNotBlank(refreshToken.getRefreshToken())) {
      token.setRefreshToken(refreshToken.getRefreshToken());
    }
    token.setExpireTime();
  }

  /**
   * OAuth configuration. Supported properties are:
   * <p/>
   * - auth.url, URL of the OAuth service to use.<br/>
   * - auth.waitTimeoutSec, Timeout in seconds to use when waiting for auth tokens when performing "device" auth type.
   * <p/>
   * Additionally a map with any device related data to submit during auth may provided.
   */
  public static class Configuration {
    public final int authWaitTimeoutSec;
    public final String oauthUrl;
    public final boolean extendExpire;
    public final Map<String, String> deviceInfo;

    public Configuration(Properties properties, Map<String, String> deviceInfo) {
      this.authWaitTimeoutSec = Integer.parseInt(properties.getProperty("auth.waitTimeoutSec"));
      this.oauthUrl = properties.getProperty("auth.url");
      this.deviceInfo = deviceInfo;
      this.extendExpire = Boolean.parseBoolean(properties.getProperty("auth.extendExpire"));
    }


    @Override
    public String toString() {
      return "OAuth Configuration{" +
          "authWaitTimeoutSec=" + authWaitTimeoutSec +
          ", oauthUrl='" + oauthUrl + '\'' +
          ", extendExpire=" + extendExpire +
          ", deviceInfo=" + deviceInfo +
          '}';
    }
  }
}
