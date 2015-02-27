package com.secucard.connect.model.general;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;

import java.util.List;

public class News extends SecuObject {
  public static final String OBJECT = "general.news";

  @JsonProperty
  private String headline;

  @JsonProperty("text_teaser")
  private String textTeaser;

  @JsonProperty("text_full")
  private String textFull;

  @JsonProperty
  private String author;

  @JsonProperty("document_id")
  private String documentId;

  @JsonProperty
  private List<SecuObject> related;

  @Override
  public String getObject() {
    return OBJECT;
  }

  public String getHeadline() {
    return headline;
  }

  public void setHeadline(String headline) {
    this.headline = headline;
  }

  public String getTextTeaser() {
    return textTeaser;
  }

  public void setTextTeaser(String textTeaser) {
    this.textTeaser = textTeaser;
  }

  public String getTextFull() {
    return textFull;
  }

  public void setTextFull(String textFull) {
    this.textFull = textFull;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getDocumentId() {
    return documentId;
  }

  public void setDocumentId(String documentId) {
    this.documentId = documentId;
  }

  public List<SecuObject> getRelated() {
    return related;
  }

  public void setRelated(List<SecuObject> related) {
    this.related = related;
  }
}
