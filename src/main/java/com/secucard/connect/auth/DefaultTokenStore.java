package com.secucard.connect.auth;

import com.secucard.connect.auth.model.Token;
import com.secucard.connect.client.DiskCache;

public class DefaultTokenStore implements TokenStore {
  private DiskCache diskCache;

  public DefaultTokenStore(String dir) {
    this.diskCache = new DiskCache(dir);
  }

  @Override
  public Token get() {
    return (Token) diskCache.get("token");
  }

  @Override
  public void set(Token token) {
    diskCache.save("token", token);
  }
}
