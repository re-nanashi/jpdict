package com.accenture.jpdict.exceptions;

public class ApiFetchException extends RuntimeException {
    public ApiFetchException(String message) {
        super(message);
    }

    public ApiFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
