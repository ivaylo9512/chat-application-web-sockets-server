package com.chat.app.exceptions;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String exception) {
        super(exception);
    }
}
