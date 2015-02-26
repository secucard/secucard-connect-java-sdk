package com.secucard.connect.model.smart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secucard.connect.model.CurrencyHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Product extends CurrencyHolder {
  protected String id;

  private String articleNumber;

  private String ean;

  protected String desc;

  private BigDecimal quantity;

  private BigDecimal priceOne;

  private BigDecimal tax;

  private List<ProductGroup> productGroups = new ArrayList<>();

  public Product() {
  }

  public Product(String id, String articleNumber, String ean, String desc, BigDecimal quantity, BigDecimal priceOne,
                 BigDecimal tax) {
    this.id = id;
    this.articleNumber = articleNumber;
    this.ean = ean;
    this.desc = desc;
    this.quantity = quantity;
    this.priceOne = priceOne;
    this.tax = tax;
  }

  public Product(String id, String articleNumber, String ean, String desc, String quantity, String priceOne, String tax) {
    this.id = id;
    this.articleNumber = articleNumber;
    this.ean = ean;
    this.desc = desc;
    setQuantity(quantity);
    setPriceOne(priceOne);
    setTax(tax);
  }

  public Product(String id, String articleNumber, String ean, String desc, BigDecimal quantity, BigDecimal priceOne,
                 BigDecimal tax, List<ProductGroup> productGroups) {
    this.id = id;
    this.articleNumber = articleNumber;
    this.ean = ean;
    this.desc = desc;
    this.quantity = quantity;
    this.priceOne = priceOne;
    this.tax = tax;
    this.productGroups = productGroups;
  }

  public Product(String id, String articleNumber, String ean, String desc, String quantity, String priceOne, String tax,
                 List<ProductGroup> productGroups) {
    this.id = id;
    this.articleNumber = articleNumber;
    this.ean = ean;
    this.desc = desc;
    setQuantity(quantity);
    setPriceOne(priceOne);
    setTax(tax);
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

  public BigDecimal getQuantityABigDecimal() {
    return quantity;
  }

  public String getQuantity() {
    return getValue(quantity);
  }

  @JsonIgnore
  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
  }

  public void setQuantity(String quantity) {
    this.quantity = getValue(quantity);
  }

  public BigDecimal getPriceOneAsBigDecimal() {
    return priceOne;
  }

  public String getPriceOne() {
    return getValue(priceOne);
  }

  @JsonIgnore
  public void setPriceOne(BigDecimal priceOne) {
    this.priceOne = priceOne;
  }

  public void setPriceOne(String priceOne) {
    this.priceOne = getValue(priceOne);
  }

  public BigDecimal getTaxAsBigDecimal() {
    return tax;
  }

  public String getTax() {
    return getValue(tax);
  }

  @JsonIgnore
  public void setTax(BigDecimal tax) {
    this.tax = tax;
  }

  public void setTax(String tax) {
    this.tax = getValue(tax);
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
