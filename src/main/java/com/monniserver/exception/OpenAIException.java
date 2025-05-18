package com.monniserver.exception;

public class OpenAIException extends RuntimeException {
    public OpenAIException(String message) {
        super(message);
    }
}
