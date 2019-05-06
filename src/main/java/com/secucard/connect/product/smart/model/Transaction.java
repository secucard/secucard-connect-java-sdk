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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.SecuObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transaction extends SecuObject {

  public static final String STATUS_CREATED = "created";
  public static final String STATUS_CANCELED = "canceled";
  public static final String STATUS_FINISHED = "finished";
  public static final String STATUS_ABORTED = "aborted";
  public static final String STATUS_FAILED = "failed";
  public static final String STATUS_TIMEOUT = "timeout";
  public static final String STATUS_OK = "ok";

  @JsonProperty("basket_info")
  private BasketInfo basketInfo;

  @JsonProperty("device_source")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Device deviceSource;

  @JsonProperty("device_destination")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Device targetDevice;

  private String status;

  private Date created;

  private Date updated;

  private List<Ident> idents;

  private Basket basket;

  private String merchantRef;

  private String transactionRef;

  @JsonProperty("payment_method")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String paymentMethod;

  @JsonProperty("receipt")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<ReceiptLine> receiptLines;

  @JsonProperty("receipt_number")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String receiptNumber;

  @JsonProperty("receipt_merchant")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<ReceiptLine> receiptLinesMerchant;

  @JsonProperty("receipt_merchant_print")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Boolean receiptMerchantPrint;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String error;

  @JsonProperty("prepaid_sales")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<PrepaidSale> prepaidSales;

  public Transaction() {
  }

  public Transaction(BasketInfo basketInfo, Basket basket, List<Ident> idents) {
    this.basketInfo = basketInfo;
    this.basket = basket;
    this.idents = idents;
  }

  public BasketInfo getBasketInfo() { return basketInfo; }
  public Device getDeviceSource() { return deviceSource; }
  public Device getTargetDevice() { return targetDevice; }
  public String getStatus() { return status; }
  public Date getCreated() { return created; }
  public Date getUpdated() { return updated; }
  public List<Ident> getIdents() { return idents; }
  public Basket getBasket() { return basket; }
  public String getMerchantRef() { return merchantRef; }
  public String getTransactionRef() { return transactionRef; }
  public String getPaymentMethod() { return paymentMethod; }
  public List<ReceiptLine> getReceiptLines() {return receiptLines; }
  public String getReceiptNumber() {return receiptNumber; }
  public List<ReceiptLine> getReceiptLinesMerchant() { return receiptLinesMerchant; }
  public Boolean getReceiptMerchantPrint() { return receiptMerchantPrint; }
  public String getError() { return error; }
  public List<PrepaidSale> getPrepaidSales() {
    return prepaidSales;
  }

  public void setBasketInfo(BasketInfo basketInfo) { this.basketInfo = basketInfo; }
  public void setDeviceSource(Device deviceSource) { this.deviceSource = deviceSource; }
  public void setTargetDevice(Device targetDevice) { this.targetDevice = targetDevice; }
  public void setStatus(String status) { this.status = status; }
  public void setCreated(Date created) { this.created = created; }
  public void setUpdated(Date updated) { this.updated = updated; }
  public void setIdents(List<Ident> idents) { this.idents = idents; }
  public void setBasket(Basket basket) { this.basket = basket; }
  public void setMerchantRef(String merchantRef) { this.merchantRef = merchantRef; }
  public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
  public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
  public void setReceiptLines(List<ReceiptLine> receiptLines) { this.receiptLines = receiptLines; }
  public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
  public void setReceiptLinesMerchant(List<ReceiptLine> receiptLinesMerchant) { this.receiptLinesMerchant = receiptLinesMerchant; }
  public void setReceiptMerchantPrint(Boolean receiptMerchantPrint) { this.receiptMerchantPrint = receiptMerchantPrint; }
  public void setError(String error) { this.error = error; }
  public void setPrepaidSales(List<PrepaidSale> prepaidSales) { this.prepaidSales = prepaidSales; }

  public void addPrepaidSale(PrepaidSale prepaidSale) { prepaidSales.add(prepaidSale); }

  @Override
  public String toString() {
    return "Transaction{" + "basketInfo=" + basketInfo + ", deviceSource=" + deviceSource + ", targetDevice=" + targetDevice + ", status='" + status
        + '\'' + ", created=" + created + ", updated=" + updated + ", idents=" + idents + ", basket=" + basket + ", merchantRef='" + merchantRef
        + '\'' + ", transactionRef='" + transactionRef + '\'' + ", paymentMethod='" + paymentMethod + '\'' + ", receiptLines=" + receiptLines
        + ", receiptNumber='" + receiptNumber + '\'' + ", error='" + error + '\'' + ", " + super.toString() + ", prepaidSales='" + prepaidSales + "'}";
  }
}
