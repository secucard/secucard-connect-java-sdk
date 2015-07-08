package com.secucard.connect.auth;

import com.secucard.connect.ServerErrorException;
import com.secucard.connect.channel.rest.RestChannelBase;
import com.secucard.connect.channel.rest.UserAgentProvider;
import com.secucard.connect.event.EventDispatcher;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.auth.DeviceAuthCode;
import com.secucard.connect.model.auth.Token;
import com.secucard.connect.storage.DataStorage;
import com.secucard.connect.util.Log;
import com.secucard.connect.util.ThreadSleep;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.HttpHeaders;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the AuthProvider interface which gets an OAuth token via REST channel.<br/>
 * Note: Maintains just one single token for one credentials at a time!
 */
public class OAuthProvider implements AuthProvider {
  public volatile boolean cancelAuth;
  private OAuthCredentials credentials;
  private EventListener authEventListener;
  private RestChannelBase http;
  protected DataStorage storage;
  private UserAgentProvider userAgentProvider = new UserAgentProvider();
  private final String instanceId;
  private final Configuration configuration;
  private Token currentToken;  // the current obtained token, may be persisted

  protected static final Log LOG = new Log(OAuthProvider.class);

  public OAuthProvider(String id, Configuration configuration) {
    this.configuration = configuration;
    this.instanceId = id;
  }

  public synchronized void setCredentials(OAuthCredentials credentials) {
    this.credentials = credentials;
  }

  public synchronized void setDataStorage(DataStorage dataStorage) {
    this.storage = dataStorage;
  }

  public synchronized void setRestChannel(RestChannelBase restChannel) {
    this.http = restChannel;
  }

  @Override
  public synchronized void registerListener(EventListener eventListener) {
    authEventListener = eventListener;
  }

  public void cancelAuth() {
    this.cancelAuth = true;
  }

  @Override
  public Map<String, String> getAdditionalInfo() {
    return configuration.deviceInfo;
  }

  public synchronized String getToken(boolean forceAuth) throws AuthException {
    if (credentials == null) {
      throw new AuthException("Missing credentials");
    }

    if (credentials instanceof AnonymousCredentials) {
      return null;
    }

    Token token = getCurrent();

    // check if token matches the current credentials
    if (token != null && !token.getId().equals(credentials.getId())) {
      LOG.debug("Credentials changed, current token invalid, must obtain new.");
      token = null;
    }

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
          token = refresh(token, credentials);
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
      // credential auth is needed but only if allowed
      if (!forceAuth && credentials instanceof DeviceCredentials) {
        throw new AuthException("Invalid or no auth token, please authenticate again.");
      }
      token = authenticate(credentials);
      token.setExpireTime();
      token.setId(credentials.getId());
      setCurrentToken(token);
      LOG.debug("Return new token: ", token);
    }

    return token.getAccessToken();
  }

  public synchronized void clearToken() {
    currentToken = null;
    if (configuration.cacheToken) {
      storage.clear(getTokenChacheId(), null);
    }
  }

  /**
   * Set the current token, also persist to cache.
   */
  private void setCurrentToken(Token token) {
    currentToken = token;
    if (configuration.cacheToken) {
      try {
        storeToken(getTokenChacheId(), currentToken);
      } catch (Exception e) {
        throw new IllegalArgumentException("Can't store token.", e);
      }
    }
  }

  /**
   * Return the current token, try load from cache before.
   */
  private Token getCurrent() {
    if (currentToken == null) {
      // check if token exist in cache  and load
      if (configuration.cacheToken) {
        try {
          currentToken = getStoredToken(getTokenChacheId());
        } catch (Exception e) {
          LOG.error("Error reading token.", e);
        }
      }
    }

    return currentToken;
  }

  protected void storeToken(String id, Token token) throws Exception {
    storage.save(id, token);
  }

  protected Token getStoredToken(String id) throws Exception {
    return (Token) storage.get(id);
  }

  /**
   * Returns the id used to put a token in the cache.
   */
  private String getTokenChacheId() {
    return "token" + instanceId;
  }

  private Token authenticate(OAuthCredentials credentials) {
    LOG.debug("Authenticate credentials: ", credentials.asMap());

    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.USER_AGENT, userAgentProvider.getValue());

    int pollInterval = 0;
    long timeout = System.currentTimeMillis();
    Integer ignoredHttpStatus = null;
    boolean deviceAuth = credentials instanceof DeviceCredentials;

    if (deviceAuth) {
      DeviceAuthCode codes = requestCodes(credentials, headers);

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

      // this is a device auth request and as long the user didn't enter the correct codes the
      // server will return 401 - it's part of the procedure in this case, so ignore
      ignoredHttpStatus = 401;
    }

    cancelAuth = false;
    do {
      // this is not supposed to be repeated when not device auth

      if (deviceAuth) {
        new ThreadSleep() {
          @Override
          protected boolean cancel() {
            if (cancelAuth) {
              throw new AuthCanceledException("Authentication canceled by request.");
            }
            return false;
          }
        }.execute(pollInterval, 1, TimeUnit.SECONDS);
      }

      Token token = request(Token.class, credentials, headers, ignoredHttpStatus);

      if (token == null && deviceAuth) {
        // not authenticated yet
        EventDispatcher.fireEvent(Events.EVENT_AUTH_PENDING, authEventListener, true);
      } else if (token != null) {
        if (deviceAuth) {
          EventDispatcher.fireEvent(Events.EVENT_AUTH_OK, authEventListener, true);
        }
        return token;
      }

    } while (System.currentTimeMillis() < timeout);

    if (deviceAuth) {
      throw new AuthCanceledException("Authentication canceled by timeout or authentication code was expired.");
    }

    throw new IllegalStateException("Unexpected failure of authentication.");
  }

  protected DeviceAuthCode requestCodes(OAuthCredentials credentials, Map<String, String> headers) {
    DeviceAuthCode codes = request(DeviceAuthCode.class, credentials, headers, null);

    if (StringUtils.isAnyBlank(codes.getDeviceCode(), codes.getUserCode(), codes.getVerificationUrl())) {
      throw new AuthException("Authentication failed, got no valid codes or URL.");
    }
    return codes;
  }

  protected <T> T request(Class<T> resultType, OAuthCredentials credentials, Map<String, String> headers,
                          Integer ignoredHttpStatus) {
    try {
      Map<String, Object> parameters = credentials.asMap();
      Map<String, String> info = getAdditionalInfo();
      if (info != null) {
        parameters.putAll(info);
      }
      return http.post(configuration.oauthUrl, parameters, headers, resultType, ignoredHttpStatus);
    } catch (ServerErrorException e) {
      // try to provide some more failure details
      if (e.getStatus() == null) {
        throw e;
      } else {
        throw new AuthException(e.getStatus());
      }
    }
  }

  /**
   * Refresh token, but copy ony access token and time of the refreshed token.
   *
   * @param token The token to refresh.
   * @return The refreshed token data.
   */
  protected Token refresh(Token token, OAuthCredentials credentials) {
    LOG.debug("Refresh token: ", credentials);
    if (!(credentials instanceof ClientCredentials)) {
      throw new IllegalArgumentException("Invalid credentials type for refresh, need any ClientCredentials type");
    }
    ClientCredentials cc = (ClientCredentials) credentials;
    RefreshCredentials rc = new RefreshCredentials(cc.getClientId(), cc.getClientSecret(), token.getRefreshToken());
    Token refreshToken = authenticate(rc);
    token.setAccessToken(refreshToken.getAccessToken());
    token.setExpiresIn(refreshToken.getExpiresIn());
    if (StringUtils.isNotBlank(refreshToken.getRefreshToken())) {
      token.setRefreshToken(refreshToken.getRefreshToken());
    }
    token.setExpireTime();
    return token;
  }

  public static class Configuration {
    private boolean cacheToken = true;
    private int authWaitTimeoutSec;
    private String oauthUrl;
    private boolean extendExpire = false;
    private Map<String, String> deviceInfo;

    public Configuration(String oauthUrl, int authWaitTimeoutSec, boolean cacheToken, Map<String, String> deviceInfo) {
      this.cacheToken = cacheToken;
      this.authWaitTimeoutSec = authWaitTimeoutSec;
      this.oauthUrl = oauthUrl;
      this.deviceInfo = deviceInfo;
    }
  }
}
