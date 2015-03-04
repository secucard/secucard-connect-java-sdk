package com.secucard.connect.model.general;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.secucard.connect.model.SecuObject;

import java.util.Date;
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

  private Date created;

  private String picture;

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = SecuObject.OBJECT_PROPERTY)
  @JsonSubTypes({
          @JsonSubTypes.Type(value = Merchant.class, name = Merchant.OBJECT)})
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

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  public List<SecuObject> getRelated() {
    return related;
  }

  public void setRelated(List<SecuObject> related) {
    this.related = related;
  }
}
