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
