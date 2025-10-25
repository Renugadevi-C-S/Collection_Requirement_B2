package com.example.DTOs;

public class LogInResponse {

    private String message;
    private String cdsId;
    private String role;
    private String firstName;
    private String lastName;

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

    public LogInResponse(String message, String csdId, String role) {
        this.message = message;
        this.cdsId = csdId;
        this.role = role;
    }

    public LogInResponse() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCdsId() {
        return cdsId;
    }

    public void setCdsId(String csdId) {
        this.cdsId = csdId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


}
