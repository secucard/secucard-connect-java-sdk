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

import com.secucard.connect.auth.exception.AuthDeniedException;
import com.secucard.connect.auth.exception.AuthFailedException;
import com.secucard.connect.auth.model.*;
import com.secucard.connect.client.ClientError;
import com.secucard.connect.net.rest.HttpErrorException;
import com.secucard.connect.net.rest.RestChannel;

import javax.ws.rs.core.HttpHeaders;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides low level API for secucard OAuth token retrieval.
 * See <a href=http://secucard.com/developer/doc/auth.html">http://secucard.com/developer/doc/auth.html</a>
 */
public class AuthService {
  private RestChannel restChannel;
  private String url;
  private String userAgentInfo;

  public AuthService(RestChannel restChannel, String url) {
    this.restChannel = restChannel;
    this.url = url;
  }

  /**
   * Override to return user agent info to include in request.
   */
  public String getUserAgentInfo() {
    return userAgentInfo;
  }

  public void setUserAgentInfo(String userAgentInfo) {
    this.userAgentInfo = userAgentInfo;
  }

  /**
   * Obtain a new token for the supplied credentials.
   *
   * @throws AuthFailedException if the retrieval went wrong.
   * @throws AuthDeniedException if the auth is not ready yet.
   */
  public Token getToken(OAuthCredentials credentials) throws AuthFailedException, AuthDeniedException {
    return post(credentials, Token.class);
  }

  /**
   * Obtain a new token for the supplied refresh credentials.
   *
   * @throws AuthFailedException if the retrieval went wrong.
   */
  public Token refresh(RefreshCredentials credentials) throws AuthFailedException {
    try {
      return post(credentials, Token.class);
    } catch (AuthDeniedException e) {
      // never happens here;
    }
    return null;
  }

  /**
   * Return the  codes for device auth flow.
   *
   * @throws AuthFailedException if the retrieval went wrong.
   * @throws AuthDeniedException if the device uuid is not configured.
   */
  public DeviceAuthCode getCodes(DeviceCredentials credentials) throws AuthFailedException, AuthDeniedException {
    return post(credentials, DeviceAuthCode.class);
  }

  private <T> T post(OAuthCredentials credentials, Class<T> type) throws AuthDeniedException, AuthFailedException {
    try {
      return restChannel.post(url, credentials.asMap(), headers(), type, Map.class);
    } catch (HttpErrorException e) {
      Map<String, String> details = null;
      if (e.getEntity() != null && e.getEntity() instanceof Map) {
        details = (Map<String, String>) e.getEntity();
      }

      if (e.getHttpStatus() == 401) {
        // should be recoverable by user, like correcting password
        throw new AuthDeniedException(details == null ? null : details.get("error_description"));
      }

      if (e.getHttpStatus() == 400) {
        if (details != null) {
          throw new AuthFailedException(details.get("error_description"), details.get("error"));
        } else {
          throw new AuthFailedException("Authentication failed for unknown reason.", e);
        }
      }

      throw new ClientError("Unexpected error executing authentication request.", e);
    }
  }

  private Map<String, String> headers() {
    Map<String, String> headers = new HashMap<>();
    headers.put(HttpHeaders.USER_AGENT, getUserAgentInfo());
    return headers;
  }
}
