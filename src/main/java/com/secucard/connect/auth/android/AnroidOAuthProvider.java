package com.secucard.connect.auth.android;

import com.secucard.connect.auth.OAuthProvider;
import com.secucard.connect.channel.JsonMapper;
import com.secucard.connect.model.auth.Token;

public class AnroidOAuthProvider extends OAuthProvider {
  private JsonMapper jsonMapper = JsonMapper.get();

  public AnroidOAuthProvider(String id, Configuration configuration) {
    super(id, configuration);
  }

  /**
   * Stores token as String.
   */
  @Override
  protected void storeToken(String id, Token token) throws Exception {
    storage.save(id, jsonMapper.map(token));
  }

  /**
   * Gets token as String.
   */
  @Override
  protected Token getStoredToken(String id) throws Exception {
    return jsonMapper.map((String) storage.get(id), Token.class);
  }
}
