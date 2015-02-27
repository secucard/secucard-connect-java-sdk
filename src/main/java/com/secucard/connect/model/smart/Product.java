package com.secucard.connect.model.smart;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class Product {
  protected String id;

  private String articleNumber;

  private String ean;

  protected String desc;

  private BigDecimal quantity;

  private BigDecimal priceOne;

  private BigDecimal tax;

  private Currency currency;

  private List<ProductGroup> productGroups = new ArrayList<>();

  public Product() {
  }

  public Product(String id, String articleNumber, String ean, String desc, String quantity, String priceOne, String tax,
                 String currency, List<ProductGroup> productGroups) {
    this.id = id;
    this.articleNumber = articleNumber;
    this.ean = ean;
    this.desc = desc;
    this.quantity = new BigDecimal(quantity);
    this.priceOne = new BigDecimal(priceOne);
    this.tax = new BigDecimal(tax);
    this.productGroups = productGroups;
    this.currency = Currency.getInstance(currency);
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

  public BigDecimal getQuantity() {
    return quantity;
  }

  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
  }

  public BigDecimal getPriceOne() {
    return priceOne;
  }

  public void setPriceOne(BigDecimal priceOne) {
    this.priceOne = priceOne;
  }

  public BigDecimal getTax() {
    return tax;
  }

  public void setTax(BigDecimal tax) {
    this.tax = tax;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
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
        ", currency=" + currency +
        ", productGroups=" + productGroups +
        '}';
  }
}
