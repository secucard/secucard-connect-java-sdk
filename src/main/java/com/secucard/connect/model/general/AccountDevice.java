package com.secucard.connect.model.general;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secucard.connect.model.SecuObject;

public class AccountDevice extends SecuObject {
  public static final String OBJECT = "general.accountdevices";

  // todo: add proerties

  @JsonIgnore
  private Boolean checkin;

  public Boolean getCheckin() {
    return checkin;
  }

  public void setCheckin(Boolean checkin) {
    this.checkin = checkin;
  }

  @Override
  public String getObject() {
    return OBJECT;
  }
}
