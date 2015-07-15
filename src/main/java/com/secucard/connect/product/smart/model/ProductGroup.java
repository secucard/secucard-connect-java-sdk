package com.secucard.connect.product.smart.model;

public class ProductGroup {

  private String id;

  private String desc;

  private int level;

  public ProductGroup() {
  }

  public ProductGroup(String id, String desc, int level) {
    this.id = id;
    this.desc = desc;
    this.level = level;
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

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  @Override
  public String toString() {
    return "ProductGroup{" +
        "id='" + id + '\'' +
        ", desc='" + desc + '\'' +
        ", level='" + level + '\'' +
        '}';
  }
}
