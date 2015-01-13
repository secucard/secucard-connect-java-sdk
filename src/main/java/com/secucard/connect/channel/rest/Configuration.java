package com.secucard.connect.channel.rest;

public class Configuration {
  private final String baseUrl;

  public Configuration(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getBaseUrl() {
    return baseUrl;
  }
}
