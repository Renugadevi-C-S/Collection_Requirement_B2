package com.example.DTOs;

public class ApprovalSubmissionResponse {

    private String message;

    public ApprovalSubmissionResponse(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
