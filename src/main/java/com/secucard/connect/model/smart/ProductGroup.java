package com.secucard.connect.model.smart;

public class ProductGroup {

  private String id;

  private String desc;

  public ProductGroup() {
  }

  public ProductGroup(String id, String desc) {
    this.id = id;
    this.desc = desc;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  @Override
  public String toString() {
    return "ProductGroup{" +
        "id='" + id + '\'' +
        ", desc='" + desc + '\'' +
        '}';
  }
}
