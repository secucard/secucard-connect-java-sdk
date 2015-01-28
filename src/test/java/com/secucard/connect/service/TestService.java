package com.secucard.connect.service;

import com.secucard.connect.ClientContext;

/**
 * This service just exposes protected methods or fields for tests.
 */
public class TestService extends AbstractService {

  public ClientContext getContext() {
    return context;
  }

  public void setContextToCurrentThread() {
    setContext();
  }
}
