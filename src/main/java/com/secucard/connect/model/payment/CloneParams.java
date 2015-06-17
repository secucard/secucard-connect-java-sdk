package com.secucard.connect.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CloneParams {

  @JsonProperty("allow_transactions")
  private boolean allowTransactions;

  @JsonProperty("url_push")
  private String pushUrl;

  @JsonProperty("payment_data")
  private Data paymentData;

  private String project;

  public boolean isAllowTransactions() {
    return allowTransactions;
  }

  public void setAllowTransactions(boolean allowTransactions) {
    this.allowTransactions = allowTransactions;
  }

  public String getPushUrl() {
    return pushUrl;
  }

  public void setPushUrl(String pushUrl) {
    this.pushUrl = pushUrl;
  }

  public Data getPaymentData() {
    return paymentData;
  }

  public void setPaymentData(Data paymentData) {
    this.paymentData = paymentData;
  }

  public String getProject() {
    return project;
  }

  public void setProject(String project) {
    this.project = project;
  }


  @Override
  public String toString() {
    return "CloneData{" +
        "allowTransactions=" + allowTransactions +
        ", urlPush='" + pushUrl + '\'' +
        ", paymentData=" + paymentData +
        ", project='" + project + '\'' +
        '}';
  }
}
