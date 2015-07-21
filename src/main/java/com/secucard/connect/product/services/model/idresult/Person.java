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

package com.secucard.connect.product.services.model.idresult;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Person {
  @JsonProperty("identificationprocess")
  private IdentificationProcess identificationProcess;

  @JsonProperty("identificationdocument")
  private IdentificationDocument identificationDocument;

  @JsonProperty("customdata")
  private CustomData customData;

  @JsonProperty("contactdata")
  private ContactData contactData;

  private List<Attachment> attachments = new ArrayList<>();

  @JsonProperty("userdata")
  private UserData userData;

  public IdentificationProcess getIdentificationProcess() {
    return identificationProcess;
  }

  public void setIdentificationProcess(IdentificationProcess identificationProcess) {
    this.identificationProcess = identificationProcess;
  }

  public IdentificationDocument getIdentificationDocument() {
    return identificationDocument;
  }

  public void setIdentificationDocument(IdentificationDocument identificationDocument) {
    this.identificationDocument = identificationDocument;
  }

  public CustomData getCustomData() {
    return customData;
  }

  public void setCustomData(CustomData customData) {
    this.customData = customData;
  }

  public ContactData getContactData() {
    return contactData;
  }

  public void setContactData(ContactData contactData) {
    this.contactData = contactData;
  }

  public List<Attachment> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<Attachment> attachments) {
    this.attachments = attachments;
  }

  public UserData getUserData() {
    return userData;
  }

  public void setUserData(UserData userData) {
    this.userData = userData;
  }
}
