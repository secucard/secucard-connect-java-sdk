package com.secucard.connect.product.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Contact;
import com.secucard.connect.product.general.model.Merchant;
import java.util.Currency;
import java.util.Date;

public class Transactions extends SecuObject {

  protected Merchant merchant;

  protected Merchant platform;

  @JsonProperty("store_name")
  protected String storeName;

  @JsonProperty("trans_id")
  protected int transId;

  @JsonProperty("product_id")
  protected int productId;

  protected String product;

  @JsonProperty("product_raw")
  protected String productRaw;

  @JsonProperty("zahlungsmittel_id")
  protected int zahlungsmittelId;

  @JsonProperty("contract_id")
  protected int contractId;

  protected long amount;

  protected Currency currency;

  protected Date created;

  protected Date updated;

  protected String description;

  @JsonProperty("description_raw")
  protected String descriptionRaw;

  protected int status;

  @JsonProperty("status_text")
  protected String statusText;

  protected TransactionsDetails details;

  protected Contact customer;

  @JsonProperty("incoming_payment_date")
  protected Date incomingPaymentDate;

  protected int tid;

  @JsonProperty("payment_data")
  protected String paymentData;

  @JsonProperty("transaction_hash")
  protected String transactionHash;

  protected boolean accrual;

  @JsonProperty("reference_id")
  protected String referenceId;

  @JsonProperty("payout_date")
  protected Date payoutDate;

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public int getTransId() {
    return transId;
  }

  public void setTransId(int transId) {
    this.transId = transId;
  }

  public int getProductId() {
    return productId;
  }

  public void setProductId(int productId) {
    this.productId = productId;
  }

  public String getProduct() {
    return product;
  }

  public void setProduct(String product) {
    this.product = product;
  }

  public String getProductRaw() {
    return productRaw;
  }

  public void setProductRaw(String productRaw) {
    this.productRaw = productRaw;
  }

  public int getZahlungsmittelId() {
    return zahlungsmittelId;
  }

  public void setZahlungsmittelId(int zahlungsmittelId) {
    this.zahlungsmittelId = zahlungsmittelId;
  }

  public int getContractId() {
    return contractId;
  }

  public void setContractId(int contractId) {
    this.contractId = contractId;
  }

  public long getAmount() {
    return amount;
  }

  public void setAmount(long amount) {
    this.amount = amount;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescriptionRaw() {
    return descriptionRaw;
  }

  public void setDescriptionRaw(String descriptionRaw) {
    this.descriptionRaw = descriptionRaw;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getStatusText() {
    return statusText;
  }

  public void setStatusText(String statusText) {
    this.statusText = statusText;
  }

  public TransactionsDetails getDetails() {
    return details;
  }

  public void setDetails(TransactionsDetails details) {
    this.details = details;
  }

  public Contact getCustomer() {
    return customer;
  }

  public void setCustomer(Contact customer) {
    this.customer = customer;
  }

  public Date getIncomingPaymentDate() {
    return incomingPaymentDate;
  }

  public void setIncomingPaymentDate(Date incomingPaymentDate) {
    this.incomingPaymentDate = incomingPaymentDate;
  }

  public Date getPayoutDate() {
    return payoutDate;
  }

  public void setPayoutDate(Date payoutDate) {
    this.payoutDate = payoutDate;
  }

  public Merchant getPlatform() {
    return platform;
  }

  public void setPlatform(Merchant platform) {
    this.platform = platform;
  }

  public String getStoreName() {
    return storeName;
  }

  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }

  public int getTid() {
    return tid;
  }

  public void setTid(int tid) {
    this.tid = tid;
  }

  public String getPaymentData() {
    return paymentData;
  }

  public void setPaymentData(String paymentData) {
    this.paymentData = paymentData;
  }

  public String getTransactionHash() {
    return transactionHash;
  }

  public void setTransactionHash(String transactionHash) {
    this.transactionHash = transactionHash;
  }

  public boolean isAccrual() {
    return accrual;
  }

  public void setAccrual(boolean accrual) {
    this.accrual = accrual;
  }

  public String getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }

  @Override
  public String toString() {
    return "Transactions{" + "transId='" + getTransId() + '\'' + ", productId='" + getProductId() + '\'' + ", product='" + getProduct() + '\''
        + ", productRaw='" + getProductRaw() + '\'' + ", zahlungsmittelId='" + getZahlungsmittelId() + '\'' + ", contractId='" + getContractId()
        + '\'' + ", amount='" + getAmount() + '\'' + ", currency='" + getCurrency() + '\'' + ", created='" + getCreated() + '\'' + ", updated='"
        + getUpdated() + '\'' + ", description='" + getDescription() + '\'' + ", descriptionRaw='" + getDescriptionRaw() + '\'' + ", status='"
        + getStatus() + '\'' + ", statusText='" + getStatusText() + '\'' + ", details='" + getDetails() + '\'' + ", customer='" + getCustomer() + '\''
        + ", incomingPaymentDate='" + getIncomingPaymentDate() + '\'' + ", payoutDate='" + getPayoutDate() + '\'' + ", platform='" + getPlatform()
        + '\'' + ", storeName='" + getStoreName() + '\'' + ", tid='" + getTid() + '\'' + ", paymentData='" + getPaymentData() + '\''
        + ", transactionHash='" + getTransactionHash() + '\'' + ", accrual='" + (isAccrual() == Boolean.TRUE ? 1 : 0) + '\'' + ", referenceId='"
        + getReferenceId() + '\'' + ", " + super.toString() + '}';
  }
}
