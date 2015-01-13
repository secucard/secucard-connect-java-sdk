package com.secucard.connect.model.smart;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.annotation.ProductInfo;

import java.util.Date;
import java.util.List;

@ProductInfo(resourceId = "smart.transactions")
public class Transaction extends SecuObject {
  @JsonProperty("basket_info")
  private BasketInfo basketInfo;

  @JsonProperty("origin_device")
  String originDevice;

  String status;

  Date created;

  List<Ident> idents;

  Basket basket;

  private String merchantRef;

  private String transactionRef;

  public Transaction() {
  }

  public Transaction(String originDevice, BasketInfo basketInfo, Basket basket, List<Ident> idents) {
    this.basketInfo = basketInfo;
    this.originDevice = originDevice;
    this.basket = basket;
    this.idents = idents;
  }

  public String getTransactionRef() {
    return transactionRef;
  }

  public void setTransactionRef(String transactionRef) {
    this.transactionRef = transactionRef;
  }

  public String getMerchantRef() {
    return merchantRef;
  }

  public void setMerchantRef(String merchantRef) {
    this.merchantRef = merchantRef;
  }

  public BasketInfo getBasketInfo() {
    return basketInfo;
  }

  public void setBasketInfo(BasketInfo basketInfo) {
    this.basketInfo = basketInfo;
  }

  public String getOriginDevice() {
    return originDevice;
  }

  public void setOriginDevice(String originDevice) {
    this.originDevice = originDevice;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public List<Ident> getIdents() {
    return idents;
  }

  public void setIdents(List<Ident> idents) {
    this.idents = idents;
  }

  public Basket getBasket() {
    return basket;
  }

  public void setBasket(Basket basket) {
    this.basket = basket;
  }

  @Override
  public String toString() {
    return "Transaction{" +
        "basketInfo=" + basketInfo +
        ", originDevice='" + originDevice + '\'' +
        ", status='" + status + '\'' +
        ", created=" + created +
        ", idents=" + idents +
        ", basket=" + basket +
        '}';
  }
}
