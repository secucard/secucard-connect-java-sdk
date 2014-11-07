package com.secucard.connect.example.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.BaseClient;
import com.secucard.connect.ClientConfig;
import com.secucard.connect.ConnectException;
import com.secucard.connect.EventListener;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.smart.Basket;
import com.secucard.connect.model.smart.BasketInfo;
import com.secucard.connect.model.smart.Ident;
import com.secucard.connect.model.smart.Result;

import java.util.Date;
import java.util.List;

public class Transaction extends SecuObject {

  private BaseClient client;

  public static final String OBJECT = "smart.transactions";

  @JsonProperty("basket_info")
  private BasketInfo basketInfo;

  @JsonProperty("origin_device")
  String originDevice;

  @JsonProperty
  String status;

  @JsonProperty
  Date created;

  @JsonProperty
  List<Ident> idents;

  @JsonProperty
  Basket basket;

  private Transaction(){
  }

  public void setEventListener(EventListener eventListener) {
    client.setEventListener(eventListener);
  }

  public void setClient(BaseClient client) {
    this.client = client;
  }

  public Transaction save() {
    return client.selectChannnel().saveObject(this);
  }

  public Result start() {
    return client.selectChannnel().execute("start", new String[]{getId(), "demo"}, this, Result.class);
  }

  public BasketInfo getBasketInfo() {
    return basketInfo;
  }

  public void setBasketInfo(BasketInfo basketInfo) {
    this.basketInfo = basketInfo;
  }

  @Override
  public String getObject() {
    return OBJECT;
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

  public static Transaction create(ClientConfig config) {
    Transaction d = new Transaction();
    d.setClient(BaseClient.create(config, BaseClient.class));
    return d;
  }

  public void connect() throws ConnectException {
    client.connect();
  }

  public void disconnect() {
    client.disconnect();
  }
}
