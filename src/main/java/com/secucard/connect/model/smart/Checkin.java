package com.secucard.connect.model.smart;

import com.secucard.connect.SecuException;
import com.secucard.connect.model.MediaResource;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.accounts.Account;

import java.net.MalformedURLException;
import java.util.Date;

public class Checkin extends SecuObject {
  public static final String OBJECT = "smart.checkins";

  private String customerName;

  private String pictureUrl;

  private Date created;

  private Account account;

  private MediaResource picture;


  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getPictureUrl() {
    return pictureUrl;
  }

  public void setPictureUrl(String pictureUrl) {
    this.pictureUrl = pictureUrl;
    if (pictureUrl != null) {
      try {
        this.picture = new MediaResource(pictureUrl);
      } catch (MalformedURLException e) {
        throw new SecuException("Invalid checkin picture URL");
      }
    }
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public MediaResource getPicture() {
    return picture;
  }

  @Override
  public String getObject() {
    return OBJECT;
  }


  @Override
  public String toString() {
    return "Checkin{" +
        "customerName='" + customerName + '\'' +
        ", pictureUrl='" + pictureUrl + '\'' +
        ", created=" + created +
        "} " + super.toString();
  }
}
