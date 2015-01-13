package com.secucard.connect.model.smart;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Product {

  protected String id;

  private String articleNumber;

  private String ean;

  protected String desc;

  private float quantity;

  private float priceOne;

  private float tax;

  private List<ProductGroup> productGroups = new ArrayList<>();

  public Product() {
  }

  public Product(String id, String articleNumber, String ean, String desc, float quantity, float priceOne, float tax) {
    this.id = id;
    this.articleNumber = articleNumber;
    this.ean = ean;
    this.desc = desc;
    this.quantity = quantity;
    this.priceOne = priceOne;
    this.tax = tax;
  }

  public Product(String id, String articleNumber, String ean, String desc, float quantity, float priceOne, float tax,
                 List<ProductGroup> productGroups) {
    this.id = id;
    this.articleNumber = articleNumber;
    this.ean = ean;
    this.desc = desc;
    this.quantity = quantity;
    this.priceOne = priceOne;
    this.tax = tax;
    this.productGroups = productGroups;
  }

  public List<ProductGroup> getProductGroups() {
    return productGroups;
  }

  public void setProductGroups(List<ProductGroup> productGroups) {
    this.productGroups = productGroups;
  }

  @JsonIgnore
  public void addProductGroup(ProductGroup group) {
    productGroups.add(group);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
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

  public float getQuantity() {
    return quantity;
  }

  public void setQuantity(float quantity) {
    this.quantity = quantity;
  }

  public float getPriceOne() {
    return priceOne;
  }

  public void setPriceOne(float priceOne) {
    this.priceOne = priceOne;
  }

  public float getTax() {
    return tax;
  }

  public void setTax(float tax) {
    this.tax = tax;
  }

  @Override
  public String toString() {
    return "Product{" +
        "id='" + id + '\'' +
        ", articleNumber='" + articleNumber + '\'' +
        ", ean='" + ean + '\'' +
        ", desc='" + desc + '\'' +
        ", quantity=" + quantity +
        ", priceOne=" + priceOne +
        ", tax=" + tax +
        '}';
  }
}
