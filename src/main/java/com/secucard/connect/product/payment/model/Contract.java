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

package com.secucard.connect.product.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Merchant;

import java.util.Date;

public class Contract extends SecuObject {
  private Date created;

  private Date updated;

  private Contract parent;

  @JsonProperty("allow_cloning")
  private boolean allowCloning;

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

  public Contract getParent() {
    return parent;
  }

  public void setParent(Contract parent) {
    this.parent = parent;
  }

  public boolean isAllowCloning() {
    return allowCloning;
  }

  public void setAllowCloning(boolean allowCloning) {
    this.allowCloning = allowCloning;
  }

  @Override
  public String toString() {
    return "Contract{" +
        ", created=" + created +
        ", updated=" + updated +
        ", parent=" + parent +
        ", allowCloning=" + allowCloning +
        "} " + super.toString();
  }


  public static class CloneParams {

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
}
