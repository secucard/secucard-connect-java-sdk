package com.secucard.connect.model.general;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.SecuException;
import com.secucard.connect.model.MediaResource;
import com.secucard.connect.model.SecuObject;

import java.net.MalformedURLException;
import java.util.List;

public class Account extends SecuObject {
  public static final String OBJECT = "general.accounts";

  private String username;

  private String password;

  private String role;

  private Contact contact;

  @JsonProperty("picture")
  private String pictureUrl;

  @JsonIgnore
  private MediaResource picture;

  private List<Assignment> assignment;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public Contact getContact() {
    return contact;
  }

  public void setContact(Contact contact) {
    this.contact = contact;
  }

  public MediaResource getPicture() {
    return picture;
  }

  public String getPictureUrl() {
    return pictureUrl;
  }

  public void setPictureUrl(String pictureUrl) {
    this.pictureUrl = pictureUrl;
    if (pictureUrl != null) {
      try {
        this.picture = new MediaResource(pictureUrl);
      } catch (MalformedURLException e) {
        throw new SecuException("Invalid acccount picture URL");
      }
    }
  }

  public List<Assignment> getAssignment() {
    return assignment;
  }

  public void setAssignment(List<Assignment> assignment) {
    this.assignment = assignment;
  }

  @Override
  public String getObject() {
    return OBJECT;
  }
}
