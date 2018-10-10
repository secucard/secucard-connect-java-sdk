package com.secucard.connect.product.payment.model;

// Experience Data Model class
public class Experience {

  public int positiv; // The number of positive customer experiences
  public int negativ; // The number of negative customer experiences (open orders)

  public int getPositiv() {
    return positiv;
  }

  public void setPositiv(int positiv) {
    this.positiv = positiv;
  }

  public int getNegativ() {
    return negativ;
  }

  public void setNegativ(int negativ) {
    this.negativ = negativ;
  }

  @Override
  public String toString() {
    return "Experience{" + "positiv='" + positiv + '\'' + ", negativ='" + negativ + '\'' + '}';
  }

}