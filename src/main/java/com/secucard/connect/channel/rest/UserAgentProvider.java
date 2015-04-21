package com.secucard.connect.channel.rest;

public class UserAgentProvider {
  public UserAgentProvider() {
  }

  public static String getValue() {
    // todo: add more info like android version
    return "connect client java v0.1"
        + "/java:" + System.getProperty("java.vendor") + " " + System.getProperty("java.version");
  }
}
