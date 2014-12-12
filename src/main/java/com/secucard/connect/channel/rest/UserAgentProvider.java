package com.secucard.connect.channel.rest;

public class UserAgentProvider {
  public String getValue() {
    return "connect client java v0.1"
        + "/java:" + System.getProperty("java.vendor") + " " + System.getProperty("java.version");
  }
}
