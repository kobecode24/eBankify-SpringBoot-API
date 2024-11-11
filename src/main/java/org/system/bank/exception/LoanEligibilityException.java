package org.system.bank.exception;

public class LoanEligibilityException extends RuntimeException {
    public LoanEligibilityException(String message) {
        super(message);
    }

    public LoanEligibilityException(String message, Throwable cause) {
        super(message, cause);
    }
}