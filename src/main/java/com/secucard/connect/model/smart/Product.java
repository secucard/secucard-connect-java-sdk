package com.secucard.connect.model.smart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Product {
  protected int id;

  private Integer parent;

  private String articleNumber;

  private String ean;

  protected String desc;

  private BigDecimal quantity;

  private int priceOne;

  private int tax;

  @JsonProperty("group")
  private List<ProductGroup> groups = new ArrayList<>();

  public Product() {
  }

  public Product(int id, Integer parent, String articleNumber, String ean, String desc, String quantity, int priceOne,
                 int tax, List<ProductGroup> productGroups) {
    this(id, parent, articleNumber, ean, desc, new BigDecimal(quantity), priceOne, tax, productGroups);
  }

  public Product(int id, Integer parent, String articleNumber, String ean, String desc, BigDecimal quantity,
                 int priceOne, int tax, List<ProductGroup> groups) {
    this.id = id;
    this.parent = parent;
    this.articleNumber = articleNumber;
    this.ean = ean;
    this.desc = desc;
    this.quantity = quantity;
    this.priceOne = priceOne;
    this.tax = tax;
    this.groups = groups;
  }

  public Integer getParent() {
    return parent;
  }

  public void setParent(Integer parent) {
    this.parent = parent;
  }

  public List<ProductGroup> getGroups() {
    return groups;
  }

  public void setGroups(List<ProductGroup> groups) {
    this.groups = groups;
  }

  @JsonIgnore
  public void addProductGroup(ProductGroup group) {
    groups.add(group);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getArticleNumber() {
    return articleNumber;
  }

  public void setArticleNumber(String articleNumber) {
    this.articleNumber = articleNumber;
  }

  public String getEan() {
    return ean;
  }

  public void setEan(String ean) {
    this.ean = ean;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public BigDecimal getQuantity() {
    return quantity;
  }

  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
  }

  public int getPriceOne() {
    return priceOne;
  }

  public void setPriceOne(int priceOne) {
    this.priceOne = priceOne;
  }

  public int getTax() {
    return tax;
  }

  public void setTax(int tax) {
    this.tax = tax;
  }

  @Override
  public String toString() {
    return "Product{" +
        "id='" + id + '\'' +
        ", parent='" + parent + '\'' +
        ", articleNumber='" + articleNumber + '\'' +
        ", ean='" + ean + '\'' +
        ", desc='" + desc + '\'' +
        ", quantity=" + quantity +
        ", priceOne=" + priceOne +
        ", tax=" + tax +
        ", productGroups=" + groups +
        '}';
  }
}
