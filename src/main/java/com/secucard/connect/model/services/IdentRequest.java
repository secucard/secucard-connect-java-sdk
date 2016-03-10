package com.secucard.connect.model.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.services.idrequest.Person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IdentRequest extends SecuObject {
  public static final String OBJECT = "services.identrequests";

  public static final String TYPE_PERSON = "person";
  public static final String TYPE_COMPANY = "company";
  public static final String STATUS_REQUESTED = "requested";
  public static final String STATUS_OK = "ok";
  public static final String STATUS_FAILED = "failed";
  public static final String PROVIDER_IDNOW = "idnow";
  public static final String PROVIDER_POSTIDENT = "post_ident";

  private String type;

  private String provider;

  private String status;

  private String owner;

  private Contract contract;

  @JsonProperty("owner_transaction_id")
  private String ownerTransactionId;

  @JsonProperty("person")
  private List<Person> persons = new ArrayList<>();

  private Date created;

  @Override
  public String getObject() {
    return OBJECT;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

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
        ", provider='" + provider + '\'' +
        ", status='" + status + '\'' +
        ", owner='" + owner + '\'' +
        ", contract=" + contract +
        ", ownerTransactionId='" + ownerTransactionId + '\'' +
        ", persons=" + persons +
        ", created=" + created +
        "} " + super.toString();
  }
}
