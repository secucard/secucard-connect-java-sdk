/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
