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
import com.secucard.connect.client.DiskCache;

/**
 * Abstract implementation which just delegates the token persistence to a file based cache.
 */
public abstract class AbstractClientAuthDetails implements ClientAuthDetails {
  private DiskCache diskCache;

  /**
   * Create an instance.
   *
   * @param dir The cache directory to use. May be absolute (starting with "/" or "\") or relative.
   */
  public AbstractClientAuthDetails(String dir) {
    this.diskCache = new DiskCache(dir);
  }

  public Token getCurrent() {
    return (Token) diskCache.get("token");
  }

  /**
   * Handles token changed event.
   * Override and call super() to place further notification hooks.
   */
  public void onTokenChanged(Token token) {
    diskCache.save("token", token);
  }
}
