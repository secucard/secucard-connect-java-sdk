package com.secucard.connect.model.smart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;

import java.util.Date;
import java.util.List;

public class Transaction extends SecuObject {
  public static final String OBJECT = "smart.transactions";

  @JsonProperty("basket_info")
  private BasketInfo basketInfo;

  @JsonProperty("device_source")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  Device deviceSource;

  @JsonProperty("target_device")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  Device targetDevice;

  String status;

  Date created;

  List<Ident> idents;

  Basket basket;

  private String merchantRef;

  private String transactionRef;

  public Transaction() {
  }

  public Transaction(BasketInfo basketInfo, Basket basket, List<Ident> idents) {
    this.basketInfo = basketInfo;
    this.basket = basket;
    this.idents = idents;
  }

  @Override
  public String getObject() {
    return OBJECT;
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

  public Device getDeviceSource() {
    return deviceSource;
  }

  public void setDeviceSource(Device deviceSource) {
    this.deviceSource = deviceSource;
  }

  public Device getTargetDevice() {
    return targetDevice;
  }

  public void setTargetDevice(Device targetDevice) {
    this.targetDevice = targetDevice;
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
        ", deviceSource=" + deviceSource +
        ", targetDevice=" + targetDevice +
        ", status='" + status + '\'' +
        ", created=" + created +
        ", idents=" + idents +
        ", basket=" + basket +
        ", merchantRef='" + merchantRef + '\'' +
        ", transactionRef='" + transactionRef + '\'' +
        "} " + super.toString();
  }
}
