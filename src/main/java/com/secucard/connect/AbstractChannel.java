package com.secucard.connect;

public abstract class AbstractChannel implements Channel {
  protected PathResolver pathResolver;

  public void setPathResolver(PathResolver pathResolver) {
    this.pathResolver = pathResolver;
  }
}
