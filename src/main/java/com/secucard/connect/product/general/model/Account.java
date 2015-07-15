package com.secucard.connect.product.general.model;

import com.secucard.connect.product.common.model.SecuObject;

import java.util.List;

public class Account extends SecuObject {
  private String username;

  private String password;

  private String role;

  private Contact contact;


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
