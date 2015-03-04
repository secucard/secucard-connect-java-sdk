package com.secucard.connect.model.general;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.components.MetaData;

import java.util.List;

public class Merchant extends SecuObject {
  public static final String OBJECT = "general.merchants";

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

  @Override
  public String getObject() {
    return OBJECT;
  }

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
