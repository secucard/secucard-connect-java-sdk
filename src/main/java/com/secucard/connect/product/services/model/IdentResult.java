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
