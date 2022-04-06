package com.example.storageit.rest.exception;

public class NotAllowedException extends RuntimeException {
    public NotAllowedException(String errorMessage) {
        super(errorMessage);
    }
}
