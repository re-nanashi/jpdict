package com.nana.jpdict.exceptions;

public class ApiFetchException extends Exception {
    public ApiFetchException(String message) {
        super(message);
    }

    public ApiFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
