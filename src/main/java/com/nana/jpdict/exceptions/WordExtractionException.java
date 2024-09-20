package com.nana.jpdict.exceptions;

public class WordExtractionException extends Exception {
    public WordExtractionException(String message) {
        super(message);
    }

    public WordExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}