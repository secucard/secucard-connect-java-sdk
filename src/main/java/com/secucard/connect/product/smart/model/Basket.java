package com.secucard.connect.product.smart.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Basket {

  private List<Product> products = new ArrayList<>();

  private List<Text> texts = new ArrayList<>();

  public List<Text> getTexts() {
    return texts;
  }

  public void setTexts(List<Text> texts) {
    this.texts = texts;
  }

  public List<Product> getProducts() {
    return products;
  }

  public void setProducts(List<Product> products) {
    this.products = products;
  }

  public void addProduct(Product product){
    products.add(product);
  }

  public void addProduct(Text text){
    texts.add(text);
  }

  @JsonIgnore
  /**
   * Returns a mixed list of products followed by belonging texts.
   */
  public List getProductsWithText() {
    List merged = new ArrayList<>();
    for (Product product : products) {
      int id = product.getId();
      merged.add(product);
      for (Text text : texts) {
        if (text.getParentId().equals(id)) {
          merged.add(text);
        }
      }
    }
    return merged;
  }

  @Override
  public String toString() {
    return "Basket{" +
        "products=" + products +
        ", texts=" + texts +
        '}';
  }
}
