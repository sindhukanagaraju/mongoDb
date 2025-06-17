package com.microservice.commonservice.exception;

public class AuthorizationDeniedException extends RuntimeException {
    public AuthorizationDeniedException(final String message) {
        super(message);
    }
}
