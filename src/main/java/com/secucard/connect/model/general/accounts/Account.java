package com.secucard.connect.model.general.accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.annotation.ProductInfo;
import com.secucard.connect.model.general.contacts.Contact;

import java.util.List;

@ProductInfo(resourceId = "general.accounts")
public class Account extends SecuObject {

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    @JsonProperty
    private String role;

    @JsonProperty
    private Contact contact;

    @JsonProperty
    private List<Assignment> assignment;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public List<Assignment> getAssignment() {
        return assignment;
    }

    public void setAssignment(List<Assignment> assignment) {
        this.assignment = assignment;
    }
}
