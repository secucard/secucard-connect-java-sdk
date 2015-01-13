package com.secucard.connect.model.services.idresult;

public class UserData {
  private Value birthday;

  private Value firstname;

  private Value lastname;

  private Address address;

  private Value birthplace;

  private Value nationality;


  public Value getBirthday() {
    return birthday;
  }

  public void setBirthday(Value birthday) {
    this.birthday = birthday;
  }

  public Value getFirstname() {
    return firstname;
  }

  public void setFirstname(Value firstname) {
    this.firstname = firstname;
  }

  public Value getLastname() {
    return lastname;
  }

  public void setLastname(Value lastname) {
    this.lastname = lastname;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public Value getBirthplace() {
    return birthplace;
  }

  public void setBirthplace(Value birthplace) {
    this.birthplace = birthplace;
  }

  public Value getNationality() {
    return nationality;
  }

  public void setNationality(Value nationality) {
    this.nationality = nationality;
  }

  @Override
  public String toString() {
    return "UserData{" +
        "birthday=" + birthday +
        ", firstname=" + firstname +
        ", lastname=" + lastname +
        ", address=" + address +
        ", birthplace=" + birthplace +
        ", nationality=" + nationality +
        '}';
  }
}
