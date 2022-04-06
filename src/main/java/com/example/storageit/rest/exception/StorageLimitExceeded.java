package com.example.storageit.rest.exception;

public class StorageLimitExceeded extends RuntimeException {
    public StorageLimitExceeded(String errorMessage) {
        super(errorMessage);
    }
}
