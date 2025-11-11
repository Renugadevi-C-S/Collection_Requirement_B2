package com.example.collectionRequirements.request;

public class RequestAlreadyLinked extends RuntimeException {
    public RequestAlreadyLinked(String message) {
        super(message);
    }
}
