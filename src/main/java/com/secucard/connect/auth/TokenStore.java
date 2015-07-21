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

import com.secucard.connect.auth.model.Token;

/**
 * Interface for retrieving and persisting an authentication token from/to any kind of store.
 * To be implemented by SDK users to control how a token is stored in the system.
 */
public interface TokenStore {

  /**
   * Retrieves an OAuth token from the store.
   *
   * @return The token instance or null when no token is available yet.
   */
  Token get();

  /**
   * Persist a given token.
   *
   * @param token The new token data.
   */
  void set(Token token);
}
