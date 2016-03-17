package com.secucard.connect.model.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.services.idresult.Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IdentResult extends SecuObject {
  public static final String OBJECT = "services.identresults";

  public static final String STATUS_OK = "ok";
  public static final String STATUS_FAILED = "failed";
  public static final String STATUS_PRELIMINARY_OK = "ok_preliminary";
  public static final String STATUS_PRELIMINARY_FAILED = "failed_preliminary";

  private IdentRequest request;

  private String status;

  @JsonProperty("person")
  List<Entity> entities = new ArrayList<>();

  private Date created;

  private Contract contract;

  @Override
  public String getObject() {
    return OBJECT;
  }

  public Contract getContract() {
    return contract;
  }

  public void setContract(Contract contract) {
    this.contract = contract;
  }

  public IdentRequest getRequest() {
    return request;
  }

  public void setRequest(IdentRequest request) {
    this.request = request;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<Entity> getEntities() {
    return entities;
  }

  public void setEntities(List<Entity> entities) {
    this.entities = entities;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  @Override
  public String toString() {
    return "IdentResult{" +
        "request=" + request +
        ", status='" + status + '\'' +
        ", entities=" + entities +
        ", created=" + created +
        "} " + super.toString();
  }
}
