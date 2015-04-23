package com.secucard.connect.auth;

import com.secucard.connect.SecuException;
import com.secucard.connect.channel.JsonMapper;
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
import java.io.IOException;
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
  private RestChannelBase httpClient;
  private EventListener authEventListener;
  private RestChannelBase restChannel;
  private DataStorage storage;
  private UserAgentProvider userAgentProvider = new UserAgentProvider();
  private final String id;
  private final Configuration configuration;
  private String lastCredentialId;
  private JsonMapper jsonMapper = JsonMapper.get();
  private Token currentToken;  // kind of second level cache to avoid frequent token read

  protected static final Log LOG = new Log(OAuthProvider.class);

  public OAuthProvider(String id, Configuration configuration) {
    this.configuration = configuration;
    this.id = id;
  }

  public void setCredentials(OAuthCredentials credentials) {
    this.credentials = credentials;
  }

  public void setDataStorage(DataStorage dataStorage) {
    this.storage = dataStorage;
  }

  public void setRestChannel(RestChannelBase restChannel) {
    this.restChannel = restChannel;
  }

  @Override
  public void registerEventListener(EventListener eventListener) {
    authEventListener = eventListener;
  }

  public void cancelAuth() {
    this.cancelAuth = true;
  }

  @Override
  public Map<String, String> getAdditionalInfo() {
    return configuration.deviceInfo;
  }

  public void authenticate() {
    getToken();
  }

  /**
   * Returns a valid OAuth token.
   * The token is retrieved by accessing OAuth server initially once by using the provided credentials and cached for
   * later usage. This method also takes care of all aspects of the token validity, for example if the token is expired
   * and must be refreshed. So any client is supposed to use only this method when a token is needed, and must NOT store
   * the token on its own or alike. If the credentials change the token is retrieved new.
   *
   * @throws com.secucard.connect.auth.AuthException         If the authentication fails for some reason. The status
   *                                                         field may contain details about failure.
   * @throws com.secucard.connect.auth.AuthCanceledException If the authentication was canceled by the user.
   */
  public synchronized Token getToken() throws AuthException {
    OAuthCredentials cr = this.credentials;
    if (cr == null) {
      throw new AuthException("Missing credentials");
    }

    Token token = getCached(cr.getId());

    boolean authenticate = false;

    if (token == null) {
      // no token, authenticate first
      authenticate = true;
    } else if (token.isExpired()) {
      // try refresh if just expired, authenticate new if no refresh possible or failed
      LOG.debug("Token expired.");
      clearCache();
      if (token.getRefreshToken() == null) {
        LOG.debug("No token refresh possible, try authenticate new.");
        authenticate = true;
      } else {
        try {
          token = refresh(token, cr);
          cache(token);
        } catch (Throwable t) {
          LOG.debug("Token refresh failed, try authenticate new.", t);
          authenticate = true;
        }
      }
    } else {
      // we should have valid token in cache, no new auth necessary
      if (configuration.extendExpire) {
        LOG.debug("Extend token expire time.");
        token.setExpireTime();
        cache(token);
      }
      LOG.debug("Return cached token: ", token);
    }

    if (authenticate) {
      token = authenticate(cr);
      token.setExpireTime();
      token.setId(cr.getId());
      cache(token);
      LOG.debug("Return new token: ", token);
    }

    return token;
  }

  /**
   * Clear token from cache.
   */
  public synchronized void clearCache() {
    if (configuration.cacheToken) {
      currentToken = null;
      storage.clear(getTokenChacheId(), null);
    }
  }

  private void cache(Token token) {
    if (configuration.cacheToken) {
      try {
        String str = jsonMapper.map(token);
        // todo: encrypt token !!
        storage.save(getTokenChacheId(), str);
        currentToken = token;
      } catch (IOException e) {
        throw new IllegalArgumentException("Can't serialize token to JSON", e);
      }
    }
  }

  private Token getCached(String id) {
    if (!configuration.cacheToken) {
      return null;
    }

    if (currentToken != null && currentToken.getId().equals(id)) {
      return currentToken;
    }

    currentToken = null;

    try {
      String str = (String) storage.get(getTokenChacheId());
      if (str == null) {
        return null;
      }
      Token token = jsonMapper.map(str, Token.class);
      if (token.getId().equals(id)) {
        currentToken = token;
        return token;
      } else {
        clearCache();
      }
    } catch (Exception e) {
      LOG.error("Error reading token.", e);
    }

    return null;
  }

  /**
   * Returns the id used to put a token in the cache.
   */
  private String getTokenChacheId() {
    return "token" + id;
  }

  protected Token authenticate(OAuthCredentials credentials) {
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
              throw new AuthCanceledException("Authorization canceled by request.");
            }
            return false;
          }
        }.execute(pollInterval, 1, TimeUnit.SECONDS);
      }

      Token token = request(Token.class, credentials, headers, ignoredHttpStatus);

      if (token == null && deviceAuth) {
        // not authenticated yet
        EventDispatcher.fireEvent(EVENT_CODE_AUTH_PENDING, authEventListener, false);
      } else if (token != null) {
        if (deviceAuth) {
          EventDispatcher.fireEvent(EVENT_CODE_AUTH_OK, authEventListener, false);
        }
        return token;
      }

    } while (System.currentTimeMillis() < timeout);

    if (deviceAuth) {
      throw new AuthCanceledException("Authorization canceled by timeout or authorization code was expired.");
    }

    throw new IllegalStateException("Unexpected failure of authentication.");
  }

  protected DeviceAuthCode requestCodes(OAuthCredentials credentials, Map<String, String> headers) {
    DeviceAuthCode codes = request(DeviceAuthCode.class, credentials, headers, null);

    if (StringUtils.isAnyBlank(codes.getDeviceCode(), codes.getUserCode(), codes.getVerificationUrl())) {
      throw new AuthException("Authorization failed, got no valid codes or URL.");
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
      return restChannel.post(configuration.oauthUrl, parameters, headers, resultType, ignoredHttpStatus);
    } catch (SecuException e) {
      // try to provide some more failure details
      if (e.getStatus() != null) {
        throw new AuthException(e.getStatus());
      } else {
        throw new AuthException("Authorization failed.", e.getCause());
      }
    } catch (Exception e) {
      throw new AuthException("Authorization failed.", e);
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
