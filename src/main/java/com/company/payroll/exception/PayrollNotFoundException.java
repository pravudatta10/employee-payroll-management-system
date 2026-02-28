package com.company.payroll.exception;

public class PayrollNotFoundException extends RuntimeException {
    public PayrollNotFoundException(String message) {
        super(message);
    }

    public PayrollNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
