package com.example.DTOs;

public class BasicUserInfo {

    private String firstName;
    private String lastName;
    private String email;
    private String cdsId;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCdsId() {
        return cdsId;
    }

    public void setCdsId(String cdsId) {
        this.cdsId = cdsId;
    }
}
