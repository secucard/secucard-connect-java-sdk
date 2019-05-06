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

  @JsonProperty("url_push")
  public String urlPush;  // The url for receiving push notifications

  public String getUrlSuccess() {
    return urlSuccess;
  }

  public void setUrlSuccess(String urlSuccess) {
    this.urlSuccess = urlSuccess;
  }

  public String getUrlFailure() {
    return urlFailure;
  }

  public void setUrlFailure(String urlFailure) {
    this.urlFailure = urlFailure;
  }

  public String getUrlIframe() {
    return iframeUrl;
  }

  public void setUrlIframe(String iframeUrl) {
    this.iframeUrl = iframeUrl;
  }

  public String getUrlPush() {
    return urlPush;
  }

  public void setUrlPush(String urlPush) {
    this.urlPush = urlPush;
  }

  @Override
  public String toString() {
    return "RedirectUrl{" + "urlSuccess='" + urlSuccess + '\'' + ", urlFailure='" + urlFailure + '\'' + ", iframeUrl='" + iframeUrl + '\''
        + ", url_push='" + urlPush + '\'' + '}';
  }

}
