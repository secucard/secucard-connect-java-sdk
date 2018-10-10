package com.secucard.connect.product.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

// Subscription Data Model class
public class RedirectUrl {

  @JsonProperty("url_success")
  public String urlSuccess;  // The url for redirect the customer back to the shop after a successful payment checkout

  @JsonProperty("url_failure")
  public String urlFailure;  // The url for redirect the customer back to the shop after a failure (or on cancel) on the payment checkout page

  @JsonProperty("iframe_url")
  public String iframeUrl;  // The url for redirect the customer to the payment checkout page

  public String getUrlSuccess() {
    return urlSuccess;
  }

  public void setUrlSuccess(String url_success) {
    this.urlSuccess = url_success;
  }

  public String getUrlFailure() {
    return urlFailure;
  }

  public void setUrlFailure(String url_failure) {
    this.urlFailure = url_failure;
  }

  public String getUrlIframe() {
    return iframeUrl;
  }

  public void setUrlIframe(String iframe_url) {
    this.iframeUrl = iframe_url;
  }

  @Override
  public String toString() {
    return "RedirectUrl{" + "urlSuccess='" + urlSuccess + '\'' + "urlFailure='" + urlFailure + '\''
        + "iframeUrl='" + iframeUrl + '\'' + '}';
  }

}
