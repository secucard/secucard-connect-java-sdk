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
import com.secucard.connect.product.common.model.MediaResource;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.loyalty.model.Program;
import java.util.List;

public class Store extends SecuObject {

  public static final String CHECKIN_STATUS_DECLINED_DISTANCE = "declined_distance";
  public static final String CHECKIN_STATUS_DECLINED_NOTAVAIL = "declined_notavail";
  public static final String CHECKIN_STATUS_AVAILABLE = "available";
  public static final String CHECKIN_STATUS_CHECKED_IN = "checked_in";

  public static final String NEWS_STATUS_READ = "read";
  public static final String NEWS_STATUS_UNREAD = "unread";

  @JsonProperty
  private String source;

  @JsonProperty
  private String key;

  @JsonProperty
  private String hash;

  @JsonProperty
  private String name;

  @JsonProperty("name_raw")
  private String nameRaw;

  @JsonProperty
  private Merchant merchant;

  @JsonProperty("_news_status")
  private String newsStatus;

  @JsonProperty("_news")
  private List<News> news;

  @JsonProperty("open_now")
  private boolean openNow;

  @JsonProperty("open_time")
  private int openTime;

  @JsonProperty("open_hours")
  private List<OpenHours> openHours;

  @JsonProperty
  private Geometry geometry;

  @JsonProperty("_geometry")
  private int distance;

  @JsonProperty("_checkin_status")
  private String checkInStatus;

  @JsonProperty("address_formatted")
  private String addressFormatted;

  @JsonProperty("address_components")
  private List<AddressComponent> addressComponents;

  @JsonProperty
  private List<String> category;

  @JsonProperty("category_main")
  private String categoryMain;

  @JsonProperty("phone_number_formatted")
  private String phoneNumberFormatted;

  @JsonProperty("url_website")
  private String urlWebsite;

  @JsonProperty("_balance")
  private int balance;

  @JsonProperty("_points")
  private int points;

  @JsonProperty("_program")
  private Program program;

  @JsonProperty("_isDefault")
  private boolean isDefault;

  @JsonProperty("facebook_id")
  private String facebookId;

  @JsonProperty("photo")
  private List<String> pictureUrls;

  @JsonProperty("photo_main")
  private String logoUrl;

  @JsonIgnore
  private MediaResource logo;

  @JsonProperty("has_beacon")
  private Boolean hasBeacon;

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNameRaw() {
    return nameRaw;
  }

  public void setNameRaw(String nameRaw) {
    this.nameRaw = nameRaw;
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public String getNewsStatus() {
    return newsStatus;
  }

  public void setNewsStatus(String newsStatus) {
    this.newsStatus = newsStatus;
  }

  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
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

  public int getDistance() {
    return distance;
  }

  public void setDistance(int geometry) {
    this.distance = geometry;
  }

  public String getCheckInStatus() {
    return checkInStatus;
  }

  public void setCheckInStatus(String checkInStatus) {
    this.checkInStatus = checkInStatus;
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

  public String getAddressFormatted() {
    return addressFormatted;
  }

  public void setAddressFormatted(String addressFormatted) {
    this.addressFormatted = addressFormatted;
  }

  public List<AddressComponent> getAddressComponents() {
    return addressComponents;
  }

  public void setAddressComponents(List<AddressComponent> addressComponents) {
    this.addressComponents = addressComponents;
  }

  public String getPhoneNumberFormatted() {
    return phoneNumberFormatted;
  }

  public void setPhoneNumberFormatted(String phoneNumberFormatted) {
    this.phoneNumberFormatted = phoneNumberFormatted;
  }

  public String getUrlWebsite() {
    return urlWebsite;
  }

  public void setUrlWebsite(String urlWebsite) {
    this.urlWebsite = urlWebsite;
  }

  public List<News> getNews() {
    return news;
  }

  public void setNews(List<News> news) {
    this.news = news;
  }

  public int getBalance() {
    return balance;
  }

  public void setBalance(int balance) {
    this.balance = balance;
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(int points) {
    this.points = points;
  }

  public Program getProgram() {
    return program;
  }

  public void setProgram(Program program) {
    this.program = program;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public void setDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }

  public String getFacebookId() {
    return facebookId;
  }

  public void setFacebookId(String facebookId) {
    this.facebookId = facebookId;
  }

  public List<String> getPictureUrls() {
    return pictureUrls;
  }

  public void setPictureUrls(List<String> pictureUrls) {
    this.pictureUrls = pictureUrls;
  }

  public MediaResource getLogo() {
    return logo;
  }

  public String getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(String value) {
    this.logoUrl = value;
    logo = MediaResource.create(value);
  }

  public Boolean getHasBeacon() {
    return hasBeacon;
  }

  public void setHasBeacon(Boolean hasBeacon) {
    this.hasBeacon = hasBeacon;
  }
}
