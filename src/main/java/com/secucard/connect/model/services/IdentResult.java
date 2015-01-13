package com.secucard.connect.model.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.annotation.ProductInfo;
import com.secucard.connect.model.services.idresult.Person;
import com.secucard.connect.model.services.idresult.Request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ProductInfo(resourceId = "services.identresults")
public class IdentResult extends SecuObject {
  private Request request;

  private String status;

  @JsonProperty("person")
  List<Person> persons = new ArrayList<>();

  private Date created;

  public Request getRequest() {
    return request;
  }

  public void setRequest(Request request) {
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
        '}';
  }

}
