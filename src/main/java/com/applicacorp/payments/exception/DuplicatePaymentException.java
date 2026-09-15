package com.applicacorp.payments.exception;

public class DuplicatePaymentException extends RuntimeException {

    public DuplicatePaymentException(String paymentId) {
        super("Payment with id " + paymentId + " already exists");
    }
}
