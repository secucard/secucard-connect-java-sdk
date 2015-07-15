package com.secucard.connect.product.document.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.SecuObject;

import java.util.Date;

public class Upload extends SecuObject {

  @JsonProperty
  private String content;

  @JsonProperty
  private Date created;

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }
}
