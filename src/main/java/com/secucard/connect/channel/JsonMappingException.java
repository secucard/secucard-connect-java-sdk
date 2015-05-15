package com.secucard.connect.channel;

import java.io.IOException;

/**
 * Indicates that the JSON mapping failed.
 * The original JSON string can be retrieved by {@link #getJson()}.
 */
public class JsonMappingException extends IOException {
  private String json;

  public String getJson() {
    return json;
  }

  public JsonMappingException(String json) {
    this.json = json;
  }

  public JsonMappingException(String json, Throwable cause) {
    super(cause);
    this.json = json;
  }
}
