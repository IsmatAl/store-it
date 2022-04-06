package com.example.storageit.rest.exception;

public class NoVacantPlace  extends RuntimeException{

    public NoVacantPlace(String errorMessage) {
        super(errorMessage);
    }
}
