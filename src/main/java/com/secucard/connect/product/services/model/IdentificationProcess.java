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

package com.secucard.connect.product.services.model;

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
