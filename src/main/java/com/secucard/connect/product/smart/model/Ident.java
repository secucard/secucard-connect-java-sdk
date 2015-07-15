package com.secucard.connect.product.smart.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.loyalty.model.Customer;
import com.secucard.connect.product.loyalty.model.MerchantCard;

import java.util.List;

public class Ident extends SecuObject {
  private String type;

  private String name;

  private int length;

  private String prefix;

  private String value;

  private Customer customer;

  @JsonProperty("merchantcard")
  private MerchantCard merchantCard;

  private boolean valid;

  public Ident() {
  }

  public Ident(Ident ident) {
    this.type = ident.getType();
    this.name = ident.getName();
    this.length = ident.getLength();
    this.prefix = ident.getPrefix();
    this.value = ident.getValue();
    this.merchantCard = ident.getMerchantCard();
    this.valid = ident.isValid();
  }

  public Ident(String type, String value) {
    this.type = type;
    this.value = value;
  }



  /**
   * Selects a indent of a given id from a list of idents.
   *
   * @param id     The ident id.
   * @param idents The idents to query.
   * @return The found ident or null.
   */
  public static Ident find(String id, List<Ident> idents) {
    if (idents != null) {
      for (Ident ident : idents) {
        if (ident.getId().equals(id)) {
          return ident;
        }
      }
    }
    return null;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public MerchantCard getMerchantCard() {
    return merchantCard;
  }

  public void setMerchantCard(MerchantCard merchantCard) {
    this.merchantCard = merchantCard;
  }


  @Override
  public String toString() {
    return "Ident{" +
        "type='" + type + '\'' +
        ", name='" + name + '\'' +
        ", length=" + length +
        ", prefix='" + prefix + '\'' +
        ", value='" + value + '\'' +
        ", customer=" + customer +
        ", merchantCard=" + merchantCard +
        ", valid=" + valid +
        "} " + super.toString();
  }
}
