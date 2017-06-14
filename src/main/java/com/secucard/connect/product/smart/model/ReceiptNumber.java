package com.secucard.connect.product.smart.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReceiptNumber {

    @JsonProperty("receipt_number")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String receiptNumber;


    public ReceiptNumber(String receiptnumber) {
        this.receiptNumber = receiptnumber;
    }

    @Override
    public String toString() {
        return  "receipt_number=" + receiptNumber;
    }
}
