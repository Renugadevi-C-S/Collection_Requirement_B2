package com.example.collectionRequirements.request;

public class RequestNotApproved extends RuntimeException {
    public RequestNotApproved(String message) {
        super(message);
    }
}
