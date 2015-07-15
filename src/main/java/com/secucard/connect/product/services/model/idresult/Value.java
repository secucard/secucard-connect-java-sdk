package com.secucard.connect.product.services.model.idresult;

public class Value {
  public static final String STATUS_NEW = "NEW";
  public static final String STATUS_MATCH = "MATCH";

  private String value;
  private String status;
  private String original;

  public Value() {
  }

  public Value(String value, String status) {
    this.value = value;
    this.status = status;
  }

  public String getOriginal() {
    return original;
  }

  public void setOriginal(String original) {
    this.original = original;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "Value{" +
        "value='" + value + '\'' +
        ", status='" + status + '\'' +
        ", original='" + original + '\'' +
        '}';
  }
}
