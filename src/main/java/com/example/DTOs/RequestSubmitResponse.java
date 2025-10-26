package com.example.DTOs;

public class RequestSubmitResponse {

    private String message;

    public RequestSubmitResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
