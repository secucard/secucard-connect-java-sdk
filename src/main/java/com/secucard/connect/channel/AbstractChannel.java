package com.secucard.connect.channel;

import java.util.logging.Logger;

public abstract class AbstractChannel implements Channel {
  protected PathResolver pathResolver;

  protected final Logger LOG = Logger.getLogger(getClass().getName());

  public void setPathResolver(PathResolver pathResolver) {
    this.pathResolver = pathResolver;
  }
}
