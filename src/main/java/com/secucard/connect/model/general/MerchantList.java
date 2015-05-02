package com.secucard.connect.model.general;

import com.secucard.connect.model.annotation.ProductInfo;

import java.util.List;

// todo: this is very superfluous, replace with ObjectList
@ProductInfo(resourceId = "publicmerchants.aroundme")
public class MerchantList {

  private int count;

  private List<String> merchants;

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public List<String> getMerchants() {
    return merchants;
  }

  public void setMerchants(List<String> merchants) {
    this.merchants = merchants;
  }

  @Override
  public String toString() {
    return "MerchantList{" +
        "count=" + count +
        ", merchants=" + merchants +
        '}';
  }
}
