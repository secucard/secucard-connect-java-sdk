package com.secucard.connect.product.smart.model;

public class PrepaidSale {

    private int id;
    private String status;

    public int getId() { return id; }
    public String getStatus() { return status; }

    public void setId(int id) { this.id = id; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "PrepaidSale{" + "id='" + id + "'" + ", status='" + status + "'}";
    }
}
