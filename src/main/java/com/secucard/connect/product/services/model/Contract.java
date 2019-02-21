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
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Merchant;
import java.util.Date;

public class Contract extends SecuObject {

  @JsonProperty("redirect_url_success")
  private String redirectUrlSuccess;

  @JsonProperty("redirect_url_failed")
  private String redirectUrlFailed;

  @JsonProperty("push_url")
  private String pushUrl;

  private Date created;

  private Merchant merchant;

  public String getRedirectUrlSuccess() {
    return redirectUrlSuccess;
  }

  public void setRedirectUrlSuccess(String redirectUrlSuccess) { this.redirectUrlSuccess = redirectUrlSuccess; }

  public String getRedirectUrlFailed() {
    return redirectUrlFailed;
  }

  public void setRedirectUrlFailed(String redirectUrlFailed) { this.redirectUrlFailed = redirectUrlFailed; }

  public String getPushUrl() {
    return pushUrl;
  }

  public void setPushUrl(String pushUrl) { this.pushUrl = pushUrl; }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) { this.created = created; }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) { this.merchant = merchant; }

  @Override
  public String toString() {
    return "Contract{" + "redirectUrlSuccess=" + redirectUrlSuccess + ", redirectUrlFailed=" + redirectUrlFailed + ", pushUrl=" + pushUrl
        + ", created=" + created + ", merchant=" + merchant + ", " + super.toString() + '}';
  }
}