package com.secucard.connect.model.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.annotation.ProductInfo;
import com.secucard.connect.model.services.idrequest.Person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ProductInfo(resourceId = "services.identrequests")
public class IdentRequest extends SecuObject {
  public static final String TYPE_PERSON = "person";
  public static final String TYPE_COMPANY = "company";

  private String type;

  private String status;

  private String owner;

  @JsonProperty("owner_transaction_id")
  private String ownerTransactionId;

  @JsonProperty("person")
  private List<Person> persons = new ArrayList<>();

  private Date created;

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

}