package com.example.DTOs;

public class LogInResponse {

    private String message;
    private String csdId;
    private String role;
    private String fristName;
    private String lastName;

    public String getFristName() {
        return fristName;
    }

    public void setFristName(String fristName) {
        this.fristName = fristName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LogInResponse(String message, String csdId, String role) {
        this.message = message;
        this.csdId = csdId;
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

    public String getCsdId() {
        return csdId;
    }

    public void setCsdId(String csdId) {
        this.csdId = csdId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


}
