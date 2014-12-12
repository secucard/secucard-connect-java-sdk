package com.secucard.connect.channel;

import com.secucard.connect.Callback;
import com.secucard.connect.auth.OAuthClientCredentials;
import com.secucard.connect.auth.OAuthUserCredentials;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class AbstractChannel implements Channel {
  protected PathResolver pathResolver;

  protected final Logger LOG = Logger.getLogger(getClass().getName());

  public void setPathResolver(PathResolver pathResolver) {
    this.pathResolver = pathResolver;
  }

  protected void onFailed(Callback callback, Throwable e) {
    if (callback != null) {
      try {
        callback.failed(e);
      } catch (Exception e1) {
        // ignore
      }
    }
  }

  protected <T> void onCompleted(Callback<T> callback, T result) {
    if (callback != null) {
      try {
        callback.completed(result);
      } catch (Exception e) {
        // ignore
      }
    }
  }

  protected Map<String, String> createAuthParams(OAuthClientCredentials clientCredentials, OAuthUserCredentials userCredentials, String refreshToken) {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("client_id", clientCredentials.getClientId());
    parameters.put("client_secret", clientCredentials.getClientSecret());
    if (refreshToken != null) {
      parameters.put("grant_type", "refresh_token");
      parameters.put("refresh_token", refreshToken);
    } else if (userCredentials != null) {
      parameters.put("grant_type", "appuser");
      parameters.put("username", userCredentials.getUsername());
      parameters.put("password", userCredentials.getPassword());
    } else {
      parameters.put("grant_type", "client_credentials");
    }
    return parameters;
  }
}
