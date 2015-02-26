package com.secucard.connect.model.document.uploads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;

import java.util.Date;

/**
 * Created by Steffen Schröder on 26.02.15.
 */
public class Document extends SecuObject {
  public static final String OBJECT = "document.uploads";

  @JsonProperty
  private String content;

  @JsonProperty
  private Date created;

  @Override
  public String getObject() {
    return OBJECT;
  }

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
