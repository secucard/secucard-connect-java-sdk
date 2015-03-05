package com.secucard.connect.model.smart;

import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.loyalty.Customer;
import com.secucard.connect.model.loyalty.MerchantCard;

import java.util.List;

public class Ident extends SecuObject {
  public static final String OBJECT = "smart.idents";

  private String type;

  private String name;

  private int length;

  private String prefix;

  private String value;

  private MerchantCard merchantCard;

  private boolean valid;


  public Ident(String type, String value) {
    this.type = type;
    this.value = value;
  }

  public Ident() {
  }

  @Override
  public String getObject() {
    return OBJECT;
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
        ", binPrefix='" + prefix + '\'' +
        ", value='" + value + '\'' +
        '}';
  }
}
