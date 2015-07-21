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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.services.model.idrequest.Person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IdentRequest extends SecuObject {
  public static final String TYPE_PERSON = "person";
  public static final String TYPE_COMPANY = "company";
  public static final String STATUS_REQUESTED = "requested";
  public static final String STATUS_OK = "ok";
  public static final String STATUS_FAILED = "failed";

  private String type;

  private String status;

  private String owner;

  private Contract contract;

  @JsonProperty("owner_transaction_id")
  private String ownerTransactionId;

  @JsonProperty("person")
  private List<Person> persons = new ArrayList<>();

  private Date created;

  public Contract getContract() {
    return contract;
  }

  public void setContract(Contract contract) {
    this.contract = contract;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getOwnerTransactionId() {
    return ownerTransactionId;
  }

  public void setOwnerTransactionId(String ownerTransactionId) {
    this.ownerTransactionId = ownerTransactionId;
  }

  public List<Person> getPersons() {
    return persons;
  }

  public void setPersons(List<Person> persons) {
    this.persons = persons;
  }

  /**
   * Adding a person.
   *
   * @param person The person to add.
   * @return True if added, false else.
   */
  @JsonIgnore
  public boolean addPerson(Person person) {
    return persons.add(person);
  }

  @Override
  public String toString() {
    return "IdentRequest{" +
        "type='" + type + '\'' +
        ", status='" + status + '\'' +
        ", owner='" + owner + '\'' +
        ", contract=" + contract +
        ", ownerTransactionId='" + ownerTransactionId + '\'' +
        ", persons=" + persons +
        ", created=" + created +
        "} " + super.toString();
  }
}
