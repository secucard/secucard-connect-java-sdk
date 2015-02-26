package com.secucard.connect.model.general.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.general.components.Assign;

import java.io.Serializable;

/**
 * Created by Steffen Schröder on 24.02.15.
 */
public class Assignment implements Serializable {

    @JsonProperty
    private String created;

    @JsonProperty
    private String type;

    @JsonProperty
    private boolean owner;

    @JsonProperty
    private Assign assign;  // todo: assign ist Typ general.Merchant oder general.AccountDevice nicht Assign

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public Assign getAssign() {
        return assign;
    }

    public void setAssign(Assign assign) {
        this.assign = assign;
    }
}
