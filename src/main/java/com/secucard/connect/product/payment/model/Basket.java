package com.secucard.connect.product.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Holds Payment basket details.
 */
public class Basket {

  public static final String ITEM_TYPE_ARTICLE = "article";
  public static final String ITEM_TYPE_SHIPPING = "shipping";
  public static final String ITEM_TYPE_DONATION = "donation";
  public static final String ITEM_TYPE_STAKEHOLDER_PAYMENT = "stakeholder_payment";

  private String ean;
  private int tax;
  private int priceOne;
  private String articleNumber;
  private String name;
  private int total;
  private int quantity;
  @JsonProperty("item_type")
  private String itemType = ITEM_TYPE_ARTICLE;
  @JsonProperty("contract_id")
  private String contractId;
  private String model;
  private String apikey;

  public String getEan() {
    return ean;
  }

  public void setEan(String ean) {
    this.ean = ean;
  }

  public int getTax() {
    return tax;
  }

  public void setTax(int tax) {
    this.tax = tax;
  }

  public int getPriceOne() {
    return priceOne;
  }

  public void setPriceOne(int priceOne) {
    this.priceOne = priceOne;
  }

  public String getArticleNumber() {
    return articleNumber;
  }

  public void setArticleNumber(String articleNumber) {
    this.articleNumber = articleNumber;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public String getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  public String getContractId() {
    return contractId;
  }

  public void setContractId(String contractId) {
    this.contractId = contractId;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getApikey() {
    return apikey;
  }

  public void setApikey(String apikey) {
    this.apikey = apikey;
  }

  @Override
  public String toString() {
    return "Basket{" + "ean='" + ean + '\'' + ", tax='" + tax + '\'' + ", priceOne='" + priceOne + '\'' + ", articleNumber='" + articleNumber + '\''
        + ", name='" + name + '\'' + ", total='" + total + '\'' + ", quantity='" + quantity + '\'' + ", itemType='" + itemType + '\''
        + ", contractId='" + contractId + '\'' + ", model='" + model + ", apikey='" + apikey + '\'' + '}';
  }

}
