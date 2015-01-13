package com.secucard.connect.service;

import com.secucard.connect.ClientContext;

/**
 * Inner class is needed to test to non public features without creating them manually.
 */
public class TestService extends AbstractService {

  public ClientContext getContext() {
    return context;
  }
}
