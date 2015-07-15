package com.secucard.connect.product.general.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secucard.connect.product.common.model.SecuObject;

public class AccountDevice extends SecuObject {

  // todo: add properties

  @JsonIgnore
  private Boolean checkin;

  public Boolean getCheckin() {
    return checkin;
  }

  public void setCheckin(Boolean checkin) {
    this.checkin = checkin;
  }

}
