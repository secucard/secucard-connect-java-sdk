package com.secucard.connect.product.services.model.idresult;

import com.secucard.connect.product.common.model.MediaResource;

import java.net.MalformedURLException;

public class Attachment extends MediaResource {
  private String type;

  public Attachment() {
  }

  public Attachment(String url, String type) throws MalformedURLException {
    super(url);
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "Attachment{" +
        "type='" + type + '\'' +
        ", url='" + getUrl() + '\'' +
        '}';
  }
}
