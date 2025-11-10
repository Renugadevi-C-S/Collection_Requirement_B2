package com.example.region;

public class RegionNotFound extends RuntimeException {
    public RegionNotFound(String message) {
        super(message);
    }
}
