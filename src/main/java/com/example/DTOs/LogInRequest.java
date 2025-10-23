package com.example.DTOs;

public class LogInRequest {
    private String cdsId;
    private String password;

    public String getCdsId() {
        return cdsId;
    }

    public void setCdsId(String cdsId) {
        this.cdsId = cdsId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
