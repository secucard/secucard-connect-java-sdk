package com.secucard.connect.model.services.idresult;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Entity {
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
