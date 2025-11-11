package com.example.DTOs;

public class EventSubmitResponse {
    private String message;
    public EventSubmitResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
