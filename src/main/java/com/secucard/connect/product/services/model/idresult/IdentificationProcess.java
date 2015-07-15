package com.secucard.connect.product.services.model.idresult;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class IdentificationProcess {
  private String status;

  @JsonProperty("identificationtime")
  private Date identificationTime;

  @JsonProperty("transactionnumber")
  private String transactionNumber;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Date getIdentificationTime() {
    return identificationTime;
  }

  public void setIdentificationTime(Date identificationTime) {
    this.identificationTime = identificationTime;
  }

  public String getTransactionNumber() {
    return transactionNumber;
  }

  public void setTransactionNumber(String transactionNumber) {
    this.transactionNumber = transactionNumber;
  }

  @Override
  public String toString() {
    return "IdentificationProcess{" +
        "status='" + status + '\'' +
        ", identificationTime=" + identificationTime +
        ", transactionNumber='" + transactionNumber + '\'' +
        '}';
  }
}
