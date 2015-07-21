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
