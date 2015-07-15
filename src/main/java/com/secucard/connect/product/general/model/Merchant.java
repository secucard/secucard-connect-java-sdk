package com.secucard.connect.product.general.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.SecuObject;

import java.util.List;

public class Merchant extends SecuObject {
  @JsonProperty
  private String name;

  @JsonProperty
  private String email;

  @JsonProperty
  private MetaData metadata;

  @JsonProperty
  private Location location;

  private List<String> photo;

  @JsonProperty("photo_main")
  private String photoMain;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public MetaData getMetadata() {
    return metadata;
  }

  public void setMetadata(MetaData metadata) {
    this.metadata = metadata;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public List<String> getPhoto() {
    return photo;
  }

  public void setPhoto(List<String> photo) {
    this.photo = photo;
  }

  public String getPhotoMain() {
    return photoMain;
  }

  public void setPhotoMain(String photoMain) {
    this.photoMain = photoMain;
  }
}
