package com.corelabsplus.el.utils;

public class Reservation {

    String userName, email, reference;

    public Reservation(String userName, String email, String reference) {
        this.userName = userName;
        this.reference = reference;
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
