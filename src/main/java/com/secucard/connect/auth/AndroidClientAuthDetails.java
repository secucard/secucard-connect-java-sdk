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

import android.content.SharedPreferences;
import com.secucard.connect.auth.model.Token;
import com.secucard.connect.client.ClientError;
import com.secucard.connect.net.util.JsonMapper;

import java.io.IOException;

/**
 * Abstract implementation which just delegates the token persistence to Android Shared Preferences store.
 * They handles just simple types, so token data is serialized and stored as JSON cleartext string.
 */
public abstract class AndroidClientAuthDetails implements ClientAuthDetails {

  /**
   *  The key used to store the token data.
   */
  public static final String TOKEN_KEY = "scctoken";

  private JsonMapper jsonMapper;
  private final SharedPreferences sharedPreferences;
  private Token currentToken; // caching instance for reads

  public void setJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  /**
   * Create an instance.
   *
   * @param sharedPreferences Th
   */
  public AndroidClientAuthDetails(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  public Token getCurrent() {
    if (currentToken == null) {
      String token = sharedPreferences.getString(TOKEN_KEY, null);
      if (token != null) {
        try {
          currentToken = jsonMapper.map(token, Token.class);
        } catch (IOException e) {
          throw new ClientError("Error deserializing token", e);
        }
      }
    }
    return currentToken;
  }

  /**
   * Handles token changed event.
   * Override and call super() to place further notification hooks.
   */
  public void onTokenChanged(Token token) {
    try {
      String str = jsonMapper.map(token);
      sharedPreferences.edit().putString(TOKEN_KEY, str).apply();
      currentToken = token;
    } catch (IOException e) {
      throw new ClientError("Error serializing token", e);
    }
  }

  public void clear() {
    sharedPreferences.edit().remove(TOKEN_KEY).apply();
  }
}
