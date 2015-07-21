/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.product.general.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.secucard.connect.product.common.model.MediaResource;
import com.secucard.connect.product.common.model.SecuObject;

import java.util.Date;
import java.util.List;

public class News extends SecuObject {
  public static final String STATUS_READ = "read";
  public static final String STATUS_UNREAD = "unread";

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

  @JsonProperty("_account_read")
  private String accountRead;

  @JsonIgnore
  private MediaResource pictureObject;

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = SecuObject.OBJECT_PROPERTY)
  @JsonSubTypes({@JsonSubTypes.Type(value = Merchant.class, name = "general.merchants")})
  private List<SecuObject> related;


  public void setPicture(String value) {
    this.picture = value;
    pictureObject = MediaResource.create(picture);
  }

  public MediaResource getPictureObject() {
    return pictureObject;
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

  public List<SecuObject> getRelated() {
    return related;
  }

  public void setRelated(List<SecuObject> related) {
    this.related = related;
  }

  public String getAccountRead() {
    return accountRead;
  }

  public void setAccountRead(String accountRead) {
    this.accountRead = accountRead;
  }
}
