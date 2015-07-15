package com.secucard.connect.product.services.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.services.model.idresult.Person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IdentResult extends SecuObject {
  public static final String STATUS_OK = "ok";
  public static final String STATUS_FAILED = "failed";
  public static final String STATUS_PRELIMINARY_OK = "ok_preliminary";
  public static final String STATUS_PRELIMINARY_FAILED = "failed_preliminary";

  private IdentRequest request;

  private String status;

  @JsonProperty("person")
  List<Person> persons = new ArrayList<>();

  private Date created;

  private Contract contract;


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

  public List<Person> getPersons() {
    return persons;
  }

  public void setPersons(List<Person> persons) {
    this.persons = persons;
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
        ", persons=" + persons +
        ", created=" + created +
        "} " + super.toString();
  }
}
