package com.secucard.connect.product.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.SecuObject;
import java.util.Currency;

public abstract class Transaction extends SecuObject {

  public static final String STATUS_ACCEPTED = "accepted"; // status for accepted debit transactions and finished prepay transactions
  public static final String STATUS_AUTHORIZED = "authorized"; // prepay transaction after creation , before payment arrives
  public static final String STATUS_DENIED = "denied"; // when scoring for debit transaction denies the payer
  public static final String STATUS_ISSUE = "issue"; // then ruecklastschrift happens, or some other issue type
  public static final String STATUS_VOID = "void"; // when transaction is cancelled by creator (it is not possible to cancel transactions any time, so the debit transaction is possible to cancel until it is cleared out)
  public static final String STATUS_ISSUE_RESOLVED = "issue_resolved"; // when issue for transaction is resolved
  public static final String STATUS_REFUND = "refund"; // special status, saying that transaction was paid back (for some reason)
  public static final String STATUS_INTERNAL_SERVER_STATUS = "internal_server_status"; // should not happen, but only when status would be empty, this status is used

  public static final String PAYMENT_ACTION_AUTHORIZATION = "authorization"; // Use the Authorization option to place a hold on the payer funds
  public static final String PAYMENT_ACTION_SALE = "sale"; // Direct payment (immediate debit of the funds from the buyer's funding source)

  protected Customer customer;

  protected Customer recipient;

  protected Basket[] basket;

  protected Experience experience;

  protected boolean accrual;

  protected Subscription subscription;

  @JsonProperty("redirect_url")
  protected RedirectUrl redirectUrl;

  @JsonProperty("opt_data")
  protected OptData optData;

  @JsonProperty("payment_action")
  protected String paymentAction = PAYMENT_ACTION_SALE;

  protected Contract contract;

  protected long amount;

  protected Currency currency;

  protected String purpose;

  @JsonProperty("order_id")
  protected String orderId;

  @JsonProperty("trans_id")
  protected String transId;

  protected String status;

  @JsonProperty("transaction_status")
  protected String transactionStatus;

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public Contract getContract() {
    return contract;
  }

  public void setContract(Contract contract) {
    this.contract = contract;
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

  public String getPurpose() {
    return purpose;
  }

  public void setPurpose(String purpose) {
    this.purpose = purpose;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public String getTransId() {
    return transId;
  }

  public void setTransId(String transId) {
    this.transId = transId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getTransactionStatus() {
    return transactionStatus;
  }

  public void setTransactionStatus(String transactionStatus) {
    this.transactionStatus = transactionStatus;
  }

  public Customer getRecipient() {
    return recipient;
  }

  public void setRecipient(Customer recipient) {
    this.recipient = recipient;
  }

  public Basket[] getBasket() {
    return basket;
  }

  public void setBasket(Basket[] basket) {
    this.basket = basket;
  }

  public Experience getExperience() {
    return experience;
  }

  public void setExperience(Experience experience) {
    this.experience = experience;
  }

  public boolean getAccrual() {
    return accrual;
  }

  public void setAccrual(boolean accrual) {
    this.accrual = accrual;
  }

  public Subscription getSubscription() {
    return subscription;
  }

  public void setSubscription(Subscription subscription) {
    this.subscription = subscription;
  }

  public RedirectUrl getRedirectUrl() {
    return redirectUrl;
  }

  public void setRedirectUrl(RedirectUrl redirectUrl) {
    this.redirectUrl = redirectUrl;
  }

  public OptData getOptData() {
    return optData;
  }

  public void setOptData(OptData optData) {
    this.optData = optData;
  }

  public String getPaymentAction() {
    return paymentAction;
  }

  public void setPaymentAction(String paymentAction) {
    this.paymentAction = paymentAction;
  }

}
