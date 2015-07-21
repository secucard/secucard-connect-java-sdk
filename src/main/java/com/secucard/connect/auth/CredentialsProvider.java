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

/**
 * Returns the credentials used to obtain OAuth access tokens.
 * To be implemented by the SDK user to have control how credentials are stored in the system.
 */
public interface CredentialsProvider {

  /**
   * Returns the credentials needed to obtain an new access token and refresh token.
   * The returned type depends on the authorisation type used with the client.
   */
  OAuthCredentials getCredentials();

  /**
   * Returns the client credentials needed to obtained an access token with an existing refresh token.
   */
  ClientCredentials getClientCredentials();
}
