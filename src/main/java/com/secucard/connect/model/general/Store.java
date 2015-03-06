package com.secucard.connect.model.general;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.components.OpenHours;
import com.secucard.connect.model.loyalty.Program;

import java.math.BigDecimal;
import java.util.List;

public class Store extends SecuObject {
  public static final String OBJECT = "general.stores";

  public static final String CHECKIN_STATUS_DECLINED_DISTANCE = "declined_distance";
  public static final String CHECKIN_STATUS_DECLINED_NOTAVAIL = "declined_notavail";
  public static final String CHECKIN_STATUS_AVAILABLE = "available";
  public static final String CHECKIN_STATUS_CHECKED_IN = "checked_in";

  public static final String NEWS_STATUS_READ = "read";
  public static final String NEWS_STATUS_UNREAD = "unread";


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

  @JsonProperty("_geometry")
  private int distance;

  @JsonProperty("_checkin_status")
  private String checkInStatus;

  @JsonProperty("address_formatted")
  private String addressFormatted;


  @JsonProperty("phone_number_formatted")
  private String phoneNumberFormatted;

  @JsonProperty("url_website")
  private String urlWebsite;

  @JsonProperty("_balance")
  private BigDecimal balance;

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
  private String logo;

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

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
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

  public String getLogo() {
    return logo;
  }

  public void setLogo(String logo) {
    this.logo = logo;
  }
}
