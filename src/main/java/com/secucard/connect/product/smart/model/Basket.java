package com.secucard.connect.product.smart.model;

import java.util.ArrayList;
import java.util.List;

public class Basket {

  private List<Product> products = new ArrayList<>();

  public List<Product> getProducts() {
    return products;
  }

  public void setProducts(List<Product> products) {
    this.products = products;
  }

  public void addProduct(Product product) {
    products.add(product);
  }

  @Override
  public String toString() {
    return "Basket{" + "products=" + products + '}';
  }
}
