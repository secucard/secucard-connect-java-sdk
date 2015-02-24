package com.secucard.connect.model.general.contacts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;

import java.io.Serializable;

/**
 * Created by Steffen Schröder on 23.02.15.
 */
public class Address implements Serializable {

    @JsonProperty
    private String street;

    @JsonProperty("street_number")
    private String streetNumber;

    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty
    private String city;

    @JsonProperty
    private String country;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
