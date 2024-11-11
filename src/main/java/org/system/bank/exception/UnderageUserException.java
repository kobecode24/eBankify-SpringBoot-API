package org.system.bank.exception;

public class UnderageUserException extends RuntimeException {
    public UnderageUserException(String message) {
        super(message);
    }
}
