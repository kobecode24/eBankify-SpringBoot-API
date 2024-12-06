package org.system.bank.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class LoanEligibilityException extends RuntimeException {
    private final List<String> reasons;

    public LoanEligibilityException(String message) {
        super(message);
        this.reasons = List.of(message);
    }

    public LoanEligibilityException(String message, List<String> reasons) {
        super(message);
        this.reasons = reasons;
    }

}