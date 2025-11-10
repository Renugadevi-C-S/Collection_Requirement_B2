package com.example.department;

public class DepartmentAlreadyExist extends RuntimeException {
    public DepartmentAlreadyExist(String message) {
        super(message);
    }
}
