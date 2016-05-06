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
import com.secucard.connect.client.DataStorage;
import com.secucard.connect.client.DiskCache;

/**
 * Abstract implementation which delegates the token persistence to a instance of {@link DataStorage}.
 * Default instance is of {@link DiskCache}.
 */
public abstract class AbstractClientAuthDetails implements ClientAuthDetails {
  private final String id;
  private DataStorage cache;

  /**
   * Create an instance.
   *
   * @param arg Any argument to pass to {@link #createCache(Object)}
   */
  public AbstractClientAuthDetails(Object arg) {
    cache = createCache(arg);
    id = tokenId();
  }

  public Token getCurrent() {
    return (Token) cache.get(id);
  }

  /**
   * Handles token changed event.
   * Override and call super() to place further notification hooks.
   */
  public void onTokenChanged(Token token) {
    cache.save(id, token);
  }

  /**
   * Clear the token from cache. The cache itself may stay.
   */
  public void clear() {
    cache.clear(id, null);
  }

  /**
   * Remove the whole cache by calling {@link DataStorage#destroy()}.
   */
  public void remove() {
    cache.destroy();
  }

  /**
   * Returns the id to use as reference to token in cache. Called one time by constructor.
   * Default is "token". Customize by overriding.
   */
  protected String tokenId() {
    return "token";
  }

  /**
   * Returns the cache instance to use to persist the token data. Called one time by constructor.
   * Default is {@link DiskCache}. Customize by overriding.
   *
   * @param arg A string denoting the cache directory to use. May be absolute (starting with "/" or "\") or relative.
   *            Creates the directory if necessary. If null is passed ".sccauth" is used.
   */
  protected DataStorage createCache(Object arg) {
    if (arg == null) {
      arg = ".sccauth";
    }
    return new DiskCache((String) arg);
  }
}
