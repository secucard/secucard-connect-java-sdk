package com.secucard.connect.model.smart;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.annotation.ProductInfo;

import java.util.List;

@ProductInfo(resourceId = "smart.idents")
public class Ident extends SecuObject {
  private String type;

  private String name;

  private int length;

  @JsonProperty("bin_prefix")
  private String binPrefix;

  private String value;


  public Ident(String type, String value) {
    this.type = type;
    this.value = value;
  }

  public Ident() {
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

  public String getBinPrefix() {
    return binPrefix;
  }

  public void setBinPrefix(String binPrefix) {
    this.binPrefix = binPrefix;
  }

  @Override
  public String toString() {
    return "Ident{" +
        "type='" + type + '\'' +
        ", name='" + name + '\'' +
        ", length=" + length +
        ", binPrefix='" + binPrefix + '\'' +
        ", value='" + value + '\'' +
        '}';
  }
}
