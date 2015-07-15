package com.secucard.connect.product.general.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.SecuObject;

import java.math.BigDecimal;
import java.util.List;

public class MerchantDetails extends SecuObject {

  private Store store;

  private Merchant merchant;

  @JsonProperty("_news")
  private List<News> news;

  @JsonProperty("_balance")
  private BigDecimal balance;

  @JsonProperty("_points")
  private int points;

  public MerchantDetails() {
  }

  public Store getStore() {
    return store;
  }

  public void setStore(Store store) {
    this.store = store;
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
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
}
