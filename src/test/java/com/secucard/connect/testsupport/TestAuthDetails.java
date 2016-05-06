package com.secucard.connect.testsupport;

import com.secucard.connect.auth.AbstractClientAuthDetails;
import com.secucard.connect.auth.model.ClientCredentials;
import com.secucard.connect.auth.model.OAuthCredentials;
import com.secucard.connect.client.DataStorage;
import com.secucard.connect.client.MemoryDataStorage;

/**
 * Uses {@link MemoryDataStorage} and {@link ClientCredentials}.
 */
public class TestAuthDetails extends AbstractClientAuthDetails {
  private ClientCredentials cd;

  public TestAuthDetails(String cid, String csec) {
    super(null);
    cd = new ClientCredentials(cid, csec);
  }

  @Override
  public OAuthCredentials getCredentials() {
    return cd;
  }

  @Override
  public ClientCredentials getClientCredentials() {
    return cd;
  }

  @Override
  protected DataStorage createCache(Object arg) {
    return new MemoryDataStorage();
  }
}
