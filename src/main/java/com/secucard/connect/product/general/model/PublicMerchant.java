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

public class PublicMerchant extends SecuObject {
  @JsonProperty
  private String source;

  @JsonProperty
  private String key;

  @JsonProperty
  private String hash;

  @JsonProperty("address_components")
  private List<AddressComponent> addressComponents;

  @JsonProperty("address_formatted")
  private String addressFormatted;

  @JsonProperty("phone_number_formatted")
  private String phoneNumberFormatted;

  @JsonProperty
  private Geometry geometry;

  @JsonProperty
  private String name;

  @JsonProperty
  private List<String> photo;

  @JsonProperty("photo_main")
  private String photoMain;

  @JsonProperty
  private List<String> category;

  @JsonProperty("category_main")
  private String categoryMain;

  @JsonProperty("url_googleplus")
  private String urlGooglePlus;

  @JsonProperty("url_website")
  private String urlWebsite;

  @JsonProperty("utc_offset")
  private int utcOffset;

  @JsonProperty("open_now")
  private boolean openNow;

  @JsonProperty("open_time")
  private int openTime;

  @JsonProperty("open_hours")
  private List<OpenHours> openHours;

  @JsonProperty("_geometry")
  private int distance;

  private boolean checkedIn;

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public List<AddressComponent> getAddressComponents() {
    return addressComponents;
  }

  public void setAddressComponents(List<AddressComponent> addressComponents) {
    this.addressComponents = addressComponents;
  }

  public String getAddressFormatted() {
    return addressFormatted;
  }

  public void setAddressFormatted(String addressFormatted) {
    this.addressFormatted = addressFormatted;
  }

  public String getPhoneNumberFormatted() {
    return phoneNumberFormatted;
  }

  public void setPhoneNumberFormatted(String phoneNumberFormatted) {
    this.phoneNumberFormatted = phoneNumberFormatted;
  }

  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public List<String> getCategory() {
    return category;
  }

  public void setCategory(List<String> category) {
    this.category = category;
  }

  public String getCategoryMain() {
    return categoryMain;
  }

  public void setCategoryMain(String categoryMain) {
    this.categoryMain = categoryMain;
  }

  public String getUrlGooglePlus() {
    return urlGooglePlus;
  }

  public void setUrlGooglePlus(String urlGooglePlus) {
    this.urlGooglePlus = urlGooglePlus;
  }

  public String getUrlWebsite() {
    return urlWebsite;
  }

  public void setUrlWebsite(String urlWebsite) {
    this.urlWebsite = urlWebsite;
  }

  public int getUtcOffset() {
    return utcOffset;
  }

  public void setUtcOffset(int utcOffset) {
    this.utcOffset = utcOffset;
  }

  public boolean isOpenNow() {
    return openNow;
  }

  public void setOpenNow(boolean openNow) {
    this.openNow = openNow;
  }

  public int getOpenTime() {
    return openTime;
  }

  public void setOpenTime(int openTime) {
    this.openTime = openTime;
  }

  public List<OpenHours> getOpenHours() {
    return openHours;
  }

  public void setOpenHours(List<OpenHours> openHours) {
    this.openHours = openHours;
  }

  public int getDistance() {
    return distance;
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  public boolean isCheckedIn() {
    return checkedIn;
  }

  public void setCheckedIn(boolean checkedIn) {
    this.checkedIn = checkedIn;
  }
}
