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

package com.secucard.connect.product.smart.model;

import java.util.Currency;

public class BasketInfo {
  private int sum;

  private Currency currency;

  public BasketInfo() {
  }

  public BasketInfo(int sum, Currency currency) {
    this.sum = sum;
    this.currency = currency;
  }

  public BasketInfo(int sum, String currencyCode) {
    this.sum = sum;
    this.currency = Currency.getInstance(currencyCode);
  }

  public int getSum() {
    return sum;
  }

  public void setSum(int sum) {
    this.sum = sum;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  @Override
  public String toString() {
    return "BasketInfo{" +
        "sum=" + sum +
        ", currency=" + currency +
        '}';
  }
}
