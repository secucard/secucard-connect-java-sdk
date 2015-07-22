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

import com.secucard.connect.auth.model.ClientCredentials;
import com.secucard.connect.auth.model.OAuthCredentials;
import com.secucard.connect.auth.model.Token;

/**
 * Defines operations to access the necessary authentication details provided by the client which request authentication.
 * Administration of confidential data is delegated to the client through this interface.
 */
public interface ClientAuthDetails {
  /**
   * Returns the credentials needed to obtain an new access token and refresh token.
   * The returned type depends on the authentication type used with the client. Should never return null.
   */
  OAuthCredentials getCredentials();

  /**
   * Returns the client credentials needed to obtained an access token with an existing refresh token.
   * Should never return null.
   */
  ClientCredentials getClientCredentials();

  /**
   * Returns an (stored) existing Oauth access token or null if no token is available yet.
   */
  Token get();

  /**
   * Persist the given token in a way that calls to {@link #get()} can return this token anytime.
   */
  void set(Token token);
}
