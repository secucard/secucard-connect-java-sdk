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
import com.secucard.connect.product.general.model.Contact;
import com.secucard.connect.product.general.model.Merchant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class IdentRequest extends SecuObject {

  public static final String TYPE_PERSON = "person";
  public static final String TYPE_COMPANY = "company";

  public static final String STATUS_REQUESTED = "requested";
  public static final String STATUS_OK = "ok";
  public static final String STATUS_FAILED = "failed";
  public static final String PROVIDER_IDNOW = "idnow";

  /**
   * @deprecated use {PROVIDER_POST_IDENT} instead
   */
  @Deprecated
  public static final String PROVIDER_POSTIDENT = "post_ident";

  public static final String PROVIDER_POST_IDENT = "post_ident";
  public static final String PROVIDER_BANK_IDENT = "bank_ident";

  private String type;

  private String provider;

  private String status;

  private String owner;

  private Contract contract;

  @JsonProperty("owner_transaction_id")
  private String ownerTransactionId;

  @JsonProperty("person")
  private List<Entity> entities = new ArrayList<>();

  private Date created;

  @JsonProperty("use_internal_contract")
  private String useInternalContract;

  private Merchant[] assignment;

  public String getUseInternalContract() {
    return useInternalContract;
  }

  public void setUseInternalContract(String useInternalContract) {
    this.useInternalContract = useInternalContract;
  }

  public Merchant[] getAssignment() {
    return assignment;
  }

  public void setAssignment(Merchant[] assignment) {
    this.assignment = assignment;
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

  public List<Entity> getEntities() {
    return entities;
  }

  public void setEntities(List<Entity> entities) {
    this.entities = entities;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  /**
   * Adding a entity.
   *
   * @param entity The entity to add.
   * @return True if added, false else.
   */
  @JsonIgnore
  public boolean addPerson(Entity entity) {
    return entities.add(entity);
  }

  @Override
  public String toString() {
    return "IdentRequest{" + "type='" + type + '\'' + ", status='" + status + '\'' + ", owner='" + owner + '\'' + ", contract=" + contract
        + ", provider=" + provider + ", ownerTransactionId='" + ownerTransactionId + '\'' + ", entities=" + entities + ", created=" + created
        + ", useInternalContract=" + useInternalContract + ", assignment=" + Arrays.toString(assignment) + ", " + super.toString() + '}';
  }

  public static class Entity {

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("redirect_url")
    private String redirectUrl;

    private String status;

    @JsonProperty("owner_transaction_id")
    private String ownerTransactionId;

    private Contact contact = new Contact();

    private String custom1;
    private String custom2;
    private String custom3;
    private String custom4;
    private String custom5;

    public Contact getContact() {
      return contact;
    }

    public void setContact(Contact contact) {
      this.contact = contact;
    }

    public String getTransactionId() {
      return transactionId;
    }

    public void setTransactionId(String transactionId) {
      this.transactionId = transactionId;
    }

    public String getRedirectUrl() {
      return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
      this.redirectUrl = redirectUrl;
    }

    public String getStatus() {
      return status;
    }

    public void setStatus(String status) {
      this.status = status;
    }

    public String getOwnerTransactionId() {
      return ownerTransactionId;
    }

    public void setOwnerTransactionId(String ownerTransactionId) {
      this.ownerTransactionId = ownerTransactionId;
    }

    public String getCustom1() {
      return custom1;
    }

    public void setCustom1(String custom1) {
      this.custom1 = custom1;
    }

    public String getCustom2() {
      return custom2;
    }

    public void setCustom2(String custom2) {
      this.custom2 = custom2;
    }

    public String getCustom3() {
      return custom3;
    }

    public void setCustom3(String custom3) {
      this.custom3 = custom3;
    }

    public String getCustom4() {
      return custom4;
    }

    public void setCustom4(String custom4) {
      this.custom4 = custom4;
    }

    public String getCustom5() {
      return custom5;
    }

    public void setCustom5(String custom5) {
      this.custom5 = custom5;
    }

    @Override
    public String toString() {
      return "Entity{" + "transactionId='" + transactionId + '\'' + ", redirectUrl='" + redirectUrl + '\'' + ", status='" + status + '\''
          + ", ownerTransactionId='" + ownerTransactionId + '\'' + ", contact=" + contact + ", custom1='" + custom1 + '\'' + ", custom2='" + custom2
          + '\'' + ", custom3='" + custom3 + '\'' + ", custom4='" + custom4 + '\'' + ", custom5='" + custom5 + '\'' + '}';
    }
  }
}
