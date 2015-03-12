package com.secucard.connect.channel.rest;

import com.secucard.connect.ClientConfiguration;
import com.secucard.connect.SecuException;
import com.secucard.connect.auth.*;
import com.secucard.connect.event.EventDispatcher;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.auth.DeviceAuthCode;
import com.secucard.connect.model.auth.Token;
import com.secucard.connect.storage.DataStorage;
import com.secucard.connect.util.Log;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.HttpHeaders;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the AuthProvider interface which gets an OAuth token via REST channel.
 * <p/>
 * The retrieved token is also cached and refreshed.
 */
public class OAuthProvider implements AuthProvider {
  private final ClientConfiguration configuration;
  private EventListener authEventListener;
  private RestChannelBase restChannel;
  private DataStorage storage;
  private UserAgentProvider userAgentProvider = new UserAgentProvider();
  private final String id;
  private volatile boolean cancelAuth;
  private boolean extendExpire = true;

  private static final Log LOG = new Log(OAuthProvider.class);

  public OAuthProvider(String id, ClientConfiguration configuration) {
    this.id = id;
    this.configuration = configuration;
  }

  public void setDataStorage(DataStorage dataStorage) {
    this.storage = dataStorage;
  }

  public void setRestChannel(RestChannelBase restChannel) {
    this.restChannel = restChannel;
  }

  protected ClientCredentials getClientCredentials() {
    return configuration.getClientCredentials();
  }

  protected UserCredentials getUserCredentials() {
    return configuration.getUserCredentials();
  }

  public void cancelAuth() {
    this.cancelAuth = true;
  }

  public void setExtendExpire(boolean extendExpire) {
    this.extendExpire = extendExpire;
  }

  public boolean isExtendExpire() {
    return extendExpire;
  }

  /**
   * Returns the client devices unique id like android id or UUID.
   * Gets it from config by default, override to retrieve it dynamically or whatever.
   */
  protected String getDeviceId() {
    return configuration.getDeviceId();
  }

  /**
   * Return additional device info like software version.
   */
  protected Map<String, String> getDeviceInfo() {
    return null;
  }


  @Override
  public void registerEventListener(EventListener eventListener) {
    authEventListener = eventListener;
  }

  @Override
  public synchronized Token getToken() {
    Token token = getStoredToken();

    if (token != null && !token.isExpired()) {
      if (extendExpire) {
        // extend expire time on every token access, assuming the token is used, if not this could cause auth failure
        token.setExpireTime();
        storeToken(token);
      }
      LOG.debug("Stored token returned: ", token);
      return token;
    }

    if (token != null && token.getRefreshToken() != null) {
      // token is expired and can be refreshed without further auth.
      Token refreshToken = null;
      try {
        refreshToken = getRefreshToken(token.getRefreshToken());
      } finally {
        if (refreshToken == null) {
          // refreshing failed, clear the token
          removeToken();
          token = null;
        } else {
          token.setAccessToken(refreshToken.getAccessToken());
          token.setExpiresIn(refreshToken.getExpiresIn());
          if (StringUtils.isNotBlank(refreshToken.getRefreshToken())) {
            token.setRefreshToken(refreshToken.getRefreshToken());
          }
          token.setExpireTime();
          storeToken(token);
        }
      }
      LOG.debug("Token refreshed and returned: ", token);
    } else {
      token = null;
    }

    if (token == null) {
      // no token yet, a new one must created
      if ("device".equalsIgnoreCase(configuration.getAuthType())) {
        // perform a device auth
        DeviceAuthCode codes = requestCodes();
        EventDispatcher.fireEvent(codes, authEventListener, true);
        LOG.debug("Retrieved codes for device auth: ", codes, ", now polling for auth.");
        token = pollToken(codes);
      } else {
        // get a new token depending on what credentials are available
        token = getToken(getClientCredentials(), getUserCredentials(), null, getDeviceId(), getDeviceInfo(), null);
      }

      LOG.debug("New token retrieved: ", token);

      if (token != null) {
        // set new expire time and store
        token.setExpireTime();
        storeToken(token);
      } else {
        LOG.warn("Unable to retrieve token.");
      }
    }

    return token;
  }

  protected Token pollToken(DeviceAuthCode codes) {
    // set poll timeout, either by config or by expire time of code
    int t = codes.getExpiresIn();
    if (t <= 0 || configuration.getAuthWaitTimeoutSec() < t) {
      t = configuration.getAuthWaitTimeoutSec();
    }
    long timeout = System.currentTimeMillis() + t * 1000;

    int pollInterval = codes.getInterval() * 1000;
    if (pollInterval <= 0) {
      pollInterval = 5000; // poll default 5s
    }

    // poll server and send events to client accordingly
    Token token = null;
    cancelAuth = false;
    while (System.currentTimeMillis() < timeout) {
      if (cancelAuth) {
        throw new AuthCanceledException("Authorization canceled by request.");
      }
      try {
        Thread.sleep(pollInterval);
      } catch (InterruptedException e) {
        break;
      }
      token = getDeviceAuthToken(codes);
      if (token != null) {
        EventDispatcher.fireEvent(EVENT_CODE_AUTH_OK, authEventListener, false);
        return token;
      }
      EventDispatcher.fireEvent(EVENT_CODE_AUTH_PENDING, authEventListener, false);
    }

    throw new AuthCanceledException("Authorization canceled by timeout or authorization code was expired.");
  }

  protected Token getStoredToken() {
    return (Token) storage.get(getTokenStoreId());
  }

  protected void removeToken() {
    storage.clear(getTokenStoreId(), null);
  }

  protected void storeToken(Token token) {
    storage.save(getTokenStoreId(), token);
  }

  private String getTokenStoreId() {
    return "token" + id;
  }

  protected Token getRefreshToken(String refreshToken) {
    return getToken(getClientCredentials(), null, refreshToken, null, null, null);
  }

  protected Token getDeviceAuthToken(DeviceAuthCode codes) {
    return getToken(getClientCredentials(), null, null, null, null, codes.getDeviceCode());
  }

  private Token getToken(ClientCredentials clientCredentials, UserCredentials userCredentials,
                     String refreshToken, String deviceId, Map<String, String> deviceInfo, String deviceCode) {
    Map<String, Object> parameters = createAuthParams(clientCredentials, userCredentials, refreshToken, deviceId,
        deviceInfo, deviceCode);
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.USER_AGENT, userAgentProvider.getValue());
    Integer ignoredHttpStatus = null;
    if (deviceCode != null) {
      // this is a device auth request and as long the user didn't enter the correct codes the
      // server will return 401 - it's part of the procedure in this case, so ignore
      ignoredHttpStatus = 401;
    }
    try {
      return restChannel.post(configuration.getOauthUrl(), parameters, headers, Token.class, ignoredHttpStatus);
    } catch (SecuException e) {
      if (e.getStatus() != null) {
        throw new AuthException(e.getStatus());
      } else {
        throw new AuthException("Authorization failed.", e.getCause());
      }
    } catch (Exception e) {
      throw new AuthException("Authorization failed.", e);
      }
    }

  protected DeviceAuthCode requestCodes() {
    Map<String, Object> parameters = createAuthParams(getClientCredentials(), null, null, getDeviceId(), null, null);
    DeviceAuthCode codes = null;
    try {
      codes = restChannel.post(configuration.getOauthUrl(), parameters, null, DeviceAuthCode.class);
    } catch (SecuException e) {
      if (e.getStatus() != null) {
        throw new AuthException(e.getStatus());
      } else {
        throw new AuthException("Authorization failed.", e.getCause());
      }
    } catch (Exception e) {
      throw new AuthException("Authorization failed.", e);
    }
    if (StringUtils.isAnyBlank(codes.getDeviceCode(), codes.getUserCode(), codes.getVerificationUrl())) {
      throw new AuthException("Authorization failed, got no valid codes or URL.");
    }
    return codes;
  }

  /**
   * Returning a map with request params for authorization purposes according to the given credentials.
   */
  protected Map<String, Object> createAuthParams(ClientCredentials clientCredentials, UserCredentials userCredentials,
                                                 String refreshToken, String deviceId, Map<String, String> deviceInfo,
                                                 String deviceCode) {
    Map<String, Object> parameters = new HashMap<>();

    // default type, client id / secret must always exist
    parameters.put("grant_type", "client_credentials");
    parameters.put("client_id", clientCredentials.getClientId());
    parameters.put("client_secret", clientCredentials.getClientSecret());

    if (refreshToken != null) {
      parameters.put("grant_type", "refresh_token");
      parameters.put("refresh_token", refreshToken);
    } else if (userCredentials != null) {
      parameters.put("grant_type", "appuser");
      parameters.put("username", userCredentials.getUsername());
      parameters.put("password", userCredentials.getPassword());
      if (deviceId != null) {
        parameters.put("device", deviceId);
      }
      if (deviceInfo != null) {
        parameters.putAll(deviceInfo);
      }
    } else if ("device".equals(configuration.getAuthType()) && (deviceId != null || deviceCode != null)) {
      parameters.put("grant_type", "device");
      if (deviceId != null) {
        parameters.put("uuid", deviceId);
      }
      if (deviceCode != null) {
        parameters.put("code", deviceCode);
      }
    }
    LOG.debug("Use OAuth parameters: ", parameters);
    return parameters;
  }
}
