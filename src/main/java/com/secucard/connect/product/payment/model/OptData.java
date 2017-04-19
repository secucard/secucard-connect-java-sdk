package com.secucard.connect.product.payment.model;

public class OptData {
    public boolean has_accepted_disclaimer;
    public boolean hide_disclaimer;

    public boolean getDisclaimerAccepted() { return has_accepted_disclaimer; }
    public void setDisclaimerAccepted(boolean has_accepted_disclaimer) {
        this.has_accepted_disclaimer = has_accepted_disclaimer;
    }

    public boolean getHideDisclaimer() { return hide_disclaimer; }
    public void setHideDisclaimer(boolean hide_disclaimer) {
        this.hide_disclaimer = hide_disclaimer;
    }

    @Override
    public String toString() {
        return "OptData{" +
                "has_accepted_disclaimer='" + has_accepted_disclaimer + '\'' +
                ", hide_disclaimer='" + hide_disclaimer + '\'' +
                '}';
    }

}
