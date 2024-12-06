package com.example.demo.exception;

public class UnderageUserException extends RuntimeException {
    public UnderageUserException(String message) {
        super(message);
    }
}
