package com.example.collectionRequirements.request;

public class RequestNotFound extends RuntimeException {
    public RequestNotFound(String message) {
        super(message);
    }
}
