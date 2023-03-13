package com.secucard.connect.product.general.model;

import com.secucard.connect.product.common.model.SecuObject;

public class App extends SecuObject {

  public static final String APP_ID_SUPPORT = "APP_3XHE5NT3Z2YGA373R5GQGZTK6033AG";

  private String name;

  public App() {
  }

  public App(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Notification{" + "name='" + this.name + '\'' + ", " + super.toString() + '}';
  }
}
