package com.accenture.jpdict.exceptions;

public class WordExtractionException extends RuntimeException {
    public WordExtractionException(String message) {
        super(message);
    }

    public WordExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}