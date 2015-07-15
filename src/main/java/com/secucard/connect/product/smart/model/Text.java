package com.secucard.connect.product.smart.model;

public class Text {

  private String parentId;

  private String desc;

  public Text() {
  }

  public Text(String parentId, String desc) {
    this.parentId = parentId;
    this.desc = desc;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  @Override
  public String toString() {
    return "Text{" +
        "parentId='" + parentId + '\'' +
        ", desc='" + desc + '\'' +
        '}';
  }
}
